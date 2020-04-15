package com.covid.tracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.covid.tracker.model.ZingChartData;

public interface ZingChartRepository extends JpaRepository<ZingChartData, Long>{
	Optional<ZingChartData> findById(long id);

	
}
