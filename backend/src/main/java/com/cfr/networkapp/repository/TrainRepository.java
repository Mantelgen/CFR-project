package com.cfr.networkapp.repository;

import com.cfr.networkapp.model.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainRepository extends JpaRepository<Train, Long> {
    // Standard CRUD methods are inherited automatically
}