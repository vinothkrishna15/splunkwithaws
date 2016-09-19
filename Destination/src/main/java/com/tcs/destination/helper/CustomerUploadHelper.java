package com.tcs.destination.helper;

import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.bean.IouCustomerMappingT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.CustomerIOUMappingRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.CustomerService;
import com.tcs.destination.utils.Constants;

import org.apache.commons.lang.StringUtils;

@Component("customerUploadHelper")
public class CustomerUploadHelper {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	GeographyRepository geographyRepository;

	@Autowired
	CommonHelper commonHelper;

	@Autowired
	CustomerIOUMappingRepository customerIouMappingTRepository;

	@Autowired
	CustomerService customerService;

	Map<String, GeographyMappingT> mapOfGeographyMappingT = null;
	Map<String, IouCustomerMappingT> mapOfIouMappingT = null;
	
	private static final Logger logger = LoggerFactory
			.getLogger(CustomerUploadHelper.class);
	
    /**
     * This method is used to validate the customer details for add operation
     * @param data
     * @param userId
     * @param customerMasterT
     * @return
     * @throws Exception
     */
	public UploadServiceErrorDetailsDTO validateCustomerAdd(String[] data,
			String userId, CustomerMasterT customerMasterT) throws Exception {
		
		logger.debug("Begin:inside validateCustomerAdd() of CustomerUploadHelper");

		String custId = data[7]; // retrieving customer id for updation/deletion
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		StringBuffer errorMsg = new StringBuffer();
		int rowNumber = Integer.parseInt(data[0]) + 1;

		if (StringUtils.isEmpty(custId)) {

			String masterCustomerName = data[3];
			CustomerMasterT customer = customerRepository
					.findByCustomerName(masterCustomerName);

			if (customer == null) {
				// Get List of geographies from DB for validating the
				// geographies
				// which
				// comes from the sheet
				mapOfGeographyMappingT = mapOfGeographyMappingT != null ? mapOfGeographyMappingT
						: commonHelper.getGeographyMappingT();

				// Get List of IOU from DB for validating the IOU which comes
				// from
				// the
				// sheet
				mapOfIouMappingT = mapOfIouMappingT != null ? mapOfIouMappingT
						: commonHelper.getIouMappingT();

				// CREATED_MODIFIED_BY
				customerMasterT.setCreatedModifiedBy(userId);

				// DOCUMENTS ATTACHED
				customerMasterT.setDocumentsAttached(Constants.NO);

				// GROUP CUSTOMER NAME
				String masterGroupClient = data[2];
				if (!StringUtils.isEmpty(masterGroupClient)) {
					customerMasterT.setGroupCustomerName(masterGroupClient);

				} else {
					error.setRowNumber(rowNumber);
					errorMsg.append("MasterGroupClient Is Mandatory;");
				}

				// CUSTOMER NAME

				if (!StringUtils.isEmpty(masterCustomerName)) {

					customerMasterT.setCustomerName(masterCustomerName);

				} else {
					error.setRowNumber(rowNumber);
					errorMsg.append("MasterCustomerName Is Mandatory;");
				}

				// IOU
				String masterIOU = data[4];
				if ((!StringUtils.isEmpty(masterIOU))
						&& (mapOfIouMappingT.containsKey(masterIOU))) {

					customerMasterT.setIou(masterIOU);

				} else {
					error.setRowNumber(rowNumber);
					errorMsg.append("MasterIOU Is Mandatory;");
				}

				// GEOGRAPHY
				String masterGeography = data[5];
				if ((!StringUtils.isEmpty(masterGeography))
						&& (mapOfGeographyMappingT.containsKey(masterGeography))) {
					customerMasterT.setGeography(masterGeography);

				} else {
					error.setRowNumber(rowNumber);
					errorMsg.append("MasterGeography Is Mandatory;");
				}

				// ACTIVE
				String active = data[6];
				if (StringUtils.isNotEmpty(active)
						&& active.equalsIgnoreCase("false")) {
					customerMasterT.setActive(false);

				}

				// check for inactive records and log
				try {
					customerService.validateInactiveIndicators(customerMasterT);
				} catch (DestinationException e) {
					error.setRowNumber(rowNumber);
					errorMsg.append(e.getMessage());
				}

			} else {
				error.setRowNumber(rowNumber);
				errorMsg.append("CustomerName already exists!;");
			}
		} else {
			error.setRowNumber(rowNumber);
			errorMsg.append("Customer Id should not be provided for a customer to be added!;");
		}
		if (!StringUtils.isEmpty(errorMsg.toString())) {
			error.setMessage(errorMsg.toString());
		}
		
		logger.debug("End:inside validateCustomerAdd() of CustomerUploadHelper");
		return error;
	}

