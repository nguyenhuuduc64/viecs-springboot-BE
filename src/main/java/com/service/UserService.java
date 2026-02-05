package com.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.entity.Role;
import com.exception.AppException;
import com.exception.ErrorCode;
import com.repository.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dto.request.UserCreationRequest;
import com.dto.request.UserUpdateRequest;
import com.dto.response.UserResponse;
import com.entity.User;
import com.mapper.UserMapper;
import com.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Slf4j
@Service
@RequiredArgsConstructor // bien nao co defind la final thi se duoc inject vao class
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true) // cac field khong co type thi se mac dinh la
                                                                     // private va dua vao contructor nhu final

public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository  roleRepository;
    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("User existed");

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById("user").ifPresent(roles::add);


        user.setRoles(roles);
        UserResponse userResponse = userMapper.toUserResponse(user);
        userRepository.save(user);

        return  userResponse;
    }

    /* khi dung Annotation builder ben request thi co the tao nhanh 1 request */
    // UserCreationRequest newUserRequest = UserCreationRequest.builder()
    // .username("name")
    // .fullName("name")
    // .build();

    public UserResponse getMyInfo() {
        //SecurityContextHolder luu thong tin dang nhap cua user sau khi login
        var context = SecurityContextHolder.getContext();
        //lay username tu info cua nguoi dung dang dang nhap hien tai
        String name = context.getAuthentication().getName();

        User byUsername = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return  userMapper.toUserResponse(byUsername);
    }

    //kiểm tra trước khi method được thực hiện
    //dung hasRole thi scope la ROLE_ADMIN con hasAuthority khi co SCOPE_ADMIN
    //@PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    public List<UserResponse> getUsers() {
        log.info("getUsers is called");
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse).toList();
    }

    // tra ve User Entity cua DB
    private User getUserEntityById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    //thực hiện method trước khi kiểm tra
    //dieu kien kiem tra chinh chu moi co the thuc hien
    //returnObject la thong tin cua user lay tu Id
    //uathenticate la thong tin cua nguoi dung nhap hien tai qua jwt
    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUserById(String id) {
        User user = getUserEntityById(id);
        return userMapper.toUserResponse(user);
    }

    public UserResponse updateUser(String id, UserUpdateRequest userUpdate) {
        User user = getUserEntityById(id);
        userMapper.updateUser(user, userUpdate);
        List<Role> roles = roleRepository.findAllById(userUpdate.getRoles());
        //do JPA tra ve List can converse sang Set cua User
        user.setRoles(new  HashSet<>(roles));
        return userMapper.toUserResponse(userRepository.save(user));
    }
    @Transactional
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}
