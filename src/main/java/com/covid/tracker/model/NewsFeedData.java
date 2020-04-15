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
@Table(name  = "news_feed_data")
public class NewsFeedData implements Serializable{
	private static final long serialVersionUID = -2343243243242432341L;
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@Column(name = "source_name", columnDefinition = "TEXT")
	private String sourceName;
	@Column(name = "author_name")
	private String authorName;
	@Column(name = "title", columnDefinition = "TEXT")
	private String title;
	@Column(name = "description", columnDefinition = "TEXT")
	private String description;
	@Column(name = "url", columnDefinition = "TEXT")
	private String url;
	@Column(name = "url_to_image", columnDefinition = "TEXT")
	private String urlToImage;
	@Id
	@Column(name = "updatedOn", columnDefinition= "TIMESTAMP WITH TIME ZONE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date publishedAt;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getSourceName() {
		return sourceName;
	}
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUrlToImage() {
		return urlToImage;
	}
	public void setUrlToImage(String urlToImage) {
		this.urlToImage = urlToImage;
	}
	public Date getPublishedAt() {
		return publishedAt;
	}
	public void setPublishedAt(Date publishedAt) {
		this.publishedAt = publishedAt;
	}
	@Override
	public String toString() {
		return "NewsFeedData [id=" + id + ", sourceName=" + sourceName + ", authorName=" + authorName + ", title="
				+ title + ", description=" + description + ", url=" + url + ", urlToImage=" + urlToImage
				+ ", publishedAt=" + publishedAt + "]";
	}

	
	
	
}
