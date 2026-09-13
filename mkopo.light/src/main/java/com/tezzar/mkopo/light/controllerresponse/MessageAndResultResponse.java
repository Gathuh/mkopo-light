package com.tezzar.mkopo.light.controllerresponse;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageAndResultResponse<T>(
        UUID requestId,
        String message,
        T result,
        String timestamp
) {
    public static <T> MessageAndResultResponse<T> success(T data,String message){
        return new MessageAndResultResponse<>(
                UUID.randomUUID(),
                message,
                data,
                LocalDateTime.now().toString()
        );
    }
}
