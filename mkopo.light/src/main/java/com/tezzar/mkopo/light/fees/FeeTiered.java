package com.tezzar.mkopo.light.fees;

import com.tezzar.mkopo.light.fees.enums.CalculationType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter@AllArgsConstructor
@NoArgsConstructor
@Builder

public class FeeTiered {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private BigDecimal minimumAmount;
    private BigDecimal maximumAmount;
    @Enumerated(EnumType.STRING)
    private CalculationType calculationType;
    private BigDecimal rateValue;
    @ManyToOne
    @JoinColumn(name = "fee_id")
    private Fee fee;


}
