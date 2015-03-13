package com.tcs.destination.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.exception.PartnerNotFoundException;

/**
 * 
 * Controller to handle partner details search requests.
 *
 */
@RestController
@RequestMapping("/partner")
public class PartnerController {

	@Autowired
	PartnerRepository partnerRepository;

	/**
	 * This method is used to find partner details for the given partner id.
	 * 
	 * @param id
	 *            is the partner id.
	 * @return partner details for the particular partner id.
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody PartnerMasterT findOne(
			@PathVariable("id") String partnerid) {
		PartnerMasterT partner = partnerRepository.findOne(partnerid);
		if (partner != null)
			return partner;
		else throw new PartnerNotFoundException();
	}
}
