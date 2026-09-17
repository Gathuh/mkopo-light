package com.tezzar.mkopo.light.client.dto;

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
}
