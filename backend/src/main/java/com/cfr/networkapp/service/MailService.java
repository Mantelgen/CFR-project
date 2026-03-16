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

    public void sendAccountConfirmationEmail(String to, String firstName, String confirmationToken) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("noreply@cfr.local");
        message.setTo(to);
        message.setSubject("Confirm your CFR Account");
        message.setText("Hello " + firstName + ",\n\n" +
                "Thank you for registering with CFR. Please click the link below to confirm your email address:\n\n" +
                "http://localhost:3000/confirm-email?token=" + confirmationToken + "\n\n" +
                "If you did not register, please ignore this email.\n\n" +
                "Best regards,\nCFR Team");

        mailSender.send(message);
    }

    public void sendReservationConfirmationEmail(String to, String firstName, String trainNumber, Integer numberOfSeats, String confirmationToken) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("noreply@cfr.local");
        message.setTo(to);
        message.setSubject("Confirm your Train Reservation - CFR");
        message.setText("Hello " + firstName + ",\n\n" +
                "You have successfully made a reservation for train " + trainNumber + " (" + numberOfSeats + " seat(s)).\n\n" +
                "Please click the link below to confirm your reservation:\n\n" +
                "http://localhost:3000/confirm-reservation?token=" + confirmationToken + "\n\n" +
                "If you did not make this reservation, please ignore this email.\n\n" +
                "Best regards,\nCFR Team");

        mailSender.send(message);
    }

    public void sendReservationConfirmedEmail(String to, String firstName, String trainNumber, String departureStation, String arrivalStation, String departureTime) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("noreply@cfr.local");
        message.setTo(to);
        message.setSubject("Reservation Confirmed - CFR");
        message.setText("Hello " + firstName + ",\n\n" +
                "Your reservation has been confirmed!\n\n" +
                "Train: " + trainNumber + "\n" +
                "Route: " + departureStation + " -> " + arrivalStation + "\n" +
                "Departure Time: " + departureTime + "\n\n" +
                "Please arrive at the station at least 15 minutes before departure.\n\n" +
                "Best regards,\nCFR Team");

        mailSender.send(message);
    }
}

