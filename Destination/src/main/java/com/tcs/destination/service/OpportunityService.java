package com.tcs.destination.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.exception.OpportunityNotFoundException;

@Component
public class OpportunityService {
	
	@Autowired
	OpportunityRepository opportunityRepository;
	
	public OpportunityT findByOpportunityName(String opportunityname) {
		OpportunityT opportunity =  opportunityRepository.findByOpportunityNameIgnoreCaseLike("%" + opportunityname + "%");
		if (opportunity == null)
			throw new OpportunityNotFoundException();
		return opportunity;
	}
	
	
}
