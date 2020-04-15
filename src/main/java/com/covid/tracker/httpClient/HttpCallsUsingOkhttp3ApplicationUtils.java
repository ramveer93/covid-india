package com.covid.tracker.httpClient;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


@Component
public class HttpCallsUsingOkhttp3ApplicationUtils {

	public JSONObject getRequestBody(HttpRequest request) throws Exception {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JSONObject object = mapper.convertValue(request.getRequestBody(), JSONObject.class);
			return object;
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

}