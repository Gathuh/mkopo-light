package com.tezzar.notifications.service;

import com.tezzar.notifications.domain.NotificationTemplate;
import com.tezzar.notifications.domain.enums.LoanEventType;
import com.tezzar.notifications.domain.enums.NotificationChannel;
import com.tezzar.notifications.kafka.LoanEventMessage;

import java.util.List;

public interface NotificationService {
    void processEvent(LoanEventMessage event);
    NotificationTemplate createTemplate(NotificationTemplate template);
    NotificationTemplate updateTemplate(String id, NotificationTemplate template);
    NotificationTemplate findTemplateById(String id);
    List<NotificationTemplate> findAllTemplates();
    List<NotificationTemplate> findTemplatesByEventType(LoanEventType eventType);
    void deleteTemplate(String id);
}
