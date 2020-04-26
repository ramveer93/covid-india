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
@Table(name = "state_wise_data")
public class StateWiseCases implements Serializable {
	
	private static final long serialVersionUID = -2343243243242432341L;
	
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@Id
	@Column(name = "state_name")
	private String stateName;
	@Column(name = "active_cases")
	private int activeCases;
	@Column(name = "cured_count")
	private int curedCount;
	@Column(name = "death_count")
	private int deathCount;
	@Column(name = "updatedOn", columnDefinition= "TIMESTAMP WITH TIME ZONE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedOn;
	@Column(name = "state_code")
	private String stateCode;
	@Column(name = "state_background_color")
	private String backGroundColor;
	@Column(name = "total_count")
	private int totalCases;
	@Column(name = "html_text", length = 1024)
	private String htmlText;
	@Column(name = "phone_number")
	private String phoneNumber;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getStateName() {
		return stateName;
	}
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}
	public int getActiveCases() {
		return activeCases;
	}
	public void setActiveCases(int activeCases) {
		this.activeCases = activeCases;
	}
	public int getCuredCount() {
		return curedCount;
	}
	public void setCuredCount(int curedCount) {
		this.curedCount = curedCount;
	}
	public int getDeathCount() {
		return deathCount;
	}
	public void setDeathCount(int deathCount) {
		this.deathCount = deathCount;
	}
	public Date getUpdatedOn() {
		return updatedOn;
	}
	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}
	public String getStateCode() {
		return stateCode;
	}
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}
	public String getBackGroundColor() {
		return backGroundColor;
	}
	public void setBackGroundColor(String backGroundColor) {
		this.backGroundColor = backGroundColor;
	}
	public int getTotalCases() {
		return totalCases;
	}
	public void setTotalCases(int totalCases) {
		this.totalCases = totalCases;
	}
	public String getHtmlText() {
		return htmlText;
	}
	public void setHtmlText(String htmlText) {
		this.htmlText = htmlText;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	@Override
	public String toString() {
		return "StateWiseCases [id=" + id + ", stateName=" + stateName + ", activeCases=" + activeCases
				+ ", curedCount=" + curedCount + ", deathCount=" + deathCount + ", updatedOn=" + updatedOn
				+ ", stateCode=" + stateCode + ", backGroundColor=" + backGroundColor + ", totalCases=" + totalCases
				+ ", htmlText=" + htmlText + ", phoneNumber=" + phoneNumber + "]";
	}
}
