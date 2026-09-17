package com.tezzar.mkopo.light.client.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClientApiResponse<T>(
        UUID requestId,
        String message,
        T result,
        LocalDateTime timestamp
) {
}
