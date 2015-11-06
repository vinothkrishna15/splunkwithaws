package com.tcs.destination.helper;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.bean.IouCustomerMappingT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.CustomerIOUMappingRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.GeographyRepository;
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

	Map<String, GeographyMappingT> mapOfGeographyMappingT = null;
	Map<String, IouCustomerMappingT> mapOfIouMappingT = null;

	public UploadServiceErrorDetailsDTO validateCustomerAdd(String[] data,
			String userId, CustomerMasterT customerMasterT) throws Exception {
		String MasterGroupClient = data[2];
		String MasterCustomerName = data[3];
		String MasterIOU = data[4];
		String MasterGoegraphy = data[5];
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		CustomerMasterT customer = customerRepository
				.findByCustomerName(MasterCustomerName);
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
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("MasterGroupClient Is Mandatory; ");

			}
			if (!StringUtils.isEmpty(MasterCustomerName)) {

				customerMasterT.setCustomerName(MasterCustomerName);

			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("MasterCustomerName Is Mandatory; ");

			}
			if (!StringUtils.isEmpty(MasterIOU)
					&& mapOfIouMappingT.containsKey(MasterIOU)) {

				customerMasterT.setIou(MasterIOU);

			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("MasterIOU Is Mandatory; ");

			}
			if (!StringUtils.isEmpty(MasterGoegraphy)
					&& mapOfGeographyMappingT.containsKey(MasterGoegraphy)) {
				customerMasterT.setGeography(MasterGoegraphy);

			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("MasterGoegraphy Is Mandatory; ");

			}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("CustomerName already exist ");

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

		String MasterGroupClient = data[2];
		String MasterCustomerName = data[3];
		String MasterIOU = data[4];
		String MasterGoegraphy = data[5];
		//we are getting the customer id i.e the primary key from customer_Master_T table
		CustomerMasterT customerId = customerRepository
				.findByCustomerName(MasterCustomerName);
		if (customerId == null) {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("customer not found,hence it cannot be updated");

		} else {
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
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("MasterIOU not found");

			}
			if (!StringUtils.isEmpty(MasterGoegraphy)) {
				if (mapOfGeographyMappingT.containsKey(MasterGoegraphy)) {
					customerMasterT.setGeography(MasterGoegraphy);
				} else {
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					error.setMessage("MasterGoegraphy not found");

				}

			}
		}

		return error;
	}

	public UploadServiceErrorDetailsDTO validateCustomerDelete(String[] data,
			String userId, CustomerMasterT customerT) {
		String MasterGroupClient = data[2];
		String MasterCustomerName = data[3];
		String MasterIOU = data[4];
		String MasterGoegraphy = data[5];
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		customerT = new CustomerMasterT();
		if (!StringUtils.isEmpty(MasterIOU)) {
			if (!mapOfIouMappingT.containsKey(MasterIOU)) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("MasterIOU not found");
			}

		}
		if (!StringUtils.isEmpty(MasterGoegraphy)) {
			if (!mapOfGeographyMappingT.containsKey(MasterGoegraphy)) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("MasterGoegraphy not found");
			}
		}
		if (!StringUtils.isEmpty(MasterGroupClient)
				&& !StringUtils.isEmpty(MasterCustomerName)) {
			String customerId = customerRepository
					.findCustomerIdForDeleteOrUpdate(MasterGroupClient,
							MasterCustomerName, MasterIOU, MasterGoegraphy);

			if (!StringUtils.isEmpty(customerId)) {
				customerT.setCustomerId(customerId);
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("customer not found");

			}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("either Master Customer Name or Master Group Client is empty");
		}
		return error;

	}

}
