package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.BidOfficeGroupOwnerLinkT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.BidOfficeGroupOwnerLinkTRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.OpportunitySalesSupportLinkTRepository;
import com.tcs.destination.enums.OpportunityRole;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.exception.NoDataFoundException;
import com.tcs.destination.exception.OpportunityNotFoundException;

@Component
public class OpportunityService {

	@Autowired
	OpportunityRepository opportunityRepository;

	@Autowired
	OpportunitySalesSupportLinkTRepository opportunitySalesSupportLinkTRepository;

	@Autowired
	BidOfficeGroupOwnerLinkTRepository bidOfficeGroupOwnerLinkTRepository;

	public OpportunityT findByOpportunityName(String nameWith) throws Exception{
		OpportunityT opportunity = opportunityRepository
				.findByOpportunityNameIgnoreCaseLike("%" + nameWith + "%");
		if (opportunity == null)
			throw new DestinationException(HttpStatus.NOT_FOUND,"No such Opportunity Found. Please ensure your Opportunity name.");
		return opportunity;
	}

	public List<OpportunityT> findRecentOpportunities(String customerId) throws Exception{

		// Date date = new Date(); // Or where ever you get it from
		// Date daysAgo = new DateTime(date).minusDays(300).toDate();
		Calendar now = Calendar.getInstance();
		now.set(Calendar.YEAR, now.get(Calendar.YEAR) - 1);
		Date fromDate = new Date(now.getTimeInMillis());
		List<OpportunityT> opportunities = opportunityRepository
				.findByCustomerIdAndOpportunityRequestReceiveDateAfter(
						customerId, fromDate);
		if (!opportunities.isEmpty()) {
			return opportunities;
		}
		throw new DestinationException(HttpStatus.NOT_FOUND,"No Relevent Data Found in the database");
	}

	public List<OpportunityT> findByTaskOwnerForRole(String taskOwner,
			String opportunityRole) throws Exception {

		if (OpportunityRole.contains(opportunityRole)) {
			switch (OpportunityRole.valueOf(opportunityRole)) {
			case PRIMARY_OWNER:
				System.out.println("Primary Owner");
				return findForPrimaryOwner(taskOwner, true);
			case SALES_SUPPORT:
				System.out.println("Sales Support");
				return findForSalesSupport(taskOwner, true);
			case BID_OFFICE:
				System.out.println("Bid office");
				return findForBidOffice(taskOwner, true);
			case ALL:
				System.out.println("All");
				List<OpportunityT> opportunities = new ArrayList<OpportunityT>();
				opportunities.addAll(findForPrimaryOwner(taskOwner, false));
				System.out.println("Primary " + opportunities.size());
				opportunities.addAll(findForSalesSupport(taskOwner, false));
				System.out.println("Sales Support " + opportunities.size());
				opportunities.addAll(findForBidOffice(taskOwner, false));
				System.out.println("Bid Office " + opportunities.size());
				 return validateAndReturnOpportunitesData(opportunities, true);
			}
			return null;
		} else {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Oppurtunity Role");
		}
	}

	private List<OpportunityT> findForPrimaryOwner(String userId, boolean isOnly)
			throws DestinationException {
		UserT userT = new UserT();
		userT.setUserId(userId);
		List<OpportunityT> opportunities = opportunityRepository
				.findByUserT(userT);

		return validateAndReturnOpportunitesData(opportunities, isOnly);
	}

	private List<OpportunityT> validateAndReturnOpportunitesData(
			List<OpportunityT> opportunities, boolean validate)
			throws DestinationException {
		System.out.println("Opportunity " + opportunities.size());
		if (validate) {
			if (opportunities.size() > 0) {
				return opportunities;
			} else {
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"No Opportunities Found");
			}
		} else {
			return opportunities;
		}
	}

	private List<OpportunityT> findForBidOffice(String userId, boolean isOnly)
			throws DestinationException {
		UserT userT = new UserT();
		userT.setUserId(userId);
		List<OpportunityT> opportunities = bidOfficeGroupOwnerLinkTRepository
				.findOpportunityTFromBidDetailsTFromBidOfficeGroupOwnerLinkTByUserId(userId);
		return validateAndReturnOpportunitesData(opportunities, isOnly);
	}

	private List<OpportunityT> findForSalesSupport(String userId, boolean isOnly)
			throws DestinationException {
		List<OpportunityT> opportunities = opportunitySalesSupportLinkTRepository
				.findOpportunityTByUserId(userId);
		return validateAndReturnOpportunitesData(opportunities, isOnly);
	}
}
