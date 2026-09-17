package com.tezzar.mkopo.light.loan.calculator;

import com.tezzar.mkopo.light.tenure.enums.TenureType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface LoanCalculatorService {
    LocalDate calculateDueDate(LocalDate disbursementDate, Integer tenureValue, TenureType tenureType);
    List<InstallmentSchedule> generateInstallmentSchedule(BigDecimal principal, Integer installmentCount, BigDecimal annualInterestRate, LocalDate disbursementDate, TenureType tenureType);
    BigDecimal calculateOutstandingBalance(BigDecimal principal, BigDecimal accruedInterest, BigDecimal capitalizedFees, BigDecimal totalRepaid);
    long calculateDaysOverdue(LocalDate dueDate);
}
