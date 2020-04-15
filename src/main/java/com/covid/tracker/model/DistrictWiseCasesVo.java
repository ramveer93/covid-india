package com.covid.tracker.model;

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
@Table(name = "district_wise_cases")
public class DistrictWiseCasesVo {
	@EmbeddedId
	private PrimaryKeyForDistrictWiseCases primaryKey;
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@Column(name = "positiveCases")
	private int positiveCases;
	@Column(name = "updatedOn", columnDefinition= "TIMESTAMP WITH TIME ZONE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;
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
	public int getPositiveCases() {
		return positiveCases;
	}
	public void setPositiveCases(int positiveCases) {
		this.positiveCases = positiveCases;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	@Override
	public String toString() {
		return "DistrictWiseCasesVo [primaryKey=" + primaryKey + ", id=" + id + ", positiveCases=" + positiveCases
				+ ", createdOn=" + createdOn + "]";
	}
	
	

}
