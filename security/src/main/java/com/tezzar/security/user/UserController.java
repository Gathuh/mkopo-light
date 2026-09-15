package com.tezzar.security.user;

import com.tezzar.security.common.ApiResponse;
import com.tezzar.security.user.request.CreateUserRequest;
import com.tezzar.security.user.request.UpdateUserRequest;
import com.tezzar.security.user.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/security/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Create and manage system users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create a new user",
            description = "Creates a system user with a role of ADMIN, LOAN_OFFICER or CUSTOMER")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        userService.createUser(request),
                        "User created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(
            @PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(
                userService.findById(id),
                "User retrieved successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all users",
            description = "Returns all users excluding deleted ones")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success(
                userService.findAll(),
                "Users retrieved successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a user")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                userService.updateUser(id, request),
                "User updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a user",
            description = "Marks the user as DELETED without removing from database")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable String id) {
        userService.softDelete(id);
        return ResponseEntity.ok(ApiResponse.success(
                null,
                "User deleted successfully"));
    }
}
