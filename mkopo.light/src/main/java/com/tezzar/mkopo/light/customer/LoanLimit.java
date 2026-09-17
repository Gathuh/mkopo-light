package com.tezzar.mkopo.light.customer;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_limits")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String customerId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal maxAmount;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal currentExposure;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal availableLimit;

    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.currentExposure == null) {
            this.currentExposure = BigDecimal.ZERO;
        }
        if (this.availableLimit == null) {
            this.availableLimit = this.maxAmount;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.availableLimit = this.maxAmount.subtract(this.currentExposure);
        if (this.availableLimit.compareTo(BigDecimal.ZERO) < 0) {
            this.availableLimit = BigDecimal.ZERO;
        }
    }
}
