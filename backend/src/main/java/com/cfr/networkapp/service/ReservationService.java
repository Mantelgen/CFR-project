package com.cfr.networkapp.service;

import com.cfr.networkapp.model.Reservation;
import com.cfr.networkapp.model.Station;
import com.cfr.networkapp.model.Train;
import com.cfr.networkapp.model.User;
import com.cfr.networkapp.repository.ReservationRepository;
import com.cfr.networkapp.repository.TrainRepository;
import com.cfr.networkapp.repository.UserRepository;
import com.cfr.networkapp.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private static final int DEFAULT_SEATS_PER_CARRIAGE = 60;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrainRepository trainRepository;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private MailService mailService;

    public Reservation createReservation(Long userId, Long trainId, Integer numberOfSeats) {
        return createReservation(userId, trainId, numberOfSeats, null);
    }

    public Reservation createReservation(Long userId, Long trainId, Integer numberOfSeats, List<Integer> selectedSeatNumbers) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new RuntimeException("Train not found"));

        if (numberOfSeats == null || numberOfSeats <= 0) {
            throw new RuntimeException("Number of seats must be greater than 0");
        }

        List<Integer> finalSeatSelection = normalizeSeatSelection(train, numberOfSeats, selectedSeatNumbers);

        Reservation reservation = new Reservation(user, train, numberOfSeats);
        reservation.setConfirmationToken(UUID.randomUUID().toString());
        reservation.setStatus("AWAITING_PAYMENT");
        reservation.setSelectedSeatNumbers(finalSeatSelection.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")));

        return reservationRepository.save(reservation);
    }

    public List<Reservation> getUserReservations(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    public Reservation confirmReservation(String token) {
        Optional<Reservation> reservationOpt = reservationRepository.findByConfirmationToken(token);

        if (reservationOpt.isEmpty()) {
            throw new RuntimeException("Invalid confirmation token");
        }

        Reservation reservation = reservationOpt.get();
        reservation.setIsConfirmed(true);
        reservation.setStatus("CONFIRMED");
        reservation.setConfirmationToken(null);

        Reservation saved = reservationRepository.save(reservation);

        sendConfirmedEmail(saved);

        return saved;
    }

    public Reservation confirmPayment(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if ("CANCELLED".equalsIgnoreCase(reservation.getStatus())) {
            throw new RuntimeException("Cannot pay for a cancelled reservation");
        }

        reservation.setIsConfirmed(true);
        reservation.setStatus("CONFIRMED");
        reservation.setConfirmationToken(null);

        Reservation saved = reservationRepository.save(reservation);
        sendConfirmedEmail(saved);

        return saved;
    }

    private void sendConfirmedEmail(Reservation reservation) {
        Train train = reservation.getTrain();
        User user = reservation.getUser();
        Station departureStation = stationRepository.findById(train.getDepartureStationId())
                .orElse(null);
        Station arrivalStation = stationRepository.findById(train.getArrivalStationId())
                .orElse(null);

        if (departureStation != null && arrivalStation != null) {
            mailService.sendReservationConfirmedEmail(
                    user.getEmail(),
                    user.getFirstName(),
                    train.getTrainNumber(),
                    departureStation.getName(),
                    arrivalStation.getName(),
                    train.getDepartureTime().toString(),
                    reservation.getSelectedSeatNumbers()
            );
        }
    }

    public Integer getTotalSeatsForTrain(Train train) {
        int class1 = train.getClass1Carriages() != null ? train.getClass1Carriages() : 0;
        int class2 = train.getClass2Carriages() != null ? train.getClass2Carriages() : 0;
        int seatsPerCarriage = train.getSeatsPerCarriage() != null && train.getSeatsPerCarriage() > 0
                ? train.getSeatsPerCarriage()
                : DEFAULT_SEATS_PER_CARRIAGE;
        return (class1 + class2) * seatsPerCarriage;
    }

    public Set<Integer> getBookedSeatNumbers(Long trainId) {
        List<Reservation> activeReservations = reservationRepository.findByTrainIdAndStatusNot(trainId, "CANCELLED");
        Set<Integer> booked = new HashSet<>();

        for (Reservation reservation : activeReservations) {
            String selectedSeatNumbers = reservation.getSelectedSeatNumbers();
            if (selectedSeatNumbers == null || selectedSeatNumbers.isBlank()) {
                continue;
            }

            String[] seatTokens = selectedSeatNumbers.split(",");
            for (String seatToken : seatTokens) {
                try {
                    booked.add(Integer.parseInt(seatToken.trim()));
                } catch (NumberFormatException ignored) {
                    // Ignore malformed legacy seat values.
                }
            }
        }

        return booked;
    }

    private List<Integer> normalizeSeatSelection(Train train, Integer numberOfSeats, List<Integer> selectedSeatNumbers) {
        int totalSeats = getTotalSeatsForTrain(train);
        if (totalSeats <= 0) {
            throw new RuntimeException("This train has no available seats configured");
        }

        Set<Integer> bookedSeatNumbers = getBookedSeatNumbers(train.getId());

        List<Integer> normalizedSelection;
        if (selectedSeatNumbers == null || selectedSeatNumbers.isEmpty()) {
            normalizedSelection = new ArrayList<>();
            for (int seat = 1; seat <= totalSeats && normalizedSelection.size() < numberOfSeats; seat++) {
                if (!bookedSeatNumbers.contains(seat)) {
                    normalizedSelection.add(seat);
                }
            }
            if (normalizedSelection.size() != numberOfSeats) {
                throw new RuntimeException("Not enough free seats for this train");
            }
            return normalizedSelection;
        }

        normalizedSelection = selectedSeatNumbers.stream()
                .filter(seat -> seat != null)
                .distinct()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

        if (normalizedSelection.size() != numberOfSeats) {
            throw new RuntimeException("Selected seats must match the requested number of seats");
        }

        for (Integer seatNumber : normalizedSelection) {
            if (seatNumber < 1 || seatNumber > totalSeats) {
                throw new RuntimeException("Seat " + seatNumber + " is outside valid range 1-" + totalSeats);
            }
            if (bookedSeatNumbers.contains(seatNumber)) {
                throw new RuntimeException("Seat " + seatNumber + " is already booked");
            }
        }

        return normalizedSelection;
    }

    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
    }

    public void cancelReservation(Long id) {
        Reservation reservation = getReservationById(id);
        reservation.setStatus("CANCELLED");
        reservationRepository.save(reservation);
    }
}

