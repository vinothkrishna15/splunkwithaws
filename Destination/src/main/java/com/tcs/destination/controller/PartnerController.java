package com.tcs.destination.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.data.repository.PartnerRepository;

@RestController
@RequestMapping("/partner")
public class PartnerController {

	@Autowired
	PartnerRepository partnerRepository;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody PartnerMasterT findOne(
			@PathVariable("id") String partnerid) {
		return partnerRepository.findOne(partnerid);
	}
}
