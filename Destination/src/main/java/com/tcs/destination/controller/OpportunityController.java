package com.tcs.destination.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

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
import com.tcs.destination.bean.OpportunityResponse;
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

	// @Autowired
	// CustomerRepository customerRepository;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findOne(
			@RequestParam("nameWith") String nameWith,
			@RequestParam(value = "customerId", defaultValue = "") String customerId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "currency", defaultValue = "") List<String> currencies,
			@RequestParam(value = "isAjax", defaultValue = "false") boolean isAjax,
			@RequestParam(value = "userId") String userId,

			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside OpportunityController /opportunity?nameWith="
				+ nameWith + " GET");
		List<OpportunityT> opportunities = opportunityService
				.findByOpportunityName(nameWith, customerId, currencies,
						isAjax, userId);
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

	@RequestMapping(value = "/taskowner", method = RequestMethod.GET)
	public @ResponseBody String findByTaskOwner(
			@RequestParam("id") String userId,
			@RequestParam(value = "currency", defaultValue = "") List<String> currencies,
			@RequestParam(value = "role", defaultValue = "ALL") String opportunityRole,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside OpportunityController /opportunity/taskowner?id="
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
			opportunityService.createOpportunity(opportunity, false);
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

	@RequestMapping(value = "/salesStage", method = RequestMethod.GET)
	public @ResponseBody String findShelved(
			@RequestParam(value = "salesStageCode") int salesStageCode,
			@RequestParam(value = "customerId", defaultValue = "") String customerId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "currency", defaultValue = "") List<String> currencies,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside OpportunityController /opportunity/shelved GET");
		List<OpportunityT> opportunities = opportunityService
				.findOpportunitiesBySalesStageCode(currencies, salesStageCode,
						customerId);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				opportunities);
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
			throws Exception {
		logger.debug("Inside OpportunityController /opportunity/team/oppDealValue?id="
				+ supervisorUserId + " GET");
		List<OpportunitiesBySupervisorIdDTO> opportunities = null;

		opportunities = opportunityService
				.findDealValueOfOpportunitiesBySupervisorId(supervisorUserId);

		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				opportunities);
	}

	@RequestMapping(value = "/reopen", method = RequestMethod.GET)
	public @ResponseBody String findAllReOpen(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside OpportunityReopenRequestService /reopen GET");
		List<OpportunityReopenRequestT> opportunityReopenRequestTs = opportunityReopenRequestService
				.findAllReOpenRequests();
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				opportunityReopenRequestTs);
	}

	@RequestMapping(value = "/reopen/{id}", method = RequestMethod.GET)
	public @ResponseBody String findById(
			@PathVariable("id") String id,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside OpportunityReopenRequestService /reopen/" + id
				+ " GET");
		OpportunityReopenRequestT opportunityReopenRequestT = opportunityReopenRequestService
				.findOne(id);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				opportunityReopenRequestT);
	}

	@RequestMapping(value = "/reopen", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> addNewReopenRequest(
			@RequestBody OpportunityReopenRequestT opportunityReopenRequestT,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside OpportunityReopenRequestService /reopen POST");
		Status status = new Status();
		status.setStatus(Status.FAILED, "Save unsuccessful");
		try {
			opportunityReopenRequestService.create(opportunityReopenRequestT);
		} catch (Exception e) {
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		status.setStatus(Status.SUCCESS,
				opportunityReopenRequestT.getOpportunityReopenRequestId());
		return new ResponseEntity<String>(
				ResponseConstructors.filterJsonForFieldAndViews("all", "",
						status), HttpStatus.OK);
	}

	@RequestMapping(value = "/reopen", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> editReopenRequest(
			@RequestBody OpportunityReopenRequestT opportunityReopenRequestT,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside OpportunityReopenRequestService /reopen PUT");
		Status status = new Status();
		status.setStatus(Status.FAILED, "Update unsuccessful");
		try {
			opportunityReopenRequestService.edit(opportunityReopenRequestT);
		} catch (Exception e) {
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		status.setStatus(Status.SUCCESS,
				opportunityReopenRequestT.getOpportunityReopenRequestId());
		return new ResponseEntity<String>(
				ResponseConstructors.filterJsonForFieldAndViews("all", "",
						status), HttpStatus.OK);
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
			throws Exception {

		logger.debug("Inside OpportunityController /opportunity/team/oppDetails?id="
				+ supervisorUserId + " GET");

		if ((page < 0) || (count < 0)) {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid pagination request");
		}

		TeamOpportunityDetailsDTO teamOpportunityDetails = null;

		teamOpportunityDetails = opportunityService
				.findTeamOpportunityDetailsBySupervisorId(supervisorUserId,
						page, count, isCurrentFinancialYear, salesStageCode);

		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				teamOpportunityDetails);
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public @ResponseBody String searchOpportunities(
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
			throws Exception {
		List<OpportunityT> opportunity = opportunityService.getByOpportunities(
				customerIdList, salesStageCode, strategicInitiative, newLogo,
				minDigitalDealValue, maxDigitalDealValue, dealCurrency,
				digitalFlag, displayIou, country, partnerId, competitorName,
				searchKeywords, bidRequestType, offering, displaySubSp,
				opportunityName, userId, currency);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				opportunity);
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
			throws Exception {
		logger.debug("Inside OpportunityService /all GET");
		OpportunityResponse opportunityResponse = opportunityService.findAll(
				sortBy, order, isCurrentFinancialYear, page, count);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				opportunityResponse);
	}

	@RequestMapping(value = "/name", method = RequestMethod.GET)
	public @ResponseBody String findOppNameOrKeyword(
			@RequestParam(value = "name", defaultValue = "") String name,
			@RequestParam(value = "keyword", defaultValue = "") String keyword,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside OpportunityService /all GET");
		ArrayList<OpportunityNameKeywordSearch> searchResults = null;

		searchResults = opportunityService.findOpportunityNameOrKeywords(name,
				keyword);
		if ((searchResults == null) || (searchResults.isEmpty())) {
			logger.error("No Results found for name {} and keyword {}", name,
					keyword);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Results found for name " + name + " and keyword "
							+ keyword);
		}

		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				searchResults);
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<InputStreamResource> uploadOpportunity(
			@RequestParam("userId") String userId,
			@RequestParam("file") MultipartFile file,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
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
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
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
	}

	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> downloadOpportunity(
			@RequestParam("userId") String userId,
			@RequestParam("downloadOpportunities") boolean oppFlag,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		HttpHeaders respHeaders = null;
		InputStreamResource opportunityDownloadExcel = null;
		try {
			opportunityDownloadExcel = opportunityDownloadService
					.downloadDocument(oppFlag, userId);
			respHeaders = new HttpHeaders();
			respHeaders.setContentDispositionFormData("attachment",
					"opportunityDownload" + DateUtils.getCurrentDate()
							+ ".xlsm");
			respHeaders.setContentType(MediaType
					.parseMediaType("application/octet-stream"));
			logger.debug("Connect Summary Report Downloaded Successfully ");
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		return new ResponseEntity<InputStreamResource>(
				opportunityDownloadExcel, respHeaders, HttpStatus.OK);

	}

}
