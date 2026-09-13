package com.tezzar.mkopo.light.product;

import com.tezzar.mkopo.light.product.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
@Builder
@Getter
@Setter

public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String productName;

    private String productDescription;

    private BigDecimal minimumAmount;

    private BigDecimal maximumAmount;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductFee> productFees = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "product_tenure_options",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tenure_option_id")
    )
    private List<TenureOption> tenureOptions = new ArrayList<>();
}

}
