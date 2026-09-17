package com.tezzar.mkopo.light.loan.calculator;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InstallmentSchedule(
        Integer installmentNumber,
        LocalDate dueDate,
        BigDecimal principalAmount,
        BigDecimal interestAmount,
        BigDecimal totalDue
) {
}
