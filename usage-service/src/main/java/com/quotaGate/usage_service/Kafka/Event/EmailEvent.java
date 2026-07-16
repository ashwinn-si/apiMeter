package com.quotaGate.usage_service.Kafka.Event;

public record EmailEvent(String toEmail, String subject, String body) {
}
