package com.tezzar.mkopo.light.tenure.response;

import com.tezzar.mkopo.light.fees.response.FeeResponse;
import com.tezzar.mkopo.light.tenure.TenureEntity;
import com.tezzar.mkopo.light.tenure.enums.RepaymentStructure;
import com.tezzar.mkopo.light.tenure.enums.TenureStatus;
import com.tezzar.mkopo.light.tenure.enums.TenureType;

import java.math.BigDecimal;
import java.util.List;

public record TenureResponse(
        String id,
        Integer tenureValue,
        TenureType tenureType,
        RepaymentStructure repaymentStructure,
        Integer installmentCount,
        Boolean capitalized,
        BigDecimal minimumProductAmount,
        BigDecimal maximumProductAmount,
        TenureStatus status,
        List<FeeResponse> fees
) {
        public static TenureResponse fromEntity(TenureEntity tenure) {
                List<FeeResponse> feeResponses = tenure.getTenureFees()
                        .stream()
                        .map(tf -> FeeResponse.fromEntity(tf.getFee()))
                        .toList();

                return new TenureResponse(
                        tenure.getId(),
                        tenure.getTenureValue(),
                        tenure.getTenureType(),
                        tenure.getRepaymentStructure(),
                        tenure.getInstallmentCount(),
                        tenure.getCapitalized(),
                        tenure.getMinimumProductAmount(),
                        tenure.getMaximumProductAmount(),
                        tenure.getStatus(),
                        feeResponses
                );
        }
}
