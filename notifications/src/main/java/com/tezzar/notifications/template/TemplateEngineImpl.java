package com.tezzar.notifications.template;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TemplateEngineImpl implements TemplateEngine {

    @Override
    public String resolve(String template, Map<String, String> variables) {
        if (template == null || template.isBlank()) return "";
        if (variables == null || variables.isEmpty()) return template;

        String resolved = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() != null ? entry.getValue() : "";
            resolved = resolved.replace(placeholder, value);
        }
        return resolved;
    }
}
