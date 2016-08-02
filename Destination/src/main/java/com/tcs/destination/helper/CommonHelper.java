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
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.GeographyCountryMappingT;
import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.bean.IouBeaconMappingT;
import com.tcs.destination.bean.IouCustomerMappingT;
import com.tcs.destination.bean.OfferingMappingT;
import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.bean.TimeZoneMappingT;
import com.tcs.destination.data.repository.ConnectTypeRepository;
import com.tcs.destination.data.repository.CustomerIOUMappingRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.GeographyCountryRepository;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.data.repository.IouBeaconMappingTRepository;
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
	private CustomerIOUMappingRepository customerIouMappingTRepository;
	

	@Autowired
	private CustomerIOUMappingRepository iouCustomerMappingRepository;
	
	@Autowired
	private IouBeaconMappingTRepository iouBeaconMappingRepository;
	
	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private GeographyRepository geographyRepository;

	@Autowired
	private GeographyCountryRepository geographyCountryRepository;
	
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
	 * Method to return actual sub sp details as map
	 * @return  Map<String, SubSpMappingT>
	 */
	public Map<String, SubSpMappingT> getSubSpMappingT(boolean isConnect) {
		List<SubSpMappingT> listOfSubSpT = null;
		listOfSubSpT = (List<SubSpMappingT>) subSpRepository.findAll();
		Map<String, SubSpMappingT> subSpMap = new HashMap<String, SubSpMappingT>();
		for (SubSpMappingT subSpT : listOfSubSpT) {
			subSpMap.put(subSpT.getActualSubSp().trim(), subSpT);
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
	
	/**
	 * This method creates a geography Map
	 * 
	 * @return geographyMap
	 */
	public Map<String, GeographyMappingT> getGeographyMappingT() {
		List<GeographyMappingT> listOfGeographyMappingT = null;
		listOfGeographyMappingT = (List<GeographyMappingT>) geographyRepository
				.findAll();
		Map<String, GeographyMappingT> geographyMap = new HashMap<String, GeographyMappingT>();
		for (GeographyMappingT geographyMappingT : listOfGeographyMappingT) {
			geographyMap.put(geographyMappingT.getGeography(),
					geographyMappingT);
		}
		return geographyMap;
	}


	/**
	 * This method creates a country Map
	 * partner data model changes
	 * @return countryMap
	 */
	public Map<String, GeographyCountryMappingT> getGeographyCountryMappingT() {
		List<GeographyCountryMappingT> listOfGeographyCountryMappingT = null;
		listOfGeographyCountryMappingT = (List<GeographyCountryMappingT>) geographyCountryRepository
				.findAll();
		Map<String, GeographyCountryMappingT> countryMap = new HashMap<String, GeographyCountryMappingT>();
		for (GeographyCountryMappingT geographyCountryMappingT : listOfGeographyCountryMappingT) {
			countryMap.put(geographyCountryMappingT.getCountry(),
					geographyCountryMappingT);
		}
		return countryMap;
	}

	/**
	 * This method creates a IOU Map
	 * 
	 * @return iouMap
	 */
	public Map<String, IouCustomerMappingT> getIouMappingT() {
		List<IouCustomerMappingT> listOfIouMappingT = null;
		listOfIouMappingT = (List<IouCustomerMappingT>) customerIouMappingTRepository
				.findAll();
		Map<String, IouCustomerMappingT> iouMap = new HashMap<String, IouCustomerMappingT>();
		for (IouCustomerMappingT iouMappingT : listOfIouMappingT) {
			iouMap.put(iouMappingT.getIou(), iouMappingT);
		}
		return iouMap;
	}
	
	/*
	 * 
	 */
	public Map<String, IouCustomerMappingT> getIouCustomerMappingT() {
		List<IouCustomerMappingT> listOfIouCustomerMappingT = null;
		listOfIouCustomerMappingT = (List<IouCustomerMappingT>) iouCustomerMappingRepository
				.findAll();
		Map<String, IouCustomerMappingT> iouMap = new HashMap<String, IouCustomerMappingT>();
		for (IouCustomerMappingT iouCustomerMappingT : listOfIouCustomerMappingT) {
			iouMap.put(iouCustomerMappingT.getIou(), iouCustomerMappingT);
		}
		return iouMap;
	}
	
	/**
	 * This method creates a Customer Map
	 * @return customerMap
	 */
	public Map<String, CustomerMasterT> getCustomerMappingT() {
		List<CustomerMasterT> listOfCustomerMappingT = null;
		listOfCustomerMappingT = (List<CustomerMasterT>) customerRepository.findAll();
		Map<String, CustomerMasterT> customerMap = new HashMap<String, CustomerMasterT>();
		for (CustomerMasterT customerMappingT : listOfCustomerMappingT) {
			customerMap.put(customerMappingT.getCustomerName(), customerMappingT);
		}
		return customerMap;
	}

	public Map<String, IouBeaconMappingT> getIouBeaconMappingT() {
		List<IouBeaconMappingT> listOfIouBeaconMappingT = null;
		listOfIouBeaconMappingT = (List<IouBeaconMappingT>) iouBeaconMappingRepository
				.findAll();
		Map<String, IouBeaconMappingT> iouMap = new HashMap<String, IouBeaconMappingT>();
		for (IouBeaconMappingT iouBeaconMappingT : listOfIouBeaconMappingT) {
			iouMap.put(iouBeaconMappingT.getBeaconIou(), iouBeaconMappingT);
		}
		return iouMap;
	}


}