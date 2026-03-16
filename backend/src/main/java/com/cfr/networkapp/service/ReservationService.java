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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReservationService {

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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new RuntimeException("Train not found"));

        Reservation reservation = new Reservation(user, train, numberOfSeats);
        reservation.setConfirmationToken(UUID.randomUUID().toString());
        reservation.setStatus("PENDING");

        Reservation saved = reservationRepository.save(reservation);

        // Send confirmation email
        mailService.sendReservationConfirmationEmail(
            user.getEmail(),
            user.getFirstName(),
            train.getTrainNumber(),
            numberOfSeats,
            saved.getConfirmationToken()
        );

        return saved;
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

        // Send confirmed email
        Train train = saved.getTrain();
        User user = saved.getUser();
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
                train.getDepartureTime().toString()
            );
        }

        return saved;
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

