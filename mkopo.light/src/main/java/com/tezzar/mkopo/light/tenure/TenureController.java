package com.tezzar.mkopo.light.tenure;

import com.tezzar.mkopo.light.controllerresponse.MessageAndResultResponse;
import com.tezzar.mkopo.light.tenure.request.TenureRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mkopo/tenures")
@RequiredArgsConstructor
@Tag(name = "Tenure Management", description = "Create and manage reusable tenure configurations")
public class TenureController {

    private final TenureService tenureService;

    @PostMapping
    @Operation(
            summary = "Create a new tenure",
            description = "Creates a reusable tenure option with repayment structure and optional fee attachments"
    )
    public ResponseEntity<MessageAndResultResponse<TenureEntity>> createTenure(@Valid @RequestBody TenureRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(MessageAndResultResponse.success(tenureService.createTenure(request), "Tenure created successfully"));
    }
}
