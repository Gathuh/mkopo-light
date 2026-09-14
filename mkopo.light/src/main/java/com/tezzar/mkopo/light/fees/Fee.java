package com.tezzar.mkopo.light.fees;

import com.tezzar.mkopo.light.fees.enums.CalculationType;
import com.tezzar.mkopo.light.fees.enums.FeeTiming;
import com.tezzar.mkopo.light.fees.enums.FeeType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@Table(name = "fees")
@NoArgsConstructor
@AllArgsConstructor
public class Fee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    private FeeType feeType;

    @Enumerated(EnumType.STRING)
    private CalculationType calculationType;

    private BigDecimal amount;

    private BigDecimal rate;

    @Enumerated(EnumType.STRING)
    private FeeTiming timing;

    private Integer triggerDays;

    @OneToMany(mappedBy = "fee", cascade = CascadeType.ALL)
    private List<FeeTiered> tiers = new ArrayList<>();

    @OneToMany(mappedBy = "fee", cascade = CascadeType.ALL)
    private List<ProductFee> productFees = new ArrayList<>();

    @OneToMany(mappedBy = "fee", cascade = CascadeType.ALL)
    private List<TenureFee> tenureFees = new ArrayList<>();
}
