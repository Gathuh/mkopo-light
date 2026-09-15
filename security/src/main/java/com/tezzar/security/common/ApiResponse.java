package com.tezzar.security.common;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApiResponse<T>(
        UUID requestId,
        String message,
        T result,
        LocalDateTime timestamp
) {
        public static <T> ApiResponse<T> success(T data, String message) {
                return new ApiResponse<>(
                        UUID.randomUUID(),
                        message,
                        data,
                        LocalDateTime.now()
                );
        }

        public static <T> ApiResponse<T> error(String message) {
                return new ApiResponse<>(
                        UUID.randomUUID(),
                        message,
                        null,
                        LocalDateTime.now()
                );
        }
}
