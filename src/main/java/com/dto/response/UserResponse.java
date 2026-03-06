package com.dto.response;

import com.entity.Role;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE) //cac field khong co type thi se mac dinh la private
public class UserResponse {
    String id;
    String username;
    //String password;
    String fullName;
    String email;
    Role roles;
}
