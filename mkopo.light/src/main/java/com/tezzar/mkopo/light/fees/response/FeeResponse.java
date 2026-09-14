package com.tezzar.mkopo.light.fees.response;

import com.tezzar.mkopo.light.fees.FeeEntity;
import com.tezzar.mkopo.light.fees.enums.CalculationType;
import com.tezzar.mkopo.light.fees.enums.FeeTiming;
import com.tezzar.mkopo.light.fees.enums.FeeStatus;
import com.tezzar.mkopo.light.fees.enums.FeeType;

import java.math.BigDecimal;

public record FeeResponse(
        String id,
        FeeType feeType,
        CalculationType calculationType,
        BigDecimal amount,
        BigDecimal rate,
        FeeTiming timing,
        Integer triggerDays,
        FeeStatus status
) {
        public static FeeResponse fromEntity(FeeEntity fee) {
                return new FeeResponse(
                        fee.getId(),
                        fee.getFeeType(),
                        fee.getCalculationType(),
                        fee.getAmount(),
                        fee.getRate(),
                        fee.getTiming(),
                        fee.getTriggerDays(),
                        fee.getStatus()
                );
        }
}
