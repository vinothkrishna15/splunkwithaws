package com.tcs.destination.controller;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/workflow")
public class WorkflowController {

	private static final Logger logger = LoggerFactory
			.getLogger(WorkflowController.class);


	@RequestMapping(value = "/requestCustomer", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> insertRequestedCustomer(
			@RequestBody WorkflowCustomerT workflowCustomerT)
			throws DestinationException {

		logger.info("Inside WorkflowController: Start of inserting requested customer");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {

			status.setStatus(Status.SUCCESS,
					"Request for creation of customer Submitted");

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
			workflowCustomerDetails = mapper.readValue(json, WorkflowCustomerDetailsDTO.class);
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
			MyWorklistDTO[] myWorklist = mapper.readValue(json, MyWorklistDTO[].class);
			List<MyWorklistDTO> myWorklists = new ArrayList<MyWorklistDTO>(); 
			for(int i=0;i<myWorklist.length;i++)
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
