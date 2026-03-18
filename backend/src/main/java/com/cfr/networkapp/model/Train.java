package com.cfr.networkapp.model;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "trains")
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "train_number")
    private String trainNumber;

    @Column(name = "departure_station_id")
    private Long departureStationId;

    @Column(name = "arrival_station_id")
    private Long arrivalStationId;

    @Column(name = "departure_time")
    private LocalTime departureTime;

    @Column(name = "arrival_time")
    private LocalTime arrivalTime;

    @Column(name = "class_1_carriages")
    private Integer class1Carriages = 0;

    @Column(name = "class_2_carriages")
    private Integer class2Carriages = 0;

    @Column(name = "route_stations")
    private String routeStations;

    public Train() {}

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

    public Long getDepartureStationId() {
        return departureStationId;
    }

    public void setDepartureStationId(Long departureStationId) {
        this.departureStationId = departureStationId;
    }

    public Long getArrivalStationId() {
        return arrivalStationId;
    }

    public void setArrivalStationId(Long arrivalStationId) {
        this.arrivalStationId = arrivalStationId;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Integer getClass1Carriages() {
        return class1Carriages;
    }

    public void setClass1Carriages(Integer class1Carriages) {
        this.class1Carriages = class1Carriages;
    }

    public Integer getClass2Carriages() {
        return class2Carriages;
    }

    public void setClass2Carriages(Integer class2Carriages) {
        this.class2Carriages = class2Carriages;
    }

    public String getRouteStations() {
        return routeStations;
    }

    public void setRouteStations(String routeStations) {
        this.routeStations = routeStations;
    }

}