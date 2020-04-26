package com.covid.tracker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.covid.tracker.model.ContactInfo;

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
	@RequestMapping("/api")
	public ModelAndView apiTest() {
		LOGGER.info("Request comes to api html ");
		ModelAndView modelAndView = new ModelAndView();
		LOGGER.info("Loading the api.html view ");
		modelAndView.setViewName("api");
		LOGGER.info("returning the api view ");
		return modelAndView;
	}
	@RequestMapping("/world")
	public ModelAndView worldTest() {
		LOGGER.info("Request comes to world html ");
		ModelAndView modelAndView = new ModelAndView();
		LOGGER.info("Loading the world.html view ");
		modelAndView.setViewName("world");
		LOGGER.info("returning the world view ");
		return modelAndView;
	}
	
	
	

}
