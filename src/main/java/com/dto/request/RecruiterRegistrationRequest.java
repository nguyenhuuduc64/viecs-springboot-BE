package com.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecruiterRegistrationRequest {
    String businessLicenseUrl;

    // Nếu ông chủ muốn cho phép User nhắn nhủ gì đó cho Admin
    String note;
}
