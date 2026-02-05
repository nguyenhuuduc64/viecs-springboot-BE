package com.controller;

import com.dto.request.ApiResponse;
import com.dto.request.CVComponentTemplateRequest;
import com.dto.response.CVComponentTemplateResponse;
import com.mapper.CVComponentTemplateMapper;
import com.repository.CVComponentTemplateRepository;
import com.repository.CVRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cv-components")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CVComponentTemplateController {
    CVComponentTemplateRepository cvComponentTemplateRepository;
    CVComponentTemplateMapper cvComponentTemplateMapper;
    @GetMapping
    public ApiResponse<List<CVComponentTemplateResponse>> getCVComponentTemplate(){
        List <CVComponentTemplateResponse> cvComponentResponses = cvComponentTemplateRepository.findAll().stream().map(
                (component) -> cvComponentTemplateMapper.toCVComponentTemplateResponse(component)
        ).toList();
        return ApiResponse.<List<CVComponentTemplateResponse>>builder()
                .code(200)
                .result(cvComponentResponses)
                .message("Get all cv component successfully")
                .build();
    }

    @PostMapping
    public ApiResponse<CVComponentTemplateResponse> createComponentTemplate(@RequestBody CVComponentTemplateRequest cvComponentTemplateRequest){
        CVComponentTemplateResponse component = cvComponentTemplateMapper.toCVComponentTemplateResponse(cvComponentTemplateRepository.save(cvComponentTemplateMapper.toCVComponentTemplate(cvComponentTemplateRequest)));
        return ApiResponse.<CVComponentTemplateResponse>builder()
                .code(200)
                .message("Create component template successfully")
                .result(component)
                .build();
    }

}
