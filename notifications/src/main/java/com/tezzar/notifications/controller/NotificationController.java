package com.tezzar.notifications.controller;

import com.tezzar.notifications.domain.NotificationLog;
import com.tezzar.notifications.domain.NotificationRule;
import com.tezzar.notifications.domain.NotificationTemplate;
import com.tezzar.notifications.domain.enums.LoanEventType;
import com.tezzar.notifications.repository.NotificationLogRepository;
import com.tezzar.notifications.repository.NotificationRuleRepository;
import com.tezzar.notifications.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Management", description = "Manage templates, rules and notification logs")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationLogRepository logRepository;
    private final NotificationRuleRepository ruleRepository;

    @PostMapping("/templates")
    @Operation(summary = "Create a notification template",
            description = "Creates a template for a given event type and channel. Body supports {{variables}}: loanId, customerId, productName, principalAmount, outstandingBalance, paymentAmount, dueDate")
    public ResponseEntity<NotificationTemplate> createTemplate(
            @Valid @RequestBody NotificationTemplate template) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(notificationService.createTemplate(template));
    }

    @GetMapping("/templates")
    @Operation(summary = "Get all notification templates")
    public ResponseEntity<List<NotificationTemplate>> getAllTemplates() {
        return ResponseEntity.ok(notificationService.findAllTemplates());
    }

    @GetMapping("/templates/{id}")
    @Operation(summary = "Get template by ID")
    public ResponseEntity<NotificationTemplate> getTemplate(@PathVariable String id) {
        return ResponseEntity.ok(notificationService.findTemplateById(id));
    }

    @GetMapping("/templates/event/{eventType}")
    @Operation(summary = "Get templates by event type",
            description = "Returns all templates configured for a specific loan event type")
    public ResponseEntity<List<NotificationTemplate>> getTemplatesByEventType(
            @PathVariable LoanEventType eventType) {
        return ResponseEntity.ok(notificationService.findTemplatesByEventType(eventType));
    }

    @PutMapping("/templates/{id}")
    @Operation(summary = "Update a template",
            description = "Updates subject, body or active status of an existing template")
    public ResponseEntity<NotificationTemplate> updateTemplate(
            @PathVariable String id,
            @Valid @RequestBody NotificationTemplate template) {
        return ResponseEntity.ok(notificationService.updateTemplate(id, template));
    }

    @DeleteMapping("/templates/{id}")
    @Operation(summary = "Deactivate a template",
            description = "Sets the template as inactive — does not delete it from the database")
    public ResponseEntity<Void> deleteTemplate(@PathVariable String id) {
        notificationService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rules")
    @Operation(summary = "Get all notification rules")
    public ResponseEntity<List<NotificationRule>> getAllRules() {
        return ResponseEntity.ok(ruleRepository.findAll());
    }

    @PostMapping("/rules")
    @Operation(summary = "Create a notification rule",
            description = "Defines which channels are triggered for a given event type. Can be scoped to a productId or customerSegment")
    public ResponseEntity<NotificationRule> createRule(
            @Valid @RequestBody NotificationRule rule) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ruleRepository.save(rule));
    }

    @PutMapping("/rules/{id}")
    @Operation(summary = "Update a notification rule")
    public ResponseEntity<NotificationRule> updateRule(
            @PathVariable String id,
            @Valid @RequestBody NotificationRule updated) {
        NotificationRule existing = ruleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rule not found with id: " + id));
        existing.setEventType(updated.getEventType());
        existing.setChannel(updated.getChannel());
        existing.setProductId(updated.getProductId());
        existing.setCustomerSegment(updated.getCustomerSegment());
        existing.setEnabled(updated.getEnabled());
        return ResponseEntity.ok(ruleRepository.save(existing));
    }

    @DeleteMapping("/rules/{id}")
    @Operation(summary = "Disable a notification rule")
    public ResponseEntity<Void> disableRule(@PathVariable String id) {
        NotificationRule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rule not found with id: " + id));
        rule.setEnabled(false);
        ruleRepository.save(rule);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/logs/loan/{loanId}")
    @Operation(summary = "Get notification logs for a loan",
            description = "Returns the full audit trail of all notifications sent for a specific loan")
    public ResponseEntity<List<NotificationLog>> getLogsByLoan(@PathVariable String loanId) {
        return ResponseEntity.ok(logRepository.findByLoanId(loanId));
    }

    @GetMapping("/logs/customer/{customerId}")
    @Operation(summary = "Get notification logs for a customer")
    public ResponseEntity<List<NotificationLog>> getLogsByCustomer(@PathVariable String customerId) {
        return ResponseEntity.ok(logRepository.findByCustomerId(customerId));
    }

    @GetMapping("/logs")
    @Operation(summary = "Get all notification logs")
    public ResponseEntity<List<NotificationLog>> getAllLogs() {
        return ResponseEntity.ok(logRepository.findAll());
    }
}
