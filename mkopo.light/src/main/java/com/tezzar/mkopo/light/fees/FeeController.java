package com.tezzar.mkopo.light.fees;

import com.tezzar.mkopo.light.controllerresponse.MessageAndResultResponse;
import com.tezzar.mkopo.light.fees.request.FeesRequest;
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
@RequestMapping("/mkopo/fees")
@RequiredArgsConstructor
@Tag(name = "Fee Management", description = "Create and manage reusable fee configurations")
public class FeeController {

    private final FeeService feeService;

    @PostMapping
    @Operation(
            summary = "Create a new fee",
            description = "Creates a reusable fee configuration that can be attached to multiple products or tenures"
    )
    public ResponseEntity<MessageAndResultResponse<Fee>> createFee(@Valid @RequestBody FeesRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(MessageAndResultResponse.success(feeService.createFee(request), "Fee created successfully"));
    }
}
