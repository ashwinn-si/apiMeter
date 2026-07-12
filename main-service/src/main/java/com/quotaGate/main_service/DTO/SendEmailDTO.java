package com.quotaGate.main_service.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendEmailDTO {
    @NotNull(message = "Email is Required")
    @Email(message = "Enter valid Email")
    private String toEmail;
    @NotNull(message = "Email is Required")
    @Length(min= 10, message = "Subject Must be Atleast 10 characters")
    private String subject;

    @NotNull(message = "Body is Required")
    @Length(min= 20, message = "Subject Must be Atleast 20 characters")
    private String body;
}
