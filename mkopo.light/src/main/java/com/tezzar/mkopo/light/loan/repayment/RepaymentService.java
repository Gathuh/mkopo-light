package com.tezzar.mkopo.light.loan.repayment;

import com.tezzar.mkopo.light.loan.loanmanagement.request.RepaymentRequest;
import com.tezzar.mkopo.light.loan.loanmanagement.response.LoanResponse;

public interface RepaymentService {
    LoanResponse processRepayment(String loanId, RepaymentRequest request);
}
