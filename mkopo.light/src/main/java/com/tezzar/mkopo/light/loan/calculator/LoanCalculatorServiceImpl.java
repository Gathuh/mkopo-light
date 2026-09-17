package com.tezzar.mkopo.light.loan.calculator;

import com.tezzar.mkopo.light.tenure.enums.TenureType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanCalculatorServiceImpl implements LoanCalculatorService {

    private static final int SCALE = 4;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    @Override
    public LocalDate calculateDueDate(LocalDate disbursementDate, Integer tenureValue, TenureType tenureType) {
        return switch (tenureType) {
            case DAYS   -> disbursementDate.plusDays(tenureValue);
            case WEEKS  -> disbursementDate.plusWeeks(tenureValue);
            case MONTHS -> disbursementDate.plusMonths(tenureValue);
            case YEARS  -> disbursementDate.plusYears(tenureValue);
            default     -> disbursementDate.plusDays(tenureValue);
        };
    }

    @Override
    public List<InstallmentSchedule> generateInstallmentSchedule(
            BigDecimal principal,
            Integer installmentCount,
            BigDecimal annualInterestRate,
            LocalDate disbursementDate,
            TenureType tenureType) {

        List<InstallmentSchedule> schedule = new ArrayList<>();
        BigDecimal outstandingPrincipal = principal;
        BigDecimal principalPerInstallment = principal
                .divide(BigDecimal.valueOf(installmentCount), SCALE, ROUNDING);

        BigDecimal periodicRate = resolvePeriodicRate(annualInterestRate, tenureType);

        for (int i = 1; i <= installmentCount; i++) {
            BigDecimal interest = outstandingPrincipal.multiply(periodicRate).setScale(SCALE, ROUNDING);
            BigDecimal principalPortion = i == installmentCount
                    ? outstandingPrincipal
                    : principalPerInstallment;
            BigDecimal totalDue = principalPortion.add(interest).setScale(SCALE, ROUNDING);
            LocalDate dueDate = calculateInstallmentDueDate(disbursementDate, i, tenureType);

            schedule.add(new InstallmentSchedule(i, dueDate, principalPortion, interest, totalDue));
            outstandingPrincipal = outstandingPrincipal.subtract(principalPortion);
        }

        return schedule;
    }

    @Override
    public BigDecimal calculateOutstandingBalance(
            BigDecimal principal,
            BigDecimal accruedInterest,
            BigDecimal capitalizedFees,
            BigDecimal totalRepaid) {

        BigDecimal gross = principal
                .add(accruedInterest != null ? accruedInterest : BigDecimal.ZERO)
                .add(capitalizedFees != null ? capitalizedFees : BigDecimal.ZERO);
        BigDecimal repaid = totalRepaid != null ? totalRepaid : BigDecimal.ZERO;
        BigDecimal balance = gross.subtract(repaid).setScale(SCALE, ROUNDING);
        return balance.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : balance;
    }

    @Override
    public long calculateDaysOverdue(LocalDate dueDate) {
        long days = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        return Math.max(0, days);
    }

    private BigDecimal resolvePeriodicRate(BigDecimal annualRate, TenureType tenureType) {
        return switch (tenureType) {
            case MONTHS -> annualRate.divide(BigDecimal.valueOf(12), new MathContext(10, ROUNDING));
            case WEEKS  -> annualRate.divide(BigDecimal.valueOf(52), new MathContext(10, ROUNDING));
            case DAYS   -> annualRate.divide(BigDecimal.valueOf(365), new MathContext(10, ROUNDING));
            case YEARS  -> annualRate;
            default     -> annualRate.divide(BigDecimal.valueOf(365), new MathContext(10, ROUNDING));
        };
    }

    private LocalDate calculateInstallmentDueDate(LocalDate disbursementDate, int installmentNumber, TenureType tenureType) {
        return switch (tenureType) {
            case MONTHS -> disbursementDate.plusMonths(installmentNumber);
            case WEEKS  -> disbursementDate.plusWeeks(installmentNumber);
            case DAYS   -> disbursementDate.plusDays(installmentNumber);
            case YEARS  -> disbursementDate.plusYears(installmentNumber);
            default     -> disbursementDate.plusMonths(installmentNumber);
        };
    }
}
