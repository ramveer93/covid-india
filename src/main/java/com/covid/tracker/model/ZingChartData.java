package com.covid.tracker.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "zing_chart_data")
public class ZingChartData implements Serializable {
	
	private static final long serialVersionUID = 1l;
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@Id
	@Column(name = "state_name")
	private String stateName;
	@Column(name = "state_code")
	private String stateCode;
	@Column(name = "state_background_color")
	private String backGroundColor;
	@Column(name = "active_cases")
	private int activeCases;
	@Column(name = "death_count")
	private int deaths;
	@Column(name = "cured_count")
	private int cured;
	@Column(name = "total_count")
	private int totalCases;
	@Column(name = "html_text")
	private String htmlText;
	@Column(name = "updated_on")
	private Date updatedOn;
	
	public String getStateName() {
		return stateName;
	}
	public void setStateName(String stateName) {
		this.stateName = stateName;
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
	public int getActiveCases() {
		return activeCases;
	}
	public void setActiveCases(int activeCases) {
		this.activeCases = activeCases;
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
	public Date getUpdatedOn() {
		return updatedOn;
	}
	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "ZingChartData [id=" + id + ", stateName=" + stateName + ", stateCode=" + stateCode
				+ ", backGroundColor=" + backGroundColor + ", activeCases=" + activeCases + ", deaths=" + deaths
				+ ", cured=" + cured + ", totalCases=" + totalCases + ", htmlText=" + htmlText + ", updatedOn="
				+ updatedOn + "]";
	}
	
	
	

}
