package com.tezzar.mkopo.light.customer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanLimitRepository extends JpaRepository<LoanLimit, String> {
    Optional<LoanLimit> findByCustomerId(String customerId);
    boolean existsByCustomerId(String customerId);
}
