package com.tcs.destination.helper;

import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.BeaconCustomerMappingT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.bean.IouCustomerMappingT;
import com.tcs.destination.bean.RevenueCustomerMappingT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.BeaconRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.RevenueCustomerMappingTRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.BeaconCustomerUploadService;
import com.tcs.destination.service.RevenueUploadService;
import com.tcs.destination.utils.StringUtils;

/**
 * Class to validate the finance details before inserting / deleting / updating into DB
 * @author tcs2
 * 2016
 */
@Component("financeCustomerMappingUploadHelper")
public class FinanceCustomerMappingUploadHelper {

	@Autowired
	BeaconRepository beaconRepository;

	@Autowired
	CommonHelper commonHelper;

	@Autowired
	RevenueCustomerMappingTRepository revenueRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	RevenueUploadService revenueUploadService;

	Map<String, GeographyMappingT> mapOfGeographyMappingT = null;
	Map<String, IouCustomerMappingT> mapOfIouMappingT = null;


	public UploadServiceErrorDetailsDTO validateFinanceCustomerAdd(
			String[] data, String userId, RevenueCustomerMappingT financeCustomer) {
		String masterCustomerName = data[3];
		String financeCustomerName = data[6];
		String financeIou = data[7];
		String financeGeography = data[8];
		String active = data[9];
		String revenuecustomerMapId = data[10];
		StringBuffer errorMsg = new StringBuffer();

		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();

		if(StringUtils.isEmpty(revenuecustomerMapId)){
			// to find the uniqueness of the primary key (here composite key)
			List<RevenueCustomerMappingT> financeCustomers = revenueRepository.findByFinanceCustomerNameAndCustomerGeographyAndFinanceIou(financeCustomerName,financeGeography,financeIou);

			int rowNumber = Integer.parseInt(data[0]) + 1;

			if (financeCustomers.isEmpty()) {
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

				CustomerMasterT customerObj = customerRepository.findByCustomerName(masterCustomerName);
				
				if(customerObj!=null)
                {
					
				if(customerObj.isActive())
				{

				financeCustomer.setCustomerId(customerObj.getCustomerId());
				financeCustomer.setActive( Boolean.parseBoolean(active));

				if (!StringUtils.isEmpty(financeCustomerName)) {
					financeCustomer.setFinanceCustomerName(financeCustomerName);
				} else {
					error.setRowNumber(rowNumber);
					errorMsg.append("financeCustomerName Is Mandatory; ");
				}


				if (!StringUtils.isEmpty(financeIou)
						&& mapOfIouMappingT.containsKey(financeIou)) {
					financeCustomer.setFinanceIou(financeIou);
				} else {
					error.setRowNumber(rowNumber);
					errorMsg.append("financeIou Is Mandatory; ");

				}
				if (!StringUtils.isEmpty(financeGeography)
						&& mapOfGeographyMappingT.containsKey(financeGeography)) {
					financeCustomer.setCustomerGeography(financeGeography);

				} else {
					error.setRowNumber(rowNumber);
					errorMsg.append("financeGeography Is Mandatory; ");
				}
				}
				else
				{
					error.setRowNumber(rowNumber);
					errorMsg.append("The Customer is not active to be added;");
				}
                }
                else
                {
                	error.setRowNumber(rowNumber);
                	errorMsg.append("The Customer Is Not Valid;");
                }
				} else {
				error.setRowNumber(rowNumber);
				errorMsg.append("Finance Customer details already exists");
			}
		}
		if (!StringUtils.isEmpty(errorMsg.toString())) {
			error.setMessage(errorMsg.toString());
		}
		return error;
	}

	public UploadServiceErrorDetailsDTO validateFinanceCustomerDelete(
			String[] data, String userId, RevenueCustomerMappingT finance) {

		String revenueCustomerMapId = validateAndRectifyValue(data[10]);
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		StringBuffer errorMsg = new StringBuffer();
		if(!StringUtils.isEmpty(revenueCustomerMapId)){
			RevenueCustomerMappingT financeCustomers = revenueRepository.findOne(Long.parseLong(revenueCustomerMapId));
			if (financeCustomers == null) {
				
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				errorMsg.append("Finance Customer details not found for the given map id for deletion");
			}
			else
			{
				finance.setActive(false);
			}
		}else{ 
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append("Finance Customer map id cannot be empty for deletion");
		}
		if (!StringUtils.isEmpty(errorMsg.toString())) {
			error.setMessage(errorMsg.toString());
		}
		return error;
	}

