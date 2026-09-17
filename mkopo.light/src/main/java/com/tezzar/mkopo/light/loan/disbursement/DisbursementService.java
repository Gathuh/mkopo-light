package com.tezzar.mkopo.light.loan.disbursement;

import com.tezzar.mkopo.light.loan.loanmanagement.response.LoanResponse;

public interface DisbursementService {
    LoanResponse disburse(String loanId, String disbursedBy);
}
