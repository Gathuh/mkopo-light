package com.tezzar.mkopo.light.tenure;

import com.tezzar.mkopo.light.product.fees.Fee;
import com.tezzar.mkopo.light.tenure.enums.TenureType;
import jakarta.persistence.*;
import lombok.*;

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

    private Fee fee;
}
