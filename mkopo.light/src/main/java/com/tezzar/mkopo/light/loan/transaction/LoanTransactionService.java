package com.tezzar.mkopo.light.loan.transaction;

import com.tezzar.mkopo.light.loan.loanmanagement.response.TransactionResponse;

import java.util.List;

public interface LoanTransactionService {
    List<TransactionResponse> findByLoanId(String loanId);
    LoanTransaction save(LoanTransaction transaction);
}
