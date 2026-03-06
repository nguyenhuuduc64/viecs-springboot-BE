package com.mapper;


import com.dto.request.RoleRequest;
import com.dto.response.RoleResponse;
import com.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role toRole(RoleRequest request);
    RoleResponse toRoleResponse(Role request);
    //do entity Role nhan 1 Set permissions nhung request thi chi co String nen can bo qua field nay


}
