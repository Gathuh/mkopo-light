package com.tezzar.mkopo.light.loan.scheduler;

import com.tezzar.mkopo.light.fees.enums.FeeType;
import com.tezzar.mkopo.light.loan.calculator.LoanCalculatorService;
import com.tezzar.mkopo.light.loan.enums.InstallmentState;
import com.tezzar.mkopo.light.loan.enums.LoanState;
import com.tezzar.mkopo.light.loan.enums.TransactionType;
import com.tezzar.mkopo.light.loan.events.LoanEvent;
import com.tezzar.mkopo.light.loan.events.LoanEventPublisher;
import com.tezzar.mkopo.light.loan.events.LoanEventType;
import com.tezzar.mkopo.light.loan.feeapplication.LoanFeeApplication;
import com.tezzar.mkopo.light.loan.feeapplication.LoanFeeApplicationService;
import com.tezzar.mkopo.light.loan.feeengine.FeeEngineService;
import com.tezzar.mkopo.light.loan.installment.LoanInstallment;
import com.tezzar.mkopo.light.loan.installment.LoanInstallmentService;
import com.tezzar.mkopo.light.loan.loanmanagement.LoanEntity;
import com.tezzar.mkopo.light.loan.loanmanagement.LoanRepository;
import com.tezzar.mkopo.light.loan.transaction.LoanTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanSweepScheduler {

    private static final int WRITE_OFF_DAYS = 90;
    private static final int DUE_REMINDER_DAYS_BEFORE = 3;

    private final LoanRepository loanRepository;
    private final LoanFeeApplicationService feeApplicationService;
    private final LoanInstallmentService installmentService;
    private final FeeEngineService feeEngineService;
    private final LoanCalculatorService loanCalculatorService;
    private final LoanEventPublisher eventPublisher;

    @Scheduled(cron = "0 0 0 * * *")
    public void runDailySweep() {
        log.info("Daily loan sweep started: {}", LocalDate.now());
        detectOverdueLoans();
        applyLateFees();
        accrueInterest();
        checkWriteOffs();
        markOverdueInstallments();
        sendDueReminders();
        log.info("Daily loan sweep completed: {}", LocalDate.now());
    }

    @Transactional
    public void detectOverdueLoans() {
        List<LoanEntity> activeLoans = loanRepository
                .findByLoanStateAndDueDateBefore(LoanState.ACTIVE, LocalDate.now());

        for (LoanEntity loan : activeLoans) {
            if (loan.getOutstandingBalance() != null
                    && loan.getOutstandingBalance().compareTo(BigDecimal.ZERO) > 0) {
                loan.transitionTo(LoanState.OVERDUE);
                loanRepository.save(loan);
                eventPublisher.publish(LoanEvent.of(
                        LoanEventType.LOAN_OVERDUE,
                        loan.getId(), loan.getCustomerId(), loan.getProductName(),
                        loan.getPrincipalAmount(), loan.getOutstandingBalance(),
                        null, loan.getDueDate(), null));
                log.info("Loan {} marked OVERDUE", loan.getId());
            }
        }
    }

    @Transactional
    public void applyLateFees() {
        List<LoanEntity> overdueLoans = loanRepository.findByLoanState(LoanState.OVERDUE);

        for (LoanEntity loan : overdueLoans) {
            if (loan.getSnapshotTriggerDays() == null) continue;

            long daysOverdue = loanCalculatorService.calculateDaysOverdue(loan.getDueDate());
            if (daysOverdue < loan.getSnapshotTriggerDays()) continue;

            if (feeApplicationService.existsForToday(loan.getId(), FeeType.LATE_FEE, LocalDate.now())) continue;

            BigDecimal lateFee = feeEngineService.calculateLateFee(loan);
            if (lateFee.compareTo(BigDecimal.ZERO) == 0) continue;

            BigDecimal balanceBefore = loan.getOutstandingBalance();
            BigDecimal balanceAfter = balanceBefore.add(lateFee);
            loan.setOutstandingBalance(balanceAfter);

            loan.getTransactions().add(LoanTransaction.builder()
                    .loan(loan).transactionType(TransactionType.FEE_APPLIED)
                    .amount(lateFee).balanceBefore(balanceBefore).balanceAfter(balanceAfter)
                    .description("Late fee applied — " + daysOverdue + " days overdue")
                    .createdBy("SYSTEM").build());

            loan.getFeeApplications().add(LoanFeeApplication.builder()
                    .loan(loan).feeType(FeeType.LATE_FEE).amount(lateFee)
                    .appliedDate(LocalDate.now()).appliedBy("SYSTEM").build());

            loanRepository.save(loan);
            log.info("Late fee {} applied to loan {}", lateFee, loan.getId());
        }
    }

    @Transactional
    public void accrueInterest() {
        List<LoanEntity> activeLoans = loanRepository.findByLoanStateIn(
                List.of(LoanState.ACTIVE, LoanState.OVERDUE));

        for (LoanEntity loan : activeLoans) {
            if (loan.getSnapshotDailyFeeRate() == null) continue;
            if (feeApplicationService.existsForToday(loan.getId(), FeeType.DAILY_FEE, LocalDate.now())) continue;

            BigDecimal accrual = feeEngineService.calculateDailyAccrual(loan);
            if (accrual.compareTo(BigDecimal.ZERO) == 0) continue;

            BigDecimal balanceBefore = loan.getOutstandingBalance();
            BigDecimal balanceAfter = balanceBefore.add(accrual);
            loan.setAccruedInterest(loan.getAccruedInterest().add(accrual));
            loan.setOutstandingBalance(balanceAfter);

            loan.getTransactions().add(LoanTransaction.builder()
                    .loan(loan).transactionType(TransactionType.INTEREST_ACCRUAL)
                    .amount(accrual).balanceBefore(balanceBefore).balanceAfter(balanceAfter)
                    .description("Daily interest accrued").createdBy("SYSTEM").build());

            loan.getFeeApplications().add(LoanFeeApplication.builder()
                    .loan(loan).feeType(FeeType.DAILY_FEE).amount(accrual)
                    .appliedDate(LocalDate.now()).appliedBy("SYSTEM").build());

            loanRepository.save(loan);
        }
    }

    @Transactional
    public void checkWriteOffs() {
        List<LoanEntity> overdueLoans = loanRepository.findByLoanState(LoanState.OVERDUE);

        for (LoanEntity loan : overdueLoans) {
            if (loan.getDueDate() == null) continue;
            long daysOverdue = loanCalculatorService.calculateDaysOverdue(loan.getDueDate());
            if (daysOverdue >= WRITE_OFF_DAYS) {
                loan.transitionTo(LoanState.WRITTEN_OFF);
                loanRepository.save(loan);
                eventPublisher.publish(LoanEvent.of(
                        LoanEventType.LOAN_WRITTEN_OFF,
                        loan.getId(), loan.getCustomerId(), loan.getProductName(),
                        loan.getPrincipalAmount(), loan.getOutstandingBalance(),
                        null, loan.getDueDate(), null));
                log.info("Loan {} written off after {} days overdue", loan.getId(), daysOverdue);
            }
        }
    }

    @Transactional
    public void markOverdueInstallments() {
        for (LoanInstallment inst : installmentService.findOverdueByStateBefore(InstallmentState.PENDING, LocalDate.now())) {
            inst.setState(InstallmentState.OVERDUE);
            installmentService.save(inst);
        }
        for (LoanInstallment inst : installmentService.findOverdueByStateBefore(InstallmentState.PARTIAL, LocalDate.now())) {
            inst.setState(InstallmentState.OVERDUE);
            installmentService.save(inst);
        }
    }

    public void sendDueReminders() {
        LocalDate reminderDate = LocalDate.now().plusDays(DUE_REMINDER_DAYS_BEFORE);
        List<LoanEntity> upcomingLoans = loanRepository
                .findByLoanStateAndDueDateBetween(LoanState.ACTIVE, reminderDate, reminderDate);

        for (LoanEntity loan : upcomingLoans) {
            eventPublisher.publish(LoanEvent.of(
                    LoanEventType.DUE_REMINDER,
                    loan.getId(), loan.getCustomerId(), loan.getProductName(),
                    loan.getPrincipalAmount(), loan.getOutstandingBalance(),
                    null, loan.getDueDate(), null));
            log.info("Due reminder published for loan {} — due on {}", loan.getId(), loan.getDueDate());
        }
    }
}
