package com.service;

import com.dto.request.RecruiterRegistrationRequest;
import com.dto.response.RecruiterRegistrationResponse;
import com.entity.RecruiterRegistration;
import com.entity.User;
import com.enums.RequestStatus;
import com.exception.AppException;
import com.exception.ErrorCode;
import com.repository.RecruiterRegistrationRepository;
import com.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RecruiterRegistrationService {
    UserRepository userRepository;
    RecruiterRegistrationRepository recruiterRegistrationRepository;
    public RecruiterRegistrationResponse create(RecruiterRegistrationRequest request){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.print("user name cua nguoi gui"+  username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        RecruiterRegistration registration = RecruiterRegistration.builder()
                .businessLicenseUrl(request.getBusinessLicenseUrl())
                .note(request.getNote())
                .user(user)
                .status(RequestStatus.PENDING) // Đang chờ duyệt
                // createdAt sẽ tự có nhờ @PrePersist đã viết
                .build();

        RecruiterRegistration saved = recruiterRegistrationRepository.save(registration);

        return RecruiterRegistrationResponse.builder()
                .businessLicenseUrl(saved.getBusinessLicenseUrl())
                .status(saved.getStatus())
                .userId(user.getId())
                .userFullName(user.getFullName())
                .userEmail(user.getEmail())
                .processedAt(null)
                .note(saved.getNote())
                .build();
    }
    
}
