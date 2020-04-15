package com.covid.tracker.httpClient;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public interface RestClient {
	//public Response externalApiCall(HttpRequest request);
	public Map<String,String> executeExternalApi(HttpRequest request) throws Exception;

}