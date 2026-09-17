package com.tezzar.mkopo.light.loan.loanmanagement.request;

import jakarta.validation.constraints.NotBlank;

public record ApprovalRequest(
        @NotBlank(message = "Approver user ID cannot be blank")
        String approvedByUserId
) {
}
