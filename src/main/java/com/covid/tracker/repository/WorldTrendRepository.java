package com.covid.tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.covid.tracker.model.WorldCountryTrend;

public interface WorldTrendRepository extends JpaRepository<WorldCountryTrend, Long> {
	@Query(value = "select * from world_country_trend where id = :id order by date asc", nativeQuery = true)
	List<WorldCountryTrend> getLatestFifteenForCountry(@Param("id")String id);

}
