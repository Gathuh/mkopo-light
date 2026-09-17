package com.tezzar.mkopo.light.loan.loanmanagement.response;

import com.tezzar.mkopo.light.loan.enums.TransactionType;
import com.tezzar.mkopo.light.loan.transaction.LoanTransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        String id,
        String loanId,
        TransactionType transactionType,
        BigDecimal amount,
        BigDecimal balanceBefore,
        BigDecimal balanceAfter,
        String description,
        String createdBy,
        LocalDateTime transactionDate
) {
        public static TransactionResponse fromEntity(LoanTransaction transaction) {
                return new TransactionResponse(
                        transaction.getId(),
                        transaction.getLoan().getId(),
                        transaction.getTransactionType(),
                        transaction.getAmount(),
                        transaction.getBalanceBefore(),
                        transaction.getBalanceAfter(),
                        transaction.getDescription(),
                        transaction.getCreatedBy(),
                        transaction.getTransactionDate()
                );
        }
}
