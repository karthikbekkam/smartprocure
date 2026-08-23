package com.smartprocure.service;

public interface EmailService {
    void sendPasswordResetEmail(String toEmail, String resetToken);
}
