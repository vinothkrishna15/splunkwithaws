package com.tcs.destination.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.OpportunityReopenRequestT;
import com.tcs.destination.data.repository.OpportunityReopenRequestRepository;

@Component
public class OpportunityReopenRequestService {

	@Autowired
	OpportunityReopenRequestRepository opportunityReopenRequestRepository;

	public List<OpportunityReopenRequestT> findAll() {
		return (List<OpportunityReopenRequestT>) opportunityReopenRequestRepository
				.findAll();
	}

}
