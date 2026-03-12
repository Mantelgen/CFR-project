package com.cfr.networkapp.controller;

import com.cfr.networkapp.model.Train;
import com.cfr.networkapp.repository.TrainRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trains")
@CrossOrigin(origins = "*") // Allows your frontend to communicate with this API
public class TrainController {

    private static final Logger logger = LoggerFactory.getLogger(TrainController.class);

    @Autowired
    private TrainRepository trainRepository;

    // Get all trains
    @GetMapping
    public List<Train> getAllTrains() {
        logger.info("Fetching all trains");
        List<Train> trains = trainRepository.findAll();
        logger.info("Retrieved {} trains", trains.size());
        return trains;
    }

    // Create a new train
    @PostMapping
    public Train createTrain(@RequestBody Train train) {
        logger.info("Creating new train: {}", train);
        Train savedTrain = trainRepository.save(train);
        logger.info("Train created successfully with ID: {}", savedTrain.getId());
        return savedTrain;
    }

    // Delete a train
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrain(@PathVariable Long id) {
        logger.info("Attempting to delete train with ID: {}", id);
        if (trainRepository.existsById(id)) {
            trainRepository.deleteById(id);
            logger.info("Train with ID {} deleted successfully", id);
            return ResponseEntity.ok().build();
        }
        logger.warn("Train with ID {} not found for deletion", id);
        return ResponseEntity.notFound().build();
    }
}