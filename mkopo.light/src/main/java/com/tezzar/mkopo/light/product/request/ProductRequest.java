package com.tezzar.mkopo.light.product.request;

import com.tezzar.mkopo.light.product.enums.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ProductRequest(
        @NotBlank(message = "Product name cannot be null")
        String productName,

        String productDescription,

        @NotNull(message = "Capitalized flag cannot be null")
        Boolean capitalized,

        @NotNull(message = "Product status cannot be null")
        ProductStatus status,

        List<String> tenureIds,

        List<String> feeIds
) {
}
