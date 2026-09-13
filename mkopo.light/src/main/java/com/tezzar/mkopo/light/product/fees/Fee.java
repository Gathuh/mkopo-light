package com.tezzar.mkopo.light.product.fees;

import com.tezzar.mkopo.light.product.ProductEntity;
import com.tezzar.mkopo.light.product.fees.enums.CalculationType;
import com.tezzar.mkopo.light.product.fees.enums.FeeType;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product_fees")
public class Fee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    private FeeType feeType;

    @Enumerated(EnumType.STRING)
    private CalculationType calculationType;

    private BigDecimal minAmount;
    private BigDecimal maximumAmount;

    private BigDecimal rate;

    @Enumerated(EnumType.STRING)
    private FeeTiming timing;

    private Integer triggerDays;


    private Boolean capitalized;
}
