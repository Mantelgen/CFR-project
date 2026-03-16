package com.cfr.networkapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendTestEmail(String to) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("noreply@cfr.local");
        message.setTo(to);
        message.setSubject("Test Email from CFR System");
        message.setText("This is a test email sent from the Spring Boot backend.");

        mailSender.send(message);
    }
}
