package com.covid.tracker.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import com.covid.tracker.httpClient.Constants;
import com.covid.tracker.httpClient.HttpRequest;
import com.covid.tracker.httpClient.RestClient;
import com.covid.tracker.model.DistrictWiseCasesVo;
import com.covid.tracker.model.GenericData;
import com.covid.tracker.model.StateWiseCases;
import com.covid.tracker.model.WorldCountryTrend;
import com.covid.tracker.model.WorldData;
import com.covid.tracker.repository.WorldGenericRepository;
import com.covid.tracker.repository.WorldTrendRepository;
import com.covid.tracker.service.CovidTrackerService;

@Component
public class WorldDataUtils {
	@Autowired
	private Environment env;

	@Autowired
	private RestClient restClient;

	@Autowired
	private CovidTrackerUtils covidUtils;

	@Autowired
	private WorldGenericRepository worldDataRepo;
	@Autowired
	private WorldTrendRepository worldTrendRepository;

	@Autowired
	private CovidTrackerService service;

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	public JSONObject getWorldGenericData(String countryId) throws Exception {
		HttpRequest request = new HttpRequest();
		String worldGenericDataUrl = this.env.getProperty("worldDataApi") + "/totals?format=json";
		Map<String, String> headers = new HashMap<>();
//		headers.put("x-rapidapi-host", "covid-19-data.p.rapidapi.com");
		headers.put("x-rapidapi-key", this.env.getProperty("rapidApiKey"));
		request.setUrl(worldGenericDataUrl);
		request.setHeader(headers);
		request.setRequestMethod(RequestMethod.GET);
		Map<String, String> resultMap = this.restClient.executeExternalApi(request);
		String response = resultMap.get(Constants.RESPONSE_STR);
		boolean isSuccess = Boolean.parseBoolean(resultMap.get(Constants.IS_SUCCESS));
		if (isSuccess) {
			JSONArray resultArray = new JSONArray(response);
			if (resultArray.length() > 0) {
				JSONObject obj = new JSONObject();
				obj = resultArray.getJSONObject(0);
				return obj;
			}
		}
		return null;
	}

	public String getUniqueDate() {
		Date currentDate = new Date();
		String modifiedDate = new SimpleDateFormat("yyyy-MM-dd").format(currentDate);
		return modifiedDate;
//		LocalDate localCurrent = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//		int currentYear = localCurrent.getYear();
//		int currentMonth = localCurrent.getMonth().getValue();
//		int currentDay = localCurrent.getDayOfMonth();
//		return currentYear + "-" + currentMonth + "-" + currentDay;
	}

	/**
	 * Get the world data from https://pomber.github.io/covid19/timeseries.json
	 * 
	 * @param countryCode
	 * @return
	 * @throws Exception
	 */
	public JSONObject getWorldData() throws Exception {
		HttpRequest request = new HttpRequest();
		String worldData = "https://pomber.github.io/covid19/timeseries.json";
		Map<String, String> headers = new HashMap<>();
		headers.put("TestHeader", "test");
		request.setUrl(worldData);
		request.setHeader(headers);
		request.setRequestMethod(RequestMethod.GET);
		Map<String, String> resultMap = this.restClient.executeExternalApi(request);
		String response = resultMap.get(Constants.RESPONSE_STR);
		boolean isSuccess = Boolean.parseBoolean(resultMap.get(Constants.IS_SUCCESS));
		if (isSuccess) {
			return new JSONObject(response);
		}
		return null;
	}

