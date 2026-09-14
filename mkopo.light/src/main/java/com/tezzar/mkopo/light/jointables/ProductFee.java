package com.tezzar.mkopo.light.jointables;

import com.tezzar.mkopo.light.fees.FeeEntity;
import com.tezzar.mkopo.light.product.ProductEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_fees")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductFee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @ManyToOne
    @JoinColumn(name = "fee_id")
    private FeeEntity fee;

    private BigDecimal overrideAmount;

    private BigDecimal overrideRate;

    private Boolean overrideCapitalized;

    private LocalDateTime attachedAt;

    @PrePersist
    protected void onCreate() {
        this.attachedAt = LocalDateTime.now();
    }
}
