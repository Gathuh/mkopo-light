package com.tezzar.mkopo.light.fees;

import com.tezzar.mkopo.light.tenure.TenureEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tenure_fees")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenureFee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "tenure_id")
    private TenureEntity tenure;

    @ManyToOne
    @JoinColumn(name = "fee_id")
    private Fee fee;

    private BigDecimal overrideAmount;

    private BigDecimal overrideRate;

    private LocalDateTime attachedAt;
}
