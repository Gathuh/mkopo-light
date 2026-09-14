package com.tezzar.mkopo.light.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateProductRequest(
        @NotBlank(message = "Product name cannot be blank")
        String productName,

        String productDescription,

        @NotNull(message = "Capitalized flag cannot be null")
        Boolean capitalized,

        List<String> tenureIds,

        List<String> feeIds
) {
}
