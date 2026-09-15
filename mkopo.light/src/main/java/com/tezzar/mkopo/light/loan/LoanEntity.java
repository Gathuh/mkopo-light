package com.tezzar.mkopo.light.loan;

import com.tezzar.mkopo.light.loan.enums.BillingType;
import com.tezzar.mkopo.light.loan.enums.LoanState;
import com.tezzar.mkopo.light.loan.enums.LoanType;
import com.tezzar.mkopo.light.loan.feeapplication.LoanFeeApplication;
import com.tezzar.mkopo.light.loan.installment.LoanInstallment;
import com.tezzar.mkopo.light.loan.transaction.LoanTransaction;
import com.tezzar.mkopo.light.tenure.enums.TenureType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loans")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String customerId;

    private String approvedByUserId;

    @Column(nullable = false)
    private String productId;

    private String productName;

    @Column(nullable = false)
    private String tenureOptionId;

    private Integer tenureValue;

    @Enumerated(EnumType.STRING)
    private TenureType tenureType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanType loanType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillingType billingType;

    private Integer consolidatedDueDay;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal principalAmount;

    @Column(precision = 19, scale = 4)
    private BigDecimal disbursedAmount;

    @Column(precision = 19, scale = 4)
    private BigDecimal outstandingBalance;

    @Column(precision = 19, scale = 4)
    private BigDecimal accruedInterest;

    @Column(precision = 19, scale = 4)
    private BigDecimal totalRepaid;

    @Column(precision = 19, scale = 4)
    private BigDecimal snapshotServiceFeeRate;

    @Enumerated(EnumType.STRING)
    private com.tezzar.mkopo.light.fees.enums.FeeTiming snapshotServiceFeeTiming;

    @Column(precision = 19, scale = 4)
    private BigDecimal snapshotLateFeeAmount;

    private Integer snapshotTriggerDays;

    @Column(precision = 19, scale = 4)
    private BigDecimal snapshotDailyFeeRate;

    @Column(precision = 19, scale = 4)
    private BigDecimal snapshotProductFeeAmount;

    private Boolean snapshotCapitalized;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanState loanState;

    private LocalDate applicationDate;
    private LocalDate approvalDate;
    private LocalDate disbursementDate;
    private LocalDate dueDate;
    private LocalDate maturityDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoanInstallment> installments = new ArrayList<>();

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoanTransaction> transactions = new ArrayList<>();

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoanFeeApplication> feeApplications = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.loanState == null) {
            this.loanState = LoanState.PENDING;
        }
        if (this.totalRepaid == null) {
            this.totalRepaid = BigDecimal.ZERO;
        }
        if (this.accruedInterest == null) {
            this.accruedInterest = BigDecimal.ZERO;
        }
        if (this.billingType == null) {
            this.billingType = BillingType.INDIVIDUAL;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void transitionTo(LoanState next) {
        if (!this.loanState.canTransitionTo(next)) {
            throw new RuntimeException(
                    "Cannot transition loan from " + this.loanState + " to " + next);
        }
        this.loanState = next;
    }
}