	public JSONObject getWorldDataForZinkChart() {
		List<WorldData> list = this.worldDataRepo.findAll();
		JSONObject result = new JSONObject();
		list.forEach(obj -> {
			JSONObject toolTip = new JSONObject();
			toolTip.put("text", obj.getHtmlText());
			toolTip.put("html-mode", true);
			JSONObject label = new JSONObject();
			label.put("visible", false);
			JSONObject innerJsonObj = new JSONObject();
			innerJsonObj.put("tooltip", toolTip);
			innerJsonObj.put("backgroundColor", obj.getBackgroundColor());
			innerJsonObj.put("label", label);
			if(!obj.getId().equals("earth")) {
				result.put(obj.getAlpha3code(), innerJsonObj);
			}
			
		});
		return result;
	}
	
		
	public String setColorForWorld(long totalCases) {
		String bgColor = "";
		if(totalCases>=1000000) {
			bgColor = "#800026";
		}else if(totalCases>=500000 && totalCases<1000000) {
			bgColor = "#bd0026";
		}else if(totalCases>=100000 && totalCases<500000) {
			bgColor = "#e31a1c";
		}else if(totalCases>=80000 && totalCases<100000) {
			bgColor = "#fc4e2a";
		}else if(totalCases>=50000 && totalCases<80000) {
			bgColor = "#fd8d3c";
		}else if(totalCases>=20000 && totalCases<50000) {
			bgColor = "#feb24c";
		}else if(totalCases>=10000 && totalCases<20000) {
			bgColor = "#fed976";
		}else if(totalCases>=5000 && totalCases<10000) {
			bgColor = "#ffeda0";
		}else if(totalCases>=1000 && totalCases<5000) {
			bgColor = "#ffffcc";
		}else if(totalCases>=500 && totalCases<1000) {
			bgColor = "##99d8c9";
		}else if(totalCases<500) {
			bgColor = "#66c2a4";
		}
		return bgColor;
	}

	public String getHtmlStringForZingChart(WorldData worldData) {
		long total = worldData.getConfirmed() + worldData.getDeaths() + worldData.getRecovered();
		return "<table class='table-sm table-striped table-borderless table-info'><tbody><tr><th>" + worldData.getName()
				+ "</th></tr><tr><th>Total</th><td class='float-right'>" + total
				+ "</th></tr><tr><th>Active</th><td class='float-right'>" + worldData.getConfirmed()
				+ "</th></tr><tr><th>Deaths</th><td class='float-right'>" + worldData.getDeaths()
				+ "</th></tr><tr><th>Cured</th><td class='float-right'>" + worldData.getRecovered()
				+ "</th></tr></tbody></table>";
	}

	public JSONObject getLineAndBarDataForWorld(String countryId) {
		List<WorldData> topTenActive = worldDataRepo.getTopTenCountriesBasedOnConfirmed();
		List<WorldData> topTenCured = worldDataRepo.getTopTenCountriesBasedOnRecovered();
		List<WorldData> topTenDeaths = worldDataRepo.getTopTenCountriesBasedOnDeaths();

		JSONObject resultJson = new JSONObject();
		/**
		 * Active
		 */

		JSONObject activeJson = new JSONObject();
		JSONArray activeStateArray = new JSONArray();
		JSONArray activeCountArray = new JSONArray();
		for (int i = 0; i < topTenActive.size(); i++) {
			WorldData temp = topTenActive.get(i);
			activeStateArray.put(i, temp.getName());
			activeCountArray.put(i, temp.getConfirmed());
		}
		activeJson.put("countries", activeStateArray);
		activeJson.put("count", activeCountArray);
		resultJson.put("activeCases", activeJson);
		/**
		 * cured
		 */

		JSONObject curedJson = new JSONObject();
		JSONArray curedStateArray = new JSONArray();
		JSONArray curedCountArray = new JSONArray();
		for (int i = 0; i < topTenCured.size(); i++) {
			WorldData temp = topTenCured.get(i);
			curedStateArray.put(i, temp.getName());
			curedCountArray.put(i, temp.getRecovered());
		}
		curedJson.put("countries", curedStateArray);
		curedJson.put("count", curedCountArray);
		resultJson.put("curedCases", curedJson);
		/**
		 * deaths
		 */

		JSONObject deathJson = new JSONObject();
		JSONArray deathStateArray = new JSONArray();
		JSONArray deathCountArray = new JSONArray();
		for (int i = 0; i < topTenDeaths.size(); i++) {
			WorldData temp = topTenDeaths.get(i);
			deathStateArray.put(i, temp.getName());
			deathCountArray.put(i, temp.getDeaths());
		}
		deathJson.put("countries", deathStateArray);
		deathJson.put("count", deathCountArray);
		resultJson.put("deathCases", deathJson);

		return getTrendAndBarChartDataForWorld(resultJson, countryId);
	}

