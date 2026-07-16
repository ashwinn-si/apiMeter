package com.quotaGate.auth_service.Kafka.Event;

public record EmailEvent (
    String toEmail,
    String subject,
    String body
){}
