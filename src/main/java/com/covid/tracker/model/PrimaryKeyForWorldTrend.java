package com.covid.tracker.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PrimaryKeyForWorldTrend implements Serializable {
	private static final long serialVersionUID = 1L;
	@Column(name = "id", nullable = false)
	private String countryId;
	@Column(name = "date", nullable = false)
	private String date;
	public String getCountryId() {
		return countryId;
	}
	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	@Override
	public String toString() {
		return "PrimaryKeyForWorldTrend [countryId=" + countryId + ", date=" + date + "]";
	}
}
