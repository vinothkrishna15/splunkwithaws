package com.tcs.destination.service;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.data.repository.SubSpRepository;

@Service
public class SubSpService {
	
	private static final Logger logger = LoggerFactory.getLogger(SubSpService.class);
	
	@Autowired
	SubSpRepository subSpRepository;

	public ArrayList<SubSpMappingT> findAll() {
		logger.debug("Inside findAll Service");
		return (ArrayList<SubSpMappingT>) subSpRepository.findAll();
	}

}
