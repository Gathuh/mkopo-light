package com.tezzar.mkopo.light.loan.feeengine;

import com.tezzar.mkopo.light.loan.loanmanagement.LoanEntity;
import com.tezzar.mkopo.light.tenure.TenureEntity;

import java.math.BigDecimal;

public interface FeeEngineService {
    void snapshotFees(LoanEntity loan, TenureEntity tenure);
    BigDecimal calculateServiceFee(BigDecimal principal, LoanEntity loan);
    BigDecimal calculateDisbursedAmount(BigDecimal principal, LoanEntity loan);
    BigDecimal calculateLateFee(LoanEntity loan);
    BigDecimal calculateDailyAccrual(LoanEntity loan);
}
