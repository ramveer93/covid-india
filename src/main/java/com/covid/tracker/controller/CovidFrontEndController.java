package com.covid.tracker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CovidFrontEndController {
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	@RequestMapping("/")
	public ModelAndView indexTest() {
		LOGGER.info("Request comes to front end controller");
		ModelAndView modelAndView = new ModelAndView();
		LOGGER.info("Loading the index view ");
		modelAndView.setViewName("index");
		LOGGER.info("returning the index view ");
		return modelAndView;
	}

}
