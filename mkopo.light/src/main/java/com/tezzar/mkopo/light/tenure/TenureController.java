package com.tezzar.mkopo.light.tenure;

import com.tezzar.mkopo.light.controllerresponse.MessageAndResultResponse;
import com.tezzar.mkopo.light.tenure.request.TenureRequest;
import com.tezzar.mkopo.light.tenure.request.UpdateTenureRequest;
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
@RequestMapping("/mkopo/tenures")
@RequiredArgsConstructor
@Tag(name = "Tenure Management", description = "Create and manage reusable tenure configurations")
public class TenureController {

    private final TenureService tenureService;

    @PostMapping
    @Operation(summary = "Create a new tenure",
            description = "Creates a reusable tenure with repayment structure and optional fee attachments")
    public ResponseEntity<MessageAndResultResponse<TenureResponse>> createTenure(
            @Valid @RequestBody TenureRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(MessageAndResultResponse.success(
                        tenureService.createTenure(request),
                        "Tenure created successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tenure by ID")
    public ResponseEntity<MessageAndResultResponse<TenureResponse>> getTenure(
            @PathVariable String id) {
        return ResponseEntity.ok(MessageAndResultResponse.success(
                tenureService.findById(id),
                "Tenure retrieved successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all tenures",
            description = "Returns all tenures excluding deleted ones")
    public ResponseEntity<MessageAndResultResponse<List<TenureResponse>>> getAllTenures() {
        return ResponseEntity.ok(MessageAndResultResponse.success(
                tenureService.findAll(),
                "Tenures retrieved successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a tenure",
            description = "Updates tenure config and replaces fee attachments")
    public ResponseEntity<MessageAndResultResponse<TenureResponse>> updateTenure(
            @PathVariable String id,
            @Valid @RequestBody UpdateTenureRequest request) {
        return ResponseEntity.ok(MessageAndResultResponse.success(
                tenureService.updateTenure(id, request),
                "Tenure updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a tenure",
            description = "Marks the tenure as DELETED without removing it from the database")
    public ResponseEntity<MessageAndResultResponse<Void>> deleteTenure(
            @PathVariable String id) {
        tenureService.softDelete(id);
        return ResponseEntity.ok(MessageAndResultResponse.success(
                null,
                "Tenure deleted successfully"));
    }

    @GetMapping("/{tenureId}/fees")
    @Operation(
            summary = "Get fees for a tenure",
            description = "Returns only the fees attached to this tenure — used during loan application"
    )
    public ResponseEntity<MessageAndResultResponse<List<FeeResponse>>> getFeesByTenure(
            @PathVariable String tenureId) {
        return ResponseEntity.ok(MessageAndResultResponse.success(
                tenureService.getFeesByTenure(tenureId),
                "Fees retrieved successfully"));
    }
}
