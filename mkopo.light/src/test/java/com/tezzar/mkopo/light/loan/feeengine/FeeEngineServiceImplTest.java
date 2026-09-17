package com.tezzar.mkopo.light.loan.feeengine;

import com.tezzar.mkopo.light.fees.enums.FeeTiming;
import com.tezzar.mkopo.light.loan.loanmanagement.LoanEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FeeEngineServiceImplTest {

    private final FeeEngineServiceImpl feeEngineService = new FeeEngineServiceImpl();

    @Test
    void calculateServiceFeeUsesPercentageWhenRateIsBelowOne() {
        LoanEntity loan = LoanEntity.builder()
                .snapshotServiceFeeRate(new BigDecimal("0.0500"))
                .snapshotServiceFeeTiming(FeeTiming.UPFRONT)
                .build();

        BigDecimal fee = feeEngineService.calculateServiceFee(new BigDecimal("1000.00"), loan);

        assertEquals(new BigDecimal("50.0000"), fee);
    }

    @Test
    void calculateDisbursedAmountNeverGoesBelowZeroForUpfrontFee() {
        LoanEntity loan = LoanEntity.builder()
                .snapshotServiceFeeRate(new BigDecimal("200.00"))
                .snapshotServiceFeeTiming(FeeTiming.UPFRONT)
                .build();

        BigDecimal disbursed = feeEngineService.calculateDisbursedAmount(new BigDecimal("100.00"), loan);

        assertEquals(new BigDecimal("0"), disbursed.stripTrailingZeros());
    }

    @Test
    void calculateDailyAccrualReturnsZeroWhenBalanceIsZero() {
        LoanEntity loan = LoanEntity.builder()
                .snapshotDailyFeeRate(new BigDecimal("0.0100"))
                .outstandingBalance(BigDecimal.ZERO)
                .build();

        BigDecimal accrual = feeEngineService.calculateDailyAccrual(loan);

        assertEquals(BigDecimal.ZERO, accrual);
    }
}
