package com.covid.tracker.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.covid.tracker.httpClient.HttpRequest;
import com.covid.tracker.repository.GenericDataRepository;
import com.covid.tracker.service.CovidTrackerService;

@Configuration
@Component
@RestController
@RequestMapping(value = "/v1/tracker")
public class CovidTrackerController {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private CovidTrackerService service;

	@Autowired
	private Environment env;

	@Autowired
	private GenericDataRepository genericDataRepo;

	/**
	 * Test method
	 * 
	 * @param jsonBody
	 * @return
	 */
	@CrossOrigin(origins = "http://localhost:5500")
	@RequestMapping(value = "/refresh", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> refreshDataFromWeb() {
		this.LOGGER.info("started refreshing data ");
		String jsonData = this.service.refreshDataFromWeb();
		this.LOGGER.info("done refreshing data  with msg: " + jsonData);
		JSONObject response = new JSONObject();
		if (jsonData.isEmpty()) {
			response.put("Statue", "Success");
			response.put("Message", "Successfully sync the data from web!!");
			return new ResponseEntity<>(response.toString(), HttpStatus.OK);
		} else {
			response.put("Statue", "Failed");
			response.put("Message", jsonData);
			return new ResponseEntity<>(response.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String indexView()
	{
		return "templates/index.html";
	}

	@RequestMapping(value = "/syncMapping", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> updateDistrictStateMapping() {
		this.LOGGER.info("started refreshing state-district mapping ");
		String msg = this.service.syncStateDistrictMapping();
		JSONObject response = new JSONObject();
		if (msg.isEmpty()) {
			response.put("Status", "Success");
			response.put("Message", "Successfully updated state-district mapping!!");
			this.LOGGER.info("Successful refreshed state-district mapping ");
			return new ResponseEntity<>(response.toString(), HttpStatus.OK);
		} else {
			response.put("Statue", "Failed");
			response.put("Message", msg);
			this.LOGGER.error("Error refreshing state-district mapping " + msg);
			return new ResponseEntity<>(response.toString(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@CrossOrigin(origins = "http://localhost:5500")
	@RequestMapping(value = "/stateData", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> getStateData(@RequestParam(value = "zinkChartData") boolean zinkChartData) {
		this.LOGGER.info("started getStateData with args: " + zinkChartData);
		JSONObject result = null;
		try {
			result = this.service.getStateData(zinkChartData);
			return new ResponseEntity<>(result.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = e.getMessage();
			result.put("Statue", "Failed");
			result.put("Message", msg);
			this.LOGGER.error("Error getStateData  " + msg);
			return new ResponseEntity<>(result.toString(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@CrossOrigin(origins = "http://localhost:5500")
	@RequestMapping(value = "/news", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> getNewsFeedData(@RequestParam(value = "q") String query,
			@RequestParam(value = "from") String from, @RequestParam(value = "to") String to) {
		LOGGER.info("getNewsFeedData called with args: " + "q=" + query + " from: " + from + " to: " + to);
		JSONObject result = new JSONObject();

		try {
			String newsServiceUrl = env.getProperty("newsServiceUrl");
			String apiKey = env.getProperty("apiKey");
			String sortBy = "popularity";
			newsServiceUrl = newsServiceUrl + "?q=" + query + "&from=" + from + "&to=" + to + "&sortBy=" + sortBy
					+ "&apiKey=" + apiKey;
			LOGGER.info("getNewsFeedData Calling api with url:  " + newsServiceUrl);
			HttpRequest request = new HttpRequest();
			request.setUrl(newsServiceUrl);
			request.setRequestMethod(RequestMethod.GET);
			Map<String, String> headers = new HashMap<>();
			headers.put("author", "author");
			request.setHeader(headers);
			JSONObject resultJson = this.service.getNewsFeedData(request);
			JSONArray resultArray = resultJson.getJSONArray("articles");
			JSONArray finalResultArray = new JSONArray();
			for (int i = 0; i < resultArray.length(); i++) {
				JSONObject obj = resultArray.getJSONObject(i);
				if (i == 0) {
					obj.put("active", "carousel-item active");
					finalResultArray.put(0, obj);
				} else {
					obj.put("active", "carousel-item");
					finalResultArray.put(obj);
				}
			}
			JSONObject finalJsonObj = new JSONObject();
			finalJsonObj.put("articles", finalResultArray);
			this.LOGGER.info("getNewsFeedData controller data found so returning it to client ");
			System.out.println("data si..." + finalJsonObj.toString());
			return new ResponseEntity<>(finalJsonObj.toString(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			String msg = e.getMessage();
			result.put("Statue", "Failed");
			result.put("Message", msg);
			this.LOGGER.error(" There is no data found or there is error getting data  " + msg);
			return new ResponseEntity<>(result.toString(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@CrossOrigin(origins = "http://localhost:5500")
	@RequestMapping(value = "/genericData", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> getLatestGenericData() {
		this.LOGGER.info("started getLatestGenericData with args: ");
		try {
			if(shouldRefresh()) {
				LOGGER.info("Old data found in DB so refreshing the DB data from web, this may take some times");
				this.refreshDataFromWeb();
				LOGGER.info("Done with refreshing the DB data from web");
			}
		}catch(Exception e) {
			LOGGER.error("There is error refreshing the DB data from web.."+e.getMessage());
		}
		LOGGER.info("Getting getLatestGenericData.... ");
		JSONObject result = null;
		try {
			result = this.service.getLatestGenericData();
			return new ResponseEntity<>(result.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = e.getMessage();
			result.put("Statue", "Failed");
			result.put("Message", msg);
			this.LOGGER.error("Error getStateData  " + msg);
			return new ResponseEntity<>(result.toString(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@CrossOrigin(origins = "http://localhost:5500")
	@RequestMapping(value = "/getLineAndBarData", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> getTrendAndBarChartData() {
		this.LOGGER.info("started getLatestGenericData with args: ");
		JSONObject result = new JSONObject();
		try {
			result = this.service.getLineAndBarData();
			return new ResponseEntity<>(result.toString(), HttpStatus.OK);
		} catch (Exception e) {

			String msg = e.getMessage();
			result.put("Statue", "Failed");
			result.put("Message", msg);
			this.LOGGER.error("Error getStateData  " + msg);
			return new ResponseEntity<>(result.toString(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@CrossOrigin(origins = "http://localhost:5500")
	@RequestMapping(value = "/getDistrictData", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> getDistrictData() {
		this.LOGGER.info("started getLatestGenericData with args: ");
		JSONArray result = new JSONArray();
		try {
			result = this.service.getDistrictData();
			return new ResponseEntity<>(result.toString(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			JSONObject obj = new JSONObject();
			String msg = e.getMessage();
			obj.put("Statue", "Failed");
			obj.put("Message", msg);
			this.LOGGER.error("Error getStateData  " + msg);
			return new ResponseEntity<>(obj.toString(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	private boolean shouldRefresh() {
		Date dbDate = this.genericDataRepo.findLatestUpdatedDate().getUpdatedOn();
		LocalDate localDbDate = dbDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		int dbYear = localDbDate.getYear();
		int dbMonth = localDbDate.getMonthValue();
		int dbDay = localDbDate.getDayOfMonth();
		int dbHour = dbDate.getHours();
		Date currentDate = new Date();
		LocalDate localCurrent = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		int currentYear = localCurrent.getYear();
		int currentMonth = localCurrent.getMonthValue();
		int currentDay = localCurrent.getDayOfMonth();
		int currentHour = currentDate.getHours();
		String dbDateString = dbYear + "-" + dbMonth + "-" + dbDay + "-" + dbHour;
		String currentDateString = currentYear + "-" + currentMonth + "-" + currentDay + "-" + currentHour;
		LOGGER.info("dbDateString........." + dbDateString);
		LOGGER.info("currentDateString..........." + currentDateString);
		if (currentYear != dbYear || dbMonth != currentMonth || dbDay != currentDay) {
			return true;
		} else if (dbHour + 5 <= currentHour) {
			return true;
		} else
			return false;
	}
	
//	private String getUpdatedAsOn() {
//		Date dbDate = this.genericDataRepo.findLatestUpdatedDate().getUpdatedOn();
//		LocalDate localDbDate = dbDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//		int dbYear = localDbDate.getYear();
//		int dbMonth = localDbDate.getMonthValue();
//		int dbDay = localDbDate.getDayOfMonth();
//		int dbHour = dbDate.getHours();
//		Date currentDate = new Date();
//		LocalDate localCurrent = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//		int currentYear = localCurrent.getYear();
//		int currentMonth = localCurrent.getMonthValue();
//		int currentDay = localCurrent.getDayOfMonth();
//		int currentHour = currentDate.getHours();
//		String dbDateString = dbYear + "-" + dbMonth + "-" + dbDay + "-" + dbHour;
//		String currentDateString = currentYear + "-" + currentMonth + "-" + currentDay + "-" + currentHour;
//		LOGGER.info("dbDateString........." + dbDateString);
//		LOGGER.info("currentDateString..........." + currentDateString);
//		String hourDiff = "";
//		if (currentYear == dbYear && dbMonth == currentMonth && dbDay == currentDay) {
//			hourDiff = String.valueOf(currentHour-dbHour);
//		}else if(currentYear == dbYear && dbMonth == currentMonth && dbDay!=currentDay) {
//			
//		}
//		
//	}

}
