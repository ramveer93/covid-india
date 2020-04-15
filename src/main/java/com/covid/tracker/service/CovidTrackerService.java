package com.covid.tracker.service;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.covid.tracker.httpClient.Constants;
import com.covid.tracker.httpClient.HttpRequest;
import com.covid.tracker.httpClient.RestClient;
import com.covid.tracker.model.DistrictResult;
import com.covid.tracker.model.DistrictWiseCasesVo;
import com.covid.tracker.model.GenericData;
import com.covid.tracker.model.NewsFeedData;
import com.covid.tracker.model.PrimaryKeyForDistrictWiseCases;
import com.covid.tracker.model.StateWiseCases;
import com.covid.tracker.repository.DistrictWiseCasesRepository;
import com.covid.tracker.repository.GenericDataRepository;
import com.covid.tracker.repository.NewsFeedDataRepository;
import com.covid.tracker.repository.StateRepository;
import com.covid.tracker.utils.CovidTrackerUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Service
public class CovidTrackerService {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private CovidTrackerUtils utils;

	@Autowired
	ResourceLoader resourceLoader;
	@Autowired
	private StateRepository stateRepo;
	@Autowired
	private RestClient restClient;
	@Autowired
	private NewsFeedDataRepository newsFeedDataRepo;
	@Autowired
	private GenericDataRepository genericDataRepo;
	@Autowired
	private DistrictWiseCasesRepository districtWiseCasesRepository;
	@Autowired
	private static Map<String, String> stateCodes = new HashMap<>();

	public String refreshDataFromWeb() {
		LOGGER.info("service started syncing data from web");
		String jsonData = this.utils.syncDataFromWeb();
		LOGGER.info("service done syncing data from web, msg: " + jsonData);
		return jsonData;
	}

	public String syncStateDistrictMapping() {
		LOGGER.info("service started syncing state-district mapping");
		return this.utils.updateDistrictStateMapping(true);
	}

	public JSONObject getStateData(boolean zinkChartData) {
		List<StateWiseCases> list = this.stateRepo.findAll();
		JSONObject result = new JSONObject();
		JSONArray resultArray = new JSONArray();

		if (zinkChartData) {
			list.forEach(obj -> {
				JSONObject toolTip = new JSONObject();
				toolTip.put("text", obj.getHtmlText());
				toolTip.put("html-mode", true);
				JSONObject label = new JSONObject();
				label.put("visible", false);
				JSONObject innerJsonObj = new JSONObject();
				innerJsonObj.put("tooltip", toolTip);
				innerJsonObj.put("backgroundColor", obj.getBackGroundColor());
				innerJsonObj.put("label", label);
				result.put(obj.getStateCode(), innerJsonObj);

			});

		} else {
			list.forEach((stateObj) -> {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("ActiveCase", stateObj.getActiveCases());
				jsonObject.put("Cured", stateObj.getCuredCount());
				jsonObject.put("Deaths", stateObj.getDeathCount());
				jsonObject.put("Total", stateObj.getTotalCases());
				jsonObject.put("BackGroundColor", stateObj.getBackGroundColor());
				jsonObject.put("HtmlText", stateObj.getHtmlText());
				jsonObject.put("StateCode", stateObj.getStateCode());
				jsonObject.put("StateName", stateObj.getStateName());
				jsonObject.put("UpdatedOn", stateObj.getUpdatedOn());
				resultArray.put(jsonObject);

			});
			result.put("data", resultArray);
		}

		return result;
	}

