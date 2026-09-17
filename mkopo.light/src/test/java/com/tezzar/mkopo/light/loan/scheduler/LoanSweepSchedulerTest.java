package com.tezzar.mkopo.light.loan.scheduler;

import com.tezzar.mkopo.light.fees.enums.FeeType;
import com.tezzar.mkopo.light.loan.calculator.LoanCalculatorService;
import com.tezzar.mkopo.light.loan.enums.LoanState;
import com.tezzar.mkopo.light.loan.events.LoanEvent;
import com.tezzar.mkopo.light.loan.events.LoanEventPublisher;
import com.tezzar.mkopo.light.loan.events.LoanEventType;
import com.tezzar.mkopo.light.loan.feeapplication.LoanFeeApplicationService;
import com.tezzar.mkopo.light.loan.feeengine.FeeEngineService;
import com.tezzar.mkopo.light.loan.installment.LoanInstallmentService;
import com.tezzar.mkopo.light.loan.loanmanagement.LoanEntity;
import com.tezzar.mkopo.light.loan.loanmanagement.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanSweepSchedulerTest {

    @Mock
    private LoanRepository loanRepository;
    @Mock
    private LoanFeeApplicationService feeApplicationService;
    @Mock
    private LoanInstallmentService installmentService;
    @Mock
    private FeeEngineService feeEngineService;
    @Mock
    private LoanCalculatorService loanCalculatorService;
    @Mock
    private LoanEventPublisher eventPublisher;

    private LoanSweepScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new LoanSweepScheduler(
                loanRepository,
                feeApplicationService,
                installmentService,
                feeEngineService,
                loanCalculatorService,
                eventPublisher
        );
    }

    @Test
    void detectOverdueLoansMarksLoanOverdueAndPublishesEvent() {
        LoanEntity loan = baseLoan();
        loan.setLoanState(LoanState.ACTIVE);
        loan.setDueDate(LocalDate.now().minusDays(1));
        loan.setOutstandingBalance(new BigDecimal("150.00"));

        when(loanRepository.findByLoanStateAndDueDateBefore(eq(LoanState.ACTIVE), any(LocalDate.class)))
                .thenReturn(List.of(loan));

        scheduler.detectOverdueLoans();

        assertEquals(LoanState.OVERDUE, loan.getLoanState());
        verify(loanRepository).save(loan);
        verify(eventPublisher).publish(any(LoanEvent.class));
    }

    @Test
    void applyLateFeesAddsFeeTransactionAndFeeApplication() {
        LoanEntity loan = baseLoan();
        loan.setLoanState(LoanState.OVERDUE);
        loan.setDueDate(LocalDate.now().minusDays(5));
        loan.setOutstandingBalance(new BigDecimal("100.00"));
        loan.setSnapshotTriggerDays(3);
        loan.setTransactions(new ArrayList<>());
        loan.setFeeApplications(new ArrayList<>());

        when(loanRepository.findByLoanState(LoanState.OVERDUE)).thenReturn(List.of(loan));
        when(loanCalculatorService.calculateDaysOverdue(loan.getDueDate())).thenReturn(5L);
        when(feeApplicationService.existsForToday(loan.getId(), FeeType.LATE_FEE, LocalDate.now())).thenReturn(false);
        when(feeEngineService.calculateLateFee(loan)).thenReturn(new BigDecimal("10.00"));

        scheduler.applyLateFees();

        assertEquals(new BigDecimal("110.00"), loan.getOutstandingBalance());
        assertEquals(1, loan.getTransactions().size());
        assertEquals(1, loan.getFeeApplications().size());
        verify(loanRepository).save(loan);
    }

    @Test
    void sendDueRemindersPublishesReminderEvent() {
        LocalDate reminderDate = LocalDate.now().plusDays(3);
        LoanEntity loan = baseLoan();
        loan.setLoanState(LoanState.ACTIVE);
        loan.setDueDate(reminderDate);

        when(loanRepository.findByLoanStateAndDueDateBetween(LoanState.ACTIVE, reminderDate, reminderDate))
                .thenReturn(List.of(loan));

        scheduler.sendDueReminders();

        verify(eventPublisher).publish(any(LoanEvent.class));
    }

    @Test
    void checkWriteOffsMovesLoanToWrittenOffAfterThreshold() {
        LoanEntity loan = baseLoan();
        loan.setLoanState(LoanState.OVERDUE);
        loan.setDueDate(LocalDate.now().minusDays(91));

        when(loanRepository.findByLoanState(LoanState.OVERDUE)).thenReturn(List.of(loan));
        when(loanCalculatorService.calculateDaysOverdue(loan.getDueDate())).thenReturn(91L);

        scheduler.checkWriteOffs();

        assertEquals(LoanState.WRITTEN_OFF, loan.getLoanState());
        verify(loanRepository).save(loan);
        verify(eventPublisher).publish(any(LoanEvent.class));
    }

    private LoanEntity baseLoan() {
        return LoanEntity.builder()
                .id("loan-1")
                .customerId("customer-1")
                .productName("Starter Loan")
                .principalAmount(new BigDecimal("1000.00"))
                .loanState(LoanState.ACTIVE)
                .outstandingBalance(new BigDecimal("1000.00"))
                .transactions(new ArrayList<>())
                .feeApplications(new ArrayList<>())
                .build();
    }
}
