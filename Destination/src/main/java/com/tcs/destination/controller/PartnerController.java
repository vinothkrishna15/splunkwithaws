package com.tcs.destination.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.utils.Constants;

@RestController
@RequestMapping("/partner")
public class PartnerController {

	@Autowired
	PartnerRepository partnerRepository;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody String findOne(
			@PathVariable("id") String partnerid,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) {
		PartnerMasterT partner = partnerRepository.findOne(partnerid);
		return Constants.filterJsonForFieldAndViews(fields, view, partner);
	}
}
