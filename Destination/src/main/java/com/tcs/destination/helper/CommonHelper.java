/**
 * 
 * CommonHelper.java 
 *
 * @author TCS
 * @Version 1.0 - 2015
 * 
 * @Copyright 2015 Tata Consultancy 
 */
package com.tcs.destination.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.ConnectTypeMappingT;
import com.tcs.destination.bean.OfferingMappingT;
import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.bean.TimeZoneMappingT;
import com.tcs.destination.data.repository.ConnectTypeRepository;
import com.tcs.destination.data.repository.OfferingRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.data.repository.TimezoneMappingRepository;

/**
 * This CommonHelper class contains methods for common functionalities
 * 
 */
@Component("commonHelper")
public class CommonHelper {

	@Autowired
	private SubSpRepository subSpRepository;

	@Autowired
	private TimezoneMappingRepository timeZoneMappingRepository;
	
	@Autowired
	private ConnectTypeRepository connectTypeRepository;
	
	@Autowired
	private OfferingRepository offeringRepository;

	/**
	 * Method to return sub sp details as map
	 * @return  Map<String, SubSpMappingT>
	 */
	public Map<String, SubSpMappingT> getSubSpMappingT() {
		List<SubSpMappingT> listOfSubSpT = null;
		listOfSubSpT = (List<SubSpMappingT>) subSpRepository.findAll();
		Map<String, SubSpMappingT> subSpMap = new HashMap<String, SubSpMappingT>();
		for (SubSpMappingT subSpT : listOfSubSpT) {
			subSpMap.put(subSpT.getSubSp().trim(), subSpT);
		}
		return subSpMap;
	}

	/**
	 * Method to return time zone details as map
	 * @return Map<String, String>
	 */
	public Map<String, String> getTimeZoneMappingT() {
		List<TimeZoneMappingT> listOfTimeZoneMappingT = null;
		listOfTimeZoneMappingT = (List<TimeZoneMappingT>) timeZoneMappingRepository
				.findAll();
		Map<String, String> timeZomeMappingTsMap = new HashMap<String, String>();
		for (TimeZoneMappingT timeZoneMappingT : listOfTimeZoneMappingT) {
			timeZomeMappingTsMap.put(timeZoneMappingT.getDescription(),timeZoneMappingT.getTimeZoneCode());
		}
		return timeZomeMappingTsMap;
	}
	
	/**
	 * Method to get the connect type details as map
	 * @return Map<String, ConnectTypeMappingT>
	 */
	public Map<String, ConnectTypeMappingT> getConnectTypeMappingT() {
		List<ConnectTypeMappingT> listOfConnectTypeMappingT = null;
		listOfConnectTypeMappingT = (List<ConnectTypeMappingT>) connectTypeRepository
				.findAll();
		Map<String, ConnectTypeMappingT> connectTypeMap = new HashMap<String, ConnectTypeMappingT>();
		for (ConnectTypeMappingT connectTypeMappingT : listOfConnectTypeMappingT) {
			connectTypeMap.put(connectTypeMappingT.getType(),
					connectTypeMappingT);
		}
		return connectTypeMap;
	}
	
	/**
	 * Method to get the offering details as map
	 * @return Map<String, OfferingMappingT>
	 */
	public Map<String, OfferingMappingT> getOfferingMappingT() {
		List<OfferingMappingT> listOfOfferingMappingT = null;
		listOfOfferingMappingT = (List<OfferingMappingT>) offeringRepository
				.findAll();
		Map<String, OfferingMappingT> offeringMap = new HashMap<String, OfferingMappingT>();
		for (OfferingMappingT offeringMappingT : listOfOfferingMappingT) {
			offeringMap.put(offeringMappingT.getOffering(), offeringMappingT);
		}
		return offeringMap;
	}

}