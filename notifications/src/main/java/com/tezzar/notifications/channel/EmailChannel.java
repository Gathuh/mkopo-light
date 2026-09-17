package com.tezzar.notifications.channel;

import com.tezzar.notifications.domain.enums.NotificationChannel;
import com.tezzar.notifications.exception.NotificationDeliveryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailChannel implements NotificationSender {

    private final JavaMailSender mailSender;

    @Override
    public void send(NotificationMessage message) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(message.recipient());
            mail.setSubject(message.subject());
            mail.setText(message.body());
            mailSender.send(mail);
            log.info("Email sent to {} for loan {}", message.recipient(), message.loanId());
        } catch (Exception ex) {
            log.error("Failed to send email to {} for loan {}: {}", message.recipient(), message.loanId(), ex.getMessage());
            throw new NotificationDeliveryException("Email", ex.getMessage());
        }
    }

    @Override
    public NotificationChannel supportedChannel() {
        return NotificationChannel.EMAIL;
    }
}
