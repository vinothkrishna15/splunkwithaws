package com.tcs.destination.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
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

import com.tcs.destination.bean.OpportunitiesBySupervisorIdDTO;
import com.tcs.destination.bean.OpportunityNameKeywordSearch;
import com.tcs.destination.bean.OpportunityReopenRequestT;
import com.tcs.destination.bean.PaginatedResponse;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.TeamOpportunityDetailsDTO;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UploadStatusDTO;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.OpportunityDownloadService;
import com.tcs.destination.service.OpportunityReopenRequestService;
import com.tcs.destination.service.OpportunityService;
import com.tcs.destination.service.OpportunityUploadService;
import com.tcs.destination.service.UploadErrorReport;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationUtils;
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

	@Autowired
	OpportunityReopenRequestService opportunityReopenRequestService;

	@Autowired
	OpportunityUploadService opportunityUploadService;

	@Autowired
	UploadErrorReport uploadErrorReport;

	@Autowired
	OpportunityDownloadService opportunityDownloadService;
   
	private static final DateFormat actualFormat = new SimpleDateFormat("dd-MMM-yyyy");
	private static final DateFormat desiredFormat = new SimpleDateFormat("MM/dd/yyyy");
	
	// @Autowired
	// CustomerRepository customerRepository;

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
		
		String userId=DestinationUtils.getCurrentUserDetails().getUserId();
		logger.debug("Inside OpportunityController /opportunity?nameWith="
				+ nameWith + " GET");
		String response = null;
		PaginatedResponse opportunities;
		try {
			opportunities = opportunityService
					.findByOpportunityName(nameWith, customerId, currencies,
							isAjax, userId, page, count);
			response = ResponseConstructors.filterJsonForFieldAndViews(fields, view,
					opportunities);
		} catch (DestinationException e) {
			throw e;
		}catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retieving the opportunity details");
		}
		return response;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody String findByOpportunityId(
			@PathVariable("id") String opportunityId,
			@RequestParam(value = "currency", defaultValue = "") List<String> currencies,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		
		logger.debug("Inside OpportunityController /opportunity/Id="
				+ opportunityId + " GET");
		String response = null;
		OpportunityT opportunity;
		try {
			opportunity = opportunityService.findByOpportunityId(
					opportunityId, currencies);
			response = ResponseConstructors.filterJsonForFieldAndViews(fields, view,
					opportunity);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retieving the opportunity detail for the id:" + opportunityId);
		}
		return response;
	}

	@RequestMapping(value = "/recent", method = RequestMethod.GET)
	public @ResponseBody String findByCustomerId(
			@RequestParam("customerId") String customerId,
			@RequestParam(value = "currency", defaultValue = "") List<String> currencies,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		
		logger.debug("Inside OpportunityController /opportunity/recent?customerId="
				+ customerId + " GET");
		String response = null;
		List<OpportunityT> opportunities;
		try {
			opportunities = opportunityService
					.findRecentOpportunities(customerId, currencies);
			response = ResponseConstructors.filterJsonForFieldAndViews(fields, view,
					opportunities);
		}  catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retieving the opportunity detail for the customer id:" + customerId);
		}
		return response;
	}

	@RequestMapping(value = "/taskowner", method = RequestMethod.GET)
	public @ResponseBody String findByTaskOwner(
			@RequestParam(value = "currency", defaultValue = "") List<String> currencies,
			@RequestParam(value = "role", defaultValue = "ALL") String opportunityRole,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		
		String userId=DestinationUtils.getCurrentUserDetails().getUserId();
		logger.debug("Inside OpportunityController /opportunity/taskowner?id="
				+ userId + " GET");
		String response = null;
		List<OpportunityT> opportunities;
		try {
			opportunities = opportunityService
					.findOpportunitiesByOwnerAndRole(userId, opportunityRole,
							currencies);
			response =  ResponseConstructors.filterJsonForFieldAndViews(fields, view,
					opportunities);
		} catch (DestinationException e) {
			throw e;
		}catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retieving the opportunity details for the task owner:" + userId);
		}
		return response;
	}

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> createOpportunity(
			@RequestBody OpportunityT opportunity,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.debug("Inside OpportunityController /opportunity POST");
		Status status = new Status();
		status.setStatus(Status.FAILED, "Save unsuccessful");
		try {
			opportunityService.createOpportunity(opportunity, false, null, null);

			status.setStatus(Status.SUCCESS, opportunity.getOpportunityId());
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in creating the opportunity");
		}
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
			throws DestinationException {
		
		logger.debug("Inside OpportunityController /opportunity PUT");
		Status status = new Status();
		try {
			opportunityService.updateOpportunity(opportunity);
			status.setStatus(Status.SUCCESS, opportunity.getOpportunityId());
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while updating the opportunity");
		}
		
	}

	@RequestMapping(value = "/salesStage", method = RequestMethod.GET)
	public @ResponseBody String findShelved(
			@RequestParam(value = "salesStageCode") int salesStageCode,
			@RequestParam(value = "customerId", defaultValue = "") String customerId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "currency", defaultValue = "") List<String> currencies,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.debug("Inside OpportunityController /opportunity/shelved GET");
		String response = null;
		List<OpportunityT> opportunities;
		try {
		opportunities = opportunityService
				.findOpportunitiesBySalesStageCode(currencies, salesStageCode,
						customerId);
		
		response = ResponseConstructors.filterJsonForFieldAndViews(fields, view,
					opportunities);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the opportunity details for the sales stage code:" + salesStageCode);
		}
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
		logger.debug("Inside OpportunityController /opportunity/team/oppDealValue?id="
				+ supervisorUserId + " GET");
		String response = null;
		List<OpportunitiesBySupervisorIdDTO> opportunities = null;

		try {
			opportunities = opportunityService
					.findDealValueOfOpportunitiesBySupervisorId(supervisorUserId);
			response = ResponseConstructors.filterJsonForFieldAndViews(fields, view,
					opportunities);
		} catch (DestinationException e) {
			throw e;
		}catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the Deal value of opportunities for supervisor id:" + supervisorUserId);
		}

		return response;
	}

	@RequestMapping(value = "/reopen", method = RequestMethod.GET)
	public @ResponseBody String findAllReOpen(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.debug("Inside OpportunityReopenRequestService /reopen GET");
		String response = null;
		List<OpportunityReopenRequestT> opportunityReopenRequestTs;
		try {
		opportunityReopenRequestTs = opportunityReopenRequestService
				.findAllReOpenRequests();
		
			response = ResponseConstructors.filterJsonForFieldAndViews(fields, view,
					opportunityReopenRequestTs);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the opportunity reopen requests");
		}
		return response;
	}

	@RequestMapping(value = "/reopen/{id}", method = RequestMethod.GET)
	public @ResponseBody String findById(
			@PathVariable("id") String id,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.debug("Inside OpportunityReopenRequestService /reopen/" + id
				+ " GET");
		String response = null;
		OpportunityReopenRequestT opportunityReopenRequestT;
		try {
		opportunityReopenRequestT = opportunityReopenRequestService
				.findOne(id);
		
			response = ResponseConstructors.filterJsonForFieldAndViews(fields, view,
					opportunityReopenRequestT);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the opportunity reopen request for id:" + id);
		}
		return response;
	}

	@RequestMapping(value = "/reopen", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> addNewReopenRequest(
			@RequestBody OpportunityReopenRequestT opportunityReopenRequestT,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.debug("Inside OpportunityReopenRequestService /reopen POST");
		Status status = new Status();
		status.setStatus(Status.FAILED, "Save unsuccessful");
		try {
			opportunityReopenRequestService.create(opportunityReopenRequestT);
			status.setStatus(Status.SUCCESS,
					opportunityReopenRequestT.getOpportunityReopenRequestId());
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

	@RequestMapping(value = "/reopen", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> editReopenRequest(
			@RequestBody OpportunityReopenRequestT opportunityReopenRequestT,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.debug("Inside OpportunityReopenRequestService /reopen PUT");
		Status status = new Status();
		status.setStatus(Status.FAILED, "Update unsuccessful");
		try {
			opportunityReopenRequestService.edit(opportunityReopenRequestT);
			status.setStatus(Status.SUCCESS,
					opportunityReopenRequestT.getOpportunityReopenRequestId());
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

		logger.debug("Inside OpportunityController /opportunity/team/oppDetails?id="
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
			response = ResponseConstructors.filterJsonForFieldAndViews(fields, view,
					teamOpportunityDetails);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the team opportunity details for the supervisor id :" + supervisorUserId);
		}

		return response;
	}

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
			@RequestParam(value = "strategicInitiative", defaultValue = "") String strategicInitiative,
			@RequestParam(value = "salesStageCode", defaultValue = "") List<Integer> salesStageCode,
			@RequestParam(value = "searchKeywords", defaultValue = "") List<String> searchKeywords,
			@RequestParam(value = "minDigitalDealValue", defaultValue = "0") double minDigitalDealValue,
			@RequestParam(value = "maxDigitalDealValue", defaultValue = ""
					+ Double.MAX_VALUE) double maxDigitalDealValue,
			@RequestParam(value = "dealCurrency", defaultValue = "USD") String dealCurrency,
			@RequestParam(value = "currency", defaultValue = "USD") List<String> currency,
			@RequestParam(value = "userId", defaultValue = "") List<String> userId,
			@RequestParam(value = "digitalFlag", defaultValue = "") String digitalFlag,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		String response = null;
		PaginatedResponse opportunityResponse;
		try {
		opportunityResponse = opportunityService
				.getByOpportunities(customerIdList, salesStageCode,
						strategicInitiative, newLogo, minDigitalDealValue,
						maxDigitalDealValue, dealCurrency, digitalFlag,
						displayIou, country, partnerId, competitorName,
						searchKeywords, bidRequestType, offering, displaySubSp,
						opportunityName, userId, currency, page, count);
		
			response = ResponseConstructors.filterJsonForFieldAndViews(fields, view,
					opportunityResponse);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the opportunity details");
		}
		return response;
	}

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
		logger.debug("Inside OpportunityService /all GET");
		String response = null;
		PaginatedResponse opportunityResponse;
		try {
		opportunityResponse = opportunityService.findAll(
				sortBy, order, isCurrentFinancialYear, page, count);
		
			response = ResponseConstructors.filterJsonForFieldAndViews(fields, view,
					opportunityResponse);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the opportunity details");
		}
		return response;
	}

	@RequestMapping(value = "/name", method = RequestMethod.GET)
	public @ResponseBody String findOppNameOrKeyword(
			@RequestParam(value = "name", defaultValue = "") String name,
			@RequestParam(value = "keyword", defaultValue = "") String keyword,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.debug("Inside OpportunityService /all GET");
		ArrayList<OpportunityNameKeywordSearch> searchResults = null;
        String response = null;
		try {
			searchResults = opportunityService.findOpportunityNameOrKeywords(name,
				keyword);
		if ((searchResults == null) || (searchResults.isEmpty())) {
			logger.error("No Results found for name {} and keyword {}", name,
					keyword);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Results found for name " + name + " and keyword "
							+ keyword);
		}
			response = ResponseConstructors.filterJsonForFieldAndViews(fields, view,
					searchResults);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the Opportunity results with the keyword " + keyword + "or name" + name);
		}
		return response;
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<InputStreamResource> uploadOpportunity(
			@RequestParam("file") MultipartFile file,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		String userId=DestinationUtils.getCurrentUserDetails().getUserId();
		logger.debug("Upload request Received : docName - ");
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
			return new ResponseEntity<InputStreamResource>(excelFile, respHeaders,
					HttpStatus.OK);
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
	 * @param userId
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
		String userId=DestinationUtils.getCurrentUserDetails().getUserId();
		HttpHeaders respHeaders = null;
		InputStreamResource opportunityDownloadExcel = null;
		try {
			opportunityDownloadExcel = opportunityDownloadService
					.downloadDocument(oppFlag, userId, dealValueFlag);
			respHeaders = new HttpHeaders();
			String todaysDate = DateUtils.getCurrentDate();
			String todaysDate_formatted=desiredFormat.format(actualFormat.parse(todaysDate));
			respHeaders.setContentDispositionFormData("attachment",
					"OpportunityDownload_" + todaysDate_formatted
							+ ".xlsm");
			respHeaders.setContentType(MediaType
					.parseMediaType("application/octet-stream"));
			logger.debug("Opportunity Data Downloaded Successfully ");
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
	 * This Controller used to retrieve the list of opportunities for the given opportunity ids
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
		logger.debug("Inside OpportunityController /opportunity/Id=" + opportunityIds + " GET");
		String response = null;
		List<OpportunityT> opportunityList;
		try {
		opportunityList = opportunityService.findByOpportunityIds(opportunityIds, currencies);
		
			response = ResponseConstructors.filterJsonForFieldAndViews(fields, view, opportunityList);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the opportunity details for opportunity id's :" + opportunityIds);
		}
		return response;
	}

}
