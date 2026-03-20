package com.cfr.networkapp.controller;

import com.cfr.networkapp.dto.ComplaintRequestDTO;
import com.cfr.networkapp.model.User;
import com.cfr.networkapp.service.MailService;
import com.cfr.networkapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final MailService mailService;
    private final UserService userService;

    public ComplaintController(MailService mailService, UserService userService) {
        this.mailService = mailService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> submitComplaint(@RequestBody ComplaintRequestDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = resolveAuthenticatedUser(authentication);
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
        }

        if (request.getSubject() == null || request.getSubject().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Complaint subject is required"));
        }
        if (request.getMessage() == null || request.getMessage().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Complaint message is required"));
        }

        String displayName = user.getFirstName() != null && !user.getFirstName().isBlank()
                ? user.getFirstName()
                : user.getUsername();

        mailService.sendComplaintEmail(
                user.getEmail(),
                user.getUsername(),
                request.getSubject().trim(),
                request.getMessage().trim(),
                request.getReservationId()
        );
        mailService.sendComplaintReceivedEmail(user.getEmail(), displayName);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Complaint submitted successfully"
        ));
    }

    private User resolveAuthenticatedUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof User user) {
            return user;
        }

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            if (username != null && !username.isBlank() && !"anonymousUser".equalsIgnoreCase(username)) {
                return userService.findByUsername(username).orElse(null);
            }
        }

        return null;
    }
}
