package com.cfr.networkapp.controller;

import com.cfr.networkapp.model.Train;
import com.cfr.networkapp.repository.TrainRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trains")
public class TrainController {

    private final TrainRepository trainRepository;

    public TrainController(TrainRepository trainRepository) {
        this.trainRepository = trainRepository;
    }

    @GetMapping("/search")
    public List<Train> search(
            @RequestParam String from,
            @RequestParam String to
    ) {

        return trainRepository.findTrains(from, to);

    }

}