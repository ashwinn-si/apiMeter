package com.quotaGate.email_service.Controller;

import com.quotaGate.email_service.DTO.SendEmailDTO;
import com.quotaGate.email_service.Service.EmailService;
import com.quotaGate.email_service.Utils.ResponseHandler;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@Validated
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    private String fromEmail = "siashwin2005@gmail.com";


    public ResponseEntity<?> sendTestEmail(){
        emailService.sendEmail(fromEmail, "ashwin23cse@gmail.com", "TESTING", "HELLO DA MAPILA");
        return ResponseEntity.status(200).body("Email send successful");
    }


    @PostMapping("/send-mail")
    public ResponseEntity<?> sendEmailController(@RequestBody @Valid SendEmailDTO sendEmailDTO){
        emailService.sendEmail(sendEmailDTO.getToEmail(), sendEmailDTO.getSubject(), sendEmailDTO.getBody());

        return ResponseHandler.handleResponse(HttpStatus.OK, null, "Email Send Successful");
    }


}
