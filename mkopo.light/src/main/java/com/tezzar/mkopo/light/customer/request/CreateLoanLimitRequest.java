package com.tezzar.mkopo.light.customer.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateLoanLimitRequest(
        @NotBlank(message = "Customer ID cannot be blank")
        String customerId,

        @NotNull(message = "Max amount cannot be null")
        @DecimalMin(value = "1.00", message = "Max amount must be greater than zero")
        BigDecimal maxAmount,

        String notes
) {
}
