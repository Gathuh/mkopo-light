package com.tezzar.notifications.template;

import java.util.Map;

public interface TemplateEngine {
    String resolve(String template, Map<String, String> variables);
}
