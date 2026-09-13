package com.tezzar.mkopo.light.fees;

import com.tezzar.mkopo.light.fees.enums.CalculationType;
import com.tezzar.mkopo.light.fees.enums.FeeType;
import com.tezzar.mkopo.light.fees.enums.FeeTiming;
import com.tezzar.mkopo.light.tenure.TenureEntity;
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

//    @ManyToOne
//    private TenureEntity tenureEntity;

    private Integer triggerDays;

}
