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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.PageDTO;
import com.tcs.destination.bean.PaginatedResponse;
import com.tcs.destination.bean.SearchResultDTO;
import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.TargetVsActualResponse;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UploadStatusDTO;
import com.tcs.destination.bean.dto.CustomerListDTO;
import com.tcs.destination.bean.dto.GroupCustomerDTO;
import com.tcs.destination.enums.SmartSearchType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.CustomerDownloadService;
import com.tcs.destination.service.CustomerService;
import com.tcs.destination.service.CustomerUploadService;
import com.tcs.destination.service.UploadErrorReport;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.PropertyUtil;
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

	/**
	 * Gets the Customer related details based on customer ID specified
	 * 
	 * @param customerId
	 *            The actual customer ID
	 * @param currency
	 *            The List of currencies to which the opportunities currency
	 *            must be converted to.
	 * @param fields
	 *            The fields that are required to be returned in the JSON
	 * @param view
	 *            The View which has collection of all the fields
	 * @return
	 * @throws Exception
	 *             Throws Destination exception if the resource ID is invalid or
	 *             Currency is invalid
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody String findOne(
			@PathVariable("id") String customerId,
			@RequestParam(value = "currency", defaultValue = "USD") List<String> currency,
			@RequestParam(value = "fields", defaultValue = "") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside CustomerController: Start of retrieving the customer details by id");
		String response = null;
		CustomerMasterT customer;
		try {
			customer = customerService.findById(customerId, currency);
			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, customer);
			logger.info("Inside CustomerController: End of retrieving the customer details by id");
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the customer details for the customer id :"
							+ customerId);
		}
		return response;
	}

	/**
	 * 
	 * @param customerId
	 * @param view
	 * @param fields
	 * @return String - Opportunity count for the respective customer
	 * @throws DestinationException
	 *             The method gives the count of opportunity for given
	 *             customerId
	 */
	@RequestMapping(value = "/opportunitycount/{id}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getCountOfOpportunitiesByCustomerId(
			@PathVariable("id") String customerId,
			@RequestParam(value = "view", defaultValue = "") String view,
			@RequestParam(value = "fields", defaultValue = "") String fields)
			throws DestinationException {
		logger.info("Inside CustomerController: Start of retrieving the count of customer details by id");

		int response = 0;
		try {
			response = customerService.getOpportunityCountByCustomerId(customerId);
			
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "", response), HttpStatus.OK);
		}  catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in creating the opportunity");
		}

	}

	/**
	 * The service used to get list of customer names that contains the
	 * specified characters
	 * 
	 * @param page
	 *            The page number (starting with 0)
	 * @param count
	 *            The no of items in the page
	 * @param nameWith
	 *            the String that should be present in the customer name.
	 * @param startsWith
	 *            The String that should be present at the start of the customer
	 *            name
	 * @param fields
	 *            The fields that are required to be returned in the JSON
	 * @param view
	 *            The View which has collection of all the fields
	 * @return
	 * @throws Exception
	 *             Throws Destination exception if there is no relevant data
	 *             found
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findNameWith(
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "30") int count,
			@RequestParam(value = "nameWith", defaultValue = "") String nameWith,
			@RequestParam(value = "startsWith", defaultValue = "") String startsWith,
			@RequestParam(value = "fields", defaultValue = "") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside CustomerController: Start of retrieving the customer details by name");
		PaginatedResponse customers = null;
		try {
			if (!nameWith.isEmpty()) {
				customers = customerService.findByNameContaining(nameWith,
						page, count);
			} else if (!startsWith.isEmpty()) {
				customers = customerService.findByNameStarting(startsWith,
						page, count);
			} else {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Either nameWith / startsWith is required");
			}
			logger.info("Inside CustomerController: End of retrieving the customer details by name");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, customers);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the customer details");
		}

	}

	/**
	 * This method retrieves the target vs actual details for a customer
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
			throws DestinationException {
		logger.info("Inside CustomerController: Start of retrieving the Target vs Actual details");
		String response = null;
		List<TargetVsActualResponse> tarVsAct;
		try {
			tarVsAct = customerService.findTargetVsActual(financialYear,
					quarter, customerName, currency);
			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, tarVsAct);
			logger.info("Inside CustomerController: End of retrieving the Target vs Actual details");
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the Target vs Actual details");
		}
		return response;
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
			throws DestinationException {
		logger.info("Inside CustomerController: Start of retrieving the Top Revenues");
		String response = null;
		List<CustomerMasterT> topRevenueCustomers;
		try {
			topRevenueCustomers = customerService.findTopRevenue(financialYear,
					count);
			response = ResponseConstructors.filterJsonForFieldAndViews(
					includeFields, view, topRevenueCustomers);
			logger.info("Inside CustomerController: End of retrieving the Top Revenues");
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the top revenues");
		}
		return response;
	}

	/**
	 * This method is used to retrieves the group customer name for the given
	 * name with.
	 * 
	 * @param nameWith
	 * @param fields
	 * @param view
	 * @return customer
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/group", method = RequestMethod.GET)
	public @ResponseBody String findByGroupCustomerName(
			@RequestParam("nameWith") String nameWith,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside CustomerController: Start of retrieving the Group customer names");
		String response = null;
		List<CustomerMasterT> customer;
		try {
			customer = (List<CustomerMasterT>) customerService
					.findByGroupCustomerName(nameWith);
			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, customer);
			logger.info("Inside CustomerController: Start of retrieving the Group customer names");
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the Group customer name for "
							+ nameWith);
		}
		return response;
	}

	/**
	 * This method is used to retrieve the group customer name based on user
	 * access privilege
	 * 
	 * @param nameWith
	 * @param fields
	 * @param view
	 * @return groupCustomer
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/privilege/group", method = RequestMethod.GET)
	public @ResponseBody String findByGroupCustomerNameBasedOnPrivilege(
			@RequestParam("nameWith") String nameWith,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside CustomerController: Start of retrieving the group customer name based on privileges");
		try {
			List<String> groupCustomer = customerService
					.findByGroupCustomerNameBasedOnPrivilege(nameWith);
			if (groupCustomer == null || groupCustomer.isEmpty()) {
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"No Results found for search : " + nameWith);
			}
			logger.info("Inside CustomerController: End of retrieving the group customer name based on privileges");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, groupCustomer);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the group customer name based on privileges for "
							+ nameWith);
		}

	}

	/**
	 * This method is used to download the customer details in excel format
	 * 
	 * @param oppFlag
	 * @param fields
	 * @param view
	 * @return customerDownloadExcel
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> downloadCustomerMaster(
			@RequestParam("downloadCustomers") boolean oppFlag,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside CustomerController: Start of Customer Details download");
		HttpHeaders respHeaders = null;
		InputStreamResource customerDownloadExcel = null;
		try {
			customerDownloadExcel = customerDownloadService
					.getCustomers(oppFlag);
			respHeaders = new HttpHeaders();
			String environmentName = PropertyUtil
					.getProperty("environment.name");
			String todaysDate_formatted = DateUtils
					.getCurrentDateInDesiredFormat();
			String repName = environmentName + "_CustomerMasterDownload_"
					+ todaysDate_formatted + ".xlsm";
			respHeaders.add("reportName", repName);
			respHeaders.setContentDispositionFormData("attachment", repName);
			respHeaders.setContentType(MediaType
					.parseMediaType("application/octet-stream"));
			logger.info("Inside CustomerController: Customer Master Report Downloaded Successfully ");
			return new ResponseEntity<InputStreamResource>(
					customerDownloadExcel, respHeaders, HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in downloading the customer details in excel");
		}
	}

	/**
	 * This method is used to download the customer contact details in excel
	 * format
	 * 
	 * @param oppFlag
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/contactDownload", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> downloadCustomerContacts(
			@RequestParam("downloadCustomerContacts") boolean oppFlag,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside CustomerController: Start of Customer Contact details download");
		HttpHeaders respHeaders = null;
		InputStreamResource customerDownloadExcel = null;
		try {
			customerDownloadExcel = customerDownloadService
					.getCustomerContacts(oppFlag);
			respHeaders = new HttpHeaders();
			String todaysDate_formatted = DateUtils
					.getCurrentDateInDesiredFormat();
			String environmentName = PropertyUtil
					.getProperty("environment.name");
			String repName = environmentName + "_CustomerContactDownload_"
					+ todaysDate_formatted + ".xlsm";
			respHeaders.add("reportName", repName);
			respHeaders.setContentDispositionFormData("attachment", repName);

			respHeaders.setContentType(MediaType
					.parseMediaType("application/octet-stream"));
			logger.info("Inside CustomerController: Customer Contact Report Downloaded Successfully ");
			return new ResponseEntity<InputStreamResource>(
					customerDownloadExcel, respHeaders, HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in downloading customer contacts");
		}

	}

	/**
	 * This controller uploads the Customer details to the database
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
			throws DestinationException {
		logger.info("Inside CustomerController: Start of customer details upload");
		List<UploadServiceErrorDetailsDTO> errorDetailsDTOs = null;
		try {
			UploadStatusDTO status = customerUploadService.upload(file);
			if (status != null) {
				errorDetailsDTOs = status.getListOfErrors();
				for (UploadServiceErrorDetailsDTO err : errorDetailsDTOs) {
					logger.debug(err.getRowNumber().toString());
					logger.debug(err.getMessage());
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
			logger.info("Inside CustomerController: End of customer details upload");
			return new ResponseEntity<InputStreamResource>(excelFile,
					respHeaders, HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in uploading the customer details");
		}

	}

	/**
	 * This method gives the functionality for customer advanced search
	 * 
	 * @param groupCustomerNameWith
	 * @param nameWith
	 * @param geography
	 * @param displayIOU
	 * @param page
	 * @param count
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public @ResponseBody String advancedSearch(
			@RequestParam(value = "groupCustomerNameWith", defaultValue = "") String groupCustomerNameWith,
			@RequestParam(value = "nameWith", defaultValue = "") String nameWith,
			@RequestParam(value = "geography", defaultValue = "") List<String> geography,
			@RequestParam(value = "displayIOU", defaultValue = "") List<String> displayIOU,
			@RequestParam(value = "inactive", defaultValue = "false") boolean inactive,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "30") int count,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside CustomerController: Start of customer advanced search");
		String response = null;
		PaginatedResponse paginatedResponse;
		try {
			paginatedResponse = customerService.search(groupCustomerNameWith,
					nameWith, geography, displayIOU, inactive, page, count);
			response = ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, paginatedResponse);
			logger.info("Inside CustomerController: End of customer advanced search");
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the customer details");
		}
		return response;
	}

	/**
	 * This handles the request for handling the edit operation on Customer
	 * details
	 * 
	 * @param customerMaster
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> editCustomer(
			@RequestBody CustomerMasterT customerMaster)
			throws DestinationException {
		logger.info("Inside CustomerController: Start of Edit Customer");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (customerService.updateCustomer(customerMaster)) {
				status.setStatus(Status.SUCCESS,
						"Customer was edited successfully!!!");
			}
			logger.info("Inside CustomerController: End of Edit Customer");
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
	 * This method is used to get user details related to customer
	 * 
	 * @param customerName
	 * @param page
	 * @param count
	 * @param fields
	 * @param view
	 * @return user details with pagination
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public @ResponseBody String bdUserSearchForCustomer(
			@RequestParam(value = "customerId", defaultValue = "") String customerId,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "30") int count,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside CustomerController: Start of bdUserSearchForCustomer");
		String response = null;
		PaginatedResponse paginatedResponse;
		try {
			paginatedResponse = customerService.searchUserDetailsForCustomer(customerId, page,count);
			logger.info("Ending CustomerController bdUserSearchForCustomer method");
			response= ResponseConstructors.filterJsonForFieldAndViews(fields, view, paginatedResponse);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the user details for customer");
		}
		return response;
	}
	
	/**
	 * Service to fetch the customer related information based on search type and the search keyword 
	 * @param searchType - category type
	 * @param term - keyword
	 * @param getAll - true, to retrieve entire result, false to filter the result to only 3 records.(<b>default:false</b>)
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
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "count", defaultValue = "30") int count,
			@RequestParam(value = "view", defaultValue = "") String view)
					throws DestinationException {
		logger.info("CustomerController: smart search by search term");
		try {
			PageDTO<SearchResultDTO<CustomerMasterT>> res = customerService.smartSearch(SmartSearchType.get(searchType), term, getAll, page, count);
			logger.info("CustomerController: End - smart search by search term");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, res, !getAll);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Error on user smartSearch", e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while retrieving customer list");
		}
		
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public @ResponseBody PageDTO<GroupCustomerDTO> findListOfGrpCustomers(
			@RequestBody CustomerListDTO customerListDTO)
			throws DestinationException {
		logger.info("Inside Customer Controller: Start of findListOfGrpCustomers");
		PageDTO<GroupCustomerDTO> grpCustomers;
		try {
			grpCustomers = customerService.getGrpCustomersByType(
					customerListDTO);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the ListOfGrpCustomers");
		}
		logger.info("Inside Customer Controller: end of findListOfGrpCustomers");
		return grpCustomers;
	}
	
	
	
}