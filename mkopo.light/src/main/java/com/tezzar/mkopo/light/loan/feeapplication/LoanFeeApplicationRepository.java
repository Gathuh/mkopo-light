package com.tezzar.mkopo.light.loan.feeapplication;

import com.tezzar.mkopo.light.fees.enums.FeeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface LoanFeeApplicationRepository extends JpaRepository<LoanFeeApplication, String> {
    boolean existsByLoanIdAndFeeTypeAndAppliedDate(String loanId, FeeType feeType, LocalDate appliedDate);
}
