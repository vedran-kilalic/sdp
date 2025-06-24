package com.example.StudentFinanceTracker.Security;

import com.example.StudentFinanceTracker.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserService userService;

    public void sendResetPasswordEmail(String email) {
        String token = UUID.randomUUID().toString();

        userService.saveResetToken(email, token);

        String resetLink = "http://localhost:8080/change-password?token=" + token;

        String subject = "Password Reset Request";
        String body = "To reset your password, click the link below:\n\n" + resetLink +
                "\n\nIf you did not request this, please ignore this email.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("brad@mail.sdp.it.com");

        mailSender.send(message);
    }
}
