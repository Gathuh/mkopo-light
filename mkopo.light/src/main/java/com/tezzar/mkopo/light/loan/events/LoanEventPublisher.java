package com.tezzar.mkopo.light.loan.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanEventPublisher {

    private final KafkaTemplate<String, LoanEvent> kafkaTemplate;

    @Value("${kafka.topics.loan-events}")
    private String loanEventsTopic;

    public void publish(LoanEvent event) {
        kafkaTemplate.send(loanEventsTopic, event.loanId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish loan event {} for loan {}: {}",
                                event.eventType(), event.loanId(), ex.getMessage());
                    } else {
                        log.info("Published loan event {} for loan {} to topic {}",
                                event.eventType(), event.loanId(), loanEventsTopic);
                    }
                });
    }
}
