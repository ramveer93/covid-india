package com.covid.tracker;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.covid.tracker.model.User;
import com.covid.tracker.repository.UserRepository;

@SpringBootApplication
@EnableScheduling
@Configuration
public class CovidTrackerApplication {
	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(CovidTrackerApplication.class, args);
	}
	
	@PostConstruct
	public void initUsers() {
		User adminUser = new User();
		adminUser.setUserName("ramveerAdmin932");
		adminUser.setPassword("Access!@34India");
		User serviceUser = new User();
		serviceUser.setUserName("service9021Ind");
		serviceUser.setPassword("Access!@34Pak");
		List<User> users = new ArrayList<>();
		users.add(serviceUser);
		users.add(adminUser);
		userRepository.saveAll(users);
	}

}
