package com.quotaGate.main_service.Service;

import com.quotaGate.main_service.Config.EmailClient;
import com.quotaGate.main_service.DTO.CustomError;
import com.quotaGate.main_service.Domain.SendEmailDTO;
import com.quotaGate.main_service.Domain.User;
import com.quotaGate.main_service.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MainService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final EmailClient emailClient;
    private final TokenService tokenService;

    @Transactional
    public void createUser(String email){

        boolean isUserExists = userService.isUserExists(email);

        if(isUserExists){
            throw  new CustomError(HttpStatus.CONFLICT, "User Already Exists");
        }

        User user = new User(email);

        userRepository.save(user);

        generateOtp(email, "OTP TO Activate Account");


    }

    public void generateOtpForTokenGeneration(String email, String subject){
        boolean isUserExists = userService.isUserExistsAndIsActive(email);

        if(!isUserExists){
            throw  new CustomError(HttpStatus.CONFLICT, "User Not Activated");
        }

        Integer otp = userService.generateOtp(email);

        sendEmail(email, subject, "Your OTPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP: " + otp);
    }

    public String checkOtpAndGenerateToken(String email, Integer otp){
        boolean isUserExists = userService.isUserExists(email);

        if(!isUserExists){
            throw  new CustomError(HttpStatus.NOT_FOUND, "User NotFound");
        }

        if(!userService.checkOtp(email, otp)){
            throw new CustomError(HttpStatus.CONFLICT, "Invalid Action");
        }

        User user = userService.findUserByEmail(email);

        String token = tokenService.generateToken(email, user.getId());

        return token;
    }

    public void checkOtpAndActivateAccount(String email, Integer otp){
        boolean isUserExists = userService.isUserExists(email);

        if(!isUserExists){
            throw  new CustomError(HttpStatus.NOT_FOUND, "User Not Found");
        }

        if(!userService.checkOtp(email, otp)){
            throw new CustomError(HttpStatus.CONFLICT, "Invalid Action");
        }

        User user = userService.findUserByEmail(email);

        user.setIsVerified(true);

        userRepository.save(user);
    }

    @Transactional
    public void clearDatabase(){
        userRepository.deleteAll();
    }


    public void generateOtp(String email, String subject){
        boolean isUserExists = userService.isUserExists(email);

        if(!isUserExists){
            throw  new CustomError(HttpStatus.CONFLICT, "User NotFound");
        }

        Integer otp = userService.generateOtp(email);

        sendEmail(email, subject, "Your OTPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP: " + otp);
    }

    private void sendEmail(String toEmail, String subject, String body){
        try{
            SendEmailDTO sendEmailDTO = new SendEmailDTO(toEmail, subject, body);
            emailClient.sendMail(sendEmailDTO);

        }catch (Exception e){
            throw new CustomError(HttpStatus.CONFLICT, e.getMessage());
        }
    }



}
