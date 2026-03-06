package com.controller;

import com.dto.request.ApiResponse;
import com.dto.request.RecruiterRegistrationRequest;
import com.dto.response.RecruiterRegistrationResponse;
import com.service.RecruiterRegistrationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/recruiter")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RecruiterRegistrationController {
    RecruiterRegistrationService recruiterRegistrationService;
    @PostMapping("")
    ApiResponse<RecruiterRegistrationResponse> create(@RequestBody RecruiterRegistrationRequest request){
        RecruiterRegistrationResponse response = recruiterRegistrationService.create(request);
        return ApiResponse.<RecruiterRegistrationResponse>builder()
                .code(200)
                .message("request register recruiter successfully")
                .result(response)
                .build();
    }
}
