package com.tezzar.mkopo.light.loan.loanmanagement.request;

import com.tezzar.mkopo.light.loan.enums.BillingType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record LoanApplicationRequest(
        @NotBlank(message = "Customer ID cannot be blank")
        String customerId,

        @NotBlank(message = "Product ID cannot be blank")
        String productId,

        @NotBlank(message = "Tenure option ID cannot be blank")
        String tenureOptionId,

        @NotNull(message = "Principal amount cannot be null")
        @DecimalMin(value = "1.00", message = "Principal amount must be greater than zero")
        BigDecimal principalAmount,

        @NotNull(message = "Billing type cannot be null")
        BillingType billingType,

        Integer consolidatedDueDay
) {
}
