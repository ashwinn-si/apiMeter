package com.quotaGate.email_service.Config;

import org.springframework.mail.javamail.JavaMailSender;

public class JavaEmailSenderConfig {
    private final JavaMailSender mailSender;

    public JavaEmailSenderConfig(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

}
