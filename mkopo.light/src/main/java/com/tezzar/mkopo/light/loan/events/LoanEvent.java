package com.tezzar.mkopo.light.loan.events;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record LoanEvent(
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
        public static LoanEvent of(LoanEventType eventType, String loanId, String customerId,
                                   String productName, BigDecimal principalAmount,
                                   BigDecimal outstandingBalance, BigDecimal paymentAmount,
                                   LocalDate dueDate, String approvedByUserId) {
                return new LoanEvent(
                        eventType,
                        loanId,
                        customerId,
                        productName,
                        principalAmount,
                        outstandingBalance,
                        paymentAmount,
                        dueDate,
                        approvedByUserId,
                        LocalDateTime.now()
                );
        }
}
