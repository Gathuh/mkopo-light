package com.tezzar.mkopo.light.product;

import com.tezzar.mkopo.light.fees.Fee;
import com.tezzar.mkopo.light.fees.FeeRepository;
import com.tezzar.mkopo.light.fees.ProductFee;
import com.tezzar.mkopo.light.product.request.ProductRequest;
import com.tezzar.mkopo.light.tenure.TenureEntity;
import com.tezzar.mkopo.light.tenure.TenureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final TenureRepository tenureRepository;
    private final FeeRepository feeRepository;

    @Override
    public ProductEntity createProduct(ProductRequest request) {
        ProductEntity product = ProductEntity.builder()
                .productName(request.productName())
                .productDescription(request.productDescription())
                .capitalized(request.capitalized())
                .status(request.status())
                .build();

        ProductEntity savedProduct = productRepository.save(product);

        if (request.tenureIds() != null && !request.tenureIds().isEmpty()) {
            List<TenureEntity> tenures = tenureRepository.findAllByIdIn(request.tenureIds());
            List<ProductTenureOption> tenureOptions = new ArrayList<>();
            for (TenureEntity tenure : tenures) {
                ProductTenureOption option = ProductTenureOption.builder()
                        .product(savedProduct)
                        .tenure(tenure)
                        .attachedAt(LocalDateTime.now())
                        .build();
                tenureOptions.add(option);
            }
            savedProduct.setTenureOptions(tenureOptions);
        }

        if (request.feeIds() != null && !request.feeIds().isEmpty()) {
            List<Fee> fees = feeRepository.findAllByIdIn(request.feeIds());
            List<ProductFee> productFees = new ArrayList<>();
            for (Fee fee : fees) {
                ProductFee productFee = ProductFee.builder()
                        .product(savedProduct)
                        .fee(fee)
                        .attachedAt(LocalDateTime.now())
                        .build();
                productFees.add(productFee);
            }
            savedProduct.setProductFees(productFees);
        }

        return productRepository.save(savedProduct);
    }

    @Override
    public ProductEntity findById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }
}
