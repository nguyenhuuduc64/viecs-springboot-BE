package com.mapper;

import com.dto.request.CVComponentTemplateRequest;
import com.dto.response.CVComponentTemplateResponse;
import com.entity.CVComponentTemplate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CVComponentTemplateMapper {
    CVComponentTemplateResponse toCVComponentTemplateResponse(CVComponentTemplate cvComponentTemplate);
    CVComponentTemplate toCVComponentTemplate(CVComponentTemplateRequest cvComponentTemplateRequest);
}
