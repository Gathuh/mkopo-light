package com.tezzar.mkopo.light.loan.transaction;

import com.tezzar.mkopo.light.loan.loanmanagement.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanTransactionServiceImpl implements LoanTransactionService {

    private final LoanTransactionRepository transactionRepository;

    @Override
    public List<TransactionResponse> findByLoanId(String loanId) {
        return transactionRepository.findByLoanIdOrderByTransactionDateAsc(loanId)
                .stream()
                .map(TransactionResponse::fromEntity)
                .toList();
    }

    @Override
    public LoanTransaction save(LoanTransaction transaction) {
        return transactionRepository.save(transaction);
    }
}
