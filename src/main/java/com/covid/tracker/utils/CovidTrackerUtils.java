package com.covid.tracker.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import com.covid.tracker.model.DistrictList;
import com.covid.tracker.model.DistrictWiseCasesVo;
import com.covid.tracker.model.GenericData;
import com.covid.tracker.model.IndiaStateTrend;
import com.covid.tracker.model.IndiaStateTrendPk;
import com.covid.tracker.model.PrimaryKeyForDistrictWiseCases;
import com.covid.tracker.model.StateWiseCases;
import com.covid.tracker.repository.DistrictRepository;
import com.covid.tracker.repository.DistrictWiseCasesRepository;
import com.covid.tracker.repository.GenericDataRepository;
import com.covid.tracker.repository.IndiaStateTrendRepository;
import com.covid.tracker.repository.StateRepository;
import com.covid.tracker.service.CovidTrackerService;

@Component
public class CovidTrackerUtils {

	private static final String DD_MMMM_YYYY_HH_MM = "dd MMMM yyyy, HH:mm";
	private static final String GMT_5_30 = "GMT+5:30";
	@Autowired
	private StateRepository stateRepo;
	@Autowired
	private GenericDataRepository genericDataRepo;

	@Autowired
	ResourceLoader resourceLoader;

	@Autowired
	private DistrictRepository districtRepo;
	@Autowired
	private IndiaStateTrendRepository stateTrendRepo;
	@Autowired
	private CovidTrackerService covidService;

	@Autowired
	private DistrictWiseCasesRepository districtWiseCasesRepo;
	private static Map<String, String> districtStateMapping = new HashMap<>();
	private static Map<String, String> stateCodes = new HashMap<>();

