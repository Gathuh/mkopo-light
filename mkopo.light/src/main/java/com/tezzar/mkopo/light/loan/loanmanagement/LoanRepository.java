package com.tezzar.mkopo.light.loan.loanmanagement;

import com.tezzar.mkopo.light.loan.enums.LoanState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<LoanEntity, String> {
    List<LoanEntity> findByCustomerId(String customerId);
    List<LoanEntity> findByLoanState(LoanState loanState);
    List<LoanEntity> findByLoanStateAndDueDateBefore(LoanState loanState, LocalDate date);
    List<LoanEntity> findByLoanStateIn(List<LoanState> states);
    List<LoanEntity> findByLoanStateAndDueDateBetween(LoanState state, LocalDate from, LocalDate to);
}
