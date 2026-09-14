package com.tezzar.mkopo.light.product.response;

import com.tezzar.mkopo.light.fees.response.FeeResponse;
import com.tezzar.mkopo.light.product.ProductEntity;
import com.tezzar.mkopo.light.product.enums.ProductStatus;
import com.tezzar.mkopo.light.tenure.response.TenureResponse;

import java.util.List;

public record ProductResponse(
        String id,
        String productName,
        String productDescription,
        Boolean capitalized,
        ProductStatus status,
        List<TenureResponse> tenures,
        List<FeeResponse> fees
) {
        public static ProductResponse fromEntity(ProductEntity product) {
                List<TenureResponse> tenureResponses = product.getTenureOptions()
                        .stream()
                        .map(pto -> TenureResponse.fromEntity(pto.getTenure()))
                        .toList();

                List<FeeResponse> feeResponses = product.getProductFees()
                        .stream()
                        .map(pf -> FeeResponse.fromEntity(pf.getFee()))
                        .toList();

                return new ProductResponse(
                        product.getId(),
                        product.getProductName(),
                        product.getProductDescription(),
                        product.getCapitalized(),
                        product.getProductStatus(),
                        tenureResponses,
                        feeResponses
                );
        }
}
