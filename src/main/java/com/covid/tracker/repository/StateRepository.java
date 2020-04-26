package com.covid.tracker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.covid.tracker.model.StateWiseCases;

public interface StateRepository extends JpaRepository<StateWiseCases, Long> {

	Optional<StateWiseCases> findById(Long id);

	@Query(value = "select * from state_wise_data order by active_cases desc limit 10", nativeQuery = true)
	List<StateWiseCases> getTopTenBasedOnActive();

	@Query(value = "select * from state_wise_data order by cured_count desc limit 10", nativeQuery = true)
	List<StateWiseCases> getTopTenBasedOnCured();

	@Query(value = "select * from state_wise_data order by death_count desc limit 10", nativeQuery = true)
	List<StateWiseCases> getTopTenBasedOnDeaths();
	
	@Query(value = "select sd.state_name as stateName,sd.active_cases as activeCases,sd.total_count as totalCount,sd.cured_count as curedCount,sd.death_count as deathCount, dw.district_name as districtName,dw.positive_cases as districtPositiveCase from state_wise_data sd join district_wise_cases dw on sd.state_name = dw.state_name and dw.updated_on  = sd.updated_on order by sd.total_count desc",nativeQuery = true)
	List<Object> getJoinData();
	
	List<StateWiseCases> findAll();
	
	@Modifying
	@Transactional
	@Query(" update StateWiseCases sd set sd.phoneNumber = :phoneNumber where sd.stateName = :stateName")
	int updatePhoneNumber(@Param("phoneNumber") String phoneNumber, @Param("stateName") String stateName);

}
