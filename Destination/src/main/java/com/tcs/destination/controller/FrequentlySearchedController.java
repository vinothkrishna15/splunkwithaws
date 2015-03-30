package com.tcs.destination.controller;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.FrequentlySearchedCustomerPartnerT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.exception.NoManditoryFieldsFoundExceptions;
import com.tcs.destination.exception.NoSuchEntityException;
import com.tcs.destination.service.FrequentlySearchedService;
import com.tcs.destination.utils.Constants;

@RestController
@RequestMapping("/frequent")
public class FrequentlySearchedController {

	@Autowired
	FrequentlySearchedService frequentService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findFrequent(
			@RequestParam(value = "entity") String entity,
			@RequestParam(value = "count", defaultValue = "4") int count,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view,
			@RequestParam(value = "owner", defaultValue = "all") String owner) throws Exception{
		return Constants.filterJsonForFieldAndViews(fields, view,
				frequentService.findFrequent(entity, count));
	}

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> insertToFrequent(
			@RequestBody FrequentlySearchedCustomerPartnerT frequent,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) {
		Status status = new Status();
		status.setStatus(Status.FAILED, "");

		frequent.setEntityType(frequent.getEntityType().toUpperCase());
		if (Constants.EntityType.contains(frequent.getEntityType())) {
			if (frequent.getEntityId() != null && frequent.getUserId() != null) {
				frequent.setSearchDatetime(new Timestamp(new Date().getTime()));
				try {
					if (frequentService.insertFrequent(frequent)) {
						status.setStatus(Status.SUCCESS,
								frequent.getFrequentlySearchedId());
					}
				} catch (Exception e) {
					status.setStatus(Status.FAILED,e.getMessage());
					return new ResponseEntity<String>(Constants.filterJsonForFieldAndViews("all", "", status), HttpStatus.BAD_REQUEST);
				}
			} else {
				throw new NoManditoryFieldsFoundExceptions();
			}
		} else {
			throw new NoSuchEntityException();
		}

		return new ResponseEntity<String>(Constants.filterJsonForFieldAndViews("all", "", status),HttpStatus.OK);
	}
}
