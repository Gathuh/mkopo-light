package com.tezzar.notifications.repository;

import com.tezzar.notifications.domain.NotificationRule;
import com.tezzar.notifications.domain.enums.LoanEventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRuleRepository extends JpaRepository<NotificationRule, String> {
    List<NotificationRule> findByEventTypeAndEnabledTrue(LoanEventType eventType);
}
