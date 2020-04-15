package com.covid.tracker.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "state_district_mapping")
public class DistrictList implements Serializable {
	private static final long serialVersionUID = -2343243243242432341L;

	@EmbeddedId
	private PrimaryKeyForDistrictWiseCases primaryKey;
	
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(name = "updatedOn", columnDefinition= "TIMESTAMP WITH TIME ZONE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn = new Date();

	public PrimaryKeyForDistrictWiseCases getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(PrimaryKeyForDistrictWiseCases primaryKey) {
		this.primaryKey = primaryKey;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	@Override
	public String toString() {
		return "DistrictList [primaryKey=" + primaryKey + ", id=" + id + ", createdOn=" + createdOn + "]";
	}
	
	
}
