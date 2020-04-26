package com.covid.tracker.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "world_country_trend")
public class WorldCountryTrend implements Serializable {
	private static final long serialVersionUID = 1L;
	@EmbeddedId
	private PrimaryKeyForWorldTrend pk;
	@Column(name = "confirmed")
	private long confirmed;
	@Column(name = "deaths")
	private long deaths;
	@Column(name = "recovered")
	private long recovered;
	@Column(name = "updatedOn", columnDefinition = "TIMESTAMP WITH TIME ZONE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedOn = new Date();

	public PrimaryKeyForWorldTrend getPk() {
		return pk;
	}

	public void setPk(PrimaryKeyForWorldTrend pk) {
		this.pk = pk;
	}

	public long getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(long confirmed) {
		this.confirmed = confirmed;
	}

	public long getDeaths() {
		return deaths;
	}

	public void setDeaths(long deaths) {
		this.deaths = deaths;
	}

	public long getRecovered() {
		return recovered;
	}

	public void setRecovered(long recovered) {
		this.recovered = recovered;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	@Override
	public String toString() {
		return "WorldCountryTrend [pk=" + pk + ", confirmed=" + confirmed + ", deaths=" + deaths + ", recovered="
				+ recovered + ", updatedOn=" + updatedOn + "]";
	}

}
