package com.tezzar.mkopo.light.product;

import com.tezzar.mkopo.light.tenure.TenureEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_tenure_options")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductTenureOption {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @ManyToOne
    @JoinColumn(name = "tenure_id")
    private TenureEntity tenure;

    private LocalDateTime attachedAt;
}
