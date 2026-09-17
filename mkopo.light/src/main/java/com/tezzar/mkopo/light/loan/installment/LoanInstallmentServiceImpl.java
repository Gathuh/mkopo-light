package com.tezzar.mkopo.light.loan.installment;

import com.tezzar.mkopo.light.loan.enums.InstallmentState;
import com.tezzar.mkopo.light.loan.loanmanagement.response.InstallmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanInstallmentServiceImpl implements LoanInstallmentService {

    private final LoanInstallmentRepository installmentRepository;

    @Override
    public List<InstallmentResponse> findByLoanId(String loanId) {
        return installmentRepository.findByLoanIdOrderByInstallmentNumberAsc(loanId)
                .stream()
                .map(InstallmentResponse::fromEntity)
                .toList();
    }

    @Override
    public List<LoanInstallment> findByLoanIdAndState(String loanId, InstallmentState state) {
        return installmentRepository.findByLoanIdAndState(loanId, state);
    }

    @Override
    public List<LoanInstallment> findOverdueByStateBefore(InstallmentState state, LocalDate date) {
        return installmentRepository.findByStateAndDueDateBefore(state, date);
    }

    @Override
    public LoanInstallment save(LoanInstallment installment) {
        return installmentRepository.save(installment);
    }
}
