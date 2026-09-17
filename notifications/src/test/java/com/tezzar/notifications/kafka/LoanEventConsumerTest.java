package com.tezzar.notifications.kafka;

import com.tezzar.notifications.domain.enums.LoanEventType;
import com.tezzar.notifications.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoanEventConsumerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private LoanEventConsumer consumer;

    @Test
    void consumePassesEventToNotificationService() {
        LoanEventMessage event = sampleEvent();

        consumer.consume(event, "loan-events", 0, 10L);

        verify(notificationService).processEvent(event);
    }

    @Test
    void consumeSwallowsServiceExceptions() {
        LoanEventMessage event = sampleEvent();
        doThrow(new RuntimeException("channel down")).when(notificationService).processEvent(event);

        assertDoesNotThrow(() -> consumer.consume(event, "loan-events", 0, 11L));
    }

    private LoanEventMessage sampleEvent() {
        return new LoanEventMessage(
                LoanEventType.LOAN_OVERDUE,
                "loan-1",
                "customer-1",
                "Starter Loan",
                new BigDecimal("1000.00"),
                new BigDecimal("300.00"),
                null,
                LocalDate.now().plusDays(2),
                "approver-1",
                LocalDateTime.now()
        );
    }
}
