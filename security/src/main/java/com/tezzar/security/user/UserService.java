package com.tezzar.security.user;

import com.tezzar.security.user.request.CreateUserRequest;
import com.tezzar.security.user.request.UpdateUserRequest;
import com.tezzar.security.user.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);
    UserResponse findById(String id);
    List<UserResponse> findAll();
    UserResponse updateUser(String id, UpdateUserRequest request);
    void softDelete(String id);
}
