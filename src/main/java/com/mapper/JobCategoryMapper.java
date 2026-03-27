package com.mapper;

import com.dto.request.JobCategoryRequest;
import com.dto.response.JobCategoryResponse;
import com.entity.JobCategory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface JobCategoryMapper {
    JobCategoryResponse toJobCategoryResponse(JobCategory jobCategory);
    JobCategory toJobCategory(JobCategoryRequest request);
}
