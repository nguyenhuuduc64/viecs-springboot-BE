package com.service;

import com.dto.request.RecruitmentRequest;
import com.dto.response.CompanyResponse;
import com.dto.response.RecruitmentResponse;
import com.entity.Company;
import com.entity.JobCategory;
import com.entity.Recruitment;
import com.entity.User;
import com.exception.AppException;
import com.exception.ErrorCode;
import com.mapper.RecruitmentMapper;
import com.repository.CompanyRepository;
import com.repository.JobCategoryRepository;
import com.repository.RecruitmentRepository;
import com.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RecruitmentService {
    RecruitmentRepository recruitmentRepository;
    RecruitmentMapper recruitmentMapper;
    UserRepository userRepository;
    CompanyRepository companyRepository;
    JobCategoryRepository jobCategoryRepository;
    public  List<RecruitmentResponse> getAll(){
        return recruitmentRepository.findAll().stream().map(
                recruitmentMapper::toRecruitmentResponse
        ).toList();
    }

    public  RecruitmentResponse getRecruitment(String id){
        Recruitment recruitment = recruitmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RECRUITMENT_NOT_FOUND));
        return recruitmentMapper.toRecruitmentResponse(recruitment);
    }

    public RecruitmentResponse create(RecruitmentRequest request){
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Company company = companyRepository.findByUserId(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND));

        JobCategory jobCategory = jobCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));


        Recruitment recruitment = recruitmentMapper.toRecruitment(request);

        recruitment.setCompany(company);
        recruitment.setCategory(jobCategory);

        log.info("thong tin tuyen dung: {}" , recruitment);
        Recruitment response = recruitmentRepository.save(recruitment);
        return recruitmentMapper.toRecruitmentResponse(response);
    }



}
