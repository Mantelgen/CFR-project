package com.cfr.networkapp.repository;

import com.cfr.networkapp.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {
	List<Station> findByNameContainingIgnoreCaseOrderByNameAsc(String query);

}