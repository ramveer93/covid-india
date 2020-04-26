package com.covid.tracker.scheduler;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.covid.tracker.repository.WorldGenericRepository;
import com.covid.tracker.service.CovidTrackerService;

@Component
public class WorldDataScheduler {

	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private CovidTrackerService service;

	@Autowired
	private WorldGenericRepository worldGenericRepo;

	@Scheduled(cron = "0 0 7 * * ?", zone = "Asia/Calcutta")
//	* /5 * * * *
//	every day at 10 * 22 * * *
//	* 0/2 * * * ? ----> worling
//	0 0 0/3 * * ?
//	0 0 12 * * ? -----> every day at 12 
	public void refreshWorldData() {
		LOGGER.info("Scheduler started to refresh world data at : " + new Date().toString());
		try {
			this.service.refreshWorldData();
			LOGGER.info("Done refreshing world data at : " + new Date().toString());
//			LOGGER.info("Started resreshing India data at : " + new Date().toString());
//			this.service.refreshDataFromWeb();
//			LOGGER.info("Successfully resreshed India data at : " + new Date().toString());
		} catch (Exception e1) {
			LOGGER.error("Error refreshing world data , Error is : " + e1.getMessage() + " cause by: "
					+ e1.getCause().getMessage());
		}
		
	}
	@Scheduled(cron = "0 0 0/2 * * ?", zone = "Asia/Calcutta")
	public void refreshIndiaData() {
		try {
//			LOGGER.info("Started refreshing world data at : " + new Date().toString());
//			this.service.refreshWorldData();
//			LOGGER.info("Done refreshing world data at : " + new Date().toString());
			
			LOGGER.info("Started resreshing India data at : " + new Date().toString());
			this.service.refreshDataFromWeb();
			LOGGER.info("Successfully resreshed India data at : " + new Date().toString());
			
		} catch (Exception e1) {
			LOGGER.error("Error refreshing India data , Error is : " + e1.getMessage() + " cause by: "
					+ e1.getCause().getMessage());
		}
	}
}
