package com.tezzar.mkopo.light.loan.loanmanagement;

import com.tezzar.mkopo.light.controllerresponse.MessageAndResultResponse;
import com.tezzar.mkopo.light.loan.disbursement.DisbursementService;
import com.tezzar.mkopo.light.loan.repayment.RepaymentService;
import com.tezzar.mkopo.light.loan.loanmanagement.request.ApprovalRequest;
import com.tezzar.mkopo.light.loan.loanmanagement.request.CancellationRequest;
import com.tezzar.mkopo.light.loan.loanmanagement.request.LoanApplicationRequest;
import com.tezzar.mkopo.light.loan.loanmanagement.request.RepaymentRequest;
import com.tezzar.mkopo.light.loan.loanmanagement.response.InstallmentResponse;
import com.tezzar.mkopo.light.loan.loanmanagement.response.LoanResponse;
import com.tezzar.mkopo.light.loan.loanmanagement.response.TransactionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mkopo/loans")
@RequiredArgsConstructor
@Tag(name = "Loan Management", description = "Apply for loans, manage lifecycle, process repayments")
public class LoanController {

    private final LoanService loanService;
    private final DisbursementService disbursementService;
    private final RepaymentService repaymentService;

    @PostMapping
    @Operation(
            summary = "Apply for a loan",
            description = "Creates a loan application in PENDING state. Customer selects a product and a tenure belonging to that product"
    )
    public ResponseEntity<MessageAndResultResponse<LoanResponse>> applyForLoan(
            @Valid @RequestBody LoanApplicationRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(MessageAndResultResponse.success(
                        loanService.applyForLoan(request),
                        "Loan application submitted successfully"));
    }

    @GetMapping
    @Operation(
            summary = "Get all loans",
            description = "Returns all loans across all states"
    )
    public ResponseEntity<MessageAndResultResponse<List<LoanResponse>>> getAllLoans() {
        return ResponseEntity.ok(MessageAndResultResponse.success(
                loanService.findAll(),
                "Loans retrieved successfully"));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get loan by ID",
            description = "Returns a single loan with its installments and transaction history"
    )
    public ResponseEntity<MessageAndResultResponse<LoanResponse>> getLoan(
            @PathVariable String id) {
        return ResponseEntity.ok(MessageAndResultResponse.success(
                loanService.findById(id),
                "Loan retrieved successfully"));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(
            summary = "Get loans by customer",
            description = "Returns all loans belonging to a specific customer"
    )
    public ResponseEntity<MessageAndResultResponse<List<LoanResponse>>> getLoansByCustomer(
            @PathVariable String customerId) {
        return ResponseEntity.ok(MessageAndResultResponse.success(
                loanService.findByCustomerId(customerId),
                "Customer loans retrieved successfully"));
    }

    @PostMapping("/{id}/approve")
    @Operation(
            summary = "Approve a loan",
            description = "Transitions loan from PENDING to APPROVED. Requires a valid loan officer or admin user ID"
    )
    public ResponseEntity<MessageAndResultResponse<LoanResponse>> approveLoan(
            @PathVariable String id,
            @Valid @RequestBody ApprovalRequest request) {
        return ResponseEntity.ok(MessageAndResultResponse.success(
                loanService.approveLoan(id, request),
                "Loan approved successfully"));
    }

    @PostMapping("/{id}/disburse")
    @Operation(
            summary = "Disburse a loan",
            description = "Transitions loan from APPROVED to ACTIVE. Snapshots fees, calculates disbursed amount, generates installment schedule if applicable"
    )
    public ResponseEntity<MessageAndResultResponse<LoanResponse>> disburseLoan(
            @PathVariable String id,
            @RequestParam String disbursedBy) {
        return ResponseEntity.ok(MessageAndResultResponse.success(
                disbursementService.disburse(id, disbursedBy),
                "Loan disbursed successfully"));
    }

    @PostMapping("/{id}/repay")
    @Operation(
            summary = "Process a repayment",
            description = "Applies payment using waterfall allocation: late fees first, then interest, then principal"
    )
    public ResponseEntity<MessageAndResultResponse<LoanResponse>> repay(
            @PathVariable String id,
            @Valid @RequestBody RepaymentRequest request) {
        return ResponseEntity.ok(MessageAndResultResponse.success(
                repaymentService.processRepayment(id, request),
                "Repayment processed successfully"));
    }

    @PostMapping("/{id}/cancel")
    @Operation(
            summary = "Cancel a loan",
            description = "Cancels a loan in PENDING or APPROVED state"
    )
    public ResponseEntity<MessageAndResultResponse<LoanResponse>> cancelLoan(
            @PathVariable String id,
            @Valid @RequestBody CancellationRequest request) {
        return ResponseEntity.ok(MessageAndResultResponse.success(
                loanService.cancelLoan(id, request),
                "Loan cancelled successfully"));
    }

    @GetMapping("/{id}/transactions")
    @Operation(
            summary = "Get loan transaction history",
            description = "Returns the full audit trail of all money movements on this loan"
    )
    public ResponseEntity<MessageAndResultResponse<List<TransactionResponse>>> getTransactions(
            @PathVariable String id) {
        return ResponseEntity.ok(MessageAndResultResponse.success(
                loanService.getTransactions(id),
                "Transactions retrieved successfully"));
    }

    @GetMapping("/{id}/installments")
    @Operation(
            summary = "Get loan installment schedule",
            description = "Returns the installment schedule for an installment-type loan"
    )
    public ResponseEntity<MessageAndResultResponse<List<InstallmentResponse>>> getInstallments(
            @PathVariable String id) {
        return ResponseEntity.ok(MessageAndResultResponse.success(
                loanService.getInstallments(id),
                "Installments retrieved successfully"));
    }
}
