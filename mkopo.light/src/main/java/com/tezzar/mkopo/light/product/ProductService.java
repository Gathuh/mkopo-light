package com.tezzar.mkopo.light.product;

import com.tezzar.mkopo.light.product.request.ProductRequest;

public interface ProductService {
    ProductEntity createProduct(ProductRequest request);
    ProductEntity findById(String id);
}
