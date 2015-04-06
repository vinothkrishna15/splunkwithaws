package com.tcs.destination.service;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.controller.SubSpController;
import com.tcs.destination.data.repository.SubSpRepository;

@Component
public class SubSpService {
	
	private static final Logger logger = LoggerFactory.getLogger(SubSpService.class);
	
	@Autowired
	SubSpRepository subSpRepository;

	public ArrayList<SubSpMappingT> findAll() {
		logger.debug("Inside findAll Service");
		return (ArrayList<SubSpMappingT>) subSpRepository.findAll();
	}

}
