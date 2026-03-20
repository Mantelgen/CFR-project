package com.cfr.networkapp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @Column(name = "number_of_seats")
    private Integer numberOfSeats;

    @Column(name = "reservation_date")
    private LocalDateTime reservationDate;

    @Column(name = "status")
    private String status = "PENDING"; // PENDING, CONFIRMED, CANCELLED

    @Column(name = "confirmation_token")
    private String confirmationToken;

    @Column(name = "is_confirmed")
    private Boolean isConfirmed = false;

    @Column(name = "selected_seat_numbers")
    private String selectedSeatNumbers;

    public Reservation() {}

    public Reservation(User user, Train train, Integer numberOfSeats) {
        this.user = user;
        this.train = train;
        this.numberOfSeats = numberOfSeats;
        this.reservationDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public Integer getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(Integer numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public Boolean getIsConfirmed() {
        return isConfirmed;
    }

    public void setIsConfirmed(Boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    public String getSelectedSeatNumbers() {
        return selectedSeatNumbers;
    }

    public void setSelectedSeatNumbers(String selectedSeatNumbers) {
        this.selectedSeatNumbers = selectedSeatNumbers;
    }
}
