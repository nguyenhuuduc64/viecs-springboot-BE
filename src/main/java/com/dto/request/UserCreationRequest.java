package com.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE) //cac field khong co type thi se mac dinh la private
public class UserCreationRequest {
    @Size(min = 3, message = "username must be at least 3 characters")
    String username;

    @Size(min = 8, message = "password must be at least 8 characters")
    String password;
    String fullName;

    String email;
    String roles;
    
}
