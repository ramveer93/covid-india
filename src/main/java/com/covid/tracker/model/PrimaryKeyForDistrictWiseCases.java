package com.covid.tracker.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PrimaryKeyForDistrictWiseCases implements Serializable {

	private static final long serialVersionUID = 1L;
	@Column(name = "district_name", nullable = false)
	private String districtName;
	@Column(name = "state_name", nullable = false)
	private String stateName;
	
	public String getDistrictName() {
		return districtName;
	}
	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}
	public String getStateName() {
		return stateName;
	}
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	@Override
	public String toString() {
		return "PrimaryKeyForDistrictWiseCases [districtName=" + districtName + ", stateName=" + stateName + "]";
	}
	

}
