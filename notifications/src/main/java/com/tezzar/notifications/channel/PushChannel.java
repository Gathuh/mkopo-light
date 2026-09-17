package com.tezzar.notifications.channel;

import com.tezzar.notifications.domain.enums.NotificationChannel;
import com.tezzar.notifications.exception.NotificationDeliveryException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PushChannel implements NotificationSender {

    @Override
    public void send(NotificationMessage message) {
        try {
            Message firebaseMessage = Message.builder()
                    .setToken(message.recipient())
                    .setNotification(Notification.builder()
                            .setTitle(message.subject())
                            .setBody(message.body())
                            .build())
                    .putData("loanId", message.loanId())
                    .putData("customerId", message.customerId())
                    .build();

            String response = FirebaseMessaging.getInstance().send(firebaseMessage);
            log.info("Push notification sent for loan {} — Firebase response: {}", message.loanId(), response);
        } catch (Exception ex) {
            log.error("Failed to send push notification for loan {}: {}", message.loanId(), ex.getMessage());
            throw new NotificationDeliveryException("Push", ex.getMessage());
        }
    }

    @Override
    public NotificationChannel supportedChannel() {
        return NotificationChannel.PUSH;
    }
}
