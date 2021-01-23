package com.covid.tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.covid.tracker.model.IndiaStateTrend;

public interface IndiaStateTrendRepository extends JpaRepository<IndiaStateTrend, Long> {
	@Query(value = "select * from india_state_trend where state_name = :state_name order by date asc limit 20", nativeQuery = true)
	List<IndiaStateTrend> getLatestTwentyForCountry(@Param("state_name") String state_name);


	
	
}
