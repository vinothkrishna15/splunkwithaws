package com.tcs.destination.service;

import java.util.Date;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.exception.NoDataFoundException;
import com.tcs.destination.exception.OpportunityNotFoundException;

@Component
public class OpportunityService {

	@Autowired
	OpportunityRepository opportunityRepository;

	public OpportunityT findByOpportunityName(String nameWith) {
		OpportunityT opportunity = opportunityRepository
				.findByOpportunityNameIgnoreCaseLike("%" + nameWith + "%");
		if (opportunity == null)
			throw new OpportunityNotFoundException();
		return opportunity;
	}

	public List<OpportunityT> findRecentOpportunities(String customerId) {

		// Date date = new Date(); // Or where ever you get it from
		// Date daysAgo = new DateTime(date).minusDays(300).toDate();
		Calendar now = Calendar.getInstance();
		now.set(Calendar.YEAR, now.get(Calendar.YEAR) - 1);
		Date fromDate = new Date(now.getTimeInMillis());
		List<OpportunityT> opportunities = opportunityRepository
				.findByCustomerIdAndOpportunityRequestReceiveDateAfter(
						customerId, fromDate);
		if (!opportunities.isEmpty()) {
			return opportunityRepository
					.findByCustomerIdAndOpportunityRequestReceiveDateAfter(
							customerId, fromDate);
		}
		throw new NoDataFoundException();
	}

}
