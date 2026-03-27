package com.controller;

import com.dto.request.ApiResponse;
import com.dto.request.JobCategoryRequest;
import com.dto.response.JobCategoryResponse;
import com.entity.JobCategory;
import com.service.JobCategoryService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/job-category")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JobCategoryController {
    JobCategoryService jobCategoryService;
    @GetMapping("")
    public ApiResponse<List<JobCategoryResponse>> getAll(){
        return ApiResponse.<List<JobCategoryResponse>>builder()
                .code(200)
                .result(jobCategoryService.getAll())
                .build();
    }
    @PostMapping("")
    public ApiResponse<JobCategoryResponse> create(@RequestBody JobCategoryRequest request){
        return ApiResponse.<JobCategoryResponse>builder()
                .code(200)
                .message("create job category successfully")
                .result(jobCategoryService.create(request))
                .build();
    }

}
