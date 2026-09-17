package com.tezzar.notifications.template;

import com.tezzar.notifications.kafka.LoanEventMessage;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TemplateVariableBuilder {

    public Map<String, String> buildFrom(LoanEventMessage event) {
        Map<String, String> vars = new HashMap<>();

        vars.put("loanId", nullSafe(event.loanId()));
        vars.put("customerId", nullSafe(event.customerId()));
        vars.put("productName", nullSafe(event.productName()));
        vars.put("principalAmount", event.principalAmount() != null
                ? event.principalAmount().toPlainString() : "0");
        vars.put("outstandingBalance", event.outstandingBalance() != null
                ? event.outstandingBalance().toPlainString() : "0");
        vars.put("paymentAmount", event.paymentAmount() != null
                ? event.paymentAmount().toPlainString() : "0");
        vars.put("dueDate", event.dueDate() != null
                ? event.dueDate().toString() : "N/A");
        vars.put("eventType", nullSafe(event.eventType() != null
                ? event.eventType().name() : null));

        return vars;
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
    }
}