	/**
	 * This method is used to validate customer update operation
	 * @param data
	 * @param userId
	 * @param customerMasterT
	 * @return
	 */
	public UploadServiceErrorDetailsDTO validateCustomerUpdate(String[] data,
			String userId, CustomerMasterT customerMasterT) {
		
		logger.debug("Begin:inside validateCustomerUpdate() of CustomerUploadHelper");

		// Get List of geographies from DB for validating the geographies which
		// comes from the sheet
		mapOfGeographyMappingT = mapOfGeographyMappingT != null ? mapOfGeographyMappingT
				: commonHelper.getGeographyMappingT();

		// Get List of IOU from DB for validating the IOU which comes from the
		// sheet
		mapOfIouMappingT = mapOfIouMappingT != null ? mapOfIouMappingT
				: commonHelper.getIouMappingT();

		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();

		String custId = data[7];// retrieving customer id for updation/deletion
		String masterGroupClient = data[2];
		String masterCustomerName = data[3];
		String masterIOU = data[4];
		String masterGeography = data[5];
		// we are getting the customer id i.e the primary key from
		// customer_Master_T table
		int rowNumber = Integer.parseInt(data[0]) + 1;
		StringBuffer errorMsg = new StringBuffer();

		if (!StringUtils.isEmpty(custId)) {
			CustomerMasterT customer = customerRepository.findOne(custId);
			if (customer == null) {
				error.setRowNumber(rowNumber);
				errorMsg.append("Customer not found,hence it cannot be updated!!;");
			} else {
				if (customer.isActive()) {
					customerMasterT.setCustomerId(custId);
					customerMasterT.setCreatedModifiedBy(userId);
					// customerMasterT.setCreatedBy(userId);
					customerMasterT.setDocumentsAttached(Constants.NO);
					if (!StringUtils.isEmpty(masterGroupClient)) {
						customerMasterT.setGroupCustomerName(masterGroupClient);

					} else {
						error.setRowNumber(rowNumber);
						errorMsg.append("Master Group Client Should Not Be Empty;");
					}

					if (!StringUtils.isEmpty(masterIOU)) {
						if (mapOfIouMappingT.containsKey(masterIOU)) {
							customerMasterT.setIou(masterIOU);
						} else {
							error.setRowNumber(rowNumber);
							errorMsg.append("Master IOU Is Not Valid;");

						}

					} else {
						error.setRowNumber(rowNumber);
						errorMsg.append("MasterIOU Should Not Be Empty;");
					}

					if (!StringUtils.isEmpty(masterCustomerName)) {

						customerMasterT.setCustomerName(masterCustomerName);

					} else {
						error.setRowNumber(rowNumber);
						errorMsg.append("Master Customer Name Not Found;");
					}
					if (!StringUtils.isEmpty(masterGeography)) {
						if (mapOfGeographyMappingT.containsKey(masterGeography)) {
							customerMasterT.setGeography(masterGeography);
						} else {
							error.setRowNumber(rowNumber);
							errorMsg.append("Master Geography Is Not Valid;");

						}
					} else {
						error.setRowNumber(rowNumber);
						errorMsg.append("Master Geography Should Not Be Empty;");
					}

					// ACTIVE
					String active = data[6];
					if (StringUtils.isNotEmpty(active)
							&& active.equalsIgnoreCase("false")) {
						customerMasterT.setActive(false);

					}

					// check for inactive records and log
					try {
						customerService
								.validateInactiveIndicators(customerMasterT);
					} catch (DestinationException e) {
						error.setRowNumber(rowNumber);
						errorMsg.append(e.getMessage());
					}

				} else {
					error.setRowNumber(rowNumber);
					errorMsg.append("Customer is inactive and cannot be updated");

				}
			}
		} else {
			error.setRowNumber(rowNumber);
			errorMsg.append("CustomerId cannot be empty");
		}

		if (!StringUtils.isEmpty(errorMsg.toString())) {
			error.setMessage(errorMsg.toString());
		}
		
		logger.debug("End:inside validateCustomerUpdate() of CustomerUploadHelper");
        return error;
	}

	/**
	 * This method is used to validate customer data for delete operation
	 * @param data
	 * @param userId
	 * @param customerT
	 * @return
	 */
	public UploadServiceErrorDetailsDTO validateCustomerDelete(String[] data,
			String userId, CustomerMasterT customerT) {
		
		logger.debug("Begin:Inside validateCustomerDelete() of CustomerUploadHelper");
		
		String custId = data[7];// retrieving customer id for updation/deletion
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		StringBuffer errorMsg = new StringBuffer();

		if (!StringUtils.isEmpty(custId)) {

			CustomerMasterT customer = customerRepository.findOne(custId);
			if (customerT != null) {
				try {
					BeanUtils.copyProperties(customerT, customer);
				} catch (Exception e) {
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorMsg.append("Backend Error while cloning");
				}
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				errorMsg.append("Customer not found!!");
			}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append("Customer id cannot be blank for deletion");
		}

		if (!StringUtils.isEmpty(errorMsg.toString())) {
			error.setMessage(errorMsg.toString());
		}
		logger.debug("End:Inside validateCustomerDelete() of CustomerUploadHelper");
		return error;
	}

}
