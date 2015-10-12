package com.tcs.destination.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.TargetVsActualResponse;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UploadStatusDTO;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.CustomerService;
import com.tcs.destination.service.CustomerUploadService;
import com.tcs.destination.service.UploadErrorReport;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * Controller to handle Customer module related requests.
 * 
 */
@RestController
@RequestMapping("/customer")
public class CustomerController {

	private static final Logger logger = LoggerFactory
			.getLogger(CustomerController.class);

	@Autowired
	CustomerService customerService;

	@Autowired
	CustomerUploadService customerUploadService;
	
	@Autowired
	UploadErrorReport uploadErrorReport;
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody String findOne(
			@PathVariable("id") String customerId,
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "currency", defaultValue = "USD") List<String> currency,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside CustomerController /customer/id=" + customerId
				+ " GET");
		CustomerMasterT customer = customerService.findById(customerId, userId, currency);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				customer);
	}

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findNameWith(
			@RequestParam(value = "nameWith", defaultValue = "") String nameWith,
			@RequestParam(value = "startsWith", defaultValue = "") String startsWith,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside CustomerController /customer?namewith=" + nameWith
				+ "and starts with " + startsWith + " GET");
		List<CustomerMasterT> customers = null;

		if (!nameWith.isEmpty()) {
			customers = customerService.findByNameContaining(nameWith);
		} else if (!startsWith.isEmpty()) {
			customers = customerService.findByNameStarting(startsWith);
		} else {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Either nameWith / startsWith is required");
		}
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				customers);

	}

	@RequestMapping(value = "/targetVsActual", method = RequestMethod.GET)
	public @ResponseBody String findTargetVsActual(
			@RequestParam(value = "year", defaultValue = "", required = false) String financialYear,
			@RequestParam(value = "quarter", defaultValue = "", required = false) String quarter,
			@RequestParam(value = "customer", defaultValue = "", required = false) String customerName,
			@RequestParam(value = "currency", defaultValue = "INR", required = false) String currency,
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "fields", defaultValue = "all", required = false) String fields,
			@RequestParam(value = "view", defaultValue = "", required = false) String view)
			throws Exception {
		logger.debug("Inside CustomerController /customer/targetVsActual GET");
		List<TargetVsActualResponse> tarVsAct = customerService
				.findTargetVsActual(financialYear, quarter, customerName,
						currency, userId);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				tarVsAct);
	}

	/**
	 * Controller method to find Top revenue customers based on user's access
	 * privileges.
	 * 
	 * @param userId
	 *            , year, count.
	 * @return Top revenue customers.
	 */
	@RequestMapping(value = "/topRevenue", method = RequestMethod.GET)
	public @ResponseBody String findTopRevenue(
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "count", defaultValue = "5") int count,
			@RequestParam(value = "fields", defaultValue = "all") String includeFields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside CustomerController /customer/topRevenue GET");
		List<CustomerMasterT> topRevenueCustomers = customerService
				.findTopRevenue(userId, financialYear, count);
		return ResponseConstructors.filterJsonForFieldAndViews(includeFields,
				view, topRevenueCustomers);
	}

	@RequestMapping(value = "/group", method = RequestMethod.GET)
	public @ResponseBody String findByGroupCustomerName(
			@RequestParam("nameWith") String nameWith,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside CustomerController /customer/group?nameWith="
				+ nameWith + " GET");
		List<CustomerMasterT> customer = (List<CustomerMasterT>) customerService
				.findByGroupCustomerName(nameWith);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				customer);
	}
	
	@RequestMapping(value ="/privilege/group", method = RequestMethod.GET)
	public @ResponseBody String findByGroupCustomerNameBasedOnPrivilege(
			@RequestParam("userId") String userId,
			@RequestParam("nameWith") String nameWith,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside CustomerController /customer/groupBasedOnPrivilege?nameWith="
				+ nameWith + " GET");
		
		List<String> groupCustomer = customerService
				.findByGroupCustomerNameBasedOnPrivilege(nameWith,userId);
		if(groupCustomer == null || groupCustomer.isEmpty()) {
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Results found for search : " + nameWith);
		} 
		
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
					groupCustomer);
		
	}
	
	/**
	 * This controller uploads the Customers to the database
	 * @param userId
	 * @param file
	 * @param fields
	 * @param view
	 * @return ResponseEntity<InputStreamResource>
	 * @throws Exception
	 */
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<InputStreamResource> uploadCustomers(
			@RequestParam("userId") String userId,
			@RequestParam("file") MultipartFile file,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		System.out.println("inside upload of customer controller");
		List<UploadServiceErrorDetailsDTO> errorDetailsDTOs = null;
		
		UploadStatusDTO status = customerUploadService.upload(file, userId);
		if (status != null) {
			System.out.println(status.isStatusFlag());
			errorDetailsDTOs = status.getListOfErrors();
			for(UploadServiceErrorDetailsDTO err : errorDetailsDTOs){
				System.out.println(err.getRowNumber());
				    System.out.println(err.getMessage());
				}
		}
		
		InputStreamResource excelFile = uploadErrorReport.getErrorSheet(errorDetailsDTOs);
		HttpHeaders respHeaders = new HttpHeaders();
		respHeaders.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		respHeaders.setContentDispositionFormData("attachment","customer_upload_error.xlsx");
		return new ResponseEntity<InputStreamResource>(excelFile, respHeaders,HttpStatus.OK);
	}
}