	public String validateAndRectifyValue(String value) {
		String val = value;
		System.out.println(value.substring(value.length() - 2, value.length()));
		if (value != null) {
			if (value.substring(value.length() - 2, value.length()).equals(".0")) {
				val = value.substring(0, value.length() - 2);
			}
		}
		return val;
	}

	public UploadServiceErrorDetailsDTO validateFinanceCustomerUpdate(
			String[] data, String userId, RevenueCustomerMappingT finance) {
		// Get List of geographies from DB for validating the geographies which
		// comes from the sheet
		mapOfGeographyMappingT = mapOfGeographyMappingT != null ? mapOfGeographyMappingT
				: commonHelper.getGeographyMappingT();

		// Get List of IOU from DB for validating the IOU which comes from the
		// sheet
		mapOfIouMappingT = mapOfIouMappingT != null ? mapOfIouMappingT
				: commonHelper.getIouMappingT();

		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		StringBuffer errorMsg = new StringBuffer();

		String masterCustomerName = data[3];
		String financeCustomerName = data[6];
		String financeIou = data[7];
		String financeGeography = data[8];
		String active = data[9];
		String revenueCustomerMapId = validateAndRectifyValue(data[10]);

		int rowNumber = Integer.parseInt(data[0]) + 1;		
		CustomerMasterT customer = customerRepository.findByCustomerName(masterCustomerName);
		// to find the uniqueness of the primary key (here composite key)
		List<RevenueCustomerMappingT> financeCustomers = revenueRepository.findByFinanceCustomerNameAndCustomerGeographyAndFinanceIou(financeCustomerName,financeGeography,financeIou);

		if(!StringUtils.isEmpty(revenueCustomerMapId)){
			RevenueCustomerMappingT financeCustomer = revenueRepository
					.findOne(Long.parseLong(revenueCustomerMapId));
			if (financeCustomer == null) {
				error.setRowNumber(rowNumber);
				errorMsg.append("Finance customer details not found for the given map id ,hence it cannot be updated");

			} else {
				if(financeCustomer.isActive()){
					if (financeCustomers.isEmpty()) {
						finance.setCustomerId(customer.getCustomerId());
						finance.setRevenueCustomerMapId(Long.parseLong(revenueCustomerMapId));
						if (!StringUtils.isEmpty(financeCustomerName)) {
							finance.setFinanceCustomerName(financeCustomerName);
						} else {
							error.setRowNumber(rowNumber);
							errorMsg.append("financeCustomerName Is Mandatory; ");
						}


						if (!StringUtils.isEmpty(financeIou)
								&& mapOfIouMappingT.containsKey(financeIou)) {
							finance.setFinanceIou(financeIou);;
						} else {
							error.setRowNumber(rowNumber);
							errorMsg.append("financeIou Is Mandatory; ");
						}

						if (!StringUtils.isEmpty(financeGeography)
								&& mapOfGeographyMappingT.containsKey(financeGeography)) {
							finance.setCustomerGeography(financeGeography);
						} else {
							error.setRowNumber(rowNumber);
							errorMsg.append("financeGeography Is Mandatory; ");
						}

						if (!StringUtils.isEmpty(active)) {
							finance.setActive(Boolean.parseBoolean(active));
						} 
						//check for inactive records and log 
						try {
							revenueUploadService.validateInactiveIndicators(finance);
						} catch(DestinationException e) {
							error.setRowNumber(rowNumber);
							errorMsg.append(e.getMessage());
						}

					} else {
						error.setRowNumber(rowNumber);
						errorMsg.append("Finance Customer details already exists");
					}
				}
				else {
					error.setRowNumber(rowNumber);
					errorMsg.append(" Finance / Revenue Customer is inactive and cannot be updated");
				}
			}
		} else {
			error.setRowNumber(rowNumber);
			errorMsg.append("Revenue Customer Map Id cannot be empty for Update");
		}
		if (!StringUtils.isEmpty(errorMsg.toString())) {
			error.setMessage(errorMsg.toString());
		}
		return error;
	}
}
