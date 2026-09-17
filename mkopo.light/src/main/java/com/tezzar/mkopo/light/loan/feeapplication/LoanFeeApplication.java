package com.tezzar.mkopo.light.loan.feeapplication;

import com.tezzar.mkopo.light.fees.enums.FeeType;
import com.tezzar.mkopo.light.loan.loanmanagement.LoanEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loan_fee_applications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanFeeApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private LoanEntity loan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeeType feeType;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate appliedDate;

    @Column(nullable = false)
    private String appliedBy;

    @PrePersist
    protected void onCreate() {
        if (this.appliedDate == null) {
            this.appliedDate = LocalDate.now();
        }
        if (this.appliedBy == null) {
            this.appliedBy = "SYSTEM";
        }
    }
}
