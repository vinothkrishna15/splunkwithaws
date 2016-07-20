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

				financeCustomer.setCustomerId(customerObj.getCustomerId());
				financeCustomer.setActive( Boolean.parseBoolean(active));

				if (!StringUtils.isEmpty(financeCustomerName)) {
					financeCustomer.setFinanceCustomerName(financeCustomerName);
				} else {
					error.setRowNumber(rowNumber);
					error.setMessage("financeCustomerName Is Mandatory; ");
				}


				if (!StringUtils.isEmpty(financeIou)
						&& mapOfIouMappingT.containsKey(financeIou)) {
					financeCustomer.setFinanceIou(financeIou);
				} else {
					error.setRowNumber(rowNumber);
					error.setMessage("financeIou Is Mandatory; ");

				}
				if (!StringUtils.isEmpty(financeGeography)
						&& mapOfGeographyMappingT.containsKey(financeGeography)) {
					financeCustomer.setCustomerGeography(financeGeography);

				} else {
					error.setRowNumber(rowNumber);
					error.setMessage("financeGeography Is Mandatory; ");
				}
			} else {
				error.setRowNumber(rowNumber);
				error.setMessage("Finance Customer Name already exists");
			}
		}
		return error;
	}

	public UploadServiceErrorDetailsDTO validateFinanceCustomerDelete(
		String[] data, String userId, RevenueCustomerMappingT finance) {

		String revenueCustomerMapId = validateAndRectifyValue(data[10]);
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();

		RevenueCustomerMappingT financeCustomers = revenueRepository.findOne(Long.parseLong(revenueCustomerMapId));
		if (financeCustomers != null) {
			if(finance!=null){
				try {
					BeanUtils.copyProperties(finance, financeCustomers);
				} catch (Exception e) {
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					error.setMessage("Backend Error while cloning");
				}
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("Finance Customer Mapping not found");
			}
		}
		return error;
	}

	private String validateAndRectifyValue(String value) {
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

		String masterCustomerName = data[3];
		String financeCustomerName = data[6];
		String financeIou = data[7];
		String financeGeography = data[8];
		String revenueCustomerMapId = validateAndRectifyValue(data[10]);

		int rowNumber = Integer.parseInt(data[0]) + 1;		
		CustomerMasterT customer = customerRepository.findByCustomerName(masterCustomerName);

		if(!StringUtils.isEmpty(revenueCustomerMapId)){
			RevenueCustomerMappingT financeCustomer = revenueRepository
					.findOne(Long.parseLong(revenueCustomerMapId));
			if (financeCustomer == null) {
				error.setRowNumber(rowNumber);
				error.setMessage("Finance customer not found,hence it cannot be updated");

			} else {
				if(financeCustomer.isActive()){
					finance.setCustomerId(customer.getCustomerId());
					finance.setRevenueCustomerMapId(Long.parseLong(revenueCustomerMapId));
					if (!StringUtils.isEmpty(financeCustomerName)) {
						finance.setFinanceCustomerName(financeCustomerName);
					} else {
						error.setRowNumber(rowNumber);
						error.setMessage("financeCustomerName Is Mandatory; ");
					}


					if (!StringUtils.isEmpty(financeIou)
							&& mapOfIouMappingT.containsKey(financeIou)) {
						finance.setFinanceIou(financeIou);;
					} else {
						error.setRowNumber(rowNumber);
						error.setMessage("financeIou Is Mandatory; ");

					}
					if (!StringUtils.isEmpty(financeGeography)
							&& mapOfGeographyMappingT.containsKey(financeGeography)) {
						finance.setCustomerGeography(financeGeography);

					} else {
						error.setRowNumber(rowNumber);
						error.setMessage("financeGeography Is Mandatory; ");

					}
					//check for inactive records and log 
					try {
						revenueUploadService.validateInactiveIndicators(finance);
					} catch(DestinationException e) {
						error.setRowNumber(rowNumber);
						error.setMessage(e.getMessage());
					}

				} else {
					error.setRowNumber(rowNumber);
					error.setMessage(" Finance / Revenue Customer is inactive and cannot be updated");
				}
			}
		} else {
			error.setRowNumber(rowNumber);
			error.setMessage("Revenue Customer Map Id cannot be empty for Update");
		}
		return error;
	}
}
