package com.covid.tracker.csvparser;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import com.covid.tracker.CovidTrackerApplication;
import com.covid.tracker.httpClient.Constants;
import com.covid.tracker.httpClient.HttpRequest;
import com.covid.tracker.httpClient.RestClient;
import com.covid.tracker.service.CovidTrackerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

@Component
public class ParseCsvFromGitHub {

	@Autowired
	private RestClient restClient;

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private CovidTrackerService service;

	@Autowired
	private Environment env;

	public JSONObject getCSVAndConvertToJson() throws Exception {
		/**
		 * recovered
		 */
		String recoveredUrl = this.env.getProperty("recovered_csv_url");
		JSONObject recoveredResult = getFormatedTsData(recoveredUrl, "recovered");

		/**
		 * deaths
		 */
		String deathsUrl = this.env.getProperty("deaths_csv_url");
		JSONObject deathsResult = getFormatedTsData(deathsUrl, "deaths");

		/**
		 * confirmed
		 */
		String confirmedUrl = this.env.getProperty("confirmed_csv_url");
		JSONObject confirmedResult = getFormatedTsData(confirmedUrl, "confirmed");

		JSONObject resultOfDeathAndConfirmedFinal = getConbinedOfConfirmedAndDeaths(deathsResult, confirmedResult);
		JSONObject finalResult = getConbinedOfConfirmedAndDeathsAndRecovered(resultOfDeathAndConfirmedFinal,
				recoveredResult);
		
		
		HashMap<String,Object> result =
		        new ObjectMapper().readValue(finalResult.toString(), HashMap.class);
		
		Map<String,Object> map = new TreeMap<>(result);
		return new JSONObject(map);

//		return finalResult;
	}

	private JSONObject getConbinedOfConfirmedAndDeathsAndRecovered(JSONObject confirmedAndDeaths,
			JSONObject recoveredResult) {
		JSONArray countryNameKeys = confirmedAndDeaths.names();

		JSONObject resultOfDeathAndConfirmedAndRecoveredFinal = new JSONObject();

		for (int k = 0; k < countryNameKeys.length(); k++) {
			/**
			 * recovered
			 */
			JSONArray singalCountryRecoveredArray = recoveredResult.getJSONArray(countryNameKeys.getString(k));
			JSONArray singCountryConfirmedAndDeathArray = confirmedAndDeaths.getJSONArray(countryNameKeys.getString(k));

			JSONObject resultOfDeathConfirmedRecovered = new JSONObject();
			JSONArray resultOfDeathConfirmedRecoveredArray = new JSONArray();

			for (int p = 0; p < singalCountryRecoveredArray.length(); p++) {
				JSONObject singleObjRecovered = singalCountryRecoveredArray.getJSONObject(p);
				for (int q = 0; q < singCountryConfirmedAndDeathArray.length(); q++) {

					JSONObject singleObjConfirmedAndDeaths = singCountryConfirmedAndDeathArray.getJSONObject(q);

					if (singleObjConfirmedAndDeaths.getString("date").equals(singleObjRecovered.getString("date"))) {
						resultOfDeathConfirmedRecovered.put("date", singleObjConfirmedAndDeaths.getString("date"));
						resultOfDeathConfirmedRecovered.put("deaths", singleObjConfirmedAndDeaths.getLong("deaths"));
						resultOfDeathConfirmedRecovered.put("confirmed",
								singleObjConfirmedAndDeaths.getLong("confirmed"));
						resultOfDeathConfirmedRecovered.put("recovered", singleObjRecovered.getLong("recovered"));

						resultOfDeathConfirmedRecoveredArray.put(resultOfDeathConfirmedRecovered);
						resultOfDeathConfirmedRecovered = new JSONObject();
						break;
					}

				}
			}
			resultOfDeathConfirmedRecoveredArray = this.service.sortJsonArrayBasedOnDate(resultOfDeathConfirmedRecoveredArray);
			resultOfDeathAndConfirmedAndRecoveredFinal.put(countryNameKeys.getString(k),
					resultOfDeathConfirmedRecoveredArray);
		}
		return resultOfDeathAndConfirmedAndRecoveredFinal;
	}

	private JSONObject getConbinedOfConfirmedAndDeaths(JSONObject deathsResult, JSONObject confirmedResult) {
		JSONArray countryNameKeys = confirmedResult.names();

		JSONObject resultOfDeathAndConfirmedFinal = new JSONObject();

		for (int k = 0; k < countryNameKeys.length(); k++) {
			/**
			 * deaths
			 */
			JSONArray singCountryDeathArray = deathsResult.getJSONArray(countryNameKeys.getString(k));
			JSONArray singCountryConfirmedArray = confirmedResult.getJSONArray(countryNameKeys.getString(k));

			JSONObject resultOfDeathAndConfirmed = new JSONObject();
			JSONArray resultOfDeathAndConfirmedArray = new JSONArray();

			for (int p = 0; p < singCountryDeathArray.length(); p++) {
				JSONObject singleObjDeaths = singCountryDeathArray.getJSONObject(p);
				for (int q = 0; q < singCountryConfirmedArray.length(); q++) {

					JSONObject singleObjConfirmed = singCountryConfirmedArray.getJSONObject(q);

					if (singleObjConfirmed.getString("date").equals(singleObjDeaths.getString("date"))) {
						resultOfDeathAndConfirmed.put("date", singleObjConfirmed.getString("date"));
						resultOfDeathAndConfirmed.put("deaths", singleObjDeaths.getLong("deaths"));
						resultOfDeathAndConfirmed.put("confirmed", singleObjConfirmed.getLong("confirmed"));
						resultOfDeathAndConfirmedArray.put(resultOfDeathAndConfirmed);
						resultOfDeathAndConfirmed = new JSONObject();
						break;
					}
				}
			}

			resultOfDeathAndConfirmedFinal.put(countryNameKeys.getString(k), resultOfDeathAndConfirmedArray);
		}
		return resultOfDeathAndConfirmedFinal;
	}

