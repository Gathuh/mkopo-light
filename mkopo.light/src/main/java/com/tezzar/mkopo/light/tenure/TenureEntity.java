package com.tezzar.mkopo.light.tenure;

import com.tezzar.mkopo.light.fees.Fee;
import com.tezzar.mkopo.light.tenure.enums.RepaymentStructure;
import com.tezzar.mkopo.light.tenure.enums.TenureType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenureEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Integer tenureValue;

    @Enumerated(EnumType.STRING)

    private TenureType tenureType;

    private BigDecimal minimumProductAmount;

    private BigDecimal maximumProductAmount;

    @OneToMany(mappedBy = "tenure", cascade = CascadeType.ALL)
    private List<Fee> fees;

    @Enumerated(EnumType.STRING)
    private RepaymentStructure repaymentStructure;

    private Integer installmentCount;

    private Boolean capitalized;


}
