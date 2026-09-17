package com.tezzar.mkopo.light.loan.loanmanagement.request;

import jakarta.validation.constraints.NotBlank;

public record CancellationRequest(
        @NotBlank(message = "Reason cannot be blank")
        String reason,

        @NotBlank(message = "Cancelled by cannot be blank")
        String cancelledBy
) {
}
