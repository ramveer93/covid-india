package com.covid.tracker.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.covid.tracker.model.NewsFeedData;

public interface NewsFeedDataRepository extends JpaRepository<NewsFeedData, Long>{
	Optional<NewsFeedData> findById(long id);
	
	@Query(value = "select * from news_feed_data order by updated_on desc limit 10",nativeQuery = true)
	List<NewsFeedData> findTopTenLatest();

}
