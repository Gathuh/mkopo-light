package com.tezzar.mkopo.light.loan.loanmanagement;

import com.tezzar.mkopo.light.loan.loanmanagement.request.ApprovalRequest;
import com.tezzar.mkopo.light.loan.loanmanagement.request.CancellationRequest;
import com.tezzar.mkopo.light.loan.loanmanagement.request.LoanApplicationRequest;
import com.tezzar.mkopo.light.loan.loanmanagement.response.LoanResponse;
import com.tezzar.mkopo.light.loan.loanmanagement.response.TransactionResponse;
import com.tezzar.mkopo.light.loan.loanmanagement.response.InstallmentResponse;

import java.util.List;

public interface LoanService {
    LoanResponse applyForLoan(LoanApplicationRequest request);
    LoanResponse approveLoan(String loanId, ApprovalRequest request);
    LoanResponse cancelLoan(String loanId, CancellationRequest request);
    LoanResponse findById(String loanId);
    List<LoanResponse> findAll();
    List<LoanResponse> findByCustomerId(String customerId);
    List<TransactionResponse> getTransactions(String loanId);
    List<InstallmentResponse> getInstallments(String loanId);
}
