package com.tezzar.security.user.request;

import com.tezzar.security.user.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRequest(
        @NotBlank(message = "First name cannot be blank")
        String firstName,

        @NotBlank(message = "Last name cannot be blank")
        String lastName,

        @NotBlank(message = "Phone number cannot be blank")
        String phoneNumber,

        @NotNull(message = "Role cannot be null")
        UserRole role
) {
}
