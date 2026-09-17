package com.tezzar.mkopo.light.customer;

import com.tezzar.mkopo.light.exception.BusinessValidationException;
import com.tezzar.mkopo.light.exception.DuplicateResourceException;
import com.tezzar.mkopo.light.exception.ResourceNotFoundException;
import com.tezzar.mkopo.light.customer.request.CreateLoanLimitRequest;
import com.tezzar.mkopo.light.customer.request.UpdateLoanLimitRequest;
import com.tezzar.mkopo.light.customer.response.LoanLimitResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanLimitServiceImpl implements LoanLimitService {

    private final LoanLimitRepository loanLimitRepository;

    @Override
    @Transactional
    public LoanLimitResponse createLimit(CreateLoanLimitRequest request) {
        if (loanLimitRepository.existsByCustomerId(request.customerId())) {
            throw new DuplicateResourceException("Loan limit", "customerId", request.customerId());
        }
        LoanLimit limit = LoanLimit.builder()
                .customerId(request.customerId())
                .maxAmount(request.maxAmount())
                .notes(request.notes())
                .build();
        return LoanLimitResponse.fromEntity(loanLimitRepository.save(limit));
    }

    @Override
    public LoanLimitResponse findByCustomerId(String customerId) {
        return LoanLimitResponse.fromEntity(getLimitOrThrow(customerId));
    }

    @Override
    public List<LoanLimitResponse> findAll() {
        return loanLimitRepository.findAll()
                .stream()
                .map(LoanLimitResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public LoanLimitResponse updateLimit(String customerId, UpdateLoanLimitRequest request) {
        LoanLimit limit = getLimitOrThrow(customerId);
        limit.setMaxAmount(request.maxAmount());
        limit.setNotes(request.notes());
        return LoanLimitResponse.fromEntity(loanLimitRepository.save(limit));
    }

    @Override
    @Transactional
    public void increaseExposure(String customerId, BigDecimal amount) {
        LoanLimit limit = getLimitOrThrow(customerId);
        limit.setCurrentExposure(limit.getCurrentExposure().add(amount));
        loanLimitRepository.save(limit);
    }

    @Override
    @Transactional
    public void decreaseExposure(String customerId, BigDecimal amount) {
        LoanLimit limit = getLimitOrThrow(customerId);
        BigDecimal newExposure = limit.getCurrentExposure().subtract(amount);
        limit.setCurrentExposure(newExposure.compareTo(BigDecimal.ZERO) < 0
                ? BigDecimal.ZERO : newExposure);
        loanLimitRepository.save(limit);
    }

    @Override
    public void validateSufficientLimit(String customerId, BigDecimal requestedAmount) {
        LoanLimit limit = loanLimitRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException(
                        "No loan limit configured for customer: " + customerId
                                + ". Please contact your loan officer."));

        loanLimitRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan limit", customerId));

        if (requestedAmount.compareTo(limit.getAvailableLimit()) > 0) {
            throw new BusinessValidationException(
                    "Requested amount " + requestedAmount
                            + " exceeds available limit " + limit.getAvailableLimit());
        }
    }

    private LoanLimit getLimitOrThrow(String customerId) {
        return loanLimitRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan limit", customerId));
    }
}
