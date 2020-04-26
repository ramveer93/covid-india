package com.covid.tracker.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "world_data")
public class WorldData implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id")
	private String id;
	@Column(name = "country_name")
	private String name;
	@Column(name = "alpha3_code")
	private String alpha3code;
	@Column(name = "data_source")
	private String dataSource;
	@Column(name = "date")
	private String date;
	@Column(name = "confirmed")
	private long confirmed;
	@Column(name = "recovered")
	private long recovered;
	@Column(name = "deaths")
	private long deaths;
	@Column(name = "background_color")
	private String backgroundColor;
	@Column(columnDefinition="TEXT" , name = "html_text")
	private String htmlText;
	
	@Column(name = "updated_on")
	private Date updatedOn = new Date();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlpha3code() {
		return alpha3code;
	}

	public void setAlpha3code(String alpha3code) {
		this.alpha3code = alpha3code;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public long getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(long confirmed) {
		this.confirmed = confirmed;
	}

	public long getRecovered() {
		return recovered;
	}

	public void setRecovered(long recovered) {
		this.recovered = recovered;
	}

	public long getDeaths() {
		return deaths;
	}

	public void setDeaths(long deaths) {
		this.deaths = deaths;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public String getHtmlText() {
		return htmlText;
	}

	public void setHtmlText(String htmlText) {
		this.htmlText = htmlText;
	}

	@Override
	public String toString() {
		return "WorldData [id=" + id + ", name=" + name + ", alpha3code=" + alpha3code + ", dataSource=" + dataSource
				+ ", date=" + date + ", confirmed=" + confirmed + ", recovered=" + recovered + ", deaths=" + deaths
				+ ", backgroundColor=" + backgroundColor + ", htmlText=" + htmlText + ", updatedOn=" + updatedOn + "]";
	}

}
