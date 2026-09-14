package com.tezzar.mkopo.light.fees;

import com.tezzar.mkopo.light.controllerresponse.MessageAndResultResponse;
import com.tezzar.mkopo.light.fees.request.FeesRequest;
import com.tezzar.mkopo.light.fees.request.UpdateFeeRequest;
import com.tezzar.mkopo.light.fees.response.FeeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mkopo/fees")
@RequiredArgsConstructor
@Tag(name = "Fee Management", description = "Create and manage reusable fee configurations")
public class FeeController {

    private final FeeService feeService;

    @PostMapping
    @Operation(summary = "Create a new fee",
            description = "Creates a reusable fee attached to products or tenures")
    public ResponseEntity<MessageAndResultResponse<FeeResponse>> createFee(
            @Valid @RequestBody FeesRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(MessageAndResultResponse.success(
                        feeService.createFee(request),
                        "Fee created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get fee by ID")
    public ResponseEntity<MessageAndResultResponse<FeeResponse>> getFee(
            @PathVariable String id) {
        return ResponseEntity.ok(MessageAndResultResponse.success(
                feeService.findById(id),
                "Fee retrieved successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all fees",
            description = "Returns all fees excluding deleted ones")
    public ResponseEntity<MessageAndResultResponse<List<FeeResponse>>> getAllFees() {
        return ResponseEntity.ok(MessageAndResultResponse.success(
                feeService.findAll(),
                "Fees retrieved successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a fee")
    public ResponseEntity<MessageAndResultResponse<FeeResponse>> updateFee(
            @PathVariable String id,
            @Valid @RequestBody UpdateFeeRequest request) {
        return ResponseEntity.ok(MessageAndResultResponse.success(
                feeService.updateFee(id, request),
                "Fee updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a fee",
            description = "Marks the fee as DELETED without removing it from the database")
    public ResponseEntity<MessageAndResultResponse<Void>> deleteFee(
            @PathVariable String id) {
        feeService.softDelete(id);
        return ResponseEntity.ok(MessageAndResultResponse.success(
                null,
                "Fee deleted successfully"));
    }
}
