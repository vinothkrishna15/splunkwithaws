package com.tcs.destination.helper;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.BeaconCustomerMappingT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.bean.IouCustomerMappingT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.BeaconRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.StringUtils;

@Component("beaconCustomerMappingUploadHelper")
public class BeaconCustomerMappingUploadHelper {

	@Autowired
	BeaconRepository beaconRepository;

	@Autowired
	CommonHelper commonHelper;
	
	@Autowired
	CustomerRepository customerRepository;

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
			List<BeaconCustomerMappingT> beaconCustomers = beaconRepository.findbeaconDuplicates(beaconCustomerName, beaconIou, beaconGeography);
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
				beacon.setActive(true);

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
		// TODO Auto-generated method stub
		return null;
	}

	public UploadServiceErrorDetailsDTO validateBeaconCustomerUpdate(
			String[] data, String userId, BeaconCustomerMappingT beacon) {
		// TODO Auto-generated method stub
		return null;
	}

}
