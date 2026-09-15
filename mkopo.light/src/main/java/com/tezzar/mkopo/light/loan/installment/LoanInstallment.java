package com.tezzar.mkopo.light.loan.installment;

import com.tezzar.mkopo.light.loan.LoanEntity;
import com.tezzar.mkopo.light.loan.enums.InstallmentState;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_installments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanInstallment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private LoanEntity loan;

    @Column(nullable = false)
    private Integer installmentNumber;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal principalAmount;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal interestAmount;

    @Column(precision = 19, scale = 4)
    private BigDecimal feeAmount;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal totalDue;

    @Column(precision = 19, scale = 4)
    private BigDecimal amountPaid;

    @Column(precision = 19, scale = 4)
    private BigDecimal outstandingAmount;

    @Enumerated(EnumType.STRING)
    private InstallmentState state;

    private LocalDate paidAt;

    @PrePersist
    protected void onCreate() {
        if (this.state == null) {
            this.state = InstallmentState.PENDING;
        }
        if (this.amountPaid == null) {
            this.amountPaid = BigDecimal.ZERO;
        }
        if (this.feeAmount == null) {
            this.feeAmount = BigDecimal.ZERO;
        }
        if (this.outstandingAmount == null) {
            this.outstandingAmount = this.totalDue;
        }
    }
}
