package com.tcs.destination.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.CustPartResultCard;
import com.tcs.destination.bean.FrequentlySearchedCustomerPartnerT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.service.FrequentlySearchedCustPartService;
import com.tcs.destination.utils.Constants;

@RestController
@RequestMapping("/frequent")
public class FrequentlySearchedController {

	@Autowired
	FrequentlySearchedCustPartService frequentService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody List<CustPartResultCard> findFrequent() {
		return frequentService.frequentCustPart();
	}

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody String insertToFrequent(
			@RequestBody FrequentlySearchedCustomerPartnerT frequent) {
		Status status = new Status();
		status.setStatus(Status.FAILED);

		if (Constants.EntityType.contains(frequent.getEntityType())
				&& frequent.getEntityId() != null
				&& frequent.getUserId() != null) {
			frequent.setSearchDatetime(new Timestamp(new Date().getTime()));
			if (frequentService.insertFrequent(frequent)) {
				status.setStatus(Status.SUCCESS);
			}
		}

		return Constants.filterJsonForFieldAndViews("all", "", status);
	}
}
