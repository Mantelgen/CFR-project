package com.cfr.networkapp.controller;

import com.cfr.networkapp.model.Station;
import com.cfr.networkapp.repository.StationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    private final StationRepository stationRepository;

    public StationController(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @GetMapping
    public ResponseEntity<List<String>> getStations(@RequestParam(required = false, defaultValue = "") String q) {
        String query = q.trim();
        List<Station> stations = query.isEmpty()
            ? stationRepository.findAll()
            : stationRepository.findByNameContainingIgnoreCaseOrderByNameAsc(query);

        Set<String> names = new LinkedHashSet<>();
        for (Station station : stations) {
            if (station.getName() != null && !station.getName().isBlank()) {
                names.add(station.getName().trim());
            }
        }

        return ResponseEntity.ok(List.copyOf(names));
    }
}
