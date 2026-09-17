package com.tezzar.mkopo.light.product;

import com.tezzar.mkopo.light.controllerresponse.MessageAndResultResponse;
import com.tezzar.mkopo.light.product.request.ProductRequest;
import com.tezzar.mkopo.light.product.request.UpdateProductRequest;
import com.tezzar.mkopo.light.product.response.ProductResponse;
import com.tezzar.mkopo.light.tenure.response.TenureResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mkopo/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "Create and manage loan products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(
            summary = "Create a new loan product",
            description = "Creates a loan product by attaching existing tenure and fee IDs"
    )
    public ResponseEntity<MessageAndResultResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(MessageAndResultResponse.success(
                        productService.createProduct(request),
                        "Product created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get product by ID",
            description = "Returns a single product with its tenures and fees"
    )
    public ResponseEntity<MessageAndResultResponse<ProductResponse>> getProduct(
            @PathVariable String id) {
        return ResponseEntity.ok(MessageAndResultResponse.success(
                productService.findById(id),
                "Product retrieved successfully"));
    }

    @GetMapping
    @Operation(
            summary = "Get all products",
            description = "Returns all active and inactive products excluding deleted ones"
    )
    public ResponseEntity<MessageAndResultResponse<List<ProductResponse>>> getAllProducts() {
        return ResponseEntity.ok(MessageAndResultResponse.success(
                productService.findAll(),
                "Products retrieved successfully"));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a product",
            description = "Updates product name, description, capitalization and replaces tenure and fee attachments"
    )
    public ResponseEntity<MessageAndResultResponse<ProductResponse>> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(MessageAndResultResponse.success(
                productService.updateProduct(id, request),
                "Product updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Soft delete a product",
            description = "Marks the product as DELETED without removing it from the database"
    )
    public ResponseEntity<MessageAndResultResponse<Void>> deleteProduct(
            @PathVariable String id) {
        productService.softDelete(id);
        return ResponseEntity.ok(MessageAndResultResponse.success(
                null,
                "Product deleted successfully"));
    }

    @GetMapping("/{productId}/tenures")
    @Operation(
            summary = "Get tenures for a product",
            description = "Returns only the tenures attached to this product — used during loan application"
    )
    public ResponseEntity<MessageAndResultResponse<List<TenureResponse>>> getTenuresByProduct(
            @PathVariable String productId) {
        return ResponseEntity.ok(MessageAndResultResponse.success(
                productService.getTenuresByProduct(productId),
                "Tenures retrieved successfully"));
    }
}
