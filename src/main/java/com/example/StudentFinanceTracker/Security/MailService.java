package com.example.StudentFinanceTracker.Security;

import com.example.StudentFinanceTracker.Service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
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
        String subject = "🔒 Reset Your Student Finance Tracker Password";

        String textBody =
                "Hi,\n\n" +
                        "You requested a password reset. Click the link below:\n" +
                        resetLink + "\n\n" +
                        "If you didn’t request this, ignore this email.\n\n" +
                        "Student Finance Tracker Team";

        String htmlBody =
                "<!DOCTYPE html><html><body>" +
                        "<p>Hi,</p>" +
                        "<p>You requested a password reset. Click the button below:</p>" +
                        "<p><a href=\"" + resetLink + "\" " +
                        "style=\"background:#4A90E2;color:#ffffff;padding:10px 20px;text-decoration:none;\" " +
                        ">Reset Password</a></p>" +
                        "<p>If you didn’t request this, you can safely ignore this.</p>" +
                        "<hr>" +
                        "<p style=\"font-size:12px;color:#666;\">" +
                        "Student Finance Tracker<br>" +
                        "1234 Main St, Sarajevo<br>" +
                        "<a href=\"https://mail.sdp.it.com/unsubscribe\">Unsubscribe</a>" +
                        "</p>" +
                        "</body></html>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(new InternetAddress("no-reply@mail.sdp.it.com", "Student Finance Tracker"));
            helper.setTo(email);
            helper.setSubject(subject);

            helper.setText(textBody, htmlBody);
            message.addHeader("List-Unsubscribe",
                    "<mailto:unsubscribe@mail.sdp.it.com>, <https://mail.sdp.it.com/unsubscribe>");

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public void sendPaymentReport(String to, byte[] pdfData) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject("Payment Confirmation");
        helper.setText("Hello,\n\nAttached is your payment confirmation PDF.\n\nThank you!");
        helper.setFrom(new InternetAddress("vedran@mail.sdp.it.com", "Payment Institution"));

        helper.addAttachment("payment_report.pdf", new ByteArrayResource(pdfData));
        mailSender.send(message);
    }
}
