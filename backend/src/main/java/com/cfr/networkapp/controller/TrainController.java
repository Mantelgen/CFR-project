package com.cfr.networkapp.controller;

import com.cfr.networkapp.model.Train;
import com.cfr.networkapp.model.Station;
import com.cfr.networkapp.service.TrainService;
import com.cfr.networkapp.repository.StationRepository;
import com.cfr.networkapp.dto.TrainDetailsDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/trains")
public class TrainController {

    private final TrainService trainService;
    private final StationRepository stationRepository;

    public TrainController(TrainService trainService, StationRepository stationRepository) {
        this.trainService = trainService;
        this.stationRepository = stationRepository;
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
        TrainDetailsDTO dto = new TrainDetailsDTO(
            train.getId(),
            train.getTrainNumber(),
            departureStationName,
            arrivalStationName,
            train.getDepartureTime() != null ? train.getDepartureTime().toString() : null,
            train.getArrivalTime() != null ? train.getArrivalTime().toString() : null
        );
        return ResponseEntity.ok(dto);
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