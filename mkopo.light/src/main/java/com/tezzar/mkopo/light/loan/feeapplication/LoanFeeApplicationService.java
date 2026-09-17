package com.tezzar.mkopo.light.loan.feeapplication;

import com.tezzar.mkopo.light.fees.enums.FeeType;

import java.time.LocalDate;

public interface LoanFeeApplicationService {
    boolean existsForToday(String loanId, FeeType feeType, LocalDate date);
    LoanFeeApplication save(LoanFeeApplication feeApplication);
}
