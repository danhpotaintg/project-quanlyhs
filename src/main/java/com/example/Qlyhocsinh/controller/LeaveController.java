package com.example.Qlyhocsinh.controller;

import com.example.Qlyhocsinh.dto.request.ApiResponse;
import com.example.Qlyhocsinh.dto.request.ApproveRequest;
import com.example.Qlyhocsinh.dto.request.LeaveRequestRequest;
import com.example.Qlyhocsinh.dto.response.LeaveRequestResponse;
import com.example.Qlyhocsinh.service.LeaveRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leave-request")
@RequiredArgsConstructor
public class LeaveController {
    private final LeaveRequestService leaveRequestService;

    @PostMapping()
    public ApiResponse<LeaveRequestResponse> createLeaveRequest(@RequestBody LeaveRequestRequest request){
        return ApiResponse.<LeaveRequestResponse>builder()
                .result(leaveRequestService.createLeaveRequest(request))
                .build();
    }

    @GetMapping()
    public ApiResponse<List<LeaveRequestResponse>> getAllLeaveRequests(){
        return ApiResponse.<List<LeaveRequestResponse>>builder()
                .result(leaveRequestService.getAllLeaveRequestByHomeroomTeacher())
                .build();
    }

    @PostMapping("/{leaveId}")
    public ApiResponse<LeaveRequestResponse> approveLeaveRequestByTeacher
            (@PathVariable Long leaveId, @RequestBody ApproveRequest request){
        return ApiResponse.<LeaveRequestResponse>builder()
                .result(leaveRequestService.approvedLeaveRequest(leaveId, request))
                .build();
    }

    @GetMapping("/student")
    public ApiResponse<List<LeaveRequestResponse>> getStudentLeaveHistory(){
        return ApiResponse.<List<LeaveRequestResponse>>builder()
                .result(leaveRequestService.getStudentLeaveHistory())
                .build();
    }
}
