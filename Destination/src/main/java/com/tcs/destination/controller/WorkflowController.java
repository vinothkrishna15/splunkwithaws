package com.tcs.destination.controller;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
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

import com.tcs.destination.bean.MyWorklistDTO;
import com.tcs.destination.bean.WorkflowCustomerDetailsDTO;
import com.tcs.destination.bean.WorkflowCustomerT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.WorkflowStepT;
import com.tcs.destination.data.repository.WorkflowCustomerTRepository;
import com.tcs.destination.data.repository.WorkflowRequestTRepository;
import com.tcs.destination.data.repository.WorkflowStepTRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.WorkflowService;
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
	@RequestMapping(value = "/approve", method = RequestMethod.POST)
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
						"The requested entity is approved!!!");
				logger.debug("Request approved Successfully");
			}
			logger.info("Inside WorkflowController: End of approve Customer");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while updating customer");
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
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (workflowService.rejectWorkflowEntity(workflowStepT)) {
				status.setStatus(Status.SUCCESS, workflowStepT.getStepStatus());
				logger.debug("Request rejected Successfully"
						+ workflowStepT.getStepStatus());
			}
			logger.info("Inside WorkflowController: End of reject Customer");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
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
			}
			logger.info("End of inserting requested customer");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while inserting requested customer");
		}

	}

	@RequestMapping(value = "/customer/{id}", method = RequestMethod.GET)
	public @ResponseBody String getRequestedCustomerById(
			@PathVariable("id") String requestedCustomerId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside WorkflowCustomerController : Start of retrieving requested customer by id");
		WorkflowCustomerDetailsDTO workflowCustomerDetails;
		try {
			ObjectMapper mapper = new ObjectMapper();
			String json = "{\"requestedCustomer\":{\"customerName\":\"ABC Corp\",\"groupCustomerName\":\"ABC Group\",\"geographyMappingT\":{\"geography\":\"Americas\"},\"iou\":\"BFS\"},\"numberOfSteps\":2,\"listOfSteps\":[{\"stepId\":\"1\",\"stepApprover\":\"PMO\",\"stepStatus\":\"APPROVED\"},{\"stepId\":\"2\",\"stepApprover\":\"STRATEGIC ADMIN\",\"stepStatus\":\"PENDING\"}]}";
			workflowCustomerDetails = mapper.readValue(json,
					WorkflowCustomerDetailsDTO.class);
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, workflowCustomerDetails);

		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving customer details");
		}
	}

	@RequestMapping(value = "/customer/myWorklist", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getAllCustomerRequestsInWorklist(
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "30") int count,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view,
			@RequestParam(value = "status", defaultValue = "ALL") String status)
			throws DestinationException {
		logger.info("Inside WorkflowCustomerController: Start of retrieving Worklist for a user");
		try {
			ObjectMapper mapper = new ObjectMapper();
			String json = "[{\"workflowStep\":{\"userT2\":{\"userName\":\"Rajan J\"},\"stepStatus\":\"Approved\",\"createdDatetime\":\"1456134044000\"},\"entityType\":\"CUSTOMER\",\"entityName\":\"ABC Corp\"},{\"workflowStep\":{\"userT2\":{\"userName\":\"Ronak Shah\"},\"stepStatus\":\"Pending\",\"createdDatetime\":\"1456134044000\"},\"entityType\":\"CUSTOMER\",\"entityName\":\"Adobe\"}]";
			MyWorklistDTO[] myWorklist = mapper.readValue(json,
					MyWorklistDTO[].class);
			List<MyWorklistDTO> myWorklists = new ArrayList<MyWorklistDTO>();
			for (int i = 0; i < myWorklist.length; i++)
				myWorklists.add(myWorklist[i]);
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews(fields,
							view, myWorklists), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while retrieving Worklist for a user");
		}
	}
}
