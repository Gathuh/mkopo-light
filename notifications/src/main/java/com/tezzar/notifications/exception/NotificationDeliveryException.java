package com.tezzar.notifications.exception;

public class NotificationDeliveryException extends RuntimeException {
    public NotificationDeliveryException(String channel, String reason) {
        super(channel + " delivery failed: " + reason);
    }
}
