package com.cfr.networkapp.controller;

import com.cfr.networkapp.model.Train;
import com.cfr.networkapp.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trains")
@CrossOrigin(origins = "*") // Allows your frontend to communicate with this API
public class TrainController {

    @Autowired
    private TrainRepository trainRepository;

    // Get all trains
    @GetMapping
    public List<Train> getAllTrains() {
        return trainRepository.findAll();
    }

    // Create a new train
    @PostMapping
    public Train createTrain(@RequestBody Train train) {
        return trainRepository.save(train);
    }

    // Delete a train
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrain(@PathVariable Long id) {
        if (trainRepository.existsById(id)) {
            trainRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}