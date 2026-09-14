package com.tezzar.mkopo.light.fees;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeeRepository extends JpaRepository<Fee, String> {
    List<Fee> findAllByIdIn(List<String> ids);
}
