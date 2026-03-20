package com.cfr.networkapp.controller;

import com.cfr.networkapp.model.Train;
import com.cfr.networkapp.model.Station;
import com.cfr.networkapp.service.ReservationService;
import com.cfr.networkapp.service.TrainService;
import com.cfr.networkapp.repository.StationRepository;
import com.cfr.networkapp.dto.TrainDetailsDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trains")
public class TrainController {

    private final TrainService trainService;
    private final StationRepository stationRepository;
    private final ReservationService reservationService;

    public TrainController(TrainService trainService, StationRepository stationRepository, ReservationService reservationService) {
        this.trainService = trainService;
        this.stationRepository = stationRepository;
        this.reservationService = reservationService;
    }

    @GetMapping("/search")
    public List<Train> search(
            @RequestParam String from,
            @RequestParam String to
    ) {
        return trainService.searchTrains(from, to);
    }

    @GetMapping
    public List<Train> getAllTrains() {
        return trainService.getAllTrains();
    }

        @GetMapping("/{id}")
        public ResponseEntity<TrainDetailsDTO> getTrainById(@PathVariable Long id) {
        Optional<Train> trainOpt = trainService.getTrainById(id);
        if (trainOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Train train = trainOpt.get();
        String departureStationName = stationRepository.findById(train.getDepartureStationId())
            .map(Station::getName).orElse("Unknown");
        String arrivalStationName = stationRepository.findById(train.getArrivalStationId())
            .map(Station::getName).orElse("Unknown");
        int totalSeats = reservationService.getTotalSeatsForTrain(train);
        int availableSeats = totalSeats - reservationService.getBookedSeatNumbers(train.getId()).size();
        TrainDetailsDTO dto = new TrainDetailsDTO(
            train.getId(),
            train.getTrainNumber(),
            departureStationName,
            arrivalStationName,
            train.getDepartureTime() != null ? train.getDepartureTime().toString() : null,
            train.getArrivalTime() != null ? train.getArrivalTime().toString() : null,
            train.getClass1Carriages(),
            train.getClass2Carriages(),
            train.getSeatsPerCarriage(),
            totalSeats,
            Math.max(availableSeats, 0),
            parseRouteStations(train.getRouteStations(), departureStationName, arrivalStationName),
            parseFacilities(train.getFacilities())
        );
        return ResponseEntity.ok(dto);
        }

    private List<String> parseRouteStations(String routeStations, String departureStationName, String arrivalStationName) {
        List<String> parsed = new ArrayList<>();
        if (routeStations != null && !routeStations.isBlank()) {
            parsed = Arrays.stream(routeStations.split(","))
                .map(String::trim)
                .filter(name -> !name.isBlank())
                .collect(Collectors.toCollection(ArrayList::new));
        }

        if (parsed.isEmpty()) {
            parsed.add(departureStationName);
            parsed.add(arrivalStationName);
            return parsed;
        }

        if (!parsed.get(0).equalsIgnoreCase(departureStationName)) {
            parsed.add(0, departureStationName);
        }
        String last = parsed.get(parsed.size() - 1);
        if (!last.equalsIgnoreCase(arrivalStationName)) {
            parsed.add(arrivalStationName);
        }
        return parsed;
    }

    private List<String> parseFacilities(String facilities) {
        if (facilities == null || facilities.isBlank()) {
            return List.of();
        }

        return Arrays.stream(facilities.split(","))
                .map(String::trim)
                .filter(name -> !name.isBlank())
                .toList();
    }

    @PostMapping
    public ResponseEntity<Train> createTrain(@RequestBody Train train) {
        try {
            Train createdTrain = trainService.createTrain(train);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTrain);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Train> updateTrain(
            @PathVariable Long id,
            @RequestBody Train trainDetails
    ) {
        try {
            Train updatedTrain = trainService.updateTrain(id, trainDetails);
            return ResponseEntity.ok(updatedTrain);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrain(@PathVariable Long id) {
        try {
            trainService.deleteTrain(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}