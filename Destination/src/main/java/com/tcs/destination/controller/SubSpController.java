package com.tcs.destination.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.PartnerSubSpMappingT;
import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.SubSpService;
import com.tcs.destination.utils.ResponseConstructors;

/*
 * This class retrieves  all the subp's
 */
@RestController
@RequestMapping("/subsp")
public class SubSpController {

	private static final Logger logger = LoggerFactory.getLogger(SubSpController.class);

	@Autowired
	SubSpService subSpService;

	/**
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findAllActive(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "partnerId", defaultValue = "") String partnerId,
			@RequestParam(value = "view", defaultValue = "") String view) throws DestinationException{
		logger.info("Inside SubSpController / Start of retrieving the subSps");

		try {
			List<PartnerSubSpMappingT> partnerSubSpMappingList = null;
			List<SubSpMappingT> subSpMappingList = null;
			if (partnerId.equals("")){
				subSpMappingList = subSpService.findAllActive();
			} else{
				partnerSubSpMappingList = subSpService.findByPartner(partnerId);
				if (!partnerSubSpMappingList.isEmpty()){
					HashSet<SubSpMappingT> subSpMappingSet = new HashSet<SubSpMappingT>();
					for (PartnerSubSpMappingT partnerSubsp : partnerSubSpMappingList){
						SubSpMappingT subspMap = subSpService.findBySubspAndActive(partnerSubsp.getSubSpId());
						if (subspMap != null ){
							subSpMappingSet.add(subspMap);
						}
					}
					subSpMappingList = new ArrayList<SubSpMappingT>(subSpMappingSet);
				}
			}
			logger.info("Inside SubSpController / End of retrieving the subSps");
			return ResponseConstructors.filterJsonForFieldAndViews(fields, view, subSpMappingList);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the SubSps");
		}
	}
}
