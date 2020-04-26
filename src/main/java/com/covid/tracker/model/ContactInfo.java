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
@Table(name = "contact_info")
public class ContactInfo implements Serializable {
	private static final long serialVersionUID = -2343243243242432341L;
	@Column(name = "email")
	private String email;
	@Column(columnDefinition="TEXT" , name = "message")
	private String message;
	@Column(name = "updated_on", columnDefinition= "TIMESTAMP WITH TIME ZONE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn = new Date();
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "ContactInfo [email=" + email + ", message=" + message + ", createdOn=" + createdOn + ", id=" + id + "]";
	}

}
