package com.quotaGate.email_service.Service;

import com.quotaGate.email_service.DTO.CustomError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private String fromEmail = "siashwin2005@gmail.com";

    @Autowired
    private JavaMailSender emailSender;



    public void sendEmail(String fromMail, String toMail, String subject, String  body){

        try{
            SimpleMailMessage email = new SimpleMailMessage();


            email.setFrom(fromMail);
            email.setTo(toMail);
            email.setSubject(subject);
            email.setText(body);

            emailSender.send(email);
        }catch (Exception e){
            throw new CustomError(HttpStatus.CONFLICT, e.getMessage());
        }

    }

    public void sendEmail(String toMail, String subject, String  body){

        try{
            SimpleMailMessage email = new SimpleMailMessage();


            email.setFrom(fromEmail);
            email.setTo(toMail);
            email.setSubject(subject);
            email.setText(body);

            emailSender.send(email);
        }catch (Exception e){
            throw new CustomError(HttpStatus.CONFLICT, e.getMessage());
        }

    }
}
