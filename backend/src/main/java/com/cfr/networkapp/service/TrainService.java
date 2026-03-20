package com.cfr.networkapp.service;

import com.cfr.networkapp.model.Train;
import com.cfr.networkapp.repository.TrainRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TrainService {

    private final TrainRepository trainRepository;

    public TrainService(TrainRepository trainRepository) {
        this.trainRepository = trainRepository;
    }

    public List<Train> searchTrains(String from, String to) {
        return trainRepository.findTrains(from, to);
    }

    public List<Train> getAllTrains() {
        return trainRepository.findAll();
    }

    public Optional<Train> getTrainById(Long id) {
        return trainRepository.findById(id);
    }

    public Train createTrain(Train train) {
        if (train.getTrainNumber() == null || train.getTrainNumber().isEmpty()) {
            throw new IllegalArgumentException("Train number cannot be empty");
        }
        if (train.getDepartureStationId() == null || train.getArrivalStationId() == null) {
            throw new IllegalArgumentException("Departure and arrival station IDs are required");
        }
        if (train.getDepartureTime() == null || train.getArrivalTime() == null) {
            throw new IllegalArgumentException("Departure and arrival times are required");
        }
        if (train.getClass1Carriages() != null && train.getClass1Carriages() < 0) {
            throw new IllegalArgumentException("Class 1 carriages cannot be negative");
        }
        if (train.getClass2Carriages() != null && train.getClass2Carriages() < 0) {
            throw new IllegalArgumentException("Class 2 carriages cannot be negative");
        }
        if (train.getClass1Carriages() == null) {
            train.setClass1Carriages(0);
        }
        if (train.getClass2Carriages() == null) {
            train.setClass2Carriages(0);
        }
        if (train.getRouteStations() != null) {
            train.setRouteStations(train.getRouteStations().trim());
        }
        if (train.getSeatsPerCarriage() == null) {
            train.setSeatsPerCarriage(60);
        }
        if (train.getSeatsPerCarriage() <= 0) {
            throw new IllegalArgumentException("Seats per carriage must be greater than 0");
        }
        if (train.getFacilities() != null) {
            train.setFacilities(train.getFacilities().trim());
        }
        return trainRepository.save(train);
    }

    public Train updateTrain(Long id, Train trainDetails) {
        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Train not found with id: " + id));

        if (trainDetails.getTrainNumber() != null && !trainDetails.getTrainNumber().isEmpty()) {
            train.setTrainNumber(trainDetails.getTrainNumber());
        }
        if (trainDetails.getDepartureStationId() != null) {
            train.setDepartureStationId(trainDetails.getDepartureStationId());
        }
        if (trainDetails.getArrivalStationId() != null) {
            train.setArrivalStationId(trainDetails.getArrivalStationId());
        }
        if (trainDetails.getDepartureTime() != null) {
            train.setDepartureTime(trainDetails.getDepartureTime());
        }
        if (trainDetails.getArrivalTime() != null) {
            train.setArrivalTime(trainDetails.getArrivalTime());
        }
        if (trainDetails.getClass1Carriages() != null) {
            if (trainDetails.getClass1Carriages() < 0) {
                throw new IllegalArgumentException("Class 1 carriages cannot be negative");
            }
            train.setClass1Carriages(trainDetails.getClass1Carriages());
        }
        if (trainDetails.getClass2Carriages() != null) {
            if (trainDetails.getClass2Carriages() < 0) {
                throw new IllegalArgumentException("Class 2 carriages cannot be negative");
            }
            train.setClass2Carriages(trainDetails.getClass2Carriages());
        }
        if (trainDetails.getRouteStations() != null) {
            train.setRouteStations(trainDetails.getRouteStations().trim());
        }
        if (trainDetails.getSeatsPerCarriage() != null) {
            if (trainDetails.getSeatsPerCarriage() <= 0) {
                throw new IllegalArgumentException("Seats per carriage must be greater than 0");
            }
            train.setSeatsPerCarriage(trainDetails.getSeatsPerCarriage());
        }
        if (trainDetails.getFacilities() != null) {
            train.setFacilities(trainDetails.getFacilities().trim());
        }

        return trainRepository.save(train);
    }

    public void deleteTrain(Long id) {
        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Train not found with id: " + id));
        trainRepository.delete(train);
    }
}
