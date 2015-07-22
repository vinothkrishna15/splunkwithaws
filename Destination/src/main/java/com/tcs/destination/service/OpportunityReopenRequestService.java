package com.tcs.destination.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.destination.bean.OpportunityReopenRequestT;
import com.tcs.destination.bean.OpportunitySalesSupportLinkT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.OpportunityReopenRequestRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.DestinationUtils;

@Service
public class OpportunityReopenRequestService {

	private static final Logger logger = LoggerFactory
			.getLogger(OpportunityReopenRequestService.class);

	private static final String ERROR_MESSAGE = "You are not allowed to access the opportunity reopen. Kindly contact the System Admin";

	@Autowired
	OpportunityReopenRequestRepository opportunityReopenRequestRepository;

	@Autowired
	OpportunityRepository opportunityRepository;

	@Autowired
	UserService userService;

	public List<OpportunityReopenRequestT> findAll()
			throws DestinationException {
		UserT loggedUser = DestinationUtils.getCurrentUserDetails();
		if (userService.isSystemAdmin(loggedUser.getUserId()))
			return (List<OpportunityReopenRequestT>) opportunityReopenRequestRepository
					.findAll();
		else
			return opportunityReopenRequestRepository
					.findByRequestedBy(loggedUser.getUserId());
	}

	public OpportunityReopenRequestT findOne(String id)
			throws DestinationException {
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		OpportunityReopenRequestT opportunityReopenRequestT = opportunityReopenRequestRepository
				.findOne(id);
		if (opportunityReopenRequestT.getRequestedBy().equals(userId)
				|| opportunityReopenRequestT.getApprovedRejectedBy().equals(
						userId) || userService.isSystemAdmin(userId)) {
			return opportunityReopenRequestT;
		} else {
			throw new DestinationException(HttpStatus.UNAUTHORIZED,
					ERROR_MESSAGE);
		}
	}

	@Transactional
	public void create(OpportunityReopenRequestT opportunityReopenRequestT)
			throws Exception {
		if (opportunityReopenRequestT.getApprovedRejectedComments() != null
				|| opportunityReopenRequestT.getApprovedRejectedBy() != null
				|| opportunityReopenRequestT.getApprovedRejectedDatetime() != null)
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Cannot create a request with approval details");
		boolean canUpdate = false;
		OpportunityT opportunityT = opportunityRepository
				.findOne(opportunityReopenRequestT.getOpportunityId());
		if (opportunityT != null) {
			if (opportunityT.getSalesStageCode() != 12) {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Cannot reopen a request which is on "
								+ opportunityT.getSalesStageMappingT()
										.getSalesStageDescription());
			}

			if (opportunityT.getOpportunityOwner().equals(
					DestinationUtils.getCurrentUserDetails().getUserId()))
				canUpdate = true;
			if (opportunityT.getOpportunitySalesSupportLinkTs() != null) {
				for (OpportunitySalesSupportLinkT opportunitySalesSupportLinkT : opportunityT
						.getOpportunitySalesSupportLinkTs()) {
					if (opportunitySalesSupportLinkT.getSalesSupportOwner()
							.equals(DestinationUtils.getCurrentUserDetails()
									.getUserId()))
						canUpdate = true;
				}
			}
		}
		logger.error("Can update? " + canUpdate);
		try {
			if (canUpdate)
				opportunityReopenRequestRepository
						.save(opportunityReopenRequestT);
			else
				throw new DestinationException(
						HttpStatus.UNAUTHORIZED,
						"You are not authorised to Request for reopen. Only Opportunity Owner or Sales Support Owner are allowed to request for update");
		} catch (Exception e) {
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
	}

	public void edit(OpportunityReopenRequestT opportunityReopenRequestT)
			throws Exception {
		if (opportunityReopenRequestT.getOpportunityReopenRequestId() == null)
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Cannot edit a request without request id");
		try {
			opportunityReopenRequestRepository.save(opportunityReopenRequestT);
		} catch (Exception e) {
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());

		}

	}

}
