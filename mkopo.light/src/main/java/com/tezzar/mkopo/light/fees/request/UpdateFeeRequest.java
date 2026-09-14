package com.tezzar.mkopo.light.fees.request;

import com.tezzar.mkopo.light.fees.enums.CalculationType;
import com.tezzar.mkopo.light.fees.enums.FeeTiming;
import com.tezzar.mkopo.light.fees.enums.FeeType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateFeeRequest(
        @NotNull(message = "Fee type cannot be null")
        FeeType feeType,

        @NotNull(message = "Calculation type cannot be null")
        CalculationType calculationType,

        BigDecimal amount,

        BigDecimal rate,

        @NotNull(message = "Fee timing cannot be null")
        FeeTiming timing,

        Integer triggerDays
) {
}
