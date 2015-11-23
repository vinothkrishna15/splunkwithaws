package com.tcs.destination.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import com.tcs.destination.bean.PaginatedResponse;
import com.tcs.destination.bean.TargetVsActualResponse;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UploadStatusDTO;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.CustomerDownloadService;
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
	CustomerDownloadService customerDownloadService;

	@Autowired
	UploadErrorReport uploadErrorReport;
	
	private static final DateFormat actualFormat = new SimpleDateFormat("dd-MMM-yyyy");
	private static final DateFormat desiredFormat = new SimpleDateFormat("MM/dd/yyyy");

	/**
	 * Gets the Customer related details based on customer ID specified
	 * @param customerId The actual customer ID
	 * @param currency The List of currencies to which the opportunities currency must be converted to.
	 * @param fields The fields that are required to be returned in the JSON
	 * @param view The View which has collection of all the fields
	 * @return
	 * @throws Exception Throws Destination exception if the resource ID is invalid or Currency is invalid
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody String findOne(
			@PathVariable("id") String customerId,
			@RequestParam(value = "currency", defaultValue = "USD") List<String> currency,
			@RequestParam(value = "fields", defaultValue = "") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside CustomerController /customer/id=" + customerId
				+ " GET");
		CustomerMasterT customer = customerService.findById(customerId,
				currency);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				customer);
	}

	/**
	 * The service used to get list of customer names that contains the specified characters
	 * @param page The page number (starting with 0)
	 * @param count The no of items in the page
	 * @param nameWith the String that should be present in the customer name.
	 * @param startsWith The String that should be present at the start of the customer name
	 * @param fields The fields that are required to be returned in the JSON
	 * @param view The View which has collection of all the fields
	 * @return
	 * @throws Exception Throws Destination exception if there is no relevant data found
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findNameWith(
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "30") int count,
			@RequestParam(value = "nameWith", defaultValue = "") String nameWith,
			@RequestParam(value = "startsWith", defaultValue = "") String startsWith,
			@RequestParam(value = "fields", defaultValue = "") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside CustomerController /customer?namewith=" + nameWith
				+ "and starts with " + startsWith + " GET");
		PaginatedResponse customers = null;

		if (!nameWith.isEmpty()) {
			customers = customerService.findByNameContaining(nameWith, page,
					count);
		} else if (!startsWith.isEmpty()) {
			customers = customerService.findByNameStarting(startsWith, page,
					count);
		} else {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Either nameWith / startsWith is required");
		}
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				customers);

	}

	/**
	 * 
	 * @param financialYear
	 * @param quarter
	 * @param customerName
	 * @param currency
	 * @param fields
	 * @param view
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/targetVsActual", method = RequestMethod.GET)
	public @ResponseBody String findTargetVsActual(
			@RequestParam(value = "year", defaultValue = "", required = false) String financialYear,
			@RequestParam(value = "quarter", defaultValue = "", required = false) String quarter,
			@RequestParam(value = "customer", defaultValue = "", required = false) String customerName,
			@RequestParam(value = "currency", defaultValue = "INR", required = false) String currency,
			@RequestParam(value = "fields", defaultValue = "all", required = false) String fields,
			@RequestParam(value = "view", defaultValue = "", required = false) String view)
			throws Exception {
		logger.debug("Inside CustomerController /customer/targetVsActual GET");
		List<TargetVsActualResponse> tarVsAct = customerService
				.findTargetVsActual(financialYear, quarter, customerName,
						currency);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				tarVsAct);
	}

	/**
	 * Controller method to find Top revenue customers based on user's access
	 * privileges.
	 * 
	 * @param year
	 *            , count.
	 * @return Top revenue customers.
	 */
	@RequestMapping(value = "/topRevenue", method = RequestMethod.GET)
	public @ResponseBody String findTopRevenue(
			@RequestParam(value = "year", defaultValue = "") String financialYear,
			@RequestParam(value = "count", defaultValue = "5") int count,
			@RequestParam(value = "fields", defaultValue = "all") String includeFields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside CustomerController /customer/topRevenue GET");
		List<CustomerMasterT> topRevenueCustomers = customerService
				.findTopRevenue(financialYear, count);
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

	@RequestMapping(value = "/privilege/group", method = RequestMethod.GET)
	public @ResponseBody String findByGroupCustomerNameBasedOnPrivilege(
			@RequestParam("nameWith") String nameWith,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside CustomerController /customer/groupBasedOnPrivilege?nameWith="
				+ nameWith + " GET");

		List<String> groupCustomer = customerService
				.findByGroupCustomerNameBasedOnPrivilege(nameWith);
		if (groupCustomer == null || groupCustomer.isEmpty()) {
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Results found for search : " + nameWith);
		}

		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				groupCustomer);

	}

	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> downloadCustomerMaster(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		HttpHeaders respHeaders = null;
		InputStreamResource customerDownloadExcel = null;
		try {
			customerDownloadExcel = customerDownloadService.getCustomers();
			respHeaders = new HttpHeaders();
			String todaysDate = DateUtils.getCurrentDate();
			String todaysDate_formatted=desiredFormat.format(actualFormat.parse(todaysDate));
			respHeaders.setContentDispositionFormData("attachment",
					"CustomerMasterDownload_" + todaysDate_formatted
							+ ".xlsm");
			respHeaders.setContentType(MediaType
					.parseMediaType("application/octet-stream"));
			logger.info("Customer Master Report Downloaded Successfully ");
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		return new ResponseEntity<InputStreamResource>(customerDownloadExcel,
				respHeaders, HttpStatus.OK);

	}

	/**
	 * This controller uploads the Customers to the database
	 * 
	 * @param file
	 * @param fields
	 * @param view
	 * @return ResponseEntity<InputStreamResource>
	 * @throws Exception
	 */
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<InputStreamResource> uploadCustomers(
			@RequestParam("file") MultipartFile file,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		System.out.println("inside upload of customer controller");
		List<UploadServiceErrorDetailsDTO> errorDetailsDTOs = null;

		UploadStatusDTO status = customerUploadService.upload(file);
		if (status != null) {
			System.out.println(status.isStatusFlag());
			errorDetailsDTOs = status.getListOfErrors();
			for (UploadServiceErrorDetailsDTO err : errorDetailsDTOs) {
				System.out.println(err.getRowNumber());
				System.out.println(err.getMessage());
			}
		}

		InputStreamResource excelFile = uploadErrorReport
				.getErrorSheet(errorDetailsDTOs);
		HttpHeaders respHeaders = new HttpHeaders();
		respHeaders
				.setContentType(MediaType
						.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		respHeaders.setContentDispositionFormData("attachment",
				"customer_upload_error.xlsx");
		return new ResponseEntity<InputStreamResource>(excelFile, respHeaders,
				HttpStatus.OK);
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public @ResponseBody String advancedSearch(
			@RequestParam(value = "groupCustomerNameWith", defaultValue = "") String groupCustomerNameWith,
			@RequestParam(value = "nameWith", defaultValue = "") String nameWith,
			@RequestParam(value = "geography", defaultValue = "") List<String> geography,
			@RequestParam(value = "displayIOU", defaultValue = "") List<String> displayIOU,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "30") int count,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside PartnerController /customer/search?name="
				+ nameWith + "&geograph=" + geography + " GET");
		PaginatedResponse paginatedResponse = customerService.search(
				groupCustomerNameWith, nameWith, geography, displayIOU, page,
				count);

		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				paginatedResponse);
	}
}