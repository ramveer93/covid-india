package com.covid.tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.covid.tracker.model.StateWiseCases;
import com.covid.tracker.model.WorldData;

public interface WorldGenericRepository extends JpaRepository<WorldData, Long> {

	@Query(value = "select * from world_data where id = :id", nativeQuery = true)
	List<WorldData> findLatestForCountry(@Param("id") String id);

	@Query(value = "select * from world_data where id!='earth' order by confirmed desc limit 10", nativeQuery = true)
	List<WorldData> getTopTenCountriesBasedOnConfirmed();

	@Query(value = "select * from world_data where id!='earth' order by recovered desc limit 10", nativeQuery = true)
	List<WorldData> getTopTenCountriesBasedOnRecovered();

	@Query(value = "select * from world_data where id!='earth' order by deaths desc limit 10", nativeQuery = true)
	List<WorldData> getTopTenCountriesBasedOnDeaths();

	@Query(value = "select country_name from world_data where id!='earth' order by  country_name asc", nativeQuery = true)
	List<String> getCountryList();
	
	@Query(value = "select id from world_data where country_name = :country_name",nativeQuery = true)
	String getCountryId(@Param("country_name") String country_name);
	
	@Query(value = "select id from world_data where id!='earth' order by  country_name asc", nativeQuery = true)
	List<String> getCountryIdsList();
	
	

}
