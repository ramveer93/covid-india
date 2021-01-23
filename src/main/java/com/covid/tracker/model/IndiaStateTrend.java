package com.covid.tracker.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name = "india_state_trend")
public class IndiaStateTrend {
	@EmbeddedId
	private IndiaStateTrendPk pk;
	@Column(name = "active")
	private int active;
	@Column(name = "deaths")
	private int deaths;
	@Column(name = "cured")
	private int cured;
	@Column(name = "updatedOn", columnDefinition = "TIMESTAMP WITH TIME ZONE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedOn = new Date();
	public IndiaStateTrendPk getPk() {
		return pk;
	}
	public void setPk(IndiaStateTrendPk pk) {
		this.pk = pk;
	}
	public int getActive() {
		return active;
	}
	public void setActive(int active) {
		this.active = active;
	}
	public int getDeaths() {
		return deaths;
	}
	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}
	public int getCured() {
		return cured;
	}
	public void setCured(int cured) {
		this.cured = cured;
	}
	public Date getUpdatedOn() {
		return updatedOn;
	}
	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}
	@Override
	public String toString() {
		return "IndiaStateTrend [pk=" + pk + ", active=" + active + ", deaths=" + deaths + ", cured=" + cured
				+ ", updatedOn=" + updatedOn + "]";
	}
	
}
