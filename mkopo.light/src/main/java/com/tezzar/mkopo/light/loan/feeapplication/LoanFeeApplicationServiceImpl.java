package com.tezzar.mkopo.light.loan.feeapplication;

import com.tezzar.mkopo.light.fees.enums.FeeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class LoanFeeApplicationServiceImpl implements LoanFeeApplicationService {

    private final LoanFeeApplicationRepository feeApplicationRepository;

    @Override
    public boolean existsForToday(String loanId, FeeType feeType, LocalDate date) {
        return feeApplicationRepository.existsByLoanIdAndFeeTypeAndAppliedDate(loanId, feeType, date);
    }

    @Override
    public LoanFeeApplication save(LoanFeeApplication feeApplication) {
        return feeApplicationRepository.save(feeApplication);
    }
}
