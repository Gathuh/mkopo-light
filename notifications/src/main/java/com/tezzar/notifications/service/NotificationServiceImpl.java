package com.tezzar.notifications.service;

import com.tezzar.notifications.channel.NotificationSender;
import com.tezzar.notifications.channel.NotificationMessage;
import com.tezzar.notifications.domain.NotificationLog;
import com.tezzar.notifications.domain.NotificationRule;
import com.tezzar.notifications.domain.NotificationTemplate;
import com.tezzar.notifications.domain.enums.LoanEventType;
import com.tezzar.notifications.domain.enums.NotificationStatus;
import com.tezzar.notifications.kafka.LoanEventMessage;
import com.tezzar.notifications.repository.NotificationLogRepository;
import com.tezzar.notifications.repository.NotificationRuleRepository;
import com.tezzar.notifications.repository.NotificationTemplateRepository;
import com.tezzar.notifications.template.TemplateEngine;
import com.tezzar.notifications.template.TemplateVariableBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationTemplateRepository templateRepository;
    private final NotificationRuleRepository ruleRepository;
    private final NotificationLogRepository logRepository;
    private final TemplateEngine templateEngine;
    private final TemplateVariableBuilder variableBuilder;
    private final List<NotificationSender> channels;

    @Override
    public void processEvent(LoanEventMessage event) {
        List<NotificationRule> rules = ruleRepository
                .findByEventTypeAndEnabledTrue(event.eventType());

        if (rules.isEmpty()) {
            log.info("No active rules for event type: {}", event.eventType());
            return;
        }

        Map<String, String> variables = variableBuilder.buildFrom(event);

        for (NotificationRule rule : rules) {
            templateRepository
                    .findByEventTypeAndChannelAndActiveTrue(rule.getEventType(), rule.getChannel())
                    .ifPresentOrElse(
                            template -> dispatch(event, template, variables, rule.getChannel()),
                            () -> log.warn("No active template found for event={} channel={}",
                                    rule.getEventType(), rule.getChannel())
                    );
        }
    }

    private void dispatch(LoanEventMessage event, NotificationTemplate template,
                          Map<String, String> variables,
                          com.tezzar.notifications.domain.enums.NotificationChannel channel) {

        String resolvedSubject = templateEngine.resolve(template.getSubject(), variables);
        String resolvedBody = templateEngine.resolve(template.getBody(), variables);

        String recipient = resolveRecipient(event, channel);

        NotificationMessage message = new NotificationMessage(
                event.loanId(), event.customerId(),
                recipient, resolvedSubject, resolvedBody);

        NotificationLog logEntry = NotificationLog.builder()
                .loanId(event.loanId())
                .customerId(event.customerId())
                .eventType(event.eventType())
                .channel(channel)
                .recipient(recipient)
                .subject(resolvedSubject)
                .body(resolvedBody)
                .status(NotificationStatus.PENDING)
                .build();

        channels.stream()
                .filter(c -> c.supportedChannel().equals(channel))
                .findFirst()
                .ifPresentOrElse(
                        c -> {
                            try {
                                c.send(message);
                                logEntry.setStatus(NotificationStatus.SENT);
                            } catch (Exception ex) {
                                logEntry.setStatus(NotificationStatus.FAILED);
                                logEntry.setFailureReason(ex.getMessage());
                                log.error("Channel {} failed for loan {}: {}",
                                        channel, event.loanId(), ex.getMessage());
                            }
                        },
                        () -> {
                            logEntry.setStatus(NotificationStatus.SKIPPED);
                            logEntry.setFailureReason("No channel implementation found for: " + channel);
                            log.warn("No channel implementation for: {}", channel);
                        }
                );

        logRepository.save(logEntry);
    }

    private String resolveRecipient(LoanEventMessage event,
                                    com.tezzar.notifications.domain.enums.NotificationChannel channel) {
        return switch (channel) {
            case EMAIL -> event.customerId() + "@placeholder.com";
            case SMS   -> event.customerId();
            case PUSH  -> event.customerId();
        };
    }

    @Override
    public NotificationTemplate createTemplate(NotificationTemplate template) {
        return templateRepository.save(template);
    }

    @Override
    public NotificationTemplate updateTemplate(String id, NotificationTemplate updated) {
        NotificationTemplate existing = findTemplateById(id);
        existing.setSubject(updated.getSubject());
        existing.setBody(updated.getBody());
        existing.setActive(updated.getActive());
        return templateRepository.save(existing);
    }

    @Override
    public NotificationTemplate findTemplateById(String id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found with id: " + id));
    }

    @Override
    public List<NotificationTemplate> findAllTemplates() {
        return templateRepository.findAll();
    }

    @Override
    public List<NotificationTemplate> findTemplatesByEventType(LoanEventType eventType) {
        return templateRepository.findByEventType(eventType);
    }

    @Override
    public void deleteTemplate(String id) {
        NotificationTemplate template = findTemplateById(id);
        template.setActive(false);
        templateRepository.save(template);
    }
}
