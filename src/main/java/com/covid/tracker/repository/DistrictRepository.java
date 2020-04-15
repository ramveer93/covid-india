package com.covid.tracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.covid.tracker.model.DistrictList;

public interface DistrictRepository extends JpaRepository<DistrictList, Long> {

	Optional<DistrictList> getById(Long id);
}