	@Autowired
	private Environment env;

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	public String syncDataFromWeb() {
		LOGGER.info("utils sync started from web");
		String msg = "";
		try {
			findAndStoreStateData();
			findAndStoreDistrictData();
		} catch (Exception e) {
			msg = "Error in sync: " + e.getMessage();
			LOGGER.error("Error in refresh: " + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info("utils: the message after sync of data from web is " + msg);
		return msg;
	}

	private boolean isNumber(String number) {
		if (number == null) {
			return false;
		}
		try {
			Integer.parseInt(number);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private void findAndStoreStatePhoneNumbers(String telephonePdf) throws Exception {
		URL pdfurl = new URL(telephonePdf);
		InputStream in = pdfurl.openStream();
		PDDocument pddDocument = PDDocument.load(in);
		PDFTextStripper textStripper = new PDFTextStripper();
		String doc = textStripper.getText(pddDocument);
		String[] lines = doc.split("\\r?\\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			try {
				if (line.contains(",")) {
					line = line.split(",")[0];
				}
			} catch (Exception e) {
				LOGGER.error(
						"Error in parsing telephone string for , " + e.getMessage() + " cause: " + e.getStackTrace());
			}

			String[] row = line.split("\\s");
			int strsSize = row.length;
			String stateName = "";
			String phoneNumber = "";
			this.stateRepo.updatePhoneNumber("03192-232102", "ANDAMANNICOBAR");
			if (strsSize > 0) {
				if (isNumber(row[0].trim())) {
					if (strsSize == 3 && isNumber(row[2].substring(0, 2))) {
						stateName = row[1].replaceAll("\\\\s", "").toUpperCase();
						phoneNumber = row[2].trim();
					} else if (strsSize == 4 && isNumber(row[3].substring(0, 2))) {
						stateName = (row[1] + row[2]).replaceAll("\\\\s", "").toUpperCase();
						phoneNumber = row[3].trim();
					} else if (strsSize >= 5 && isNumber(row[strsSize - 1].substring(0, 2))) {
						String state = "";
						for (int j = 0; j < strsSize - 1; j++) {
							state = state + row[j];
						}
						state = state.trim().toUpperCase();
						stateName = state.replace("5", "");
						phoneNumber = row[strsSize - 1];
					}
					if (stateName.equals("MADHYAPRADESH")) {
						stateName = "MP";
					} else if (stateName.equals("TELANGANA")) {
						stateName = "TELENGANA";
					} else if (stateName.equals("JAMMU&KASHMIR")) {
						stateName = "J&K";
					}
					this.stateRepo.updatePhoneNumber(phoneNumber, stateName);
				}
			}

		}

	}

	private void findAndStoreDistrictData() throws Exception {
		LOGGER.info("utils: findAndStoreDistrictData started district data");
		Map<String, String> pdfUrls = getDistrictPdf();
		String districtPdfUrl = pdfUrls.get("districtPdf");
		String telephonePdf = pdfUrls.get("telephonePdf");
		findAndStoreStatePhoneNumbers(telephonePdf);
		String districtPdf = districtPdfUrl == null ? this.genericDataRepo.findLatestDistrictPdfUrl() : districtPdfUrl;
		URL pdfurl = new URL(districtPdf);
		InputStream in = pdfurl.openStream();
		PDDocument pddDocument = PDDocument.load(in);
		PDFTextStripper textStripper = new PDFTextStripper();
		String doc = textStripper.getText(pddDocument);
		String[] lines = doc.split("\\r?\\n");
		Date latestUpdatedDate = this.genericDataRepo.findLatestUpdatedDate().getUpdatedOn();
		if (districtStateMapping.isEmpty()) {
			LOGGER.debug(
					"state district mapping map is found empty so syncing this map , there wont be any DB calls here");
			updateDistrictStateMapping(false);
		}
		LOGGER.debug("There are total " + lines.length
				+ " lines in the pdf file, All lines will be read one by one to extract the data");
		for (int i = 0; i < lines.length; i++) {
			String singleLineData = lines[i];
			if (!singleLineData.startsWith("State")) {
				String[] singleLine = singleLineData.split("(?<=\\D)(?=\\d)");
				int lengthOfString = singleLine.length;
				if (lengthOfString == 2 && isNumber(singleLine[1].trim())) {
					String districtName = singleLine[0].trim();
					if (Character.isDigit(districtName.charAt(0))) {
						districtName = districtName.replaceAll("[0-9]", "").trim();

					}
					int casesInDistrict = Integer.parseInt(singleLine[1].trim());
					String stateName = districtStateMapping.get(districtName.toUpperCase());
					if (stateName != null && !(stateName.replaceAll("\\s", "").toUpperCase())
							.equals(districtName.replaceAll("\\s", "").toUpperCase())) {
						DistrictWiseCasesVo districtData = new DistrictWiseCasesVo();
						PrimaryKeyForDistrictWiseCases pk = new PrimaryKeyForDistrictWiseCases();
						pk.setDistrictName(districtName);
						pk.setStateName(stateName);
						districtData.setPrimaryKey(pk);
						districtData.setPositiveCases(casesInDistrict);
						districtData.setCreatedOn(latestUpdatedDate);

//						LOGGER.info("Saving for district: "+districtData.getPrimaryKey().getDistrictName()+" state: "+districtData.getPrimaryKey().getStateName()+" cases: "+casesInDistrict);
						districtWiseCasesRepo.save(districtData);
					} else {
						LOGGER.info("Not saving for stateName " + stateName + " districtName " + districtName);
					}

				} else if (lengthOfString == 3 && isNumber(singleLine[2].trim())) {
					String partialDistrict = singleLine[1];
					String numberInString = (partialDistrict.replaceAll("[^0-9]", "")).trim();
					String districtName = partialDistrict.replaceAll(numberInString, "").trim();
					int casesInDistrict = Integer.parseInt(singleLine[2].trim());
					String stateName = districtStateMapping.get(districtName.toUpperCase());
					stateName = (stateName == null ? districtName : stateName);
					DistrictWiseCasesVo districtData = new DistrictWiseCasesVo();
					PrimaryKeyForDistrictWiseCases pk = new PrimaryKeyForDistrictWiseCases();
					if (districtName.startsWith("*")) {
						districtName.replace("*", "");
						districtName.trim();
					}
					pk.setDistrictName(districtName);
					pk.setStateName(stateName);
					districtData.setPrimaryKey(pk);
					districtData.setPositiveCases(casesInDistrict);
					districtData.setCreatedOn(latestUpdatedDate);
//					LOGGER.info("Saving for district: "+districtData.getPrimaryKey().getDistrictName()+" state: "+districtData.getPrimaryKey().getStateName());
					districtWiseCasesRepo.save(districtData);
				}else {
					LOGGER.info("Not saving for this line .." + singleLineData);
				}
				
			}
		}
		if (pddDocument != null) {
			pddDocument.close();
		}

	}

	private Map<String, String> getDistrictPdf() {
		String url = this.env.getProperty("districtPdfSite");
		try {
			Document doc = Jsoup.connect(url).get();
			Elements contentClearfix = doc.getElementsByClass("canvas-menu");
			Element contentClearFixElement = contentClearfix.get(0);
			Elements urlElement = contentClearFixElement.getElementsByAttributeValue("target", "_blank");
			String districtPdf = null;
			try {
				districtPdf = urlElement.get(0).absUrl("href");
			} catch (Exception e) {
				LOGGER.error("Error fetching district report " + e.getMessage());
			}

			Elements colXs12TextCenter = doc.getElementsByClass("col-xs-12 text-center");
			Elements phoneElements = colXs12TextCenter.get(0).getElementsByAttributeValue("target", "_blank");
			Elements telephones = phoneElements.get(0).getElementsContainingText("Union Territories");
			String urlForTelephonePdf = telephones.get(0).absUrl("href");
			LOGGER.info(" Telephone pdf url .." + urlForTelephonePdf);
			LOGGER.info(" District pdf url .." + districtPdf);
			Map<String, String> result = new HashMap<>();
			result.put("districtPdf", districtPdf);
			result.put("telephonePdf", urlForTelephonePdf);
			return result;
		} catch (Exception e) {
			LOGGER.error("Error extracting the district pdf url for mohfw " + e.getMessage() + " cause: "
					+ e.getStackTrace() + " Other error : " + e.getCause());
			return null;
		}
	}

	private void findAndStoreStateData() throws IOException {
		LOGGER.info("started syning state data from web");
		String url = env.getProperty("crawlerurl");
		LOGGER.info("URL of web to sync data is " + url);
		Map<String, String> hashMap = new HashMap<>();
		GenericData genericData = new GenericData();
		Document doc = Jsoup.connect(url).get();
		Element root = doc.getElementById("block-system-main");
		Elements contentClearfix = root.getElementsByClass("content clearfix");
		Map<String, StateWiseCases> stateDataMap = new HashMap<>();
		if (stateCodes.isEmpty()) {
			LOGGER.debug("state code map is empty so filling it with static codes");
			updateStateCodes();
		}

//	        Elements updatedLabel = null;
		for (Element div : contentClearfix) {
			LOGGER.debug("Trying to read the value and label for last updated on");
			Elements updatedLabel = div
					.getElementsByClass("field field-name-field-covid-india-as-on field-type-text field-label-above");
//	        	this for loop is to get the last updated text
			for (Element updatedOn : updatedLabel) {
				Elements labels = updatedOn.getElementsByClass("field-label");
				Elements updatedOnDate = updatedOn.getElementsByClass("field-items");
				String lastUpdatedDate = updatedOnDate.get(0).text();
				LOGGER.info("Successfully got value of last updated, it is : " + lastUpdatedDate);
				hashMap.put("LastUpdatedOn", lastUpdatedDate);
				genericData.setUpdatedOn(parseDate(lastUpdatedDate, GMT_5_30, DD_MMMM_YYYY_HH_MM));

			}
//	        	Total airport passenger screened counts

			Elements passengerScreenedOnAirports = div.getElementsByClass(
					"field field-name-field-passenger-screened-format field-type-text field-label-above");
			for (Element element : passengerScreenedOnAirports) {
				Elements airportPassengerLabel = element.getElementsByClass("field-label");
//				LOGGER.info("passenger screened on airport label : " + airportPassengerLabel.get(0).text());
				Elements airportPassengerCount = element.getElementsByClass("field-items");
//				LOGGER.info("totoal airport passenger count: " + airportPassengerCount.get(0).text());
				String totalAirportPassengersScreened = (airportPassengerCount.get(0).text()).replaceAll(",", "");
				hashMap.put("TotalAirportPassengerScreened", totalAirportPassengersScreened);
				genericData.setPassengerScreenedOnAirport(Integer.parseInt(totalAirportPassengersScreened));
//				LOGGER.info("Successfully got value of total passengers screened on airport, it is : "
//						+ totalAirportPassengersScreened);
			}

//	        	active cases
//			LOGGER.info("Trying to read the value and label for total active cases");
			Elements activeCases = div.getElementsByClass(
					"field field-name-field-total-active-case field-type-number-integer field-label-above");
			for (Element element : activeCases) {
				Elements activeCasesLabel = element.getElementsByClass("field-label");
//				LOGGER.info("activeCasesLabel label : " + activeCasesLabel.get(0).text());
				Elements activeCasesCount = element.getElementsByClass("field-items");
//				LOGGER.info("activeCasesCount count: " + activeCasesCount.get(0).text());
				String totalActiveCases = activeCasesCount.get(0).text();
				hashMap.put("ActiveCases", totalActiveCases);
				genericData.setActiveCases(Integer.parseInt(totalActiveCases.replaceAll(",", "")));
				LOGGER.info("Successfully got value of total active cases, it is : " + totalActiveCases);
			}

//	        	cured/Discharged
//			LOGGER.info("Trying to read the value and label for total cured/discharged count");
			Elements curedDischarged = div.getElementsByClass(
					"field field-name-field-total-cured-discharged field-type-number-integer field-label-above");
			for (Element element : curedDischarged) {
				Elements curedDischargedLabel = element.getElementsByClass("field-label");
//				LOGGER.debug("curedDischargedLabel label : " + curedDischargedLabel.get(0).text());
				Elements curedDischargedCount = element.getElementsByClass("field-items");
//				LOGGER.debug("curedDischargedCount count: " + curedDischargedCount.get(0).text());
				String totalCured = curedDischargedCount.get(0).text();
				hashMap.put("Cured", totalCured);
				genericData.setCured(Integer.parseInt(totalCured.replaceAll(",", "")));
				LOGGER.info("Successfully got value of total cured cases, it is : " + totalCured);
			}

//	        	Migrated
//			LOGGER.info("Trying to read the value and label for total migrated count");
			Elements migrated = div
					.getElementsByClass("field field-name-field-migrated-counts field-type-text field-label-above");
			for (Element element : migrated) {
				Elements migratedLabel = element.getElementsByClass("field-label");
//				LOGGER.debug("migratedLabel label : " + migratedLabel.get(0).text());
				Elements migratedCount = element.getElementsByClass("field-items");
//				LOGGER.debug("curedDischargedCount count: " + migratedCount.get(0).text());
				String totalMigrated = migratedCount.get(0).text();
				hashMap.put("Migrated", totalMigrated);
				genericData.setMigrated(Integer.parseInt(totalMigrated.replaceAll(",", "")));
//				LOGGER.info("Successfully got value of total migrated , it is : " + totalMigrated);
			}

//	        	Deaths
//			LOGGER.info("Trying to read the value and label for total deaths count");
			Elements deaths = div.getElementsByClass(
					"field field-name-field-total-death-case field-type-number-integer field-label-above");
			for (Element element : deaths) {
				Elements deathsLabel = element.getElementsByClass("field-label");
//				LOGGER.debug("deathsLabel label : " + deathsLabel.get(0).text());
				Elements deathCount = element.getElementsByClass("field-items");
//				LOGGER.debug("deathCount count: " + deathCount.get(0).text());
				String totalDeaths = deathCount.get(0).text();
				hashMap.put("Deaths", totalDeaths);
				genericData.setDeaths(Integer.parseInt(totalDeaths.replaceAll(",", "")));
				LOGGER.info("Successfully got value of total deaths , it is : " + totalDeaths);
			}

//	        	District reporting
//			LOGGER.info("Trying to read the value for district report pdf");
			Elements districtReporting = div
					.getElementsByClass("field field-name-field-district-reporting field-type-text field-label-above");
			for (Element element : districtReporting) {
				Elements districtReportingLabel = element.getElementsByClass("field-label");
//				LOGGER.debug("districtReportingLabel label : " + districtReportingLabel.get(0).text());
				Elements districtReportingUrl = element.getElementsByClass("field-items");
//				LOGGER.debug("districtReportingUrl : " + districtReportingUrl.get(0).text());
				String districtReport = districtReportingUrl.get(0).text();
				hashMap.put("DistrictReportPdf", districtReport);
				genericData.setDistrictWiseUrl(districtReport);
				LOGGER.info("Successfully got value of district pdf report , it is : " + districtReport);
			}
//	        	Top states data
//			LOGGER.info("Trying to read the value for state data");
			Elements stateWise = div.getElementsByClass("field-collection-container clearfix");
			for (Element element : stateWise) {
				Elements stateWiseLabel = element.getElementsByClass(
						"field field-name-field-covid-statewise-data field-type-field-collection field-label-above");
//				LOGGER.debug("stateWiseLabel label : " + stateWiseLabel.get(0).text());
				String statesString = stateWiseLabel.get(0).text();
				String[] statesData = statesString.split("State Name:");
				LOGGER.info("Now is the dirty logic to get state level data!!!");
				for (String stateData : statesData) {
					StateWiseCases state = new StateWiseCases();
					String temp = stateData;
					int stateIndex = temp.indexOf("Total Confirmed:");
					int confirmCasesIndex = temp.indexOf("Cured/Discharged/Migrated:");
					int deathIndex = temp.indexOf("Death:");
					if (stateIndex != -1 && confirmCasesIndex != -1 && deathIndex != -1) {
						String stateName = temp.substring(0, stateIndex);
						stateName = stateName.replaceAll("\\s", "");
//						LOGGER.debug("stateName: " + stateName);
						state.setStateName(stateName.trim().toUpperCase());
						String confirmedCases = temp.substring(stateIndex, confirmCasesIndex).split(":")[1];
//						LOGGER.debug("Confirmed cases in this state " + confirmedCases);
						state.setActiveCases(Integer.parseInt(confirmedCases.trim()));
						String curedCases = temp.substring(confirmCasesIndex, deathIndex).split(":")[1];
//						LOGGER.debug("Total cured in this state: " + curedCases);
						String deathCases = temp.substring(deathIndex + 1, temp.length()).split(":")[1];
//						LOGGER.debug("Total deaths in this state : " + deathCases);
						state.setDeathCount(Integer.parseInt(deathCases.trim()));
						state.setCuredCount(Integer.parseInt(curedCases.trim()));
						state.setUpdatedOn(parseDate(hashMap.get("LastUpdatedOn"), GMT_5_30, DD_MMMM_YYYY_HH_MM));
						state.setTotalCases(state.getActiveCases() + state.getCuredCount() + state.getDeathCount());
						LOGGER.info("getting state code for state..." + state.getStateName());
						state.setStateCode(stateCodes.get(state.getStateName()));
						state.setHtmlText(getHtmlStringForZingChart(state));
//						state.setBackGroundColor(getRandomColor());
						stateDataMap.put(stateName, state);
//						LOGGER.info("Successfully got data for state : " + state.toString());
					}
				}
			}
//			LOGGER.debug("Saving generic data to DB table!!");
			genericDataRepo.save(genericData);
//			LOGGER.debug("Saving the generic data in DB table");
//			LOGGER.debug("successfully saved generic data to table ");
			List<StateWiseCases> stateWiseCasesList = setColors(new ArrayList<>(stateDataMap.values()));
			for (StateWiseCases stateData : stateWiseCasesList) {
//				StateWiseCases state = stateData.getValue();
//				LOGGER.debug("saving state data to DB for state:  " + stateData);
				stateRepo.save(stateData);
				IndiaStateTrend stateTrend = new IndiaStateTrend();
				IndiaStateTrendPk pk = new IndiaStateTrendPk();
				pk.setStateName(stateData.getStateName());
				pk.setDate(stateData.getUpdatedOn());
				stateTrend.setPk(pk);
				stateTrend.setActive(stateData.getActiveCases());
				stateTrend.setCured(stateData.getCuredCount());
				stateTrend.setDeaths(stateData.getDeathCount());
				stateTrendRepo.save(stateTrend);
				LOGGER.info(
						"successfully saved data for " + stateData.getStateName() + " in state cases and state trend");
//				LOGGER.debug("Successfully saved state data for state:  " + stateData);
//				LOGGER.info("state wise data saved successfully Now saving the data for zing Chart");

			}
		}
	}

	private List<StateWiseCases> setColors(List<StateWiseCases> input) {
		List<StateWiseCases> result = new ArrayList<>();
		LOGGER.info("setting color");
		try {
//			JSONObject jsonObject = loadJSONObject("colors.json");
//			input.sort(Comparator.comparing(StateWiseCases::getTotalCases).reversed()
//					.thenComparing(StateWiseCases::getDeathCount));

			for (int i = 0; i < input.size(); i++) {
				StateWiseCases stateWiseCases = input.get(i);
//				stateWiseCases.setBackGroundColor(jsonObject.getString(String.valueOf(i)));
				stateWiseCases.setBackGroundColor(setColor(stateWiseCases.getTotalCases()));
				result.add(stateWiseCases);
			}
		} catch (Exception e) {
			LOGGER.error("Error in set colors...." + e.getMessage());
		}
		return result;

	}

	private String setColor(long totalCases) {
		String bgColor = "";
		if (totalCases >= 5000) {
			bgColor = "#7a0177";
		} else if (totalCases >= 4000 && totalCases < 5000) {
			bgColor = "#ae017e";
		} else if (totalCases >= 3000 && totalCases < 4000) {
			bgColor = "#dd3497";
		} else if (totalCases >= 2000 && totalCases < 3000) {
			bgColor = "#f768a1";
		} else if (totalCases >= 1000 && totalCases < 2000) {
			bgColor = "#fa9fb5";
		} else if (totalCases >= 500 && totalCases < 1000) {
			bgColor = "#fcc5c0";
		} else if (totalCases < 500) {
			bgColor = "#fde0dd";
		}
		return bgColor;
	}

	public String getRandomColor() {
		Random random = new Random();
		int nextInt = random.nextInt(0xffffff + 1);
		return String.format("#%06x", nextInt);

	}

	private String getHtmlStringForZingChart(StateWiseCases stateData) {
		return "<table class='table-sm  table-borderless'><tbody><tr><th>"
				+ this.covidService.capetalizeFirstChar(stateData.getStateName()) + "</th></tr><tr><th>Total</th><td class='float-right'>"
				+ stateData.getTotalCases() + "</th></tr><tr><th>Active</th><td class='float-right'>"
				+ stateData.getActiveCases() + "</th></tr><tr><th>Deaths</th><td class='float-right'>"
				+ stateData.getDeathCount() + "</th></tr><tr><th>Cured</th><td class='float-right'>"
				+ stateData.getCuredCount() + "</th></tr></tbody></table>";
	}

	public Timestamp parseDate(String date, String timeZone, String format) {
//		LOGGER.debug("convert date: " + date + " to sql date");
		date = timeZone == null ? date : date.substring(0, date.indexOf(timeZone));
//		LOGGER.debug("date without gmt string: " + date);
		DateFormat converter = new SimpleDateFormat(format);
		try {
			Date parsedDate = converter.parse(date);
//			LOGGER.debug("Parsed date: " + parsedDate.toString());
			Timestamp sqlDate = new java.sql.Timestamp(parsedDate.getTime());
//			LOGGER.debug("Timestamp of this Date!!" + sqlDate);
			return sqlDate;
		} catch (ParseException e) {
			e.printStackTrace();
			LOGGER.error("Error parsing the date : " + e.getMessage() + " " + e.getCause() + " " + e.getStackTrace());
		}
		return null;
	}

	private String updateStateCodes() {
		String msg = "";
		try {
			JSONObject obj = loadJSONObject("stateCodes.json");
			Iterator<String> keys = obj.keys();

			while (keys.hasNext()) {
				String stateName = keys.next();
				String stateCode = obj.get(stateName).toString();
				CovidTrackerUtils.stateCodes.put(stateName.trim().toUpperCase(), stateCode);
			}
		} catch (Exception e) {
			msg = e.getMessage();
		}
		return msg;
	}

	public String updateDistrictStateMapping(boolean updateTable) {
		LOGGER.debug("Started updating the state-district mapping");
		String msg = "";
		try {
			JSONObject obj = loadJSONObject("districts.json");
			JSONArray states = obj.getJSONArray("states");
			for (int i = 0; i < states.length(); i++) {
				JSONObject districts = states.getJSONObject(i);
				String state = districts.getString("state");
				JSONArray districtArray = districts.getJSONArray("districts");
				for (int j = 0; j < districtArray.length(); j++) {
					String district = districtArray.getString(j);
					DistrictList districtObj = new DistrictList();
					PrimaryKeyForDistrictWiseCases pk = new PrimaryKeyForDistrictWiseCases();
					pk.setDistrictName(district.toUpperCase());
					pk.setStateName(state.toUpperCase());
					districtObj.setPrimaryKey(pk);
					CovidTrackerUtils.districtStateMapping.put(district.toUpperCase(), state.toUpperCase());
					if (updateTable) {
						districtRepo.save(districtObj);
					}

				}
			}
		} catch (Exception e) {
			msg = e.getMessage();
			LOGGER.error("Error syncing the state-district mapping : " + e.getMessage());
		}
		return msg;
	}

	public JSONObject loadJSONObject(String fileName) throws IOException {
		String data = "";
		ClassPathResource cpr = new ClassPathResource(fileName);
		try {
			byte[] bdata = FileCopyUtils.copyToByteArray(cpr.getInputStream());
			data = new String(bdata, StandardCharsets.UTF_8);
			return new JSONObject(data);
		} catch (IOException e) {
			LOGGER.error("Error loading the json file " + e.getMessage());
		}
		return null;

	}
}
