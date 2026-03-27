package com.service;

import com.dto.request.JobCategoryRequest;
import com.dto.response.JobCategoryResponse;
import com.entity.JobCategory;
import com.mapper.JobCategoryMapper;
import com.repository.JobCategoryRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JobCategoryService {
    JobCategoryRepository jobCategoryRepository;
    JobCategoryMapper jobCategoryMapper;
    public List<JobCategoryResponse> getAll(){

        return jobCategoryRepository.findAll().stream().map(
                jobCategoryMapper::toJobCategoryResponse
        ).toList();
    }

    public JobCategoryResponse create(JobCategoryRequest request){
        log.info("request gui {}", request);
        JobCategory jobCategory = jobCategoryMapper.toJobCategory(request);
        log.info("map qua entity {}", jobCategory);
        jobCategoryRepository.save(jobCategory);
        return jobCategoryMapper.toJobCategoryResponse(jobCategory);
    }

}
