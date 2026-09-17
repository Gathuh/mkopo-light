package com.tezzar.mkopo.light.loan.repayment;

import java.math.BigDecimal;

public record RepaymentAllocation(
        BigDecimal toLateFees,
        BigDecimal toInterest,
        BigDecimal toPrincipal,
        BigDecimal excess
) {
}
