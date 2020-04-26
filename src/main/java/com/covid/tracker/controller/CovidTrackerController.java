package com.covid.tracker.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.covid.tracker.httpClient.HttpRequest;
import com.covid.tracker.model.AuthRequest;
import com.covid.tracker.model.ContactInfo;
import com.covid.tracker.model.User;
import com.covid.tracker.repository.ContactRepository;
import com.covid.tracker.repository.GenericDataRepository;
import com.covid.tracker.repository.WorldGenericRepository;
import com.covid.tracker.security.JwtUtils;
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

	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private WorldGenericRepository worldGenericRepo;

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private AuthenticationManager authenticationManager;

	/**
	 * Test method
	 * 
	 * @param jsonBody
	 * @return
	 */
	@CrossOrigin(origins = "http://localhost:8080")
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
	public String indexView() {
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

	@CrossOrigin(origins = "http://localhost:8080")
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

	@CrossOrigin(origins = "http://localhost:8080")
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
			LOGGER.info("getNewsFeedData controller data found so returning it to client ");
			return new ResponseEntity<>(finalJsonObj.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = e.getMessage() + " cause : " + e.getCause() + " error stack trace: " + e.getStackTrace();
			result.put("Statue", "Failed");
			result.put("Message", msg);
			this.LOGGER.error(" There is no data found or there is error getting data  " + msg);
			return new ResponseEntity<>(result.toString(), HttpStatus.INTERNAL_SERVER_ERROR);

		}

	}

	@CrossOrigin(origins = "http://localhost:8080")
	@RequestMapping(value = "/genericData", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> getLatestGenericData() {
		this.LOGGER.info("started getLatestGenericData with args: ");
//		try {
////			if (shouldRefresh()) {
//			LOGGER.info("Old data found in DB so refreshing the DB data from web, this may take some times");
//			this.refreshDataFromWeb();
//			LOGGER.info("Done with refreshing the DB data from web");
////			}
//		} catch (Exception e) {
//			LOGGER.error("There is error refreshing the DB data from web.." + e.getMessage());
//		}

		JSONObject result = null;
		try {
			result = this.service.getLatestGenericData();
			LOGGER.info("Got getLatestGenericData ");
			return new ResponseEntity<>(result.toString(), HttpStatus.OK);
		} catch (Exception e) {
			String msg = e.getMessage() + " cause : " + e.getCause() + " error stack trace: " + e.getStackTrace();
			result.put("Statue", "Failed");
			result.put("Message", msg);
			this.LOGGER.error("Error getStateData  " + msg);
			return new ResponseEntity<>(result.toString(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@CrossOrigin(origins = "http://localhost:8080")
	@RequestMapping(value = "/lineAndBarData", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> getTrendAndBarChartData() {
		this.LOGGER.info("started getTrendAndBarChartData with args: {} ");
		JSONObject result = new JSONObject();
		try {
			result = this.service.getLineAndBarData();
			return new ResponseEntity<>(result.toString(), HttpStatus.OK);
		} catch (Exception e) {

			String msg = e.getMessage() + " cause : " + e.getCause() + " error stack trace: " + e.getStackTrace();
			result.put("Statue", "Failed");
			result.put("Message", msg);
			this.LOGGER.error("Error getStateData  " + msg);
			return new ResponseEntity<>(result.toString(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@CrossOrigin(origins = "http://localhost:8080")
	@RequestMapping(value = "/districtData", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> getDistrictData() {
		this.LOGGER.info("started getLatestGenericData with args: ");
		JSONArray result = new JSONArray();
		try {
			result = this.service.getDistrictData();
			this.LOGGER.info("Done getLatestGenericData with 200 ");
			return new ResponseEntity<>(result.toString(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			JSONObject obj = new JSONObject();
			String msg = e.getMessage() + " cause : " + e.getCause() + " error stack trace: " + e.getStackTrace();
			obj.put("Statue", "Failed");
			obj.put("Message", msg);
			this.LOGGER.error("Error getStateData  " + msg);
			return new ResponseEntity<>(obj.toString(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@RequestMapping(value = "/contact", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	public ResponseEntity<Object> saveContact(@RequestBody ContactInfo contact) {
		JSONObject result = new JSONObject();
		try {
			System.out.println("contact insfo" + contact.toString());
			if (!contact.getEmail().isEmpty() && !contact.getMessage().isEmpty() && contact.getEmail() != null
					&& contact.getMessage() != null && contact.getEmail().length() >= 0
					&& contact.getMessage().length() >= 0) {
				contactRepository.save(contact);
				LOGGER.info("saccessfully saved.." + contact);
				result.put("status", "Success");
				result.put("message", "Thank you for your query, we will get back to you soon!!");
				return new ResponseEntity<>(result.toString(), HttpStatus.OK);
			} else {
				result.put("status", "Error");
				result.put("message", "hmm, validations available on both fields, correct the input and try again");
				System.out.println("contact insfo" + contact.toString());
				return new ResponseEntity<>(result.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
			}

		} catch (Exception e) {
			result.put("status", "Error");
			result.put("message", "You loser , you cant pass even two things correctly: " + e.getMessage());
			return new ResponseEntity<>(result.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value = "/worldGenericData", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> getWorldGenericData(@RequestParam("countryId") String countryId) {
		try {
			JSONObject result = this.service.getWorldGenericData(countryId);
			return new ResponseEntity<>(result.toString(), HttpStatus.OK);
		} catch (Exception e) {
			JSONObject obj = new JSONObject();
			e.printStackTrace();
			obj.put("status", "Error");
			obj.put("message", "" + e.getMessage() + " caused By: " + e.getCause() + " throws: " + e.getStackTrace());
			return new ResponseEntity<>(obj.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//	@RequestMapping(value = "/refreshWorldGenericData", method = RequestMethod.GET, produces = "application/json")
//	public ResponseEntity<Object> refreshWorldGenericData() {
//		try {
//			JSONObject result = this.service.getWorldGenericData("earth", false);
//			return new ResponseEntity<>(result.toString(), HttpStatus.OK);
//		} catch (Exception e) {
//			JSONObject obj = new JSONObject();
//			e.printStackTrace();
//			obj.put("status", "Error");
//			obj.put("message", "" + e.getMessage() + " caused By: " + e.getCause() + " throws: " + e.getStackTrace());
//			return new ResponseEntity<>(obj.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}

	@RequestMapping(value = "/refreshWorldData", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> refreshWorldData() {
		try {
			JSONObject result = this.service.refreshWorldData();
			return new ResponseEntity<>(result.toString(), HttpStatus.OK);
		} catch (Exception e) {
			JSONObject obj = new JSONObject();
			e.printStackTrace();
			obj.put("status", "Error");
			obj.put("message", "" + e.getMessage() + " caused By: " + e.getCause() + " throws: " + e.getStackTrace());
			return new ResponseEntity<>(obj.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/worldMapData", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> getWorldMapData() {
		try {
			JSONObject result = this.service.getWorldDataForZinkChart();
			return new ResponseEntity<>(result.toString(), HttpStatus.OK);
		} catch (Exception e) {
			JSONObject obj = new JSONObject();
			e.printStackTrace();
			obj.put("status", "Error");
			obj.put("message", "" + e.getMessage() + " caused By: " + e.getCause() + " throws: " + e.getStackTrace());
			return new ResponseEntity<>(obj.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//	@RequestMapping(value = "/refreshLineBarData", method = RequestMethod.GET, produces = "application/json")
//	public ResponseEntity<Object> refreshLineBarData(@RequestParam("countryId") String countryId) {
//		try {
//			JSONObject result = this.service.refreshLineBarData(countryId);
//			return new ResponseEntity<>(result.toString(), HttpStatus.OK);
//		} catch (Exception e) {
//			JSONObject obj = new JSONObject();
//			e.printStackTrace();
//			obj.put("status", "Error");
//			obj.put("message", "" + e.getMessage() + " caused By: " + e.getCause() + " throws: " + e.getStackTrace());
//			return new ResponseEntity<>(obj.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}

	@RequestMapping(value = "/lineAndBarDataForCountry", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> getLineAndBarDataForFountry(@RequestParam("countryId") String countryId,
			@RequestParam("dropDownValue") boolean dropDownValue) {
		try {
			if (dropDownValue) {
				countryId = this.worldGenericRepo.getCountryId(countryId);
				LOGGER.info("Req is from dropdown so find countryId from country name: " + countryId);
			}
			JSONObject result = this.service.getLineAndBarDataForCountry(countryId);
			return new ResponseEntity<>(result.toString(), HttpStatus.OK);
		} catch (Exception e) {
			JSONObject obj = new JSONObject();
			e.printStackTrace();
			obj.put("status", "Error");
			obj.put("message", "" + e.getMessage() + " caused By: " + e.getCause() + " throws: " + e.getStackTrace());
			return new ResponseEntity<>(obj.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/refreshCompleteWorld", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> refreshCompleteWorld() {
		JSONObject result = new JSONObject();
		try {
			LOGGER.info("Completely world data refresh started at : "+new Date().toString());
			this.service.refreshWorldData();
			LOGGER.info("Completely world data refresh Done at : "+new Date().toString());
			result.put("message", "Successfully refreshed world data completely");
			result.put("status", "success");
			return new ResponseEntity<>(result.toString(), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Completely world data refresh aborted due to error : "+e.getMessage()+" "+e.getCause());
			JSONObject obj = new JSONObject();
			e.printStackTrace();
			obj.put("status", "Error");
			obj.put("message", "" + e.getMessage() + " caused By: " + e.getCause() + " throws: " + e.getStackTrace());
			return new ResponseEntity<>(obj.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/countryList", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Object> getCountryList() {
		try {
			System.out.println("-----------------ip is---" + getLocalIp());
			JSONArray resultArray = this.service.getCountryList();
			return new ResponseEntity<>(resultArray.toString(), HttpStatus.OK);
		} catch (Exception e) {
			JSONObject obj = new JSONObject();
			e.printStackTrace();
			obj.put("status", "Error");
			obj.put("message", "" + e.getMessage() + " caused By: " + e.getCause() + " throws: " + e.getStackTrace());
			return new ResponseEntity<>(obj.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/token", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> getToken(@RequestBody AuthRequest authRequest) {
		JSONObject result = new JSONObject();
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
			String token = jwtUtils.generateToken(authRequest.getUsername());
			result.put("token", token);
			result.put("status", "success");
			return new ResponseEntity<>(result.toString(), HttpStatus.OK);
		} catch (Exception e) {
			result.put("message", e.getMessage() + " cause: " + e.getCause());
			result.put("status", "failed");
			return new ResponseEntity<>(result.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
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

		if (currentYear != dbYear || dbMonth != currentMonth || dbDay != currentDay) {
			return true;
		} else if (dbHour + 5 <= currentHour) {
			return true;
		} else
			return false;
	}

	private String getLocalIp() {
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			return inetAddress.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;

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
