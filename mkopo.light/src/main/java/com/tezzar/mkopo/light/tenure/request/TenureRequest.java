package com.tezzar.mkopo.light.tenure.request;

import com.tezzar.mkopo.light.tenure.enums.RepaymentStructure;
import com.tezzar.mkopo.light.tenure.enums.TenureType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record TenureRequest(
        @NotNull(message = "Tenure value cannot be null")
        @Min(value = 1, message = "Tenure value must be at least 1")
        Integer tenureValue,

        @NotNull(message = "Tenure type cannot be null")
        TenureType tenureType,

        @NotNull(message = "Repayment structure cannot be null")
        RepaymentStructure repaymentStructure,

        Integer installmentCount,

        Boolean capitalized,

        @NotNull(message = "Minimum product amount cannot be null")
        BigDecimal minimumProductAmount,

        @NotNull(message = "Maximum product amount cannot be null")
        BigDecimal maximumProductAmount,

        List<String> feeIds
) {
}
