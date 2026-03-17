package com.cfr.networkapp.dto;

public class TrainDetailsDTO {
    private Long id;
    private String trainNumber;
    private String departureStationName;
    private String arrivalStationName;
    private String departureTime;
    private String arrivalTime;

    public TrainDetailsDTO() {}

    public TrainDetailsDTO(Long id, String trainNumber, String departureStationName, String arrivalStationName, String departureTime, String arrivalTime) {
        this.id = id;
        this.trainNumber = trainNumber;
        this.departureStationName = departureStationName;
        this.arrivalStationName = arrivalStationName;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
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
}
