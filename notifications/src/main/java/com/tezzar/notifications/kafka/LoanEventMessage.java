package com.tezzar.notifications.kafka;

import com.tezzar.notifications.domain.enums.LoanEventType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record LoanEventMessage(
        LoanEventType eventType,
        String loanId,
        String customerId,
        String productName,
        BigDecimal principalAmount,
        BigDecimal outstandingBalance,
        BigDecimal paymentAmount,
        LocalDate dueDate,
        String approvedByUserId,
        LocalDateTime occurredAt
) {
}
