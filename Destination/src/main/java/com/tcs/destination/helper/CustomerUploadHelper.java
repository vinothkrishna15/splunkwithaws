package com.tcs.destination.helper;

import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
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
import com.tcs.destination.utils.StringUtils;

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

	public UploadServiceErrorDetailsDTO validateCustomerAdd(String[] data,
			String userId, CustomerMasterT customerMasterT) throws Exception {
		String MasterGroupClient = data[2];
		String MasterCustomerName = data[3];
		String MasterIOU = data[4];
		String MasterGoegraphy = data[5];
		String custId = data[7]; // retrieving customer id for updation/deletion 
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		if(StringUtils.isEmpty(custId)){
		CustomerMasterT customer = customerRepository
				.findByCustomerName(MasterCustomerName);
		int rowNumber = Integer.parseInt(data[0]) + 1;
		if (customer == null) {
			// Get List of geographies from DB for validating the geographies
			// which
			// comes from the sheet
			mapOfGeographyMappingT = mapOfGeographyMappingT != null ? mapOfGeographyMappingT
					: commonHelper.getGeographyMappingT();

			// Get List of IOU from DB for validating the IOU which comes from
			// the
			// sheet
			mapOfIouMappingT = mapOfIouMappingT != null ? mapOfIouMappingT
					: commonHelper.getIouMappingT();

			customerMasterT.setCreatedModifiedBy(userId);
			// customerMasterT.setCreatedBy(userId);
			customerMasterT.setDocumentsAttached(Constants.NO);
			if (!StringUtils.isEmpty(MasterGroupClient)) {
				customerMasterT.setGroupCustomerName(MasterGroupClient);

			} else {
				error.setRowNumber(rowNumber);
				error.setMessage("MasterGroupClient Is Mandatory; ");

			}
			if (!StringUtils.isEmpty(MasterCustomerName)) {

				customerMasterT.setCustomerName(MasterCustomerName);

			} else {
				error.setRowNumber(rowNumber);
				error.setMessage("MasterCustomerName Is Mandatory; ");

			}
			if (!StringUtils.isEmpty(MasterIOU)
					&& mapOfIouMappingT.containsKey(MasterIOU)) {

				customerMasterT.setIou(MasterIOU);

			} else {
				error.setRowNumber(rowNumber);
				error.setMessage("MasterIOU Is Mandatory; ");

			}
			if (!StringUtils.isEmpty(MasterGoegraphy)
					&& mapOfGeographyMappingT.containsKey(MasterGoegraphy)) {
				customerMasterT.setGeography(MasterGoegraphy);

			} else {
				error.setRowNumber(rowNumber);
				error.setMessage("MasterGoegraphy Is Mandatory; ");

			}
			
			//check for inactive records and log 
			try {
				customerService.validateInactiveIndicators(customerMasterT);
			} catch(DestinationException e) {
				error.setRowNumber(rowNumber);
				error.setMessage(e.getMessage());
			}
			
		} else {
			error.setRowNumber(rowNumber);
			error.setMessage("CustomerName already exist ");

		}
		}
		return error;
	}

	public UploadServiceErrorDetailsDTO validateCustomerUpdate(String[] data,
			String userId, CustomerMasterT customerMasterT) {

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
		String MasterGroupClient = data[2];
		String MasterCustomerName = data[3];
		String MasterIOU = data[4];
		String MasterGoegraphy = data[5];
		//we are getting the customer id i.e the primary key from customer_Master_T table
		int rowNumber = Integer.parseInt(data[0]) + 1;
		if(!StringUtils.isEmpty(custId)){
		CustomerMasterT customer = customerRepository
				.findOne(custId);
		if (customer == null) {
			error.setRowNumber(rowNumber);
			error.setMessage("customer not found,hence it cannot be updated");

		} else {
			if(customer.isActive()){
				customerMasterT.setCustomerId(custId);
			customerMasterT.setCreatedModifiedBy(userId);
			// customerMasterT.setCreatedBy(userId);
			customerMasterT.setDocumentsAttached(Constants.NO);
			if (!StringUtils.isEmpty(MasterGroupClient)) {
				customerMasterT.setGroupCustomerName(MasterGroupClient);

			}

			if (!StringUtils.isEmpty(MasterIOU)) {
				if (mapOfIouMappingT.containsKey(MasterIOU)) {
					customerMasterT.setIou(MasterIOU);
				}

			} else {
				error.setRowNumber(rowNumber);
				error.setMessage("MasterIOU not found");

			}
			if (!StringUtils.isEmpty(MasterCustomerName)) {
				
					customerMasterT.setCustomerName(MasterCustomerName);

			} else {
				error.setRowNumber(rowNumber);
				error.setMessage("Master Customer Name not found");

			}
			if (!StringUtils.isEmpty(MasterGoegraphy)) {
				if (mapOfGeographyMappingT.containsKey(MasterGoegraphy)) {
					customerMasterT.setGeography(MasterGoegraphy);
				} else {
					error.setRowNumber(rowNumber);
					error.setMessage("MasterGoegraphy not found");

				}

			}
			
			//check for inactive records and log 
			try {
				customerService.validateInactiveIndicators(customerMasterT);
			} catch(DestinationException e) {
				error.setRowNumber(rowNumber);
				error.setMessage(e.getMessage());
			}
			
		} else {
			error.setRowNumber(rowNumber);
			error.setMessage("Customer is inactive and cannot be updated");
		}
		}
		} else {
			error.setRowNumber(rowNumber);
			error.setMessage("CustomerId cannot be empty");
		}
		return error;
	}

	public UploadServiceErrorDetailsDTO validateCustomerDelete(String[] data,
			String userId, CustomerMasterT customerT) {
		String custId = data[7];// retrieving customer id for updation/deletion 
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		if(!StringUtils.isEmpty(custId)){
			
			CustomerMasterT customer = customerRepository.findOne(custId);
			if(customerT!=null){
				try {
					BeanUtils.copyProperties(customerT, customer );
				} catch (Exception e) {
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					error.setMessage("Backend Error while cloning");
				}
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("customer not found");
			}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Customer id cannot be blank for deletion");
		}
		return error;
	}

}
