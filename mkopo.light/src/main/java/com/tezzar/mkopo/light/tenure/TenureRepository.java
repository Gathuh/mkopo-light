package com.tezzar.mkopo.light.tenure;

import com.tezzar.mkopo.light.tenure.enums.TenureStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TenureRepository extends JpaRepository<TenureEntity, String> {
    List<TenureEntity> findAllByIdIn(List<String> ids);
    List<TenureEntity> findByStatusNot(TenureStatus status);
}