	public JSONObject getNewsFeedData(HttpRequest request) {
		try {
			Map<String, String> resultMap;
			resultMap = this.restClient.executeExternalApi(request);
			String response = resultMap.get(Constants.RESPONSE_STR);
			System.out.println("response from news: " + response);
			boolean isSuccess = Boolean.parseBoolean(resultMap.get(Constants.IS_SUCCESS));
			JSONObject object = new JSONObject(response);
			JSONArray array = new JSONArray();
			array = object.getJSONArray("articles");
			if (isSuccess && array.length() > 0) {
				LOGGER.info(
						"getNewsFeedData data returned from the news api so manipulating this data and storing in db and return to client");
				JSONArray resultArticles = new JSONArray();
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					if (i <= 10) {
						if (obj.get("author") != JSONObject.NULL && obj.get("publishedAt") != JSONObject.NULL
								&& obj.get("description") != JSONObject.NULL && obj.get("title") != JSONObject.NULL
								&& obj.get("url") != JSONObject.NULL && obj.get("urlToImage") != JSONObject.NULL
								&& obj.get("source") != JSONObject.NULL) {
							JSONObject temp = new JSONObject();
							System.out.println("obj--------" + obj.toString());
							NewsFeedData newsFeed = new NewsFeedData();
							Instant instant = Instant.parse(obj.getString("publishedAt"));
							Date parsedPublishedDate = Date.from(instant);
							newsFeed.setPublishedAt(parsedPublishedDate);
							newsFeed.setAuthorName(obj.getString("author"));
							newsFeed.setDescription(obj.getString("description"));
							newsFeed.setTitle(obj.getString("title"));
							newsFeed.setUrl(obj.getString("url"));
							newsFeed.setUrlToImage(obj.getString("urlToImage"));

							JSONObject source = obj.getJSONObject("source");
							newsFeed.setSourceName(source.getString("name"));
							this.newsFeedDataRepo.save(newsFeed);
							temp.put("publishedAt", newsFeed.getPublishedAt());
							temp.put("author", newsFeed.getAuthorName());
							temp.put("description", newsFeed.getDescription());
							temp.put("title", newsFeed.getTitle());
							temp.put("url", newsFeed.getUrl());
//							temp.put("active", newsFeed.getActive());
							temp.put("source", newsFeed.getSourceName());
							temp.put("urlToImage", newsFeed.getUrlToImage());
//							if(temp.getString("active").equals("carousel-item active")) {
							resultArticles.put(temp);
//							}else if(temp.getString("active").equals("carousel-item")) {
//								resultArticles.put(temp);
//							}
							LOGGER.info("Successfully saved the news feed data to DB");
						}

					} else {
						LOGGER.info("getNewsFeedData: skipping the results for performance improvement");
					}
				}
				JSONObject result = new JSONObject();
				result.put("articles", resultArticles);
//				result.put("totalResults", Integer.parseInt(object.get("totalResults").toString()));
				LOGGER.info("Successfully returned the newsFeed data json from service to controller");
				return result;

			} else {
				LOGGER.error("Some error getting data from news api so returning the DB data");
				return this.getNewsFromDb();
			}

		} catch (Exception e) {
			LOGGER.error(
					"Some error getting data from news api so returning the DB data, the error is : " + e.getMessage());
			return this.getNewsFromDb();
		}
	}

	private JSONObject getNewsFromDb() {
		LOGGER.info(
				"Either there is no data returned from the news api or there is exception getting the data so returning the data from DB");
		List<NewsFeedData> resultList = this.newsFeedDataRepo.findTopTenLatest();
		JSONObject result = new JSONObject();
		JSONArray resultArray = new JSONArray();
		resultList.forEach((newsFeedObj) -> {
//			first get the element which has active as carousel-item active

////			if(newsFeedObj.getActive().equals("carousel-item active")) {
//				JSONObject firstJsonObj = new JSONObject();
//				firstJsonObj.put("active", newsFeedObj.getActive());
//				firstJsonObj.put("author", newsFeedObj.getAuthorName());
//				firstJsonObj.put("description", newsFeedObj.getDescription());
//				firstJsonObj.put("publishedAt", newsFeedObj.getPublishedAt());
//				firstJsonObj.put("source", newsFeedObj.getSourceName());
//				firstJsonObj.put("url", newsFeedObj.getUrl());
//				firstJsonObj.put("urlToImage", newsFeedObj.getUrlToImage());
//				firstJsonObj.put("title", newsFeedObj.getTitle());
//				resultArray.put(0,firstJsonObj);
////			}else if(newsFeedObj.getActive().equals("carousel-item")) {
			JSONObject jsonObject = new JSONObject();
//				jsonObject.put("active", newsFeedObj.getActive());
			jsonObject.put("author", newsFeedObj.getAuthorName());
			jsonObject.put("description", newsFeedObj.getDescription());
			jsonObject.put("publishedAt", newsFeedObj.getPublishedAt());
			jsonObject.put("source", newsFeedObj.getSourceName());
			jsonObject.put("url", newsFeedObj.getUrl());
			jsonObject.put("urlToImage", newsFeedObj.getUrlToImage());
			jsonObject.put("title", newsFeedObj.getTitle());
			resultArray.put(jsonObject);

		});
		result.put("articles", resultArray);
		LOGGER.info("Successfully returned the newsFeed data json from service vie DB");
		return result;
	}

	public JSONObject getLatestGenericData() {
		GenericData genericData = genericDataRepo.findLatestUpdatedDate();
		JSONObject resultJson = new JSONObject();
		if(genericData!=null) {
			resultJson.put("totalActive", genericData.getActiveCases());
			resultJson.put("totalCured", genericData.getCured());
			resultJson.put("totalDeaths", genericData.getDeaths());
			resultJson.put("totalMigrated", genericData.getMigrated());
			resultJson.put("updatedOn", genericData.getUpdatedOn());
			List<GenericData> genericDataList = genericDataRepo.findTwoLatest();
			if (genericDataList.size() == 2) {
				int active = 
						 (genericDataList.get(0).getActiveCases()) - (genericDataList.get(1).getActiveCases());
				String activeIncrement = (active<0?"-"+String.valueOf(active):"+"+String.valueOf(active));
				int cured = (genericDataList.get(0).getCured()) - (genericDataList.get(1).getCured());
				String curedIncrement = (cured<0?"-"+String.valueOf(cured):"+"+String.valueOf(cured));
				
				int death = (genericDataList.get(0).getDeaths()) - (genericDataList.get(1).getDeaths());
				String deathIncrement = (death<0?"-"+String.valueOf(death):"+"+String.valueOf(death));
				
				int migrated = (genericDataList.get(0).getMigrated()) - (genericDataList.get(1).getMigrated());
				String migratedIncrement = (migrated<0?"-"+String.valueOf(migrated):"+"+String.valueOf(migrated));
				
				resultJson.put("activeIncrement", activeIncrement);
				resultJson.put("curedIncrement", curedIncrement);
				resultJson.put("deathIncrement", deathIncrement);
				resultJson.put("migratedIncrement", migratedIncrement);

			}
		}
		return resultJson;
	}

	public JSONObject getLineAndBarData() {
		List<StateWiseCases> topTenActive = stateRepo.getTopTenBasedOnActive();
		List<StateWiseCases> topTenCured = stateRepo.getTopTenBasedOnCured();
		List<StateWiseCases> topTenDeaths = stateRepo.getTopTenBasedOnDeaths();

		JSONObject resultJson = new JSONObject();
		/**
		 * Active
		 */

		JSONObject activeJson = new JSONObject();
		JSONArray activeStateArray = new JSONArray();
		JSONArray activeCountArray = new JSONArray();
		for (int i = 0; i < topTenActive.size(); i++) {
			StateWiseCases temp = topTenActive.get(i);
			activeStateArray.put(i, temp.getStateName());
			activeCountArray.put(i, temp.getActiveCases());
		}
		activeJson.put("states", activeStateArray);
		activeJson.put("count", activeCountArray);
		resultJson.put("activeCases", activeJson);
		/**
		 * cured
		 */

		JSONObject curedJson = new JSONObject();
		JSONArray curedStateArray = new JSONArray();
		JSONArray curedCountArray = new JSONArray();
		for (int i = 0; i < topTenCured.size(); i++) {
			StateWiseCases temp = topTenCured.get(i);
			curedStateArray.put(i, temp.getStateName());
			curedCountArray.put(i, temp.getCuredCount());
		}
		curedJson.put("states", curedStateArray);
		curedJson.put("count", curedCountArray);
		resultJson.put("curedCases", curedJson);
		/**
		 * deaths
		 */

		JSONObject deathJson = new JSONObject();
		JSONArray deathStateArray = new JSONArray();
		JSONArray deathCountArray = new JSONArray();
		for (int i = 0; i < topTenDeaths.size(); i++) {
			StateWiseCases temp = topTenDeaths.get(i);
			deathStateArray.put(i, temp.getStateName());
			deathCountArray.put(i, temp.getDeathCount());
		}
		deathJson.put("states", deathStateArray);
		deathJson.put("count", deathCountArray);
		resultJson.put("deathCases", deathJson);

		return getTrendAndBarChartData(resultJson);
	}

	private JSONObject getTrendAndBarChartData(JSONObject input) {
		JSONObject finalResult = new JSONObject();
		JSONObject finalTopTen = new JSONObject();
		/**
		 * Active
		 */
		JSONObject activeFinal = new JSONObject();
		JSONObject activeDataSet = new JSONObject();
		JSONObject activeCasesCountObj = input.getJSONObject("activeCases");
		JSONArray activeCasesCountArray = activeCasesCountObj.getJSONArray("count");
		JSONArray activeCasesLabels = activeCasesCountObj.getJSONArray("states");
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
		JSONArray deathCasesLabels = deathCasesCountObj.getJSONArray("states");
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
		JSONArray curedCasesLabels = curedCasesCountObj.getJSONArray("states");
		curedDataSet.put("label", "Cured");
		curedDataSet.put("backgroundColor", getBgColorsForBar());
		curedDataSet.put("data", curedCasesCountArray);
		JSONArray datasetsCured = new JSONArray();
		datasetsCured.put(curedDataSet);
		curedFinal.put("datasets", datasetsCured);
		curedFinal.put("labels", curedCasesLabels);
		finalTopTen.put("Cured", curedFinal);
		finalResult.put("topTenStateBar", finalTopTen);

		/**
		 * Now get data for top 10 districts first get from db top 10 districts
		 */

		List<DistrictWiseCasesVo> topTenActiveDistricts = this.districtWiseCasesRepository.getTopTenActiveDistricts();

		JSONObject activeDistrictJson = new JSONObject();
		JSONArray activeDistrictArray = new JSONArray();
		JSONArray activeCountDistrictArray = new JSONArray();

		JSONObject activeDistrictResult = new JSONObject();

		for (int i = 0; i < topTenActiveDistricts.size(); i++) {
			DistrictWiseCasesVo temp = topTenActiveDistricts.get(i);
			activeDistrictArray.put(i, temp.getPrimaryKey().getDistrictName());
			activeCountDistrictArray.put(i, temp.getPositiveCases());
		}
		activeDistrictJson.put("districts", activeDistrictArray);
		activeDistrictJson.put("count", activeCountDistrictArray);
		activeDistrictResult.put("activeDistrict", activeDistrictJson);
		/**
		 * now since we have the data for top 10 districts we will cook the bar chart
		 * data
		 */
		/**
		 * district
		 */

		JSONObject finalTopTenDistrict = new JSONObject();
		JSONObject districtFinal = new JSONObject();
		JSONObject districtDataSet = new JSONObject();
		JSONObject districtCasesCountObj = activeDistrictResult.getJSONObject("activeDistrict");
		JSONArray districtCasesCountArray = districtCasesCountObj.getJSONArray("count");
		JSONArray districtCasesLabels = districtCasesCountObj.getJSONArray("districts");
		districtDataSet.put("label", "Active");
		districtDataSet.put("backgroundColor", getBgColorsForBar());
		districtDataSet.put("data", districtCasesCountArray);
		JSONArray datasetsDistrict = new JSONArray();
		datasetsDistrict.put(districtDataSet);
		districtFinal.put("datasets", datasetsDistrict);
		districtFinal.put("labels", districtCasesLabels);
		finalTopTenDistrict.put("Active", districtFinal);
		finalResult.put("topTenDistrictBar", finalTopTenDistrict);

		/**
		 * now get dailyTrend data for 3 categories first get the data from db
		 */

		List<GenericData> dailTrendList = genericDataRepo.findDailyTrendForLastTenDays();

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
			GenericData genericData = dailTrendList.get(i);
			dataForActive.put(genericData.getActiveCases());
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
			GenericData genericData = dailTrendList.get(i);
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
			GenericData genericData = dailTrendList.get(i);
			dataForCured.put(genericData.getCured());
			labelsForAll.put(genericData.getUpdatedOn());
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

	public JSONArray getDistrictData() {

		if (stateCodes.isEmpty()) {
			updateStateCodes();
		}

		List<StateWiseCases> allStates = stateRepo.findAll();
		List<DistrictWiseCasesVo> allDistricts = districtWiseCasesRepository.findAll();
		JSONArray result = new JSONArray();

		for (int i = 0; i < allStates.size(); i++) {
			StateWiseCases state = allStates.get(i);
			JSONObject stateObj = new JSONObject();
			JSONArray districtArray = new JSONArray();

			for (int j = 0; j < allDistricts.size(); j++) {

				DistrictWiseCasesVo district = allDistricts.get(j);
				String expectedDistCode = stateCodes.get(state.getStateName());
				System.out.println("-----expectedDistCode----"+expectedDistCode+" for state.getStateName()"+state.getStateName());
				System.out.println("district.getPrimaryKey().getStateName()........."+district.getPrimaryKey().getStateName());
				if (expectedDistCode!=null && expectedDistCode.equals(district.getPrimaryKey().getStateName())

				) {

					stateObj.put("state", district.getPrimaryKey().getStateName());
					stateObj.put("activeCases", state.getActiveCases());
					stateObj.put("cured", state.getCuredCount());
					stateObj.put("deaths", state.getDeathCount());
					stateObj.put("total", state.getTotalCases());
					if (state.getStateName().equals("J&K")) {
						stateObj.put("classx", "JK");
					} else {
						stateObj.put("classx", state.getStateName());
					}

					JSONObject districtJson = new JSONObject();
					districtJson.put("ActiveCases", district.getPositiveCases());
					districtJson.put("DistrictName", district.getPrimaryKey().getDistrictName());
					districtArray.put(districtJson);
				}
			}
			if (!districtArray.isEmpty()) {
				stateObj.put("district", districtArray);
				result.put(stateObj);
			}

		}

		return result;

	}

	private String updateStateCodes() {
		String msg = "";
		try {
			JSONObject obj = this.utils.loadJSONObject("state.json");
			Iterator<String> keys = obj.keys();

			while (keys.hasNext()) {
				String state = keys.next();
				String dist = obj.get(state).toString();
				stateCodes.put(state, dist);
			}
		} catch (Exception e) {
			msg = e.getMessage();
		}
		return msg;
	}
}
