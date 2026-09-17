package com.tezzar.notifications.channel;

public record NotificationMessage(
        String loanId,
        String customerId,
        String recipient,
        String subject,
        String body
) {
}
