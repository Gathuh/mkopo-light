package com.tezzar.mkopo.light.loan.loanmanagement.response;

import com.tezzar.mkopo.light.loan.enums.InstallmentState;
import com.tezzar.mkopo.light.loan.installment.LoanInstallment;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InstallmentResponse(
        String id,
        Integer installmentNumber,
        LocalDate dueDate,
        BigDecimal principalAmount,
        BigDecimal interestAmount,
        BigDecimal feeAmount,
        BigDecimal totalDue,
        BigDecimal amountPaid,
        BigDecimal outstandingAmount,
        InstallmentState state,
        LocalDate paidAt
) {
        public static InstallmentResponse fromEntity(LoanInstallment installment) {
                return new InstallmentResponse(
                        installment.getId(),
                        installment.getInstallmentNumber(),
                        installment.getDueDate(),
                        installment.getPrincipalAmount(),
                        installment.getInterestAmount(),
                        installment.getFeeAmount(),
                        installment.getTotalDue(),
                        installment.getAmountPaid(),
                        installment.getOutstandingAmount(),
                        installment.getState(),
                        installment.getPaidAt()
                );
        }
}
