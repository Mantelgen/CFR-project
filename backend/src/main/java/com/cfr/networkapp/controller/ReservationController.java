package com.cfr.networkapp.controller;

import com.cfr.networkapp.model.Reservation;
import com.cfr.networkapp.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.cfr.networkapp.model.User;
import com.cfr.networkapp.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserService userService;

    @PostMapping("/book")
    public ResponseEntity<?> bookReservation(@RequestBody Map<String, Object> request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long userId = resolveAuthenticatedUserId(auth);
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }
            Long trainId = Long.parseLong(request.get("trainId").toString());
            Integer numberOfSeats = Integer.parseInt(request.get("numberOfSeats").toString());
            List<Integer> selectedSeatNumbers = parseSelectedSeatNumbers(request.get("selectedSeatNumbers"));

            Reservation reservation = reservationService.createReservation(userId, trainId, numberOfSeats, selectedSeatNumbers);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Reservation created. Please complete payment to confirm booking.");
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
    public ResponseEntity<?> getMyReservations(@RequestParam(required = false) Long userId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long resolvedUserId = resolveAuthenticatedUserId(auth);
            if (resolvedUserId == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }

            List<Reservation> reservations = reservationService.getUserReservations(resolvedUserId);

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

    @PostMapping("/pay")
    public ResponseEntity<?> confirmPayment(@RequestBody Map<String, Object> request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long userId = resolveAuthenticatedUserId(auth);
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }

            Long reservationId = Long.parseLong(request.get("reservationId").toString());
            Reservation existing = reservationService.getReservationById(reservationId);
            if (existing.getUser() == null || !userId.equals(existing.getUser().getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Reservation does not belong to current user"));
            }
            Reservation reservation = reservationService.confirmPayment(reservationId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment confirmed. Your booking confirmation email has been sent.");
            response.put("reservationId", reservation.getId());
            response.put("status", reservation.getStatus());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long resolvedUserId = resolveAuthenticatedUserId(auth);
            if (resolvedUserId == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }

            Reservation existing = reservationService.getReservationById(id);
            if (existing.getUser() == null || !resolvedUserId.equals(existing.getUser().getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Reservation does not belong to current user"));
            }

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

    private Long resolveAuthenticatedUserId(Authentication auth) {
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof User user) {
            return user.getId();
        }
        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName();
            if (username != null && !username.isBlank() && !"anonymousUser".equalsIgnoreCase(username)) {
                return userService.findByUsername(username).map(User::getId).orElse(null);
            }
        }
        return null;
    }

    private List<Integer> parseSelectedSeatNumbers(Object selectedSeatNumbersPayload) {
        if (!(selectedSeatNumbersPayload instanceof List<?> listPayload)) {
            return null;
        }

        List<Integer> selectedSeatNumbers = new ArrayList<>();
        for (Object value : listPayload) {
            if (value == null) {
                continue;
            }
            selectedSeatNumbers.add(Integer.parseInt(value.toString()));
        }
        return selectedSeatNumbers;
    }
}
