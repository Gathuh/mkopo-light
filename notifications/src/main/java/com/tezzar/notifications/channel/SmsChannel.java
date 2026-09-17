package com.tezzar.notifications.channel;

import com.tezzar.notifications.domain.enums.NotificationChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SmsChannel implements NotificationSender {

    @Override
    public void send(NotificationMessage message) {
        log.info("[SMS] To: {} | Loan: {} | Body: {}",
                message.recipient(), message.loanId(), message.body());
    }

    @Override
    public NotificationChannel supportedChannel() {
        return NotificationChannel.SMS;
    }
}
