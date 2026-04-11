package com.example.Qlyhocsinh.dto.request;

import com.example.Qlyhocsinh.enums.LeaveRequestStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level =  AccessLevel.PRIVATE)
public class ApproveRequest {
    LeaveRequestStatus status;
}
