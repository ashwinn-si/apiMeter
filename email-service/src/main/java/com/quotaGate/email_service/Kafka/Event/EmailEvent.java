package com.quotaGate.email_service.Kafka.Event;

public record EmailEvent(
    String toEmail,
    String subject,
    String body
){}
