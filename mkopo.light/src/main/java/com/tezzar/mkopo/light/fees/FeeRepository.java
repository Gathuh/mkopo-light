package com.tezzar.mkopo.light.fees;

import com.tezzar.mkopo.light.fees.enums.FeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeeRepository extends JpaRepository<FeeEntity, String> {
    List<FeeEntity> findAllByIdIn(List<String> ids);
    List<FeeEntity> findByStatusNot(FeeStatus status);
}
