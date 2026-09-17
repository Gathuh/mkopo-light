package com.tezzar.mkopo.light.loan.feeengine;

import com.tezzar.mkopo.light.fees.enums.CalculationType;
import com.tezzar.mkopo.light.fees.enums.FeeTiming;
import com.tezzar.mkopo.light.jointables.TenureFee;
import com.tezzar.mkopo.light.loan.loanmanagement.LoanEntity;
import com.tezzar.mkopo.light.tenure.TenureEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class FeeEngineServiceImpl implements FeeEngineService {

    private static final int SCALE = 4;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    @Override
    public void snapshotFees(LoanEntity loan, TenureEntity tenure) {
        Boolean capitalized = tenure.getCapitalized() != null
                ? tenure.getCapitalized()
                : Boolean.FALSE;
        loan.setSnapshotCapitalized(capitalized);

        for (TenureFee tenureFee : tenure.getTenureFees()) {
            var fee = tenureFee.getFee();
            BigDecimal effectiveAmount = tenureFee.getOverrideAmount() != null
                    ? tenureFee.getOverrideAmount()
                    : fee.getAmount();
            BigDecimal effectiveRate = tenureFee.getOverrideRate() != null
                    ? tenureFee.getOverrideRate()
                    : fee.getRate();

            switch (fee.getFeeType()) {
                case SERVICE_FEE -> {
                    loan.setSnapshotServiceFeeRate(effectiveRate != null ? effectiveRate : effectiveAmount);
                    loan.setSnapshotServiceFeeTiming(fee.getTiming());
                }
                case DAILY_FEE -> loan.setSnapshotDailyFeeRate(effectiveRate);
                case LATE_FEE -> {
                    loan.setSnapshotLateFeeAmount(
                            fee.getCalculationType() == CalculationType.PERCENTAGE ? effectiveRate : effectiveAmount);
                    loan.setSnapshotTriggerDays(fee.getTriggerDays());
                }
                default -> {}
            }
        }
    }

    @Override
    public BigDecimal calculateServiceFee(BigDecimal principal, LoanEntity loan) {
        if (loan.getSnapshotServiceFeeRate() == null) return BigDecimal.ZERO;

        FeeTiming timing = loan.getSnapshotServiceFeeTiming();
        if (timing == FeeTiming.ON_REPAYMENT || timing == FeeTiming.ON_MATURITY) return BigDecimal.ZERO;

        BigDecimal rate = loan.getSnapshotServiceFeeRate();
        if (rate.compareTo(BigDecimal.ONE) < 0) {
            return principal.multiply(rate).setScale(SCALE, ROUNDING);
        }
        return rate.setScale(SCALE, ROUNDING);
    }

    @Override
    public BigDecimal calculateDisbursedAmount(BigDecimal principal, LoanEntity loan) {
        BigDecimal serviceFee = calculateServiceFee(principal, loan);
        FeeTiming timing = loan.getSnapshotServiceFeeTiming();

        if (timing == FeeTiming.UPFRONT) {
            BigDecimal disbursed = principal.subtract(serviceFee).setScale(SCALE, ROUNDING);
            return disbursed.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : disbursed;
        }
        return principal.setScale(SCALE, ROUNDING);
    }

    @Override
    public BigDecimal calculateLateFee(LoanEntity loan) {
        if (loan.getSnapshotLateFeeAmount() == null) return BigDecimal.ZERO;

        BigDecimal lateFeeConfig = loan.getSnapshotLateFeeAmount();
        if (lateFeeConfig.compareTo(BigDecimal.ONE) < 0) {
            return loan.getOutstandingBalance()
                    .multiply(lateFeeConfig)
                    .setScale(SCALE, ROUNDING);
        }
        return lateFeeConfig.setScale(SCALE, ROUNDING);
    }

    @Override
    public BigDecimal calculateDailyAccrual(LoanEntity loan) {
        if (loan.getSnapshotDailyFeeRate() == null) return BigDecimal.ZERO;
        if (loan.getOutstandingBalance() == null || loan.getOutstandingBalance().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return loan.getOutstandingBalance()
                .multiply(loan.getSnapshotDailyFeeRate())
                .setScale(SCALE, ROUNDING);
    }
}
