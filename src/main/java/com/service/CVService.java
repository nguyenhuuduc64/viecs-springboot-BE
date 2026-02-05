package com.service;

import com.dto.request.ApiResponse;
import com.dto.request.CVRequest;
import com.dto.response.CVResponse;
import com.entity.CV;
import com.entity.User;
import com.exception.AppException;
import com.exception.ErrorCode;
import com.mapper.CVMapper;
import com.repository.CVRepository;
import com.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE,  makeFinal = true)
@RequiredArgsConstructor
@Service
public class CVService {
    CVRepository cvRepository;
    UserRepository userRepository;
    CVMapper cvMapper;
    public List<CVResponse> getCVs(){
        SecurityContext context = SecurityContextHolder.getContext();

        String username = context.getAuthentication().getName();
        // 1. Tìm User và kiểm tra sự tồn tại
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // 2. Lấy danh sách CV từ User (Debug xem user có bao nhiêu CV)
        List<CV> cvList = user.getCvs();
        System.out.println("Số lượng CV tìm thấy: " + (cvList != null ? cvList.size() : 0));

        // 3. Chuyển đổi từ Entity sang DTO (CV -> CVResponse)
        List<CVResponse> responses = cvList.stream()
                .map(cv -> {
                    // Debug nội dung từng CV trước khi build
                    CVResponse response = CVResponse.builder()
                            .id(cv.getId())
                            .content(cv.getContent())
                            .build();

                    return response;
                })
                .toList();

        return responses;
    }

    public CVResponse getCVById(String id) {
        return cvRepository.findById(id)
                .map(cvMapper::toCVResponse) // Mapper đã lo hết việc copy id, content, date...
                .orElseThrow(() -> new RuntimeException("Không tìm thấy CV với ID: " + id));
        // Nên ném lỗi thay vì trả về null để Frontend dễ xử lý lỗi 404
    }

    public CVResponse createCV(CVRequest cvRequest){
        SecurityContext context = SecurityContextHolder.getContext();
        CV cv = cvMapper.toCV(cvRequest);
        String username = context.getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        //System.out.printf(cv.toString());
        cv.setUser(user);
        cvRepository.save(cv);
        return cvMapper.toCVResponse(cv);
    }

    public CVResponse updateCV(String id, CVRequest cvRequest){
        SecurityContext context = SecurityContextHolder.getContext();


        if (id == null ||id.isEmpty()){
            return this.createCV(cvRequest);
        } else {
            CV cv = cvRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy CV"));
            System.out.print("da tim thay cv");
            cv.setContent(cvRequest.getContent());
            return cvMapper.toCVResponse(cvRepository.save(cv));
        }
    }

}
