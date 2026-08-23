package com.smartprocure.service.impl;

import com.smartprocure.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${app.mail.from:noreply@smartprocure.com}")
    private String mailFrom;

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        String resetUrl = "http://localhost:8080/smartprocure/reset-password?token=" + resetToken;

        if (!mailEnabled) {
            log.info("[DEV MODE] Password reset email delivery disabled. Token generated for email {}: {}", toEmail, resetUrl);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(toEmail);
            message.setSubject("SmartProcure Enterprise - Password Reset Request");
            message.setText("Hello,\n\nYou have requested to reset your password on SmartProcure Enterprise.\n\n"
                    + "Please click the link below to set a new password:\n" + resetUrl + "\n\n"
                    + "This link will expire in 24 hours.\n\nIf you did not request this, please ignore this email.\n\n"
                    + "Best regards,\nSmartProcure Security Team");

            mailSender.send(message);
            log.info("Password reset email sent successfully to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
        }
    }
}
