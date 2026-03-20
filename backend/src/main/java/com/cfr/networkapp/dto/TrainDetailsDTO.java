package com.cfr.networkapp.dto;

import java.util.List;

public class TrainDetailsDTO {
    private Long id;
    private String trainNumber;
    private String departureStationName;
    private String arrivalStationName;
    private String departureTime;
    private String arrivalTime;
    private Integer class1Carriages;
    private Integer class2Carriages;
    private Integer totalCarriages;
    private Integer seatsPerCarriage;
    private Integer totalSeats;
    private Integer availableSeats;
    private List<String> routeStations;
    private List<String> facilities;

    public TrainDetailsDTO() {}

    public TrainDetailsDTO(Long id, String trainNumber, String departureStationName, String arrivalStationName, String departureTime, String arrivalTime, Integer class1Carriages, Integer class2Carriages, Integer seatsPerCarriage, Integer totalSeats, Integer availableSeats, List<String> routeStations, List<String> facilities) {
        this.id = id;
        this.trainNumber = trainNumber;
        this.departureStationName = departureStationName;
        this.arrivalStationName = arrivalStationName;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.class1Carriages = class1Carriages != null ? class1Carriages : 0;
        this.class2Carriages = class2Carriages != null ? class2Carriages : 0;
        this.totalCarriages = this.class1Carriages + this.class2Carriages;
        this.seatsPerCarriage = seatsPerCarriage;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.routeStations = routeStations;
        this.facilities = facilities;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTrainNumber() { return trainNumber; }
    public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }
    public String getDepartureStationName() { return departureStationName; }
    public void setDepartureStationName(String departureStationName) { this.departureStationName = departureStationName; }
    public String getArrivalStationName() { return arrivalStationName; }
    public void setArrivalStationName(String arrivalStationName) { this.arrivalStationName = arrivalStationName; }
    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }
    public Integer getClass1Carriages() { return class1Carriages; }
    public void setClass1Carriages(Integer class1Carriages) { this.class1Carriages = class1Carriages; }
    public Integer getClass2Carriages() { return class2Carriages; }
    public void setClass2Carriages(Integer class2Carriages) { this.class2Carriages = class2Carriages; }
    public Integer getTotalCarriages() { return totalCarriages; }
    public void setTotalCarriages(Integer totalCarriages) { this.totalCarriages = totalCarriages; }
    public Integer getSeatsPerCarriage() { return seatsPerCarriage; }
    public void setSeatsPerCarriage(Integer seatsPerCarriage) { this.seatsPerCarriage = seatsPerCarriage; }
    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }
    public Integer getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(Integer availableSeats) { this.availableSeats = availableSeats; }
    public List<String> getRouteStations() { return routeStations; }
    public void setRouteStations(List<String> routeStations) { this.routeStations = routeStations; }
    public List<String> getFacilities() { return facilities; }
    public void setFacilities(List<String> facilities) { this.facilities = facilities; }
}
