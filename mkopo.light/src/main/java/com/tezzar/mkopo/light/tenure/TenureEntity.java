package com.tezzar.mkopo.light.tenure;

import com.tezzar.mkopo.light.fees.TenureFee;
import com.tezzar.mkopo.light.product.ProductTenureOption;
import com.tezzar.mkopo.light.tenure.enums.RepaymentStructure;
import com.tezzar.mkopo.light.tenure.enums.TenureType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tenure_options")
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

    @Enumerated(EnumType.STRING)
    private RepaymentStructure repaymentStructure;

    private Integer installmentCount;

    private Boolean capitalized;

    @OneToMany(mappedBy = "tenure", cascade = CascadeType.ALL)
    private List<TenureFee> tenureFees = new ArrayList<>();

    @OneToMany(mappedBy = "tenure", cascade = CascadeType.ALL)
    private List<ProductTenureOption> productTenureOptions = new ArrayList<>();
}