	private JSONObject getTrendAndBarChartDataForWorld(JSONObject input, String countryId) {
		JSONObject finalResult = new JSONObject();
		JSONObject finalTopTen = new JSONObject();
		/**
		 * Active
		 */
		JSONObject activeFinal = new JSONObject();
		JSONObject activeDataSet = new JSONObject();
		JSONObject activeCasesCountObj = input.getJSONObject("activeCases");
		JSONArray activeCasesCountArray = activeCasesCountObj.getJSONArray("count");
		JSONArray activeCasesLabels = activeCasesCountObj.getJSONArray("countries");
		activeDataSet.put("label", "Active");
		activeDataSet.put("backgroundColor", getBgColorsForBar());
		activeDataSet.put("data", activeCasesCountArray);
		JSONArray datasetsActive = new JSONArray();
		datasetsActive.put(activeDataSet);
		activeFinal.put("datasets", datasetsActive);
		activeFinal.put("labels", activeCasesLabels);
		finalTopTen.put("Active", activeFinal);
		/**
		 * Deaths
		 */
		JSONObject deathFinal = new JSONObject();
		JSONObject deathDataSet = new JSONObject();
		JSONObject deathCasesCountObj = input.getJSONObject("deathCases");
		JSONArray deathCasesCountArray = deathCasesCountObj.getJSONArray("count");
		JSONArray deathCasesLabels = deathCasesCountObj.getJSONArray("countries");
		deathDataSet.put("label", "Deaths");
		deathDataSet.put("backgroundColor", getBgColorsForBar());
		deathDataSet.put("data", deathCasesCountArray);
		JSONArray datasetsDeath = new JSONArray();
		datasetsDeath.put(deathDataSet);
		deathFinal.put("datasets", datasetsDeath);
		deathFinal.put("labels", deathCasesLabels);
		finalTopTen.put("Deaths", deathFinal);
		/**
		 * Cured
		 */
		JSONObject curedFinal = new JSONObject();
		JSONObject curedDataSet = new JSONObject();
		JSONObject curedCasesCountObj = input.getJSONObject("curedCases");
		JSONArray curedCasesCountArray = curedCasesCountObj.getJSONArray("count");
		JSONArray curedCasesLabels = curedCasesCountObj.getJSONArray("countries");
		curedDataSet.put("label", "Cured");
		curedDataSet.put("backgroundColor", getBgColorsForBar());
		curedDataSet.put("data", curedCasesCountArray);
		JSONArray datasetsCured = new JSONArray();
		datasetsCured.put(curedDataSet);
		curedFinal.put("datasets", datasetsCured);
		curedFinal.put("labels", curedCasesLabels);
		finalTopTen.put("Cured", curedFinal);
		finalResult.put("topTenCountryBar", finalTopTen);

		/**
		 * get last 15 days trend for a country
		 */
		List<WorldCountryTrend> dailTrendList = worldTrendRepository.getLatestFifteenForCountry(countryId);
		LOGGER.info("Records for " + countryId + " are : " + dailTrendList.size());
		if (dailTrendList.size() == 0) {
			try {
				LOGGER.info("There was no data found so refreshing data from source for country: " + countryId);
//				service.refreshLineBarData(countryId);
			} catch (Exception e) {
				LOGGER.error("There is some error in refreshing trend data for : " + countryId + " error: "
						+ e.getMessage() + " cause" + e.getCause());
				e.printStackTrace();
			}
			LOGGER.info("Refresh for bar chart world done again calling trend data....");
			dailTrendList = worldTrendRepository.getLatestFifteenForCountry(countryId);
			LOGGER.info("Now trend data for : " + countryId + " is : " + dailTrendList.size());
		}

		/**
		 * first get the trend for active
		 */
		JSONArray dataSetArrayForAll = new JSONArray();

		/**
		 * Active trend
		 */
		JSONObject activeObj = new JSONObject();
		activeObj.put("fill", false);
		activeObj.put("label", "Active Cases");
		activeObj.put("borderColor", "#4B515D");
		activeObj.put("backgroundColor", "#4B515D");
		activeObj.put("pointBorderColor", "#4B515D");
		activeObj.put("pointRadius", 4);
		activeObj.put("pointHoverRadius", 4);
		activeObj.put("pointBorderWidth", 8);
		JSONArray dataForActive = new JSONArray();

		for (int i = 0; i < dailTrendList.size(); i++) {
			WorldCountryTrend genericData = dailTrendList.get(i);
			dataForActive.put(genericData.getConfirmed());
		}
		activeObj.put("data", dataForActive);
		dataSetArrayForAll.put(activeObj);

		/**
		 * Death trend
		 */
		JSONObject deathObj = new JSONObject();
		deathObj.put("fill", false);
		deathObj.put("label", "Deaths");
		deathObj.put("borderColor", "#ff4444");
		deathObj.put("backgroundColor", "#ff4444");
		deathObj.put("pointBorderColor", "#ff4444");
		deathObj.put("pointRadius", 4);
		deathObj.put("pointHoverRadius", 4);
		deathObj.put("pointBorderWidth", 8);
		JSONArray dataForDeath = new JSONArray();

		for (int i = 0; i < dailTrendList.size(); i++) {
			WorldCountryTrend genericData = dailTrendList.get(i);
			dataForDeath.put(genericData.getDeaths());
		}
		deathObj.put("data", dataForDeath);
		dataSetArrayForAll.put(deathObj);

		/**
		 * Cured trend
		 */
		JSONObject curedObj = new JSONObject();
		curedObj.put("fill", false);
		curedObj.put("label", "Cured");
		curedObj.put("borderColor", "#00C851");
		curedObj.put("backgroundColor", "#00C851");
		curedObj.put("pointBorderColor", "#00C851");
		curedObj.put("pointRadius", 4);
		curedObj.put("pointHoverRadius", 4);
		curedObj.put("pointBorderWidth", 8);
		JSONArray dataForCured = new JSONArray();
		JSONArray labelsForAll = new JSONArray();

		for (int i = 0; i < dailTrendList.size(); i++) {
			WorldCountryTrend genericData = dailTrendList.get(i);
			dataForCured.put(genericData.getRecovered());
			labelsForAll.put(genericData.getPk().getDate());
		}
		curedObj.put("data", dataForCured);
		dataSetArrayForAll.put(curedObj);

		/**
		 * we are done with dataset now create fin obj
		 */
		JSONObject dailyTrendFinalObj = new JSONObject();
		dailyTrendFinalObj.put("labels", labelsForAll);
		dailyTrendFinalObj.put("datasets", dataSetArrayForAll);

		finalResult.put("dailyTrend", dailyTrendFinalObj);

		return finalResult;

	}

