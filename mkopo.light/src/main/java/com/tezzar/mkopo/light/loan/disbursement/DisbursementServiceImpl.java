package com.tezzar.mkopo.light.loan.disbursement;

import com.tezzar.mkopo.light.customer.LoanLimitService;
import com.tezzar.mkopo.light.fees.enums.FeeTiming;
import com.tezzar.mkopo.light.loan.calculator.InstallmentSchedule;
import com.tezzar.mkopo.light.loan.calculator.LoanCalculatorService;
import com.tezzar.mkopo.light.loan.enums.LoanState;
import com.tezzar.mkopo.light.loan.enums.LoanType;
import com.tezzar.mkopo.light.loan.enums.TransactionType;
import com.tezzar.mkopo.light.loan.events.LoanEvent;
import com.tezzar.mkopo.light.loan.events.LoanEventPublisher;
import com.tezzar.mkopo.light.loan.events.LoanEventType;
import com.tezzar.mkopo.light.loan.feeengine.FeeEngineService;
import com.tezzar.mkopo.light.loan.installment.LoanInstallment;
import com.tezzar.mkopo.light.loan.loanmanagement.LoanEntity;
import com.tezzar.mkopo.light.loan.loanmanagement.LoanRepository;
import com.tezzar.mkopo.light.loan.loanmanagement.LoanServiceImpl;
import com.tezzar.mkopo.light.loan.loanmanagement.response.LoanResponse;
import com.tezzar.mkopo.light.loan.transaction.LoanTransaction;
import com.tezzar.mkopo.light.tenure.TenureEntity;
import com.tezzar.mkopo.light.tenure.TenureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DisbursementServiceImpl implements DisbursementService {

    private final LoanRepository loanRepository;
    private final LoanServiceImpl loanService;
    private final TenureService tenureService;
    private final FeeEngineService feeEngineService;
    private final LoanCalculatorService loanCalculatorService;
    private final LoanLimitService loanLimitService;
    private final LoanEventPublisher eventPublisher;

    @Override
    @Transactional
    public LoanResponse disburse(String loanId, String disbursedBy) {
        LoanEntity loan = loanService.getLoanOrThrow(loanId);
        loan.transitionTo(LoanState.ACTIVE);

        TenureEntity tenure = tenureService.findEntityById(loan.getTenureOptionId());
        feeEngineService.snapshotFees(loan, tenure);

        BigDecimal principal = loan.getPrincipalAmount();
        BigDecimal disbursedAmount = feeEngineService.calculateDisbursedAmount(principal, loan);
        BigDecimal serviceFee = feeEngineService.calculateServiceFee(principal, loan);

        loan.setDisbursedAmount(disbursedAmount);
        loan.setDisbursementDate(LocalDate.now());

        LocalDate dueDate = loanCalculatorService.calculateDueDate(
                LocalDate.now(), tenure.getTenureValue(), tenure.getTenureType());
        loan.setDueDate(dueDate);
        loan.setMaturityDate(dueDate);

        BigDecimal initialBalance = loan.getSnapshotCapitalized() != null && loan.getSnapshotCapitalized()
                ? principal.add(serviceFee)
                : principal;
        loan.setOutstandingBalance(initialBalance);

        LoanTransaction disbursementTx = LoanTransaction.builder()
                .loan(loan)
                .transactionType(TransactionType.DISBURSEMENT)
                .amount(disbursedAmount)
                .balanceBefore(BigDecimal.ZERO)
                .balanceAfter(initialBalance)
                .description("Loan disbursed")
                .createdBy(disbursedBy)
                .build();
        loan.getTransactions().add(disbursementTx);

        if (serviceFee.compareTo(BigDecimal.ZERO) > 0
                && loan.getSnapshotServiceFeeTiming() == FeeTiming.UPFRONT) {
            LoanTransaction feeTx = LoanTransaction.builder()
                    .loan(loan)
                    .transactionType(TransactionType.FEE_APPLIED)
                    .amount(serviceFee)
                    .balanceBefore(initialBalance)
                    .balanceAfter(initialBalance)
                    .description("Service fee applied upfront")
                    .createdBy("SYSTEM")
                    .build();
            loan.getTransactions().add(feeTx);
        }

        if (loan.getLoanType() == LoanType.INSTALLMENT && tenure.getInstallmentCount() != null) {
            BigDecimal annualRate = loan.getSnapshotDailyFeeRate() != null
                    ? loan.getSnapshotDailyFeeRate().multiply(BigDecimal.valueOf(365))
                    : loan.getSnapshotServiceFeeRate() != null
                        ? loan.getSnapshotServiceFeeRate()
                        : BigDecimal.ZERO;

            List<InstallmentSchedule> schedule = loanCalculatorService.generateInstallmentSchedule(
                    principal, tenure.getInstallmentCount(), annualRate,
                    LocalDate.now(), tenure.getTenureType());

            for (InstallmentSchedule s : schedule) {
                loan.getInstallments().add(LoanInstallment.builder()
                        .loan(loan)
                        .installmentNumber(s.installmentNumber())
                        .dueDate(s.dueDate())
                        .principalAmount(s.principalAmount())
                        .interestAmount(s.interestAmount())
                        .totalDue(s.totalDue())
                        .build());
            }

            loan.setMaturityDate(schedule.getLast().dueDate());
        }

        loanLimitService.increaseExposure(loan.getCustomerId(), principal);
        LoanEntity saved = loanRepository.save(loan);

        eventPublisher.publish(LoanEvent.of(
                LoanEventType.LOAN_DISBURSED,
                saved.getId(), saved.getCustomerId(), saved.getProductName(),
                saved.getPrincipalAmount(), saved.getOutstandingBalance(),
                null, saved.getDueDate(), null));

        return LoanResponse.fromEntity(saved);
    }
}
