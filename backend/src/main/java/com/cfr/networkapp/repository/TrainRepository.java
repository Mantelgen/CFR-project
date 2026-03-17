package com.cfr.networkapp.repository;

import com.cfr.networkapp.model.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrainRepository extends JpaRepository<Train, Long> {

    @Query("""
    SELECT t FROM Train t
    JOIN Station s1 ON s1.id = t.departureStationId
    JOIN Station s2 ON s2.id = t.arrivalStationId
    WHERE s1.name = :from
    AND s2.name = :to
    """)
    List<Train> findTrains(String from, String to);

}