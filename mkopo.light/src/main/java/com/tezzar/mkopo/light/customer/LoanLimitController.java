package com.tezzar.mkopo.light.customer;

import com.tezzar.mkopo.light.controllerresponse.MessageAndResultResponse;
import com.tezzar.mkopo.light.customer.request.CreateLoanLimitRequest;
import com.tezzar.mkopo.light.customer.request.UpdateLoanLimitRequest;
import com.tezzar.mkopo.light.customer.response.LoanLimitResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mkopo/loan-limits")
@RequiredArgsConstructor
@Tag(name = "Loan Limits", description = "Manage customer loan limits and exposure tracking")
public class LoanLimitController {

    private final LoanLimitService loanLimitService;

    @PostMapping
    @Operation(
            summary = "Create a loan limit for a customer",
            description = "Sets the maximum borrowing limit for a customer. Customer must exist in security-service"
    )
    public ResponseEntity<MessageAndResultResponse<LoanLimitResponse>> createLimit(
            @Valid @RequestBody CreateLoanLimitRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(MessageAndResultResponse.success(
                        loanLimitService.createLimit(request),
                        "Loan limit created successfully"));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get loan limit for a customer")
    public ResponseEntity<MessageAndResultResponse<LoanLimitResponse>> getLimit(
            @PathVariable String customerId) {
        return ResponseEntity.ok(MessageAndResultResponse.success(
                loanLimitService.findByCustomerId(customerId),
                "Loan limit retrieved successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all loan limits")
    public ResponseEntity<MessageAndResultResponse<List<LoanLimitResponse>>> getAllLimits() {
        return ResponseEntity.ok(MessageAndResultResponse.success(
                loanLimitService.findAll(),
                "Loan limits retrieved successfully"));
    }

    @PutMapping("/customer/{customerId}")
    @Operation(
            summary = "Update loan limit for a customer",
            description = "Adjusts the maximum borrowing limit. Useful after repayment history review"
    )
    public ResponseEntity<MessageAndResultResponse<LoanLimitResponse>> updateLimit(
            @PathVariable String customerId,
            @Valid @RequestBody UpdateLoanLimitRequest request) {
        return ResponseEntity.ok(MessageAndResultResponse.success(
                loanLimitService.updateLimit(customerId, request),
                "Loan limit updated successfully"));
    }
}
