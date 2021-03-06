package com.tcs.destination.controller;


import java.io.ByteArrayInputStream;

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

import com.tcs.destination.bean.OpportunityReopenRequestT;
import com.tcs.destination.bean.PaginatedResponse;
import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.bean.WorkflowBfmDetailsDTO;
import com.tcs.destination.bean.WorkflowBfmT;
import com.tcs.destination.bean.WorkflowCompetitorDetailsDTO;
import com.tcs.destination.bean.WorkflowCompetitorT;
import com.tcs.destination.bean.WorkflowCustomerDetailsDTO;
import com.tcs.destination.bean.WorkflowCustomerT;
import com.tcs.destination.bean.WorkflowPartnerDetailsDTO;
import com.tcs.destination.bean.WorkflowPartnerT;
import com.tcs.destination.bean.WorkflowStepT;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.data.repository.WorkflowCustomerTRepository;
import com.tcs.destination.data.repository.WorkflowRequestTRepository;
import com.tcs.destination.data.repository.WorkflowStepTRepository;
import com.tcs.destination.enums.EntityTypeId;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.WorkflowService;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * This controller deals with the workflow related functionalities
 * 
 * @author
 *
 */
@RestController
@RequestMapping("/workflow")
public class WorkflowController {

	private static final Logger logger = LoggerFactory
			.getLogger(WorkflowController.class);

	@Autowired
	WorkflowStepTRepository workflowStepTRepository;

	@Autowired
	WorkflowRequestTRepository workflowRequestTRepository;

	@Autowired
	WorkflowCustomerTRepository workflowCustomerTRepository;

	@Autowired
	WorkflowService workflowService;
	
	@Autowired
	UserRepository userRepository;

