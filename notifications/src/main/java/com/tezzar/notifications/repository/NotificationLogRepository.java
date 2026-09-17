package com.tezzar.notifications.repository;

import com.tezzar.notifications.domain.NotificationLog;
import com.tezzar.notifications.domain.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, String> {
    List<NotificationLog> findByLoanId(String loanId);
    List<NotificationLog> findByCustomerId(String customerId);
    List<NotificationLog> findByStatus(NotificationStatus status);
}
