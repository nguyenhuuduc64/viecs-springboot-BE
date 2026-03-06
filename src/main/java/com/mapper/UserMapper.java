package com.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.dto.request.UserUpdateRequest;
import com.dto.request.UserCreationRequest;
import com.dto.response.UserResponse;
import com.entity.User;


@Mapper(componentModel = "spring")
public interface UserMapper {
    //tự động nhận request và trả về User
    @Mapping(target = "roles", ignore = true)
    User toUser(UserCreationRequest request);
    //nhận vào 1 Object đã tồn tại và cập nhật lại nó với các field được map
    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);


    UserResponse toUserResponse(User user);
}
