package com.tezzar.mkopo.light.customer.response;

import com.tezzar.mkopo.light.customer.LoanLimit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LoanLimitResponse(
        String id,
        String customerId,
        BigDecimal maxAmount,
        BigDecimal currentExposure,
        BigDecimal availableLimit,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
        public static LoanLimitResponse fromEntity(LoanLimit limit) {
                return new LoanLimitResponse(
                        limit.getId(),
                        limit.getCustomerId(),
                        limit.getMaxAmount(),
                        limit.getCurrentExposure(),
                        limit.getAvailableLimit(),
                        limit.getNotes(),
                        limit.getCreatedAt(),
                        limit.getUpdatedAt()
                );
        }
}
