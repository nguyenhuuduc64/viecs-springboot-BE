package com.controller;

import com.dto.request.ApiResponse;
import com.dto.request.RecruitmentRequest;
import com.dto.response.RecruitmentResponse;
import com.service.RecruitmentService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recruitment")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class RecruitmentController {
    RecruitmentService recruitmentService;
    @GetMapping("")
    public ApiResponse<List<RecruitmentResponse>> getAllRecruitment(){
        return ApiResponse.<List<RecruitmentResponse>>builder()
                .code(200)
                .message("get all recruiment successfully")
                .result(recruitmentService.getAll())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<RecruitmentResponse> getRecruitment(@PathVariable("id") String id){
        return ApiResponse.<RecruitmentResponse>builder()
                .code(200)
                .message("get recruiment successfully")
                .result(recruitmentService.getRecruitment(id))
                .build();
    }

    @PostMapping("")
    public ApiResponse<RecruitmentResponse> create(@RequestBody RecruitmentRequest request){
        return ApiResponse.<RecruitmentResponse>builder()
                .message("Create recruitment successfully")
                .code(200)
                .result(recruitmentService.create(request))
                .build();
    }



}
