package com.tezzar.notifications.repository;

import com.tezzar.notifications.domain.NotificationTemplate;
import com.tezzar.notifications.domain.enums.LoanEventType;
import com.tezzar.notifications.domain.enums.NotificationChannel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, String> {
    Optional<NotificationTemplate> findByEventTypeAndChannelAndActiveTrue(LoanEventType eventType, NotificationChannel channel);
    List<NotificationTemplate> findByEventType(LoanEventType eventType);
}
