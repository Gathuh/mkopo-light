package com.tezzar.mkopo.light.product;

import com.tezzar.mkopo.light.product.request.ProductRequest;
import com.tezzar.mkopo.light.product.request.UpdateProductRequest;
import com.tezzar.mkopo.light.product.response.ProductResponse;
import com.tezzar.mkopo.light.tenure.response.TenureResponse;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse findById(String id);
    List<ProductResponse> findAll();
    ProductResponse updateProduct(String id, UpdateProductRequest request);
    void softDelete(String id);
    List<TenureResponse> getTenuresByProduct(String productId);
    ProductEntity findEntityById(String id);
}
