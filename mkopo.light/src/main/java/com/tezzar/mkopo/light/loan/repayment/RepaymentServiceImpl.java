package com.tezzar.mkopo.light.loan.repayment;

import com.tezzar.mkopo.light.customer.LoanLimitService;
import com.tezzar.mkopo.light.exception.BusinessValidationException;
import com.tezzar.mkopo.light.exception.InvalidLoanStateException;
import com.tezzar.mkopo.light.loan.enums.InstallmentState;
import com.tezzar.mkopo.light.loan.enums.LoanState;
import com.tezzar.mkopo.light.loan.enums.LoanType;
import com.tezzar.mkopo.light.loan.enums.TransactionType;
import com.tezzar.mkopo.light.loan.events.LoanEvent;
import com.tezzar.mkopo.light.loan.events.LoanEventPublisher;
import com.tezzar.mkopo.light.loan.events.LoanEventType;
import com.tezzar.mkopo.light.loan.installment.LoanInstallment;
import com.tezzar.mkopo.light.loan.installment.LoanInstallmentService;
import com.tezzar.mkopo.light.loan.loanmanagement.LoanEntity;
import com.tezzar.mkopo.light.loan.loanmanagement.LoanRepository;
import com.tezzar.mkopo.light.loan.loanmanagement.LoanServiceImpl;
import com.tezzar.mkopo.light.loan.loanmanagement.request.RepaymentRequest;
import com.tezzar.mkopo.light.loan.loanmanagement.response.LoanResponse;
import com.tezzar.mkopo.light.loan.transaction.LoanTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RepaymentServiceImpl implements RepaymentService {

    private static final int SCALE = 4;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    private final LoanRepository loanRepository;
    private final LoanServiceImpl loanService;
    private final LoanInstallmentService installmentService;
    private final LoanLimitService loanLimitService;
    private final LoanEventPublisher eventPublisher;

    @Override
    @Transactional
    public LoanResponse processRepayment(String loanId, RepaymentRequest request) {
        LoanEntity loan = loanService.getLoanOrThrow(loanId);

        if (loan.getLoanState() != LoanState.ACTIVE && loan.getLoanState() != LoanState.OVERDUE) {
            throw new InvalidLoanStateException("Loan is not in a repayable state: " + loan.getLoanState());
        }
        if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Payment amount must be greater than zero");
        }

        BigDecimal balanceBefore = loan.getOutstandingBalance();
        RepaymentAllocation allocation = allocate(request.amount(), loan);

        BigDecimal appliedToBalance = allocation.toLateFees()
                .add(allocation.toInterest())
                .add(allocation.toPrincipal())
                .setScale(SCALE, ROUNDING);

        BigDecimal newBalance = balanceBefore.subtract(appliedToBalance).setScale(SCALE, ROUNDING);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) newBalance = BigDecimal.ZERO;

        BigDecimal newAccruedInterest = loan.getAccruedInterest().subtract(allocation.toInterest()).setScale(SCALE, ROUNDING);
        if (newAccruedInterest.compareTo(BigDecimal.ZERO) < 0) newAccruedInterest = BigDecimal.ZERO;

        loan.setOutstandingBalance(newBalance);
        loan.setAccruedInterest(newAccruedInterest);
        loan.setTotalRepaid(loan.getTotalRepaid().add(appliedToBalance).setScale(SCALE, ROUNDING));

        StringBuilder description = new StringBuilder("Repayment received.");
        if (allocation.toLateFees().compareTo(BigDecimal.ZERO) > 0)
            description.append(" Late fees: ").append(allocation.toLateFees());
        if (allocation.toInterest().compareTo(BigDecimal.ZERO) > 0)
            description.append(" Interest: ").append(allocation.toInterest());
        if (allocation.toPrincipal().compareTo(BigDecimal.ZERO) > 0)
            description.append(" Principal: ").append(allocation.toPrincipal());
        if (request.reference() != null)
            description.append(" Ref: ").append(request.reference());

        loan.getTransactions().add(LoanTransaction.builder()
                .loan(loan)
                .transactionType(TransactionType.REPAYMENT)
                .amount(request.amount())
                .balanceBefore(balanceBefore)
                .balanceAfter(newBalance)
                .description(description.toString())
                .createdBy(request.paidBy())
                .build());

        if (loan.getLoanType() == LoanType.INSTALLMENT) {
            applyToInstallments(loan, appliedToBalance);
        }

        LoanEventType eventType = LoanEventType.REPAYMENT_RECEIVED;

        if (newBalance.compareTo(BigDecimal.ZERO) == 0) {
            loan.transitionTo(LoanState.CLOSED);
            loanLimitService.decreaseExposure(loan.getCustomerId(), loan.getPrincipalAmount());
            eventType = LoanEventType.LOAN_CLOSED;
        } else if (loan.getLoanState() == LoanState.OVERDUE) {
            loan.transitionTo(LoanState.ACTIVE);
        }

        LoanEntity saved = loanRepository.save(loan);

        eventPublisher.publish(LoanEvent.of(
                eventType,
                saved.getId(), saved.getCustomerId(), saved.getProductName(),
                saved.getPrincipalAmount(), saved.getOutstandingBalance(),
                request.amount(), saved.getDueDate(), null));

        return LoanResponse.fromEntity(saved);
    }

    private RepaymentAllocation allocate(BigDecimal payment, LoanEntity loan) {
        BigDecimal remaining = payment;

        BigDecimal totalLateFees = loan.getFeeApplications().stream()
                .map(fa -> fa.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .max(BigDecimal.ZERO);

        BigDecimal toLateFees = remaining.min(totalLateFees).setScale(SCALE, ROUNDING);
        remaining = remaining.subtract(toLateFees).setScale(SCALE, ROUNDING);

        BigDecimal accruedInterest = loan.getAccruedInterest() != null ? loan.getAccruedInterest() : BigDecimal.ZERO;
        BigDecimal toInterest = remaining.min(accruedInterest).setScale(SCALE, ROUNDING);
        remaining = remaining.subtract(toInterest).setScale(SCALE, ROUNDING);

        BigDecimal principalOutstanding = loan.getOutstandingBalance()
                .subtract(accruedInterest).subtract(totalLateFees)
                .max(BigDecimal.ZERO).setScale(SCALE, ROUNDING);
        BigDecimal toPrincipal = remaining.min(principalOutstanding).setScale(SCALE, ROUNDING);
        BigDecimal excess = remaining.subtract(toPrincipal).setScale(SCALE, ROUNDING);

        return new RepaymentAllocation(toLateFees, toInterest, toPrincipal, excess);
    }

    private void applyToInstallments(LoanEntity loan, BigDecimal totalApplied) {
        List<LoanInstallment> partialList = installmentService
                .findByLoanIdAndState(loan.getId(), InstallmentState.PARTIAL)
                .stream().sorted(Comparator.comparing(LoanInstallment::getInstallmentNumber)).toList();

        List<LoanInstallment> pending = installmentService
                .findByLoanIdAndState(loan.getId(), InstallmentState.PENDING)
                .stream().sorted(Comparator.comparing(LoanInstallment::getInstallmentNumber)).toList();

        BigDecimal remaining = totalApplied;

        for (LoanInstallment inst : partialList) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;
            remaining = applyToInstallment(inst, remaining);
        }
        for (LoanInstallment inst : pending) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;
            remaining = applyToInstallment(inst, remaining);
        }
    }

    private BigDecimal applyToInstallment(LoanInstallment installment, BigDecimal available) {
        BigDecimal outstanding = installment.getOutstandingAmount();
        BigDecimal toApply = available.min(outstanding).setScale(SCALE, ROUNDING);

        installment.setAmountPaid(installment.getAmountPaid().add(toApply).setScale(SCALE, ROUNDING));
        installment.setOutstandingAmount(outstanding.subtract(toApply).setScale(SCALE, ROUNDING));

        if (installment.getOutstandingAmount().compareTo(BigDecimal.ZERO) == 0) {
            installment.setState(InstallmentState.PAID);
            installment.setPaidAt(LocalDate.now());
        } else {
            installment.setState(InstallmentState.PARTIAL);
        }

        installmentService.save(installment);
        return available.subtract(toApply).setScale(SCALE, ROUNDING);
    }
}
