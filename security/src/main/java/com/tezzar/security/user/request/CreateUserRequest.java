package com.tezzar.security.user.request;

import com.tezzar.security.user.UserEntity;
import com.tezzar.security.user.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotBlank(message = "First name cannot be blank")
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        String lastName,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email must be a valid email address")
        String email,

        @NotBlank(message = "Phone number cannot be blank")
        String phoneNumber,

        @NotBlank(message = "Password cannot be blank")
        String password,

        @NotNull(message = "Role cannot be null")
        UserRole role
) {
        public static UserEntity toUserEntity(CreateUserRequest request) {
                return UserEntity.builder()
                        .firstName(request.firstName())
                        .lastName(request.lastName())
                        .email(request.email())
                        .phoneNumber(request.phoneNumber())
                        .password(request.password())
                        .role(request.role())
                        .build();
        }
}
