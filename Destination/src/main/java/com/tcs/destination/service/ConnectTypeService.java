package com.tcs.destination.service;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.ConnectTypeMappingT;
import com.tcs.destination.data.repository.ConnectTypeRepository;

@Service
public class ConnectTypeService {
	
	private static final Logger logger = LoggerFactory.getLogger(ConnectTypeService.class);

	@Autowired
	ConnectTypeRepository conTypeRepository;

	public ArrayList<ConnectTypeMappingT> findAll() {
		logger.debug("Inside findAll service");
		return (ArrayList<ConnectTypeMappingT>) conTypeRepository.findAll();
	}

}
