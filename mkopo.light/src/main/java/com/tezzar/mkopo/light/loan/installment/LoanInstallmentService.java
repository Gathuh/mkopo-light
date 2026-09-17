package com.tezzar.mkopo.light.loan.installment;

import com.tezzar.mkopo.light.loan.enums.InstallmentState;
import com.tezzar.mkopo.light.loan.loanmanagement.response.InstallmentResponse;

import java.time.LocalDate;
import java.util.List;

public interface LoanInstallmentService {
    List<InstallmentResponse> findByLoanId(String loanId);
    List<LoanInstallment> findByLoanIdAndState(String loanId, InstallmentState state);
    List<LoanInstallment> findOverdueByStateBefore(InstallmentState state, LocalDate date);
    LoanInstallment save(LoanInstallment installment);
}
