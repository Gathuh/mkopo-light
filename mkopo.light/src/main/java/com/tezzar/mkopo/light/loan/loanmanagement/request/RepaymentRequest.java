package com.tezzar.mkopo.light.loan.loanmanagement.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RepaymentRequest(
        @NotNull(message = "Payment amount cannot be null")
        @DecimalMin(value = "1.00", message = "Payment amount must be greater than zero")
        BigDecimal amount,

        @NotBlank(message = "Paid by cannot be blank")
        String paidBy,

        String reference
) {
}
