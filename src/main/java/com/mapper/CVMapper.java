package com.mapper;

import com.dto.request.CVRequest;
import com.dto.response.CVResponse;
import com.entity.CV;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CVMapper {
    CV  toCV(CVRequest cvRequest);
    CVResponse toCVResponse(CV cvRequest);
}
