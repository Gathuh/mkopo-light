package com.tezzar.mkopo.light.product;

import com.tezzar.mkopo.light.fees.FeeEntity;
import com.tezzar.mkopo.light.fees.FeeService;
import com.tezzar.mkopo.light.jointables.ProductFee;
import com.tezzar.mkopo.light.product.enums.ProductStatus;
import com.tezzar.mkopo.light.product.request.ProductRequest;
import com.tezzar.mkopo.light.product.request.UpdateProductRequest;
import com.tezzar.mkopo.light.product.response.ProductResponse;
import com.tezzar.mkopo.light.tenure.TenureEntity;
import com.tezzar.mkopo.light.tenure.TenureService;
import com.tezzar.mkopo.light.tenure.response.TenureResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final TenureService tenureService;
    private final FeeService feeService;

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {
        ProductEntity product = ProductRequest.toProductEntity(productRequest);
        productRepository.save(product);

        if (productRequest.tenureIds() != null && !productRequest.tenureIds().isEmpty()) {
            List<TenureEntity> tenures = tenureService.findAllByIds(productRequest.tenureIds());
            for (TenureEntity tenure : tenures) {
                ProductTenureOption option = ProductTenureOption.builder()
                        .product(product)
                        .tenure(tenure)
                        .build();
                product.getTenureOptions().add(option);
            }
        }

        if (productRequest.feeIds() != null && !productRequest.feeIds().isEmpty()) {
            List<FeeEntity> fees = feeService.findAllByIds(productRequest.feeIds());
            for (FeeEntity fee : fees) {
                ProductFee productFee = ProductFee.builder()
                        .product(product)
                        .fee(fee)
                        .build();
                product.getProductFees().add(productFee);
            }
        }

        return ProductResponse.fromEntity(productRepository.save(product));
    }

    @Override
    public ProductResponse findById(String id) {
        return ProductResponse.fromEntity(getProductOrThrow(id));
    }

    @Override
    public List<ProductResponse> findAll() {
        return productRepository.findByProductStatusNot(ProductStatus.DELETED)
                .stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(String id, UpdateProductRequest request) {
        ProductEntity product = getProductOrThrow(id);

        product.setProductName(request.productName());
        product.setProductDescription(request.productDescription());
        product.setCapitalized(request.capitalized());

        if (request.tenureIds() != null && !request.tenureIds().isEmpty()) {
            product.getTenureOptions().clear();
            List<TenureEntity> tenures = tenureService.findAllByIds(request.tenureIds());
            for (TenureEntity tenure : tenures) {
                ProductTenureOption option = ProductTenureOption.builder()
                        .product(product)
                        .tenure(tenure)
                        .build();
                product.getTenureOptions().add(option);
            }
        }

        if (request.feeIds() != null && !request.feeIds().isEmpty()) {
            product.getProductFees().clear();
            List<FeeEntity> fees = feeService.findAllByIds(request.feeIds());
            for (FeeEntity fee : fees) {
                ProductFee productFee = ProductFee.builder()
                        .product(product)
                        .fee(fee)
                        .build();
                product.getProductFees().add(productFee);
            }
        }

        return ProductResponse.fromEntity(productRepository.save(product));
    }

    @Override
    @Transactional
    public void softDelete(String id) {
        ProductEntity product = getProductOrThrow(id);
        product.setProductStatus(ProductStatus.DELETED);
        productRepository.save(product);
    }

    private ProductEntity getProductOrThrow(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    @Override
    public List<TenureResponse> getTenuresByProduct(String productId) {
        ProductEntity product = getProductOrThrow(productId);
        return product.getTenureOptions()
                .stream()
                .map(pto -> TenureResponse.fromEntity(pto.getTenure()))
                .toList();
    }

    @Override
    public ProductEntity findEntityById(String id) {
        return getProductOrThrow(id);
    }
}
