package com.covid.tracker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.covid.tracker.model.DistrictWiseCasesVo;

public interface DistrictWiseCasesRepository extends JpaRepository<DistrictWiseCasesVo, Long> {
	Optional<DistrictWiseCasesVo> getById(Long id);

	@Query(value = "select * from district_wise_cases order by positive_cases desc limit 10", nativeQuery = true)
	List<DistrictWiseCasesVo> getTopTenActiveDistricts();
	
//	List<DistrictWiseCasesVo> findAll();

}
