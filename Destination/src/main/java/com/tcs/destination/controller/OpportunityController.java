package com.tcs.destination.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.AsyncJobRequest;
import com.tcs.destination.bean.DeliveryCentreT;
import com.tcs.destination.bean.DeliveryOwnershipT;
import com.tcs.destination.bean.OpportunitiesBySupervisorIdDTO;
import com.tcs.destination.bean.OpportunityNameKeywordSearch;
import com.tcs.destination.bean.OpportunityReopenRequestT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.PageDTO;
import com.tcs.destination.bean.PaginatedResponse;
import com.tcs.destination.bean.SearchResultDTO;
import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.TeamOpportunityDetailsDTO;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UploadStatusDTO;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.enums.JobName;
import com.tcs.destination.enums.OperationType;
import com.tcs.destination.enums.SmartSearchType;
import com.tcs.destination.enums.Switch;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.OpportunityDownloadService;
import com.tcs.destination.service.OpportunityReopenRequestService;
import com.tcs.destination.service.OpportunityService;
import com.tcs.destination.service.OpportunityUploadService;
import com.tcs.destination.service.UploadErrorReport;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.PropertyUtil;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * This Controller handles the opportunity module
 * 
 * @author TCS
 *
 */
@RestController
@RequestMapping("/opportunity")
public class OpportunityController {

	private static final Logger logger = LoggerFactory
			.getLogger(OpportunityController.class);

	@Autowired
	OpportunityService opportunityService;

	@Autowired
	OpportunityReopenRequestService opportunityReopenRequestService;

	@Autowired
	OpportunityUploadService opportunityUploadService;

	@Autowired
	UploadErrorReport uploadErrorReport;

	@Autowired
	OpportunityDownloadService opportunityDownloadService;
	
	@Autowired
	private JobLauncherController jobLauncherController;

	
	

