package com.cfr.networkapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "trains")
public class Train {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="train_number", nullable = false)
    private String trainNumber;

    @Column(name="departure_city")
    private String departureCity;

    @Column(name="arrival_city")
    private String arrivalCity;

    public Train(){

    }

    public Train(String trainNumber, String departureCity, String arrivalCity) {
        this.trainNumber = trainNumber;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }

    public String getArrivalCity() {
        return arrivalCity;
    }

    public void setArrivalCity(String arrivalCity) {
        this.arrivalCity = arrivalCity;
    }
}
