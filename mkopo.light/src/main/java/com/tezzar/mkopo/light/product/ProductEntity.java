package com.tezzar.mkopo.light.product;

import com.tezzar.mkopo.light.fees.ProductFee;
import com.tezzar.mkopo.light.product.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
@Builder
@Getter
@Setter
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String productName;

    private String productDescription;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    private Boolean capitalized;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductFee> productFees = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductTenureOption> tenureOptions = new ArrayList<>();
}


