/**
 * 
 */
package com.tcs.destination.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import scala.Array;

import com.tcs.destination.bean.dto.QualifiedPipelineDTO;
import com.tcs.destination.bean.dto.QualifiedPipelineDetails;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.OpportunitiesQualifiedService;

/**
 * @author tcs2
 *
 */
@RestController
@RequestMapping("/opportunitiesQualified")
public class OpportunitiesQualifiedController {

	private static final Logger logger = LoggerFactory
			.getLogger(CustomerConsultingController.class);

	@Autowired
	OpportunitiesQualifiedService opportunitiesQualifiedService;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public QualifiedPipelineDetails<QualifiedPipelineDTO> retrieveQualifiedPipelineOpportunityDetails(
			@RequestParam(value = "oppType", defaultValue = "") String oppType)
			throws DestinationException {

		logger.info("Inside Opportunities Qualified Controller: Start of fetching qualified details");

		QualifiedPipelineDetails response = null;
		try {

			if (oppType != null && oppType.equalsIgnoreCase("SALES")) {
				List<String> userGroup = new ArrayList<String>();
				userGroup.add("GEO Heads");
				userGroup.add("BDM Supervisor");
				userGroup.add("BDM");

				response = opportunitiesQualifiedService
						.findQualifiedPipelineOpportunityDetailsForSalesAndConsulting(userGroup);

			} else if (oppType != null
					&& oppType.equalsIgnoreCase("CONSULTING")) {
				List<String> userGroup = new ArrayList<String>();
				userGroup.add("Practice Head");
				userGroup.add("Practice Owner");
				response = opportunitiesQualifiedService
						.findQualifiedPipelineOpportunityDetailsForSalesAndConsulting(userGroup);
			} else {

				response = opportunitiesQualifiedService
						.findAllQualifiedPipelineOpportunityDetails();
			}
			logger.info("Inside Opportunities Qualified Controller: End of fetching qualified details");

			response = customerConsultingService
					.findRevenueDetailsOfConsultedCustomers(financialYear);

			logger.info("Inside Customer Consulting Controller: Exit");

		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend Error while retrieving customer consulting details");
		}

		return response;

	}

}
