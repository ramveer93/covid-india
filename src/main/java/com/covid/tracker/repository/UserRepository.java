package com.covid.tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.covid.tracker.model.User;

public interface UserRepository extends JpaRepository<User, Integer>{

	@Query(value = "select * from user_detail where user_name = :username" , nativeQuery = true)
	User findByUserName(@Param("username")String username);

}
