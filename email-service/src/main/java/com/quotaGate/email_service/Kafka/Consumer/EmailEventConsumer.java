package com.quotaGate.email_service.Kafka.Consumer;

import com.quotaGate.email_service.Kafka.Event.EmailEvent;
import com.quotaGate.email_service.Service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailEventConsumer {
    private final EmailService emailService;

    @KafkaListener(topics = "send-email", groupId = "email-service-group")
    public void onEmailSend(EmailEvent emailEvent){
        emailService.sendEmail(emailEvent.toEmail(), emailEvent.body(), emailEvent.subject());
    }
}