	/**
	 * Canada, Australia , China there is no data
	 * 
	 * @param recoveredUrl
	 * @param type
	 * @return
	 * @throws Exception
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	private JSONObject getFormatedTsData(String recoveredUrl, String type)
			throws Exception, IOException, JsonProcessingException {
		JSONArray recoveredJsonArray = getRecoveredTSData(recoveredUrl);
		JSONArray aus = new JSONArray();

		JSONObject result = new JSONObject();
		for (int i = 0; i < recoveredJsonArray.length(); i++) {
			JSONObject singleCountry = recoveredJsonArray.getJSONObject(i);
			if (singleCountry.has("Province/State") && singleCountry.getString("Province/State").equals("")) {
				if (singleCountry.has("Country/Region")) {
					String countryName = singleCountry.getString("Country/Region");
					singleCountry.remove("Country/Region");
					singleCountry.remove("Province/State");
					singleCountry.remove("Lat");
					singleCountry.remove("Long");
					JSONArray keys = singleCountry.names();
					JSONArray singleCountryArray = new JSONArray();
					for (int j = 0; j < keys.length(); j++) {
						JSONObject singleEntry = new JSONObject();
						String date = keys.getString(j);
						date = convertDateToReadableFormat(date);
						singleEntry.put("date", date);
						singleEntry.put(type, singleCountry.getLong(keys.getString(j)));
						singleCountryArray.put(singleEntry);
					}
					result.put(countryName, singleCountryArray);
				}
			} else {
				LOGGER.info("skipping for this country......" + singleCountry.getString("Country/Region"));
//				if (singleCountry.has("Province/State") && singleCountry.has("Country/Region")
//						&& singleCountry.getString("Country/Region").equals("Australia")) {
//					singleCountry.remove("Country/Region");
//					singleCountry.remove("Province/State");
//					singleCountry.remove("Lat");
//					singleCountry.remove("Long");
//					aus.put(singleCountry);
//
//				}
			}
		}
//		JSONObject ausObj = getResultForAustralia(aus);
//		JSONArray keys = ausObj.names();
//		JSONArray singleCountryArrayForAus = new JSONArray();
//		for (int j = 0; j < keys.length(); j++) {
//			JSONObject singleEntry = new JSONObject();
//			String date = keys.getString(j);
//			date = convertDateToReadableFormat(date);
//			singleEntry.put("date", date);
//			singleEntry.put(type, ausObj.getLong(keys.getString(j)));
//			singleCountryArrayForAus.put(singleEntry);
//		}
//		result.put("Australia", singleCountryArrayForAus);

		return result;
	}

	private JSONObject getResultForAustralia(JSONArray input) {

		JSONObject finalJsonObj = new JSONObject();
		JSONArray keys = input.getJSONObject(0).names();

		for (int k = 0; k < keys.length(); k++) {
			for (int i = 0; i < input.length(); i++) {
				JSONObject obji = input.getJSONObject(i);
				if (finalJsonObj.has(keys.getString(k))) {
					long last = finalJsonObj.getLong(keys.getString(k));
					finalJsonObj.put(keys.getString(k), obji.getLong(keys.getString(k)) + last);
					obji = new JSONObject();
				} else {
					finalJsonObj.put(keys.getString(k), obji.getLong(keys.getString(k)));
					obji = new JSONObject();
				}
			}
		}

		System.out.println("finalJsonObj..." + finalJsonObj.toString());
		return finalJsonObj;

	}

	private JSONArray getRecoveredTSData(String url) throws Exception, IOException, JsonProcessingException {
		HttpRequest request = new HttpRequest();
		Map<String, String> headers = new HashMap<>();
		headers.put("TestHeader", "test");
		request.setUrl(url);
		request.setHeader(headers);
		request.setRequestMethod(RequestMethod.GET);
		Map<String, String> resultMap = this.restClient.executeExternalApi(request);
		String response = resultMap.get(Constants.RESPONSE_STR);

//	    JSONParser parser = new JSONParser();
		CsvSchema csvSchema = CsvSchema.builder().setUseHeader(true).build();
		CsvMapper csvMapper = new CsvMapper();

		// Read data from CSV file
		List<? extends Object> readAll = csvMapper.readerFor(Map.class).with(csvSchema).readValues(response).readAll();

		ObjectMapper mapper = new ObjectMapper();

		JSONArray jsonArray = new JSONArray(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(readAll));
		return jsonArray;
	}

	private String convertDateToReadableFormat(String date) {

		try {
			DateFormat formatter = new SimpleDateFormat("MM/dd/yy");
			Date inputDate = formatter.parse(date);
			formatter = new SimpleDateFormat("yyyy-MM-dd");
			String outputDate = formatter.format(inputDate);
			return outputDate;
		} catch (ParseException e) {
			LOGGER.error("Error parsing the JHC date to desired date: " + e.getMessage());
		}
		return "";

	}

}
