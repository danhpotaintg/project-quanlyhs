package com.example.Qlyhocsinh.service;

import com.example.Qlyhocsinh.entity.IdSequence;
import com.example.Qlyhocsinh.repository.IdSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IdGeneratorService {

    private final IdSequenceRepository idSequenceRepository;

    @Transactional
    public String generateId(String type, String period, int extraInfo) {
        IdSequence sequence = idSequenceRepository.findByTypeAndPeriod(type, period)
                .orElseGet(() -> {
                    IdSequence newSeq = new IdSequence(type, period, 0);
                    return idSequenceRepository.save(newSeq);
                });

        int nextVal = sequence.getCurrentValue() + 1;
        sequence.setCurrentValue(nextVal);
        idSequenceRepository.save(sequence);

        if ("STUDENT".equals(type)) {
            String yearSuffix = period.length() >= 4 ? period.substring(2) : period;
            return String.format("K%sST%05d", yearSuffix, nextVal);
        } else if ("TEACHER".equals(type)) {
            String birthYearStr = String.valueOf(extraInfo);
            String birthYearSuffix = birthYearStr.length() >= 4
                    ? birthYearStr.substring(birthYearStr.length() - 2)
                    : birthYearStr;

            return String.format("D%sTC%05d", birthYearSuffix, nextVal);
        }

        throw new IllegalArgumentException("Loại đối tượng không hợp lệ!");
    }
}