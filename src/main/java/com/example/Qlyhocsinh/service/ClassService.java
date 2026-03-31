package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.dto.request.AssignStudentClassRequest;
import com.example.Qlyhocsinh.dto.request.ClassRequest;
import com.example.Qlyhocsinh.dto.response.AssignStudentClassResponse;
import com.example.Qlyhocsinh.dto.response.ClassResponse;
import com.example.Qlyhocsinh.dto.response.StudentResponse;
import com.example.Qlyhocsinh.entity.ClassRoom;
import com.example.Qlyhocsinh.entity.Student;
import com.example.Qlyhocsinh.entity.Teacher;
import com.example.Qlyhocsinh.exception.AppException;
import com.example.Qlyhocsinh.exception.ErrorCode;
import com.example.Qlyhocsinh.mapper.AssignMapper;
import com.example.Qlyhocsinh.mapper.ClassMapper;
import com.example.Qlyhocsinh.mapper.StudentMapper;
import com.example.Qlyhocsinh.repository.ClassRepository;
import com.example.Qlyhocsinh.repository.StudentRepository;
import com.example.Qlyhocsinh.repository.TeacherRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClassService {

    private final ClassRepository classRepository;
    private final ClassMapper classMapper;
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;
    private final TeacherRepository teacherRepository;
    private final AssignMapper assignMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public ClassResponse createClass(ClassRequest request){
        if(classRepository.findByClassName(request.getClassName()).isPresent()){
            log.info(request.getClassName());
            throw new AppException(ErrorCode.ClASS_EXISTED);
        }
        ClassRoom classRoom = classMapper.toClassRoom(request);
        return classMapper.toClassResponse(classRepository.save(classRoom));
    }

    public ClassResponse updateClass(Long id, ClassRequest request){
        ClassRoom classRoom = classRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.CLASS_NOT_FOUND));
        classMapper.updateClass(classRoom, request);
        return classMapper.toClassResponse(classRepository.save(classRoom));
    }

    public List<ClassResponse> getAllClass(){
        var cls = classRepository.findAll();
        return cls.stream().map(classMapper::toClassResponse).toList();
    }

    @Transactional
    public void deletedClass(Long id){
        ClassRoom classRoom = classRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));

        List<Student> students = studentRepository.findByClassRoomId(id);
        for( Student student : students){
            student.setClassRoom(null);
        }
        studentRepository.saveAll(students);
        classRepository.deleteById(id);
    }

    public StudentResponse addStudentToClass(String studentId, Long classId){
        ClassRoom classRoom = classRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(()-> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        if (student.getClassRoom() != null) {
            throw new RuntimeException("Student already in a class");
        }

        student.setClassRoom(classRoom);
        return studentMapper.toStudentResponse(studentRepository.save(student));
    }

    public AssignStudentClassResponse addStudentsToClass(Long classId, AssignStudentClassRequest request){
        ClassRoom classRoom = classRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));

        List<Student> studentValidIDs = studentRepository.findAllWithoutClass()
                .stream()
                .filter(s -> request.getStudentIds().contains(s.getUserId()))
                .toList();


        for(Student student : studentValidIDs){
            student.setClassRoom(classRoom);
            student.setAcademicYear(classRoom.getAcademicYear());
        }

        List<Student> saved = studentRepository.saveAll(studentValidIDs);
        return assignMapper.toAssignStudentClassResponse(
                classRoom, classRoom.getAcademicYear(), saved
        );

    }

    public List<StudentResponse> getStuInClass(Long id){
        return studentMapper.toStudentResponseList(studentRepository.findByClassRoomId(id));
    }

    public void removeStudentFromClass(String studentId, Long classId){
        Student student = studentRepository.findByUserIdAndClassRoomId(studentId, classId)
                .orElseThrow(()-> new RuntimeException("Student not in this class"));

        student.setClassRoom(null);
        studentRepository.save(student);
    }

    public void transferStudent(String studentId, Long newClassId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        ClassRoom newClass = classRepository.findById(newClassId)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));

        if(student.getClassRoom().getId().equals(newClassId)) throw new RuntimeException("Student already in to class");

        student.setClassRoom(newClass);

        studentRepository.save(student);

    }

    public void addClassToTeacher(String teacherId, Long classId){
        ClassRoom classRoom = classRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new AppException(ErrorCode.TEACHER_NOT_FOUND));

        if(classRoom.getTeacher() != null){
            throw new RuntimeException("Class already a teacher");
        }

        boolean exists = classRepository.existsByTeacherUserId(teacherId);
        if(exists) throw new RuntimeException("Teacher already assigned to another class");

        classRoom.setTeacher(teacher);
        classRepository.save(classRoom);

    }

    public void removeTeacherFormClass(String teacherId, Long classId){
        ClassRoom classRoom = classRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        if(classRoom.getTeacher() == null){
            throw new RuntimeException("Class does not have teacher");
        }

        if(! classRoom.getTeacher().getUserId().equals(teacherId)){
            throw new RuntimeException("Teacher does not assigned to this class");
        }

        classRoom.setTeacher(null);
        classRepository.save(classRoom);
    }

}
