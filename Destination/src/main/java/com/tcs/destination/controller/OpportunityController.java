package com.tcs.destination.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.TasksBySupervisorIdDTO;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.OpportunityService;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/opportunity")
public class OpportunityController {
	// @Autowired
	// OpportunityRepository opportunityRepository;

	private static final Logger logger = LoggerFactory
			.getLogger(OpportunityController.class);

	@Autowired
	OpportunityService opportunityService;

	// @Autowired
	// CustomerRepository customerRepository;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findOne(
			@RequestParam("nameWith") String nameWith,
			@RequestParam(value = "customerId", defaultValue = "") String customerId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "currency", defaultValue = "") List<String> currencies,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside OpportunityController /opportunity?nameWith="
				+ nameWith + " GET");
		List<OpportunityT> opportunities = opportunityService
				.findByOpportunityName(nameWith, customerId, currencies);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				opportunities);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody String findByOpportunityId(
			@PathVariable("id") String opportunityId,
			@RequestParam(value = "currency", defaultValue = "") List<String> currencies,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside OpportunityController /opportunity/Id="
				+ opportunityId + " GET");
		OpportunityT opportunity = opportunityService.findByOpportunityId(
				opportunityId, currencies);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				opportunity);
	}

	@RequestMapping(value = "/recent", method = RequestMethod.GET)
	public @ResponseBody String findByCustomerId(
			@RequestParam("customerId") String customerId,
			@RequestParam(value = "currency", defaultValue = "") List<String> currencies,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside OpportunityController /opportunity/recent?customerId="
				+ customerId + " GET");
		List<OpportunityT> opportunities = opportunityService
				.findRecentOpportunities(customerId, currencies);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				opportunities);
	}

	@RequestMapping(value = "/taskOwner", method = RequestMethod.GET)
	public @ResponseBody String findByTaskOwner(
			@RequestParam("id") String userId,
			@RequestParam(value = "currency", defaultValue = "") List<String> currencies,
			@RequestParam(value = "role", defaultValue = "ALL") String opportunityRole,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside OpportunityController /opportunity/taskOwner?id="
				+ userId + " GET");
		List<OpportunityT> opportunities = opportunityService
				.findOpportunitiesByOwnerAndRole(userId, opportunityRole,
						currencies);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				opportunities);
	}

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> createOpportunity(
			@RequestBody OpportunityT opportunity,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside OpportunityController /opportunity POST");
		Status status = new Status();
		status.setStatus(Status.FAILED, "Save unsuccessful");
		try {
			opportunityService.createOpportunity(opportunity);
		} catch (Exception e) {
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		status.setStatus(Status.SUCCESS, opportunity.getOpportunityId());
		return new ResponseEntity<String>(
				ResponseConstructors.filterJsonForFieldAndViews("all", "",
						status), HttpStatus.OK);
	}

	// @RequestMapping(value = "/insertImage", method = RequestMethod.GET)
	// public @ResponseBody byte[] sampleInsert() throws IOException {
	// // open image
	// File imgPath = new File("/Users/lax/Downloads/att.jpg");
	// byte[] fileContent = Files.readAllBytes(imgPath.toPath());
	// System.out.println("Byte Array >>> \n"+fileContent);
	// customerRepository.addImage(fileContent,"CUS550");
	// return fileContent;
	// }

	@RequestMapping(method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> editOpportunity(
			@RequestBody OpportunityT opportunity,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside OpportunityController /opportunity PUT");
		Status status = new Status();
		try {
			opportunityService.updateOpportunity(opportunity);
		} catch (Exception e) {
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		status.setStatus(Status.SUCCESS, opportunity.getOpportunityId());
		return new ResponseEntity<String>(
				ResponseConstructors.filterJsonForFieldAndViews("all", "",
						status), HttpStatus.OK);
	}

	@RequestMapping(value = "/shelved", method = RequestMethod.GET)
	public @ResponseBody String findShelved(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "currency", defaultValue = "") List<String> currencies,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside OpportunityController /opportunity/shelved GET");
		List<OpportunityT> opportunities = opportunityService
				.findShelvedOpportunities(currencies);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				opportunities);
	}

	/**
	 * This method retrieves the opportunities that are associated with a supervisor.
	 * All the associates' opportunities who comes under the supervisor are retrieved.  
	 * 
	 * @param supervisorUserId
	 * @param fields
	 * @param view
	 * @return
	 * @throws Exception
	 */	@RequestMapping(value = "/tasksBySupervisorId", method = RequestMethod.GET)
	public @ResponseBody String findTasksBySupervisorId(
			@RequestParam("id") String supervisorUserId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside OpportunityController /tasksBySupervisorId?id="
				+ supervisorUserId + " GET");
		
		List<TasksBySupervisorIdDTO> opportunities = opportunityService.findOpportunitiesBySupervisorId(supervisorUserId);
		
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				opportunities);
	}
}
