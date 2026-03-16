package com.cfr.networkapp.controller;

import com.cfr.networkapp.model.Reservation;
import com.cfr.networkapp.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.cfr.networkapp.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PostMapping("/book")
    public ResponseEntity<?> bookReservation(@RequestBody Map<String, Object> request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }

            User user = (User) auth.getPrincipal();
            Long trainId = Long.parseLong(request.get("trainId").toString());
            Integer numberOfSeats = Integer.parseInt(request.get("numberOfSeats").toString());

            Reservation reservation = reservationService.createReservation(user.getId(), trainId, numberOfSeats);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Reservation created. Please check your email to confirm.");
            response.put("reservationId", reservation.getId());
            response.put("confirmationToken", reservation.getConfirmationToken());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/my-reservations")
    public ResponseEntity<?> getMyReservations() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }

            User user = (User) auth.getPrincipal();
            List<Reservation> reservations = reservationService.getUserReservations(user.getId());

            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmReservation(@RequestParam String token) {
        try {
            Reservation reservation = reservationService.confirmReservation(token);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Reservation confirmed successfully");
            response.put("reservationId", reservation.getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id) {
        try {
            reservationService.cancelReservation(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Reservation cancelled successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
