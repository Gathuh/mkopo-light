package com.tezzar.mkopo.light.exception;

import com.tezzar.mkopo.light.controllerresponse.MessageAndResultResponse;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<MessageAndResultResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(MessageAndResultResponse.success(null, ex.getMessage()));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<MessageAndResultResponse<Void>> handleDuplicate(DuplicateResourceException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(MessageAndResultResponse.success(null, ex.getMessage()));
    }

    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<MessageAndResultResponse<Void>> handleBusinessValidation(BusinessValidationException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(MessageAndResultResponse.success(null, ex.getMessage()));
    }

    @ExceptionHandler(InvalidLoanStateException.class)
    public ResponseEntity<MessageAndResultResponse<Void>> handleInvalidState(InvalidLoanStateException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(MessageAndResultResponse.success(null, ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedOperationException.class)
    public ResponseEntity<MessageAndResultResponse<Void>> handleUnauthorized(UnauthorizedOperationException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(MessageAndResultResponse.success(null, ex.getMessage()));
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<MessageAndResultResponse<Void>> handleServiceUnavailable(ServiceUnavailableException ex) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(MessageAndResultResponse.success(null, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageAndResultResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(MessageAndResultResponse.success(null, message));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<MessageAndResultResponse<Void>> handleMissingParam(MissingServletRequestParameterException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(MessageAndResultResponse.success(null, "Missing required parameter: " + ex.getParameterName()));
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<MessageAndResultResponse<Void>> handleFeignNotFound(FeignException.NotFound ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(MessageAndResultResponse.success(null, "Remote service resource not found"));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<MessageAndResultResponse<Void>> handleFeignException(FeignException ex) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(MessageAndResultResponse.success(null, "A remote service is unavailable. Please try again later"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageAndResultResponse<Void>> handleGeneral(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(MessageAndResultResponse.success(null, "An unexpected error occurred: " + ex.getMessage()));
    }
}
