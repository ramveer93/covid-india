package com.covid.tracker.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class IndiaStateTrendPk implements Serializable {
	private static final long serialVersionUID = 1L;
	@Column(name = "state_name", nullable = false)
	private String stateName;
	
	@Column(name = "date", nullable = false)
	private Date date;

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "IndiaStateTrendPk [stateName=" + stateName + ", date=" + date + "]";
	}
	

}
