package com.tezzar.mkopo.light.product;

import com.tezzar.mkopo.light.fees.Fee;
import com.tezzar.mkopo.light.product.enums.ProductStatus;
import com.tezzar.mkopo.light.tenure.TenureEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    @Enumerated(EnumType.STRING)
    private ProductStatus ProductStatus;

    private Boolean capitalized;

@ManyToMany
private List<Fee> fee;
    @ManyToMany
    @JoinTable(
            name = "product_tenure_options",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tenure_option_id")
    )
    private List<TenureEntity> tenureOptions = new ArrayList<>();
}


