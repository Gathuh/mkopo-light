package com.tezzar.mkopo.light.product;

import com.tezzar.mkopo.light.product.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, String> {
    List<ProductEntity> findByProductStatusNot(ProductStatus status);
}
