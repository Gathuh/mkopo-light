package com.tezzar.mkopo.light.loan.loanmanagement;

import com.tezzar.mkopo.light.client.UserClient;
import com.tezzar.mkopo.light.client.dto.UserResponse;
import com.tezzar.mkopo.light.client.dto.UserRole;
import com.tezzar.mkopo.light.client.dto.UserStatus;
import com.tezzar.mkopo.light.customer.LoanLimitService;
import com.tezzar.mkopo.light.exception.BusinessValidationException;
import com.tezzar.mkopo.light.exception.ResourceNotFoundException;
import com.tezzar.mkopo.light.exception.ServiceUnavailableException;
import com.tezzar.mkopo.light.exception.UnauthorizedOperationException;
import com.tezzar.mkopo.light.loan.enums.LoanState;
import com.tezzar.mkopo.light.loan.enums.LoanType;
import com.tezzar.mkopo.light.loan.events.LoanEvent;
import com.tezzar.mkopo.light.loan.events.LoanEventPublisher;
import com.tezzar.mkopo.light.loan.events.LoanEventType;
import com.tezzar.mkopo.light.loan.installment.LoanInstallmentService;
import com.tezzar.mkopo.light.loan.loanmanagement.request.ApprovalRequest;
import com.tezzar.mkopo.light.loan.loanmanagement.request.CancellationRequest;
import com.tezzar.mkopo.light.loan.loanmanagement.request.LoanApplicationRequest;
import com.tezzar.mkopo.light.loan.loanmanagement.response.InstallmentResponse;
import com.tezzar.mkopo.light.loan.loanmanagement.response.LoanResponse;
import com.tezzar.mkopo.light.loan.loanmanagement.response.TransactionResponse;
import com.tezzar.mkopo.light.loan.transaction.LoanTransactionService;
import com.tezzar.mkopo.light.product.ProductEntity;
import com.tezzar.mkopo.light.product.ProductService;
import com.tezzar.mkopo.light.product.enums.ProductStatus;
import com.tezzar.mkopo.light.tenure.TenureEntity;
import com.tezzar.mkopo.light.tenure.TenureService;
import com.tezzar.mkopo.light.tenure.enums.RepaymentStructure;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final ProductService productService;
    private final TenureService tenureService;
    private final LoanLimitService loanLimitService;
    private final UserClient userClient;
    private final LoanInstallmentService installmentService;
    private final LoanTransactionService transactionService;
    private final LoanEventPublisher eventPublisher;

    @Override
    @Transactional
    public LoanResponse applyForLoan(LoanApplicationRequest request) {
        UserResponse customer = validateUserExists(request.customerId());
        if (customer.role() != UserRole.CUSTOMER) {
            throw new UnauthorizedOperationException("User is not a customer");
        }
        if (customer.status() != UserStatus.ACTIVE) {
            throw new BusinessValidationException("Customer account is not active");
        }

        loanLimitService.validateSufficientLimit(request.customerId(), request.principalAmount());

        ProductEntity product = productService.findEntityById(request.productId());
        if (product.getProductStatus() != ProductStatus.ACTIVE) {
            throw new BusinessValidationException("Product is not active");
        }

        TenureEntity tenure = tenureService.findEntityById(request.tenureOptionId());

        boolean tenureBelongsToProduct = product.getTenureOptions()
                .stream()
                .anyMatch(pto -> pto.getTenure().getId().equals(tenure.getId()));
        if (!tenureBelongsToProduct) {
            throw new BusinessValidationException("Tenure does not belong to selected product");
        }

        if (request.principalAmount().compareTo(tenure.getMinimumProductAmount()) < 0
                || request.principalAmount().compareTo(tenure.getMaximumProductAmount()) > 0) {
            throw new BusinessValidationException("Amount outside allowed range for this tenure: "
                    + tenure.getMinimumProductAmount() + " - " + tenure.getMaximumProductAmount());
        }

        if (request.billingType() == com.tezzar.mkopo.light.loan.enums.BillingType.CONSOLIDATED
                && request.consolidatedDueDay() == null) {
            throw new BusinessValidationException("Consolidated due day is required for consolidated billing");
        }

        LoanType loanType = tenure.getRepaymentStructure() == RepaymentStructure.INSTALLMENT
                ? LoanType.INSTALLMENT
                : LoanType.BULLET;

        LoanEntity loan = LoanEntity.builder()
                .customerId(request.customerId())
                .productId(product.getId())
                .productName(product.getProductName())
                .tenureOptionId(tenure.getId())
                .tenureValue(tenure.getTenureValue())
                .tenureType(tenure.getTenureType())
                .loanType(loanType)
                .billingType(request.billingType())
                .consolidatedDueDay(request.consolidatedDueDay())
                .principalAmount(request.principalAmount())
                .applicationDate(LocalDate.now())
                .build();

        LoanEntity saved = loanRepository.save(loan);

        eventPublisher.publish(LoanEvent.of(
                LoanEventType.LOAN_CREATED,
                saved.getId(), saved.getCustomerId(), saved.getProductName(),
                saved.getPrincipalAmount(), null, null, null, null));

        return LoanResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public LoanResponse approveLoan(String loanId, ApprovalRequest request) {
        UserResponse approver = validateUserExists(request.approvedByUserId());
        if (approver.role() != UserRole.LOAN_OFFICER && approver.role() != UserRole.ADMIN) {
            throw new UnauthorizedOperationException("User is not authorized to approve loans. Required role: LOAN_OFFICER or ADMIN");
        }
        if (approver.status() != UserStatus.ACTIVE) {
            throw new BusinessValidationException("Approver account is not active");
        }

        LoanEntity loan = getLoanOrThrow(loanId);
        loan.transitionTo(LoanState.APPROVED);
        loan.setApprovedByUserId(request.approvedByUserId());
        loan.setApprovalDate(LocalDate.now());
        LoanEntity saved = loanRepository.save(loan);

        eventPublisher.publish(LoanEvent.of(
                LoanEventType.LOAN_APPROVED,
                saved.getId(), saved.getCustomerId(), saved.getProductName(),
                saved.getPrincipalAmount(), null, null, null, request.approvedByUserId()));

        return LoanResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public LoanResponse cancelLoan(String loanId, CancellationRequest request) {
        LoanEntity loan = getLoanOrThrow(loanId);
        loan.transitionTo(LoanState.CANCELLED);
        LoanEntity saved = loanRepository.save(loan);

        eventPublisher.publish(LoanEvent.of(
                LoanEventType.LOAN_CANCELLED,
                saved.getId(), saved.getCustomerId(), saved.getProductName(),
                saved.getPrincipalAmount(), null, null, null, null));

        return LoanResponse.fromEntity(saved);
    }

    @Override
    public LoanResponse findById(String loanId) {
        return LoanResponse.fromEntity(getLoanOrThrow(loanId));
    }

    @Override
    public List<LoanResponse> findAll() {
        return loanRepository.findAll()
                .stream()
                .map(LoanResponse::fromEntity)
                .toList();
    }

    @Override
    public List<LoanResponse> findByCustomerId(String customerId) {
        return loanRepository.findByCustomerId(customerId)
                .stream()
                .map(LoanResponse::fromEntity)
                .toList();
    }

    @Override
    public List<TransactionResponse> getTransactions(String loanId) {
        getLoanOrThrow(loanId);
        return transactionService.findByLoanId(loanId);
    }

    @Override
    public List<InstallmentResponse> getInstallments(String loanId) {
        getLoanOrThrow(loanId);
        return installmentService.findByLoanId(loanId);
    }

    public LoanEntity getLoanOrThrow(String loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", loanId));
    }

    private UserResponse validateUserExists(String userId) {
        var response = userClient.getUserById(userId);
        if (response.result() == null) {
            throw new ResourceNotFoundException("User", userId);
        }
        return response.result();
    }
}
