package com.covid.tracker.httpClient;
import java.util.Map;

import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HttpRequest {
	@JsonProperty("url")
	private String url;
	@JsonProperty("requestMethod")
	private RequestMethod requestMethod;
	@JsonProperty("header")
	private Map<String, String> header;
	@JsonProperty("queryParams")
	private Map<String, String> queryParams;
	@JsonProperty("requestBody")
	private Object requestBody;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public RequestMethod getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMethod(RequestMethod requestMethod) {
		this.requestMethod = requestMethod;
	}

	public Map<String, String> getHeader() {
		return header;
	}

	public void setHeader(Map<String, String> header) {
		this.header = header;
	}

	public Map<String, String> getQueryParams() {
		return queryParams;
	}

	public void setQueryParams(Map<String, String> queryParams) {
		this.queryParams = queryParams;
	}
	public Object getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(Object requestBody) {
		this.requestBody = requestBody;
	}
	/**
	 * Validate the input
	 * 
	 * @return
	 */
	public boolean isValid() {
		try {
			Assert.hasLength(this.url, "Url must not be empty");
			Assert.hasLength(this.requestMethod.toString(), "Request Method must not be empty");
		} catch (IllegalArgumentException ex) {
			// throw new HttpClientException(ex.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "HttpRequest [url=" + url + ", requestMethod=" + requestMethod + ", header=" + header + ", queryParams="
				+ queryParams + ", requestBody=" + requestBody + "]";
	}

	

}