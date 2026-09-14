package com.tezzar.mkopo.light.jointables;

import com.tezzar.mkopo.light.fees.FeeEntity;
import com.tezzar.mkopo.light.fees.enums.CalculationType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "fee_tiers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeeTiered {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private BigDecimal minimumAmount;

    private BigDecimal maximumAmount;

    @Enumerated(EnumType.STRING)
    private CalculationType tierCalculationType;

    private BigDecimal tierAmount;

    private BigDecimal tierRate;

    @ManyToOne
    @JoinColumn(name = "fee_id")
    private FeeEntity fee;
}