	/**
	 * work flow for rejection process
	 * 
	 * @param <T>
	 * @param WorkflowStepT
	 * @param workflowCustomerT
	 * @param fields
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/approve/customer", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> approveCustomers(
			@RequestBody WorkflowCustomerT workflowCustomerT,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {

		logger.info("Inside WorkflowController: Start of approve Customer");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (workflowService.approveWorkflowEntity(workflowCustomerT)) {
				status.setStatus(Status.SUCCESS,
						"The requested Customer entity:" + workflowCustomerT.getCustomerName() + " is approved!!!");
				logger.debug("Request approved Successfully");
			}
			logger.info("Inside WorkflowController: End of approve Customer");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while updating customer");
		}
	}

	@RequestMapping(value = "/approve/partner", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> approvePartners(
			@RequestBody WorkflowPartnerT workflowPartnerT,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {

		logger.info("Inside WorkflowController: Start of approve partner");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (workflowService.approvePartnerWorkflowEntity(workflowPartnerT)) {
				status.setStatus(Status.SUCCESS,
						"The requested Partner entity:" + workflowPartnerT.getPartnerName() + " is approved!!!");
				logger.debug("Request approved Successfully");
			}
			logger.info("Inside WorkflowController: End of approve Partner");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while updating Partner");
		}
	}
	
	/**
	 * work flow for rejection process
	 * 
	 * @param WorkflowStepT
	 * @param workflowCustomerT
	 * @param fields
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/reject", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> rejectCustomers(
			@RequestBody WorkflowStepT workflowStepT,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {

		logger.info("Inside WorkflowController: Start of reject Customer");
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		UserT userT = userRepository.findByUserId(userId);
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (workflowService.rejectWorkflowEntity(workflowStepT)) {
				status.setStatus(Status.SUCCESS, "Request is REJECTED by " + userT.getUserName() + "!!!");
				logger.debug("Request is REJECTED by " + userT.getUserName() + "!!!");
			}
			logger.info("Inside WorkflowController: End of reject Customer");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while Rejecting the request");
		}
	}

	/**
	 * This method inserts the workflow customer including respective workflow
	 * request and steps for normal users and inserts the customer and mapping
	 * details for strategic group admin
	 * 
	 * @param workflowCustomerT
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/requestCustomer", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> insertRequestedCustomer(
			@RequestBody WorkflowCustomerT workflowCustomerT)
			throws DestinationException {

		logger.info("Inside WorkflowController: Start of inserting requested customer");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (workflowService.insertWorkflowCustomer(workflowCustomerT,
					status)) {
				logger.info("End of inserting requested customer");
			}
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while inserting requested customer");
		}

	}

	/**
	 * This method is used to retrieve requested new customer details based on request id
	 * @param requestedCustomerId
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/customer/{id}", method = RequestMethod.GET)
	public @ResponseBody String getRequestedCustomerById(
			@PathVariable("id") int requestedCustomerId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside WorkflowController : Start of retrieving requested customer details by id");
		WorkflowCustomerDetailsDTO workflowCustomerDetails = null;
		try {			
			workflowCustomerDetails = workflowService
					.findRequestedCustomerDetailsById(requestedCustomerId);
			logger.info("Inside WorkflowCustomerController : End of retrieving requested customer details by id");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
				view, workflowCustomerDetails);

		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving customer details");
		}
	}
	
	/**
	 * This method is used to retrieve requested new partner details based on request id
	 * @param requestedPartnerId
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/partner/{id}", method = RequestMethod.GET)
	public @ResponseBody String getRequestedParnerById(
			@PathVariable("id") int requestedPartnerId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside WorkflowController : Start of retrieving requested partner details by id");
		WorkflowPartnerDetailsDTO workflowPartnerDetails = null;
		try {			
			workflowPartnerDetails = workflowService
					.findRequestedPartnerDetailsById(requestedPartnerId);
			logger.info("Inside WorkflowCustomerController : End of retrieving requested partner details by id");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
				view, workflowPartnerDetails);

		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving partner details");
		}
	}
	
	/**
	 * This method is used to retrieve requested new competitor details based on request id
	 * @param requestedCompetitorId
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/competitor/{id}", method = RequestMethod.GET)
	public @ResponseBody String getRequestedCompetitorById(
			@PathVariable("id") int requestedCompetitorId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside WorkflowController : Start of retrieving requested competitor details by id");
		WorkflowCompetitorDetailsDTO workflowCompetitorDetails = null;
		try {			
			workflowCompetitorDetails = workflowService
					.findRequestedCompetitorDetailsById(requestedCompetitorId);
			logger.info("Inside WorkflowCustomerController : End of retrieving requested competitor details by id");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
				view, workflowCompetitorDetails);

		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving competitor details");
		}
	}



	
	/**
	 * This service is used to retrive the details of worklist of the logged in user
	 * @param page
	 * @param count
	 * @param fields
	 * @param view
	 * @param status
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/myWorklist", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getAllCustomerRequestsInWorklist(
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "30") int count,
			@RequestParam(value = "fields", defaultValue = "") String fields,
			@RequestParam(value = "view", defaultValue = "") String view,
			@RequestParam(value = "status", defaultValue = "ALL") String status)
			throws DestinationException {
		logger.info("Inside WorkflowController: Start of retrieving Worklist for a user");
		PaginatedResponse pageWorklist = new PaginatedResponse();
		try {
			pageWorklist = workflowService.getMyWorklist(status,page,count);
			logger.info("Inside WorkflowCustomerController: End of retrieving Worklist for a user");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews(fields,
							view, pageWorklist), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while retrieving Worklist for a user");
		}
	}
	
	/**
	 * This method inserts the workflow partner including respective workflow
	 * request and steps for normal users and inserts the customer and mapping
	 * details for strategic group admin
	 * @param workflowPartnerT
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/requestPartner", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> addPartnerRequest(
			@RequestBody WorkflowPartnerT workflowPartnerT)
			throws DestinationException {

		logger.info("Inside WorkflowController: Start of inserting requested partner");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (workflowPartnerT != null) {
				if (workflowService.addPartner(workflowPartnerT, status)) {
					logger.info("Inside WorkflowController: End of inserting requested partner");
				}
			}
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while Inserting Workflow Partner");
		}

	}
	
	/**
	 * This method is used to request to reopen the opportunity which got shelved
	 * @param opportunityReopenRequestT
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/opportunityReopen", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> opportunityReopenRequest(
			@RequestBody OpportunityReopenRequestT opportunityReopenRequestT)
			throws DestinationException {

		logger.info("Inside WorkflowController: Start of requesting opportunity reopen");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (opportunityReopenRequestT != null) {
				if (workflowService.requestOpportunityReopen(opportunityReopenRequestT, status)) {
					logger.info("Inside WorkflowController: End of requesting opportunity reopen");
				}
			}
				return new ResponseEntity<String>(
						ResponseConstructors.filterJsonForFieldAndViews("all", "",
								status), HttpStatus.OK);
			} catch (DestinationException e) {
				throw e;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
						"Backend error while requesting opportunity reopen");
			}
		}
	
				
				
	@RequestMapping(value = "/requestCompetitor", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> addCompetitorRequest(
			@RequestBody WorkflowCompetitorT workflowCompetitorT)
					throws DestinationException {
		logger.info("Inside WorkflowController: Start of inserting requested competitor");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (workflowService.insertWorkflowCompetitor(workflowCompetitorT,
					status)) {
				logger.info("Inside WorkflowController: End of inserting requested competitor");
			}
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while requesting New Competitor");
		}

	}
	
	/**
	 * This method is used to approve or reject the opportunity reopen request
	 * @param opportunityReopenRequestT
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/approveOrReject/opportunityReopen", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> approveOpportunityReopenRequest(
			@RequestBody OpportunityReopenRequestT opportunityReopenRequestT)
			throws DestinationException {

		logger.info("Inside WorkflowController: Start of approving opportunity reopen");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (opportunityReopenRequestT != null) {
				if (workflowService.approveOrRejectOpportunityReopen(opportunityReopenRequestT, status)) {
					logger.info("Inside WorkflowController: End of approving opportunity reopen");
				}
			}
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while approving opportunity reopen");
		}

	}
	
	/**
	 * To approve the new competitor request
	 * @param workflowCompetitorT
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/approve/competitor", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> approveCompetitors(
			@RequestBody WorkflowCompetitorT workflowCompetitorT,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {

		logger.info("Inside WorkflowController: Start of approve competitor");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (workflowService.approveCompetitorWorkflowEntity(workflowCompetitorT)) {
				status.setStatus(Status.SUCCESS,
						"The requested competitor entity:" + workflowCompetitorT.getWorkflowCompetitorName() + " is approved!!!");
				logger.debug("Request approved Successfully");
			}
			logger.info("Inside WorkflowController: End of approve competitor");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while updating competitor");
		} 
	}
	/**
	 * This method is used to approve or reject or escalate the BFM request
	 * @param opportunityReopenRequestT
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/approveOrRejectOrEscalate/bfm", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> approveOrEscalateBfm(
			@RequestBody WorkflowBfmT workflowBfmT)
			throws DestinationException {

		logger.info("Inside WorkflowController: Start of approveOrRejectOrEscalate bfm request");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (workflowBfmT != null) {
				if (workflowService.approveOrEscalateBfm(workflowBfmT, status)) {
					logger.info("Inside WorkflowController: End of approveOrRejectOrEscalate bfm request");
				}
			}
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while approving opportunity reopen");
		}

	}
	
	/**
	 * This service is used to retrive the details of worklist of the logged in user based upon the type
	 * @param type
	 * @param page
	 * @param count
	 * @param fields
	 * @param view
	 * @param status
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/myWorklistNew", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getMyWorklist(
			@RequestParam(value = "type") Integer type,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "30") int count,
			@RequestParam(value = "fields", defaultValue = "") String fields,
			@RequestParam(value = "view", defaultValue = "") String view,
			@RequestParam(value = "status", defaultValue = "ALL") String status)
			throws DestinationException {
		logger.info("Inside WorkflowController: Start of retrieving Worklist for a user");
		PaginatedResponse pageWorklist = new PaginatedResponse();
		try {
			pageWorklist = workflowService.getMyWorklistByType(EntityTypeId.getFrom(type), status,page,count);
			logger.info("Inside WorkflowController: End of retrieving Worklist for a user");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews(fields,
							view, pageWorklist), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while retrieving Worklist for a user");
		}
	}
	
	/**
	 * This method is used to retrieve requested new opportunity deal financial details based on request id
	 * @param requestedBfmId
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/bfm/{id}", method = RequestMethod.GET)
	public @ResponseBody String getRequestedBfmById(
			@PathVariable("id") int requestedBfmId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside WorkflowController : Start of retrieving requested deal financial details by id");
		WorkflowBfmDetailsDTO workflowBfmDetails = null;
		try {			
			workflowBfmDetails = workflowService.findRequestedBfmDetailsById(requestedBfmId);
			logger.info("Inside WorkflowCustomerController : End of retrieving requested deal financial details by id");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
				view, workflowBfmDetails);

		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving deal financial details");
		}
	}


	/**
	 * Service method used to download the deal financial file
	 * @param id - request id
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/bfm/download", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> downloadBFMFile(
			@RequestParam("requestId") Integer id)
			throws DestinationException {
		logger.info("Inside WorkflowController: Start of downloadBFMFile download");
		HttpHeaders respHeaders = null;
		InputStreamResource bfmStream = null;
		try {
			WorkflowBfmT bfmT = workflowService.downloadBFMFile(id);
			bfmStream = new InputStreamResource(new ByteArrayInputStream(bfmT.getDealFinancialFile()));
			
			respHeaders = new HttpHeaders();
			//String fileName = bfmT.getOpportunityId() + "_" + bfmT.getOpportunityT().getCustomerMasterT().getCustomerName() + "." + DestinationUtils.getExtension(bfmT.getBfmFileName());
			String fileName = bfmT.getBfmFileName();
			respHeaders.add("reportName", fileName);
			respHeaders.setContentDispositionFormData("attachment", fileName);
			respHeaders.setContentType(MediaType
					.parseMediaType("application/octet-stream"));
			logger.info("Inside WorkflowController: BFMFile Downloaded Successfully ");
			return new ResponseEntity<InputStreamResource>(
					bfmStream, respHeaders, HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR : ", e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in downloading the BFM file");
		}
	}
	
	/**
	 * Service method used to download the template for deal financial file
	 * @param id - request id
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/bfm/templatedownload", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> downloadBFMFTemplateile()
			throws DestinationException {
		logger.info("Inside WorkflowController: Start of downloadBFMFTemplateile download");
		HttpHeaders respHeaders = null;
		InputStreamResource bfmStream = null;
		try {
			bfmStream =  workflowService.downloadBfmTemplate();
			
			respHeaders = new HttpHeaders();
			String fileName = "Deal_Financials_Template_FY17_eff_3rd_Aug_2016_v2.xlsx";
			respHeaders.add("reportName", fileName);
			respHeaders.setContentDispositionFormData("attachment", fileName);
			respHeaders.setContentType(MediaType
					.parseMediaType("application/octet-stream"));
			logger.info("Inside WorkflowController: BFM Template Downloaded Successfully ");
			return new ResponseEntity<InputStreamResource>(
					bfmStream, respHeaders, HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR : ", e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in downloading the BFM Tempalte file");
		}
	}
	

}