	/**
	 * This method retrieves the opportunity details for the given nameWith
	 * 
	 * @param page
	 * @param count
	 * @param nameWith
	 * @param customerId
	 * @param fields
	 * @param currencies
	 * @param isAjax
	 * @param view
	 * @return opportunities
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findOne(
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "30") int count,
			@RequestParam("nameWith") String nameWith,
			@RequestParam(value = "customerId", defaultValue = "") String customerId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "currency", defaultValue = "") List<String> currencies,
			@RequestParam(value = "isAjax", defaultValue = "false") boolean isAjax,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {

		UserT user = DestinationUtils.getCurrentUserDetails();
		logger.info("Inside OpportunityController: Start of nameWith search");
		String response = null;
		PaginatedResponse opportunities;
		try {
			opportunities = opportunityService.getOpportunitiesByOpportunityName(nameWith,
					customerId, currencies, isAjax, user, page, count);
			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, opportunities);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retieving the opportunity details");
		}
		logger.info("Inside OpportunityController: End of nameWith search");
		return response;
	}

	/**
	 * This method is used to get the opportunity details for the given
	 * opportunity id
	 * 
	 * @param opportunityId
	 * @param currencies
	 * @param fields
	 * @param view
	 * @return opportunity
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody String findByOpportunityId(
			@PathVariable("id") String opportunityId,
			@RequestParam(value = "currency", defaultValue = "") List<String> currencies,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {

		logger.info("Inside OpportunityController: Start of search by id");
		String response = null;
		OpportunityT opportunity;
		try {
			opportunity = opportunityService.findByOpportunityId(opportunityId,
					currencies);
			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, opportunity);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retieving the opportunity detail for the id:"
							+ opportunityId);
		}
		logger.info("Inside OpportunityController: End of search by id");
		return response;
	}

	

	/**
	 * This method is used to get the opportunity details by Task Owner
	 * 
	 * @param currencies
	 * @param opportunityRole
	 * @param fields
	 * @param view
	 * @return opportunities
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/taskowner", method = RequestMethod.GET)
	public @ResponseBody String findByTaskOwner(
			@RequestParam(value = "currency", defaultValue = "") List<String> currencies,
			@RequestParam(value = "role", defaultValue = "ALL") String opportunityRole,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {

		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		logger.info("Inside OpportunityController: Start of /opportunity/taskowner GET");
		String response = null;
		List<OpportunityT> opportunities;
		try {
			opportunities = opportunityService.findOpportunitiesByOwnerAndRole(
					userId, opportunityRole, currencies);
			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, opportunities);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retieving the opportunity details for the task owner:"
							+ userId);
		}
		logger.info("Inside OpportunityController: End of /opportunity/taskowner GET");
		return response;
	}

	/**
	 * This method is used to create an opportunity
	 * 
	 * @param opportunity
	 * @param fields
	 * @param view
	 * @return status
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> createOpportunity(
			@RequestBody OpportunityT opportunity,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside OpportunityController: Start of create opportunity");
		Status status = new Status();
		status.setStatus(Status.FAILED, "Save unsuccessful");
		try {
			List<AsyncJobRequest> asyncJobRequest = opportunityService.createOpportunity(opportunity, false, null, null,status);
			for(AsyncJobRequest jobRequest : asyncJobRequest) {
				if (jobRequest.getOn().equals(Switch.ON)) {
					jobLauncherController.asyncJobLaunch(jobRequest.getJobName(), jobRequest.getEntityType().name(), jobRequest.getEntityId(), jobRequest.getDealValue(), jobRequest.getDeliveryCentreId());
				}
			}
            jobLauncherController.asyncJobLaunchForNotification(JobName.notification, EntityType.OPPORTUNITY, opportunity.getOpportunityId(),OperationType.OPPORTUNITY_CREATE,opportunity.getModifiedBy());
			logger.info("Inside OpportunityController: End of create opportunity");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in creating the opportunity");
		}
	}

	/**
	 * This method is used to edit an existing opportunity
	 * 
	 * @param opportunity
	 * @param fields
	 * @param view
	 * @return status
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> editOpportunity(
			@RequestBody OpportunityT opportunity,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {

		logger.info("Inside OpportunityController: Start of edit opportunity");
		Status status = new Status();
		try {
			List<AsyncJobRequest> asyncJobRequest = opportunityService.updateOpportunityT(opportunity, status);
			for(AsyncJobRequest jobRequest : asyncJobRequest) {
				if (jobRequest.getOn().equals(Switch.ON)) {
					jobLauncherController.asyncJobLaunch(jobRequest.getJobName(), jobRequest.getEntityType().name(), jobRequest.getEntityId(), jobRequest.getDealValue(), jobRequest.getDeliveryCentreId());
				}
			}
			jobLauncherController.asyncJobLaunchForNotification(JobName.notification, EntityType.OPPORTUNITY, opportunity.getOpportunityId(),OperationType.OPPORTUNITY_EDIT,opportunity.getModifiedBy());

			
			logger.info("Inside OpportunityController: End of edit opportunity");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while updating the opportunity");
		}

	}

	/**
	 * This method is used to get the opportunities by sales stage code
	 * 
	 * @param salesStageCode
	 * @param customerId
	 * @param fields
	 * @param currencies
	 * @param view
	 * @return opportunities
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/salesStage", method = RequestMethod.GET)
	public @ResponseBody String findShelved(
			@RequestParam(value = "salesStageCode") int salesStageCode,
			@RequestParam(value = "customerId", defaultValue = "") String customerId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "currency", defaultValue = "") List<String> currencies,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside OpportunityController: Start of /opportunity/salesStage GET");
		String response = null;
		List<OpportunityT> opportunities;
		try {
			opportunities = opportunityService
					.findOpportunitiesBySalesStageCode(currencies,
							salesStageCode, customerId);

			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, opportunities);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the opportunity details for the sales stage code:"
							+ salesStageCode);
		}
		logger.info("Inside OpportunityController: End of /opportunity/salesStage GET");
		return response;
	}

	/**
	 * This method retrieves the Deal Value of Opportunities that are associated
	 * with a supervisor. All the associates' opportunities under the supervisor
	 * are retrieved.
	 * 
	 * @param supervisorUserId
	 * @param fields
	 * @param view
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/team/oppdealvalue", method = RequestMethod.GET)
	public @ResponseBody String findDealValueOfOpportunitiesBySupervisorId(
			@RequestParam("id") String supervisorUserId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside OpportunityController: Start of /opportunity/team/oppDealValue?id="
				+ supervisorUserId + " GET");
		String response = null;
		List<OpportunitiesBySupervisorIdDTO> opportunities = null;
		try {
			opportunities = opportunityService
					.findDealValueOfOpportunitiesBySupervisorId(supervisorUserId);
			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, opportunities);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(
					HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the Deal value of opportunities for supervisor id:"
							+ supervisorUserId);
		}
		logger.info("Inside OpportunityController: End of /opportunity/team/oppDealValue?id="
				+ supervisorUserId + " GET");
		return response;
	}

	/**
	 * This method retrieves all the opportunity reopen requests
	 * 
	 * @param fields
	 * @param view
	 * @return opportunityReopenRequestTs
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/reopen", method = RequestMethod.GET)
	public @ResponseBody String findAllReOpen(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside OpportunityController: Start of OpportunityReopenRequestService /reopen   GET");
		String response = null;
		List<OpportunityReopenRequestT> opportunityReopenRequestTs;
		try {
			opportunityReopenRequestTs = opportunityReopenRequestService
					.findAllReOpenRequests();

			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, opportunityReopenRequestTs);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the opportunity reopen requests");
		}
		logger.info("Inside OpportunityController: End of OpportunityReopenRequestService /reopen GET");
		return response;
	}

	/**
	 * This method retrieves the opportunity reopen request by id
	 * 
	 * @param id
	 * @param fields
	 * @param view
	 * @return opportunityReopenRequestT
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/reopen/{id}", method = RequestMethod.GET)
	public @ResponseBody String findById(
			@PathVariable("id") String id,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside OpportunityController: Start of /opportunity/reopen/"
				+ id + " GET");
		String response = null;
		OpportunityReopenRequestT opportunityReopenRequestT;
		try {
			opportunityReopenRequestT = opportunityReopenRequestService
					.findOne(id);

			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, opportunityReopenRequestT);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the opportunity reopen request for id:"
							+ id);
		}
		logger.info("Inside OpportunityController: End of /opportunity/reopen/"
				+ id + " GET");
		return response;
	}

	/**
	 * This method creates a new opportunity reopen request
	 * 
	 * @param opportunityReopenRequestT
	 * @param fields
	 * @param view
	 * @return status
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/reopen", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> addNewReopenRequest(
			@RequestBody OpportunityReopenRequestT opportunityReopenRequestT,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside OpportunityController: Start of /opportunity/reopen POST");
		Status status = new Status();
		status.setStatus(Status.FAILED, "Save unsuccessful");
		try {
			opportunityReopenRequestService.create(opportunityReopenRequestT);
			status.setStatus(Status.SUCCESS,
					opportunityReopenRequestT.getOpportunityReopenRequestId());
			logger.info("Inside OpportunityController: End of /opportunity/reopen POST");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while creating opportunity reopen request");
		}

	}

	/**
	 * This method is used to update an opportunity reopen request
	 * 
	 * @param opportunityReopenRequestT
	 * @param fields
	 * @param view
	 * @return status
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/reopen", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> editReopenRequest(
			@RequestBody OpportunityReopenRequestT opportunityReopenRequestT,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside OpportunityController: Start of /opportunity/reopen PUT");
		Status status = new Status();
		status.setStatus(Status.FAILED, "Update unsuccessful");
		try {
			opportunityReopenRequestService.edit(opportunityReopenRequestT);
			status.setStatus(Status.SUCCESS,
					opportunityReopenRequestT.getOpportunityReopenRequestId());
			logger.info("Inside OpportunityController: End of /opportunity/reopen PUT");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in updating the opportunity reopen request");
		}

	}

	/**
	 * This Controller retrieves the Team Opportunity Details of all Users
	 * specific to a Supervisor
	 * 
	 * @param supervisorUserId
	 * @param fields
	 * @param view
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/team/oppdetails", method = RequestMethod.GET)
	public @ResponseBody String findTeamOpportunityDetailsBySupervisorId(
			@RequestParam("id") String supervisorUserId,
			@RequestParam(value = "isCurrentFinancialYear", defaultValue = "false") boolean isCurrentFinancialYear,
			@RequestParam(value = "salesStageCode", defaultValue = "all") String salesStageCode,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "5") int count,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {

		logger.info("Inside OpportunityController: Start of /opportunity/team/oppDetails?id="
				+ supervisorUserId + " GET");

		if ((page < 0) || (count < 0)) {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid pagination request");
		}
		String response = null;
		TeamOpportunityDetailsDTO teamOpportunityDetails = null;

		try {
			teamOpportunityDetails = opportunityService
					.findTeamOpportunityDetailsBySupervisorId(supervisorUserId,
							page, count, isCurrentFinancialYear, salesStageCode);
			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, teamOpportunityDetails);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(
					HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the team opportunity details for the supervisor id :"
							+ supervisorUserId);
		}

		logger.info("Inside OpportunityController: End of /opportunity/team/oppDetails?id="
				+ supervisorUserId + " GET");
		return response;
	}

	/**
	 * This method gives the functionality for opportunity advanced search
	 * 
	 * @param page
	 * @param count
	 * @param customerIdList
	 * @param displayIou
	 * @param country
	 * @param opportunityName
	 * @param partnerId
	 * @param offering
	 * @param competitorName
	 * @param displaySubSp
	 * @param bidRequestType
	 * @param newLogo
	 * @param strategicDeal
	 * @param salesStageCode
	 * @param searchKeywords
	 * @param minDigitalDealValue
	 * @param maxDigitalDealValue
	 * @param dealCurrency
	 * @param currency
	 * @param userId
	 * @param digitalFlag
	 * @param fields
	 * @param view
	 * @return opportunityResponse
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public @ResponseBody String searchOpportunities(
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "30") int count,
			@RequestParam(value = "customerIdList", defaultValue = "") List<String> customerIdList,
			@RequestParam(value = "displayIou", defaultValue = "") List<String> displayIou,
			@RequestParam(value = "country", defaultValue = "") List<String> country,
			@RequestParam(value = "opportunityName", defaultValue = "") List<String> opportunityName,
			@RequestParam(value = "partnerId", defaultValue = "") List<String> partnerId,
			@RequestParam(value = "offering", defaultValue = "") List<String> offering,
			@RequestParam(value = "competitorName", defaultValue = "") List<String> competitorName,
			@RequestParam(value = "displaySubSp", defaultValue = "") List<String> displaySubSp,
			@RequestParam(value = "bidRequestType", defaultValue = "") List<String> bidRequestType,
			@RequestParam(value = "newLogo", defaultValue = "") String newLogo,
			@RequestParam(value = "strategicDeal", defaultValue = "") String strategicDeal,
			@RequestParam(value = "salesStageCode", defaultValue = "") List<Integer> salesStageCode,
			@RequestParam(value = "searchKeywords", defaultValue = "") List<String> searchKeywords,
			@RequestParam(value = "minDigitalDealValue", defaultValue = "0") double minDigitalDealValue,
			@RequestParam(value = "isCurrentFinancialYear", defaultValue = "false") Boolean isCurrentFinancialYear,
			@RequestParam(value = "maxDigitalDealValue", defaultValue = ""
					+ Double.MAX_VALUE) double maxDigitalDealValue,
			@RequestParam(value = "dealCurrency", defaultValue = "USD") String dealCurrency,
			@RequestParam(value = "currency", defaultValue = "USD") List<String> currency,
			@RequestParam(value = "userId", defaultValue = "") List<String> userId,
			@RequestParam(value = "digitalFlag", defaultValue = "") String digitalFlag,
			@RequestParam(value = "role", defaultValue = "ALL") String role,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside OpportunityController: Start of /opportunity/search GET");
		String response = null;
		UserT user = DestinationUtils.getCurrentUserDetails();
		PaginatedResponse opportunityResponse;
		try {
			opportunityResponse = opportunityService.getByOpportunities(
					customerIdList, salesStageCode, strategicDeal,
					newLogo, minDigitalDealValue, maxDigitalDealValue,
					dealCurrency, digitalFlag, displayIou, country, partnerId,
					competitorName, searchKeywords, bidRequestType, offering,
					displaySubSp, opportunityName, userId, currency, page,
					count, role,isCurrentFinancialYear,user);

			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, opportunityResponse);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the opportunity details");
		}
		logger.info("Inside OpportunityController: End of /opportunity/search GET");
		return response;
	}

	/**
	 * This method retrieves all the opportunities
	 * 
	 * @param page
	 * @param count
	 * @param isCurrentFinancialYear
	 * @param order
	 * @param sortBy
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public @ResponseBody String findAll(
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "30") int count,
			@RequestParam(value = "isCurrentFinancialYear", defaultValue = "false") Boolean isCurrentFinancialYear,
			@RequestParam(value = "order", defaultValue = "DESC") String order,
			@RequestParam(value = "sortBy", defaultValue = "modifiedDatetime") String sortBy,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside OpportunityController: Start of /opportunity/all GET");
		String response = null;
		PaginatedResponse opportunityResponse;
		UserT user = DestinationUtils.getCurrentUserDetails();
		try {
			opportunityResponse = opportunityService.findAll(sortBy, order,
					isCurrentFinancialYear, page, count, user);

			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, opportunityResponse);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the opportunity details");
		}
		logger.info("Inside OpportunityController: End of /opportunity/all GET");
		return response;
	}

	/**
	 * This method gives the Opportunity name or keyword search
	 * 
	 * @param name
	 * @param keyword
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/name", method = RequestMethod.GET)
	public @ResponseBody String findOppNameOrKeyword(
			@RequestParam(value = "name", defaultValue = "") String name,
			@RequestParam(value = "keyword", defaultValue = "") String keyword,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside OpportunityController: Start of /opportunity/name GET");
		ArrayList<OpportunityNameKeywordSearch> searchResults = null;
		String response = null;
		try {
			UserT user = DestinationUtils.getCurrentUserDetails();
			searchResults = opportunityService.findOpportunityNameOrKeywords(
					name, keyword, user);
			if ((searchResults == null) || (searchResults.isEmpty())) {
				logger.error("No Results found for name {} and keyword {}",
						name, keyword);
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"No Results found for name " + name + " and keyword "
								+ keyword);
			}
			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, searchResults);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the Opportunity results with the keyword "
							+ keyword + "or name" + name);
		}
		logger.info("Inside OpportunityController: End of /opportunity/name GET");
		return response;
	}

	/**
	 * This method uploads the opportunity details from excel file to the
	 * database
	 * 
	 * @param file
	 * @param fields
	 * @param view
	 * @return excelFile
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<InputStreamResource> uploadOpportunity(
			@RequestParam("file") MultipartFile file,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		logger.info("Inside OpportunityController: Start of /opportunity/upload POST");
		UploadStatusDTO status = null;
		List<UploadServiceErrorDetailsDTO> errorDetailsDTOs = null;
		try {
			status = opportunityUploadService.saveDocument(file, userId);
			if (status != null) {
				errorDetailsDTOs = status.getListOfErrors();
				for (UploadServiceErrorDetailsDTO up : status.getListOfErrors()) {
					logger.error(up.getRowNumber() + "   " + up.getMessage());
				}
			}
			InputStreamResource excelFile = uploadErrorReport
					.getErrorSheet(errorDetailsDTOs);
			HttpHeaders respHeaders = new HttpHeaders();
			respHeaders
					.setContentType(MediaType
							.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
			respHeaders.setContentDispositionFormData("attachment",
					"upload_error.xlsx");
			logger.info("Inside OpportunityController: end of /opportunity/upload POST");
			return new ResponseEntity<InputStreamResource>(excelFile,
					respHeaders, HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in uploading the opportunity details");
		}

	}

	/**
	 * This Controller used to download the opportunity_t in excel format
	 * 
	 * @param oppFlag
	 * @param dealValueFlag
	 * @param fields
	 * @param view
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> downloadOpportunity(
			@RequestParam("downloadOpportunities") boolean oppFlag,
			@RequestParam("isDealValuesInUSDRequired") boolean dealValueFlag,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside OpportunityController: Start of /opportunity/download GET");
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		HttpHeaders respHeaders = null;
		InputStreamResource opportunityDownloadExcel = null;
		try {
			opportunityDownloadExcel = opportunityDownloadService
					.downloadDocument(oppFlag, userId, dealValueFlag);
			respHeaders = new HttpHeaders();
			String todaysDate_formatted = DateUtils.getCurrentDateInDesiredFormat();
			String environmentName=PropertyUtil.getProperty("environment.name");
			String repName = environmentName+"_OpportunityDownload_" + todaysDate_formatted + ".xlsm";
			respHeaders.add("reportName", repName);
			respHeaders.setContentDispositionFormData("attachment",repName);
			respHeaders.setContentType(MediaType
					.parseMediaType("application/octet-stream"));
			logger.info("Inside OpportunityController: End of /opportunity/download GET");
			return new ResponseEntity<InputStreamResource>(
					opportunityDownloadExcel, respHeaders, HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in downloading the opportunity in excel");
		}
	}

	/**
	 * This Controller used to retrieve the list of opportunities for the given
	 * opportunity ids
	 * 
	 * @param opportunityIds
	 * @param fields
	 * @param view
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public @ResponseBody String findByOpportunityIds(
			@RequestParam("ids") List<String> opportunityIds,
			@RequestParam(value = "currency", defaultValue = "") List<String> currencies,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside OpportunityController: Start of /opportunity/list/Id="
				+ opportunityIds + " GET");
		String response = null;
		List<OpportunityT> opportunityList;
		try {
			opportunityList = opportunityService.findByOpportunityIds(
					opportunityIds, currencies);

			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, opportunityList);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the opportunity details for opportunity id's :"
							+ opportunityIds);
		}
		logger.info("Inside OpportunityController: End of /opportunity/list/Id="
				+ opportunityIds + " GET");
		return response;
	}
	
	/**
	 * This Controller used to retrieve the list of delivery centres 
	 * 
	 * @param opportunityIds
	 * @param fields
	 * @param view
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/deliveryownership", method = RequestMethod.GET)
	public @ResponseBody String fetchDeliveryOwnershipDetails(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside OpportunityController: Start of /opportunity/deliveryownership GET");
		String response = null;
		List<DeliveryOwnershipT> deliveryOwnershipDetails;
		try {
			deliveryOwnershipDetails = opportunityService.fetchDeliveryOwnershipDetails();

			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, deliveryOwnershipDetails);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the delivery ownership details");
		}
		logger.info("Inside OpportunityController: End of/opportunity/deliveryownership GET");
		return response;
	}
	
	/**
	 * This Controller used to retrieve the list of delivery ownership options
	 * 
	 * @param opportunityIds
	 * @param fields
	 * @param view
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/deliverycentre", method = RequestMethod.GET)
	public @ResponseBody String fetchDeliveryCentre(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside OpportunityController: Start of /opportunity/deliverycentre GET");
		String response = null;
		List<DeliveryCentreT> deliveryCentres;
		try {
			deliveryCentres = opportunityService.fetchDeliveryCentre();

			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, deliveryCentres);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the delivery centre details");
		}
		logger.info("Inside OpportunityController: End of/opportunity/deliverycentre GET");
		return response;
	}
	
	/**
	 * Service to fetch the opportunity related information based on search type and the search keyword 
	 * @param searchType - category type
	 * @param term - keyword
	 * @param getAll - true, to retrieve entire result, false to filter the result to only 3 records.(<b>default:false</b>)
	 * @param currency - currency to convert the deal value. Default : USD
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/search/smart", method = RequestMethod.GET)
	public @ResponseBody String smartSearch(
			@RequestParam("searchType") String searchType,
			@RequestParam("term") String term,
			@RequestParam(value = "getAll", defaultValue = "false") boolean getAll,
			@RequestParam(value = "currency", defaultValue = "USD") List<String> currency,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "30") int count,
			@RequestParam(value = "view", defaultValue = "") String view)
					throws DestinationException {
		logger.info("Inside OpportunityController: smart search by search term");
		try {
			UserT user = DestinationUtils.getCurrentUserDetails();
			PageDTO<SearchResultDTO<OpportunityT>> res = opportunityService.smartSearch(SmartSearchType.get(searchType), term, getAll, currency, page, count,user);
			logger.info("Inside OpportunityController: End - smart search by search term");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, res, !getAll);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Error on smartSearch", e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while retrieving opportunity smart search");
		}
		
	}

	/**
	 * This method retrieves the opportunity details by customer id
	 * 
	 * @param customerId
	 * @param currencies
	 * @param fields
	 * @param view
	 * @return opportunities
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/search/link", method = RequestMethod.GET)
	public @ResponseBody String findByCustomerId(
			@RequestParam(value = "currency", defaultValue = "") List<String> currencies,
			@RequestParam("from") @DateTimeFormat(pattern = "ddMMyyyy") Date fromDate,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "30") int count,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view,
			@RequestParam(value = "customerId") String customerId,
			@RequestParam("searchType") String searchType,
			@RequestParam("term") String term)
			throws DestinationException {

		logger.info("Inside OpportunityController: Start of /opportunity/search/link GET");
		String response = null;
		PaginatedResponse paginatedResponse = null;
		try {
			paginatedResponse = opportunityService.findOpportunitiesByCustomerIdAndSearchTerm(fromDate, 
					customerId, currencies, SmartSearchType.get(searchType), term, page, count);
			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, paginatedResponse);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retieving the opportunity detail for the customer id:"
							+ customerId);
		}
		logger.info("Inside OpportunityController End of /opportunity/search/link GET");
		return response;
	}
	
	@RequestMapping(value = "/all/privilege", method = RequestMethod.GET)
	public @ResponseBody String findOppByPrivilege(
			@RequestParam(value = "fromDate", defaultValue = "") @DateTimeFormat(pattern = "ddMMyyyy") Date fromDate,
			@RequestParam(value = "toDate", defaultValue = "") @DateTimeFormat(pattern = "ddMMyyyy") Date toDate,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside OpportunityController: Start of /opportunity/all/privilege");
		String response = null;
		PaginatedResponse opportunities;
		try {
			opportunities = opportunityService.getOpportunitiesBasedOnPrivileges(fromDate,toDate);
			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, opportunities);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the opportunity details ");
		}
		logger.info("Inside OpportunityController: End of /opportunity/all/privilege");
		return response;
	}
	
	
}
