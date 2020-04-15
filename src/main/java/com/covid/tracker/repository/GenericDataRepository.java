package com.covid.tracker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.covid.tracker.model.GenericData;

public interface GenericDataRepository extends JpaRepository<GenericData, Long> {

	Optional<GenericData> findById(long id);

	@Query(value = "select district_wise_url from generic_data order by id desc limit 1", nativeQuery = true)
	String findLatestDistrictPdfUrl();

	@Query(value = "select * from generic_data order by updated_on desc limit 1", nativeQuery = true)
	GenericData findLatestUpdatedDate();

	@Query(value = "select * from generic_data order by updated_on desc limit 10", nativeQuery = true)
	List<GenericData> findDailyTrendForLastTenDays();
	@Query(value = "select * from generic_data order by updated_on desc limit 2", nativeQuery = true)
	List<GenericData> findTwoLatest();
}
