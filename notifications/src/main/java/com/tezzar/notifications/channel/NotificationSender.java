package com.tezzar.notifications.channel;

import com.tezzar.notifications.domain.enums.NotificationChannel;

public interface NotificationSender {
    void send(NotificationMessage message);
    NotificationChannel supportedChannel();
}
