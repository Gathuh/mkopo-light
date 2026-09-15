package com.tezzar.security.user.response;

import com.tezzar.security.user.UserEntity;
import com.tezzar.security.user.enums.UserRole;
import com.tezzar.security.user.enums.UserStatus;

import java.time.LocalDateTime;

public record UserResponse(
        String id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        UserRole role,
        UserStatus status,
        LocalDateTime createdAt
) {
        public static UserResponse fromEntity(UserEntity user) {
                return new UserResponse(
                        user.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getPhoneNumber(),
                        user.getRole(),
                        user.getStatus(),
                        user.getCreatedAt()
                );
        }
}
