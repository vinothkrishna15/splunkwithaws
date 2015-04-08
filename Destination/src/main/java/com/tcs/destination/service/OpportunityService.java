package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private static final Logger logger = LoggerFactory.getLogger(OpportunityService.class);

	@Autowired
	OpportunityRepository opportunityRepository;

	@Autowired
	OpportunitySalesSupportLinkTRepository opportunitySalesSupportLinkTRepository;

	@Autowired
	BidOfficeGroupOwnerLinkTRepository bidOfficeGroupOwnerLinkTRepository;

	public OpportunityT findByOpportunityName(String nameWith) throws Exception{
		logger.debug("Inside findByOpportunityName Service");
		OpportunityT opportunity = opportunityRepository
				.findByOpportunityNameIgnoreCaseLike("%" + nameWith + "%");
		if (opportunity == null)
		{
			logger.error("NOT_FOUND: No such Opportunity Found. Please ensure your Opportunity name.");
			throw new DestinationException(HttpStatus.NOT_FOUND,"No such Opportunity Found. Please ensure your Opportunity name.");
		}
		return opportunity;
	}

	public List<OpportunityT> findRecentOpportunities(String customerId) throws Exception{
		logger.debug("Inside findRecentOpportunities Service");
		// Date date = new Date(); // Or where ever you get it from
		// Date daysAgo = new DateTime(date).minusDays(300).toDate();
		Calendar now = Calendar.getInstance();
		now.set(Calendar.YEAR, now.get(Calendar.YEAR) - 1);
		Date fromDate = new Date(now.getTimeInMillis());
		List<OpportunityT> opportunities = opportunityRepository
				.findByCustomerIdAndOpportunityRequestReceiveDateAfter(
						customerId, fromDate);
		if (!opportunities.isEmpty()) {
			logger.debug("Opportunity Not Empty");
			return opportunities;
		}
		logger.error("NOT_FOUND: No Relevent Data Found in the database");
		throw new DestinationException(HttpStatus.NOT_FOUND,"No Relevent Data Found in the database");
	}

	public List<OpportunityT> findByTaskOwnerForRole(String opportunityOwner,
			String opportunityRole) throws Exception {
		logger.debug("Inside findByTaskOwnerForRole Service");
		if (OpportunityRole.contains(opportunityRole)) {
			logger.error("Opportunity Role is Present");
			switch (OpportunityRole.valueOf(opportunityRole)) {
			case PRIMARY_OWNER:
				logger.debug("Primary Owner Found");
				System.out.println("Primary Owner");
				return findForPrimaryOwner(opportunityOwner, true);
			case SALES_SUPPORT:
				logger.debug("Sales Support Found");
				System.out.println("Sales Support");
				return findForSalesSupport(opportunityOwner, true);
			case BID_OFFICE:
				logger.debug("Bid Office Found");
				System.out.println("Bid office");
				return findForBidOffice(opportunityOwner, true);
			case ALL:
				logger.debug("ALL Found");
				System.out.println("All");
				List<OpportunityT> opportunities = new ArrayList<OpportunityT>();
				opportunities.addAll(findForPrimaryOwner(opportunityOwner, false));
				System.out.println("Primary " + opportunities.size());
				opportunities.addAll(findForSalesSupport(opportunityOwner, false));
				System.out.println("Sales Support " + opportunities.size());
				opportunities.addAll(findForBidOffice(opportunityOwner, false));
				System.out.println("Bid Office " + opportunities.size());
				 return validateAndReturnOpportunitesData(opportunities, true);
			}
			return null;
		} else {
			logger.error("BAD_REQUEST: Invalid Oppurtunity Role");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Oppurtunity Role");
		}
	}
	private List<OpportunityT> findForPrimaryOwner(String userId, boolean isOnly)
			throws DestinationException {
		logger.debug("Inside findForPrimaryOwner Service");
		UserT userT = new UserT();
		userT.setUserId(userId);
		List<OpportunityT> opportunities = opportunityRepository
				.findByUserT(userT);

		return validateAndReturnOpportunitesData(opportunities, isOnly);
	}

	private List<OpportunityT> validateAndReturnOpportunitesData(
			List<OpportunityT> opportunities, boolean validate)
			throws DestinationException {
		logger.debug("validateAndReturnOpportunitesData");
		System.out.println("Opportunity " + opportunities.size());
		if (validate) {
			if (opportunities.size() > 0) {
				logger.debug("Opportunity List Is Present");
				return opportunities;
			} else {
				logger.error("NOT_FOUND: No Opportunities Found");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"No Opportunities Found");
			}
		} else {
			return opportunities;
		}
	}

	private List<OpportunityT> findForBidOffice(String userId, boolean isOnly)
			throws DestinationException {
		logger.debug("Inside findForBidOffice Service");
		UserT userT = new UserT();
		userT.setUserId(userId);
		List<OpportunityT> opportunities = bidOfficeGroupOwnerLinkTRepository
				.findOpportunityTFromBidDetailsTFromBidOfficeGroupOwnerLinkTByUserId(userId);
		return validateAndReturnOpportunitesData(opportunities, isOnly);
	}

	private List<OpportunityT> findForSalesSupport(String userId, boolean isOnly)
			throws DestinationException {
		logger.debug("Inside findForSalesSupport Service");
		List<OpportunityT> opportunities = opportunitySalesSupportLinkTRepository
				.findOpportunityTByUserId(userId);
		return validateAndReturnOpportunitesData(opportunities, isOnly);
	}

	public OpportunityT findByOpportunityId(String opportunityId) throws DestinationException{
		logger.debug("Inside findByOpportunityId Service");
		OpportunityT opportunity=opportunityRepository.findByOpportunityId(opportunityId);
		if(opportunity != null)
		return opportunity;
		else
			throw new DestinationException(HttpStatus.NOT_FOUND, "Opportuinty Id "+opportunityId+" Not Found");
		// TODO Auto-generated method stub
	}
}