	private JSONArray getBgColorsForBar() {
		JSONArray backgroundColorForBar = new JSONArray();
		backgroundColorForBar.put("#3498DB");
		backgroundColorForBar.put("#ABEBC6");
		backgroundColorForBar.put("#orange");
		backgroundColorForBar.put("#F8C471");
		backgroundColorForBar.put("#BB8FCE");
		backgroundColorForBar.put("#73C6B6");
		backgroundColorForBar.put("#80b6f4");
		backgroundColorForBar.put("#f49080");
		backgroundColorForBar.put("#fad874");
		backgroundColorForBar.put("#94d973");
		return backgroundColorForBar;
	}

	public JSONObject getWorldTrendData(String countryId) throws Exception {
		HttpRequest request = new HttpRequest();
		String worldTrendDataUrl = this.env.getProperty("worldDataApi") + "/places/" + countryId + "/data";
		Map<String, String> headers = new HashMap<>();
		headers.put("TestHeader", "test");
		request.setUrl(worldTrendDataUrl);
		request.setHeader(headers);
		request.setRequestMethod(RequestMethod.GET);
		Map<String, String> resultMap = this.restClient.executeExternalApi(request);
		String response = resultMap.get(Constants.RESPONSE_STR);
		boolean isSuccess = Boolean.parseBoolean(resultMap.get(Constants.IS_SUCCESS));
		if (isSuccess) {
			return new JSONObject(response);
		}
		return null;
	}
}
