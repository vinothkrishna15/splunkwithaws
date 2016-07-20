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
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.BeaconCustomerMappingRepository;
import com.tcs.destination.data.repository.BeaconRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.BeaconCustomerUploadService;
import com.tcs.destination.utils.StringUtils;

@Component("beaconCustomerMappingUploadHelper")
public class BeaconCustomerMappingUploadHelper {

	@Autowired
	BeaconRepository beaconRepository;

	@Autowired
	CommonHelper commonHelper;

	@Autowired
	BeaconCustomerMappingRepository beaconCustomerMappingRepository;

	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	BeaconCustomerUploadService beaconCustomerUploadService;

	Map<String, GeographyMappingT> mapOfGeographyMappingT = null;
	Map<String, IouCustomerMappingT> mapOfIouMappingT = null;


	public UploadServiceErrorDetailsDTO validateBeaconCustomerAdd(
			String[] data, String userId, BeaconCustomerMappingT beacon) {
		String masterCustomerName = data[3];
		String beaconCustomerName = data[6];
		String beaconIou = data[7];
		String beaconGeography = data[8];
		String active = data[9];
		String beaconcustomerMapId = data[10];

		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();

		if(StringUtils.isEmpty(beaconcustomerMapId)){
			// to find the uniqueness of the primary key (here composite key)
			List<BeaconCustomerMappingT> beaconCustomers = beaconCustomerMappingRepository.checkBeaconMappingPK(beaconCustomerName, beaconGeography, beaconIou);
			int rowNumber = Integer.parseInt(data[0]) + 1;

			if (beaconCustomers.isEmpty()) {
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

				beacon.setCustomerId(customerObj.getCustomerId());
				beacon.setActive(Boolean.parseBoolean(active));

				if (!StringUtils.isEmpty(beaconCustomerName)) {
					beacon.setBeaconCustomerName(beaconCustomerName);
				} else {
					error.setRowNumber(rowNumber);
					error.setMessage("beaconCustomerName Is Mandatory; ");
				}


				if (!StringUtils.isEmpty(beaconIou)
						&& mapOfIouMappingT.containsKey(beaconIou)) {
					beacon.setBeaconIou(beaconIou);
				} else {
					error.setRowNumber(rowNumber);
					error.setMessage("beaconIou Is Mandatory; ");

				}
				if (!StringUtils.isEmpty(beaconGeography)
						&& mapOfGeographyMappingT.containsKey(beaconGeography)) {
					beacon.setCustomerGeography(beaconGeography);

				} else {
					error.setRowNumber(rowNumber);
					error.setMessage("beaconGeography Is Mandatory; ");

				}
			} else {
				error.setRowNumber(rowNumber);
				error.setMessage("Beacon Customer Name already exist ");

			}
		}
		return error;
	}

	public UploadServiceErrorDetailsDTO validateBeaconCustomerDelete(
			String[] data, String userId, BeaconCustomerMappingT beacon) {

		String beaconCustomerMapId = validateAndRectifyValue(data[10]);// retrieving beacon customer id for updation / deletion 

		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();

		//List<BeaconCustomerMappingT> beaconCustomer = beaconRepository.findbeaconDuplicates(beaconCustomerName, beaconIou, beaconGeography);
		BeaconCustomerMappingT beaconCustomer = beaconCustomerMappingRepository.findOne(Long.parseLong(beaconCustomerMapId));
		if (beaconCustomer != null) {
			if(beacon!=null){
				try {
					BeanUtils.copyProperties(beacon, beaconCustomer);
				} catch (Exception e) {
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					error.setMessage("Backend Error while cloning");
				}
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("Beacon Customer Mapping not found");
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

	public UploadServiceErrorDetailsDTO validateBeaconCustomerUpdate(
			String[] data, String userId, BeaconCustomerMappingT beacon) {
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
		String beaconCustomerName = data[6];
		String beaconIou = data[7];
		String beaconGeography = data[8];
		String beaconcustomerMapId = validateAndRectifyValue(data[10]);

		int rowNumber = Integer.parseInt(data[0]) + 1;		
		CustomerMasterT customer = customerRepository.findByCustomerName(masterCustomerName);

		if(!StringUtils.isEmpty(beaconcustomerMapId)){
			BeaconCustomerMappingT beaconCustomer = beaconCustomerMappingRepository
					.findOne(Long.parseLong(beaconcustomerMapId));
			if (beaconCustomer == null) {
				error.setRowNumber(rowNumber);
				error.setMessage("Beacon customer not found,hence it cannot be updated");

			} else {
				if(beaconCustomer.isActive()){
					beacon.setCustomerId(customer.getCustomerId());
					beacon.setBeaconCustomerMapId(Long.parseLong(beaconcustomerMapId));
					if (!StringUtils.isEmpty(beaconCustomerName)) {
						beacon.setBeaconCustomerName(beaconCustomerName);
					} else {
						error.setRowNumber(rowNumber);
						error.setMessage("beaconCustomerName Is Mandatory; ");
					}


					if (!StringUtils.isEmpty(beaconIou)
							&& mapOfIouMappingT.containsKey(beaconIou)) {
						beacon.setBeaconIou(beaconIou);
					} else {
						error.setRowNumber(rowNumber);
						error.setMessage("beaconIou Is Mandatory; ");

					}
					if (!StringUtils.isEmpty(beaconGeography)
							&& mapOfGeographyMappingT.containsKey(beaconGeography)) {
						beacon.setCustomerGeography(beaconGeography);

					} else {
						error.setRowNumber(rowNumber);
						error.setMessage("beaconGeography Is Mandatory; ");

					}
					//check for inactive records and log 
					try {
						beaconCustomerUploadService.validateInactiveIndicators(beacon);
					} catch(DestinationException e) {
						error.setRowNumber(rowNumber);
						error.setMessage(e.getMessage());
					}

				} else {
					error.setRowNumber(rowNumber);
					error.setMessage("Beacon Customer is inactive and cannot be updated");
				}
			}
		} else {
			error.setRowNumber(rowNumber);
			error.setMessage("Beacon Customer map Id cannot be empty for update");
		}
		return error;
	}
}
