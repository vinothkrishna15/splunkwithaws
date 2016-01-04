package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.CityMapping;
import com.tcs.destination.data.repository.CityMappingRepository;
import com.tcs.destination.exception.DestinationException;

@Service
public class CityService {
	
	private static final Logger logger = LoggerFactory.getLogger(CityService.class);

	@Autowired
	CityMappingRepository cityRepository;

	public List<String> getCityByCityName(String pattern) throws Exception{
		List<CityMapping> cityNameList = new ArrayList<CityMapping>();
		List<String> resultList = new ArrayList<String>();
		cityNameList = cityRepository.findByCityIgnoreCaseContainingOrderByCityAsc(pattern.toUpperCase());
		if(cityNameList == null || cityNameList.isEmpty()){
			logger.error(" city service : pattern not found" + pattern);
			throw new DestinationException(HttpStatus.NOT_FOUND,"Pattern not found");
		} else {
			for(CityMapping city : cityNameList){
				resultList.add(city.getCity());
			}
		}
		return resultList;
	}
	

}
