package com.covid.tracker.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name  = "generic_data")
public class GenericData implements Serializable {
	
	private static final long serialVersionUID = -2343243243242432341L;

	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Id
	@Column(name = "updated_on", columnDefinition= "TIMESTAMP WITH TIME ZONE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedOn;
	@Column(name = "passenger_screened_on_airport")
	private int passengerScreenedOnAirport;
	@Column(name = "active_cases")
	private int activeCases;
	@Column(name = "cured")
	private int cured;
	@Column(name = "migrated")
	private int migrated;
	@Column(name = "deaths")
	private int deaths;
	@Column(name = "district_wise_url")
	private String districtWiseUrl;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Date getUpdatedOn() {
		return updatedOn;
	}
	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}
	public int getPassengerScreenedOnAirport() {
		return passengerScreenedOnAirport;
	}
	public void setPassengerScreenedOnAirport(int passengerScreenedOnAirport) {
		this.passengerScreenedOnAirport = passengerScreenedOnAirport;
	}
	public int getActiveCases() {
		return activeCases;
	}
	public void setActiveCases(int activeCases) {
		this.activeCases = activeCases;
	}
	public int getCured() {
		return cured;
	}
	public void setCured(int cured) {
		this.cured = cured;
	}
	public int getMigrated() {
		return migrated;
	}
	public void setMigrated(int migrated) {
		this.migrated = migrated;
	}
	public int getDeaths() {
		return deaths;
	}
	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}
	public String getDistrictWiseUrl() {
		return districtWiseUrl;
	}
	public void setDistrictWiseUrl(String districtWiseUrl) {
		this.districtWiseUrl = districtWiseUrl;
	}
	@Override
	public String toString() {
		return "GenericData [id=" + id + ", updatedOn=" + updatedOn + ", passengerScreenedOnAirport="
				+ passengerScreenedOnAirport + ", activeCases=" + activeCases + ", cured=" + cured + ", migrated="
				+ migrated + ", deaths=" + deaths + ", districtWiseUrl=" + districtWiseUrl + "]";
	}
	

	

}
