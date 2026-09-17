package com.tezzar.mkopo.light.loan.transaction;

import com.tezzar.mkopo.light.loan.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanTransactionRepository extends JpaRepository<LoanTransaction, String> {
    List<LoanTransaction> findByLoanIdOrderByTransactionDateAsc(String loanId);
    List<LoanTransaction> findByLoanIdAndTransactionType(String loanId, TransactionType type);
}
