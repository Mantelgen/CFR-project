package com.cfr.networkapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.public-base-url:http://cfr.local}")
    private String publicBaseUrl;

    @Value("${app.complaints.recipient:complaints@cfr.local}")
    private String complaintsRecipient;

    private String buildPublicUrl(String path, String token) {
        String normalizedBase = publicBaseUrl.endsWith("/")
                ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1)
                : publicBaseUrl;
        return normalizedBase + path + "?token=" + token;
    }

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
        String confirmationUrl = buildPublicUrl("/confirm-email", confirmationToken);

        message.setFrom("noreply@cfr.local");
        message.setTo(to);
        message.setSubject("Confirm your CFR Account");
        message.setText("Hello " + firstName + ",\n\n" +
                "Thank you for registering with CFR. Please click the link below to confirm your email address:\n\n" +
            confirmationUrl + "\n\n" +
                "If you did not register, please ignore this email.\n\n" +
                "Best regards,\nCFR Team");

        mailSender.send(message);
    }

    public void sendReservationConfirmationEmail(String to, String firstName, String trainNumber, Integer numberOfSeats, String confirmationToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        String confirmationUrl = buildPublicUrl("/confirm-reservation", confirmationToken);

        message.setFrom("noreply@cfr.local");
        message.setTo(to);
        message.setSubject("Confirm your Train Reservation - CFR");
        message.setText("Hello " + firstName + ",\n\n" +
                "You have successfully made a reservation for train " + trainNumber + " (" + numberOfSeats + " seat(s)).\n\n" +
                "Please click the link below to confirm your reservation:\n\n" +
            confirmationUrl + "\n\n" +
                "If you did not make this reservation, please ignore this email.\n\n" +
                "Best regards,\nCFR Team");

        mailSender.send(message);
    }

    public void sendReservationConfirmedEmail(String to, String firstName, String trainNumber, String departureStation, String arrivalStation, String departureTime, String selectedSeatNumbers) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("noreply@cfr.local");
        message.setTo(to);
        message.setSubject("Reservation Confirmed - CFR");
        message.setText("Hello " + firstName + ",\n\n" +
                "Your reservation has been confirmed!\n\n" +
                "Train: " + trainNumber + "\n" +
                "Route: " + departureStation + " -> " + arrivalStation + "\n" +
                "Departure Time: " + departureTime + "\n" +
                "Seat Numbers: " + (selectedSeatNumbers == null || selectedSeatNumbers.isBlank() ? "Will be assigned at station" : selectedSeatNumbers) + "\n\n" +
                "Please arrive at the station at least 15 minutes before departure.\n\n" +
                "Best regards,\nCFR Team");

        mailSender.send(message);
    }

    public void sendComplaintEmail(String userEmail, String username, String subject, String messageText, Long reservationId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@cfr.local");
        message.setTo(complaintsRecipient);
        message.setReplyTo(userEmail);
        message.setSubject("[CFR Complaint] " + subject);
        message.setText(
                "Complaint submitted from CFR application\n\n" +
                "User: " + username + "\n" +
                "User Email: " + userEmail + "\n" +
                "Reservation ID: " + (reservationId == null ? "N/A" : reservationId) + "\n\n" +
                "Message:\n" + messageText
        );
        mailSender.send(message);
    }

    public void sendComplaintReceivedEmail(String to, String firstName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@cfr.local");
        message.setTo(to);
        message.setSubject("We received your complaint - CFR");
        message.setText(
                "Hello " + firstName + ",\n\n" +
                "Your complaint was received by CFR support. We will review it and respond shortly.\n\n" +
                "Best regards,\nCFR Team"
        );
        mailSender.send(message);
    }
}

