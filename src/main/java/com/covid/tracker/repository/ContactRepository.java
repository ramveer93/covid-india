package com.covid.tracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.covid.tracker.model.ContactInfo;

public interface ContactRepository extends JpaRepository<ContactInfo, Long> {
	Optional<ContactInfo> getById(long id);

}
