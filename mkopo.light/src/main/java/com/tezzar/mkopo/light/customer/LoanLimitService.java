package com.tezzar.mkopo.light.customer;

import com.tezzar.mkopo.light.customer.request.CreateLoanLimitRequest;
import com.tezzar.mkopo.light.customer.request.UpdateLoanLimitRequest;
import com.tezzar.mkopo.light.customer.response.LoanLimitResponse;

import java.math.BigDecimal;
import java.util.List;

public interface LoanLimitService {
    LoanLimitResponse createLimit(CreateLoanLimitRequest request);
    LoanLimitResponse findByCustomerId(String customerId);
    LoanLimitResponse updateLimit(String customerId, UpdateLoanLimitRequest request);
    List<LoanLimitResponse> findAll();
    void increaseExposure(String customerId, BigDecimal amount);
    void decreaseExposure(String customerId, BigDecimal amount);
    void validateSufficientLimit(String customerId, BigDecimal requestedAmount);
}
