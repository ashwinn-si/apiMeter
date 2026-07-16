package com.quotaGate.auth_service.Kafka.Publisher;

import com.quotaGate.auth_service.Kafka.Event.EmailEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailPublisher {
    private  final KafkaTemplate<String, EmailEvent> kafkaTemplate;

    public void publishEmail(String toEmail, String subject, String body) {
        EmailEvent emailEvent = new EmailEvent(toEmail, subject, body);
        kafkaTemplate.send("send-email", emailEvent.toEmail(), emailEvent);
    }
}
