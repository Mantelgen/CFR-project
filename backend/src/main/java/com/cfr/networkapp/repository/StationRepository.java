package com.cfr.networkapp.repository;

import com.cfr.networkapp.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Long> {

}