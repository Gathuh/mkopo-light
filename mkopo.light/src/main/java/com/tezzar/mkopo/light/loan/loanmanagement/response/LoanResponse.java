package com.tezzar.mkopo.light.loan.loanmanagement.response;

import com.tezzar.mkopo.light.fees.enums.FeeTiming;
import com.tezzar.mkopo.light.loan.loanmanagement.LoanEntity;
import com.tezzar.mkopo.light.loan.enums.BillingType;
import com.tezzar.mkopo.light.loan.enums.LoanState;
import com.tezzar.mkopo.light.loan.enums.LoanType;
import com.tezzar.mkopo.light.tenure.enums.TenureType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record LoanResponse(
        String id,
        String customerId,
        String approvedByUserId,
        String productId,
        String productName,
        String tenureOptionId,
        Integer tenureValue,
        TenureType tenureType,
        LoanType loanType,
        BillingType billingType,
        Integer consolidatedDueDay,
        BigDecimal principalAmount,
        BigDecimal disbursedAmount,
        BigDecimal outstandingBalance,
        BigDecimal accruedInterest,
        BigDecimal totalRepaid,
        BigDecimal snapshotServiceFeeRate,
        FeeTiming snapshotServiceFeeTiming,
        BigDecimal snapshotLateFeeAmount,
        Integer snapshotTriggerDays,
        BigDecimal snapshotDailyFeeRate,
        BigDecimal snapshotProductFeeAmount,
        Boolean snapshotCapitalized,
        LoanState loanState,
        LocalDate applicationDate,
        LocalDate approvalDate,
        LocalDate disbursementDate,
        LocalDate dueDate,
        LocalDate maturityDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<InstallmentResponse> installments,
        List<TransactionResponse> transactions
) {
        public static LoanResponse fromEntity(LoanEntity loan) {
                List<InstallmentResponse> installmentResponses = loan.getInstallments()
                        .stream()
                        .map(InstallmentResponse::fromEntity)
                        .toList();

                List<TransactionResponse> transactionResponses = loan.getTransactions()
                        .stream()
                        .map(TransactionResponse::fromEntity)
                        .toList();

                return new LoanResponse(
                        loan.getId(),
                        loan.getCustomerId(),
                        loan.getApprovedByUserId(),
                        loan.getProductId(),
                        loan.getProductName(),
                        loan.getTenureOptionId(),
                        loan.getTenureValue(),
                        loan.getTenureType(),
                        loan.getLoanType(),
                        loan.getBillingType(),
                        loan.getConsolidatedDueDay(),
                        loan.getPrincipalAmount(),
                        loan.getDisbursedAmount(),
                        loan.getOutstandingBalance(),
                        loan.getAccruedInterest(),
                        loan.getTotalRepaid(),
                        loan.getSnapshotServiceFeeRate(),
                        loan.getSnapshotServiceFeeTiming(),
                        loan.getSnapshotLateFeeAmount(),
                        loan.getSnapshotTriggerDays(),
                        loan.getSnapshotDailyFeeRate(),
                        loan.getSnapshotProductFeeAmount(),
                        loan.getSnapshotCapitalized(),
                        loan.getLoanState(),
                        loan.getApplicationDate(),
                        loan.getApprovalDate(),
                        loan.getDisbursementDate(),
                        loan.getDueDate(),
                        loan.getMaturityDate(),
                        loan.getCreatedAt(),
                        loan.getUpdatedAt(),
                        installmentResponses,
                        transactionResponses
                );
        }
}
