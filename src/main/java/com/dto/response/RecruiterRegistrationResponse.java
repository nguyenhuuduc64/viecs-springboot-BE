package com.dto.response;

import com.enums.RequestStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecruiterRegistrationResponse {

    String businessLicenseUrl;
    RequestStatus status;

    // Chỉ trả về thông tin cần thiết của User để Admin biết là ai
    String userId;
    String userFullName;
    String userEmail;


    LocalDateTime processedAt;
    String note;
}