package com.tezzar.mkopo.light.loan.installment;

import com.tezzar.mkopo.light.loan.enums.InstallmentState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, String> {
    List<LoanInstallment> findByLoanIdOrderByInstallmentNumberAsc(String loanId);
    List<LoanInstallment> findByLoanIdAndState(String loanId, InstallmentState state);
    List<LoanInstallment> findByStateAndDueDateBefore(InstallmentState state, LocalDate date);
}
