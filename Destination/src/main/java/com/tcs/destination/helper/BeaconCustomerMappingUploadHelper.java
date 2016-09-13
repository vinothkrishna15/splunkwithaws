package com.tcs.destination.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.apache.commons.lang.StringUtils;

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
	
	private static final Logger logger = LoggerFactory
			.getLogger(BeaconCustomerMappingUploadHelper.class);

	/**
	 * This method is used to validate the beacon customer add
	 * @param data
	 * @param userId
	 * @param beacon
	 * @return
	 */
	public UploadServiceErrorDetailsDTO validateBeaconCustomerAdd(
			String[] data, String userId, BeaconCustomerMappingT beacon) {
		
		logger.debug("Begin:inside validateBeaconCustomerAdd() of CustomerUploadHelper");

		String masterCustomerName = data[3];
		String beaconcustomerMapId = data[10];
		int rowNumber = Integer.parseInt(data[0]) + 1;

		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		StringBuffer errorMsg = new StringBuffer();
		String beaconCustomerName = data[6];
		String beaconGeography = data[8];
		String beaconIou = data[7];
		List<BeaconCustomerMappingT> beaconCustomers=new ArrayList<BeaconCustomerMappingT>();
		
		if(StringUtils.isEmpty(beaconcustomerMapId)){
			
			if((!StringUtils.isEmpty(beaconCustomerName))&&(!StringUtils.isEmpty(beaconGeography))&&(!StringUtils.isEmpty(beaconIou)))
			{
			// to find the uniqueness of the primary key (here composite key)
	          beaconCustomers = beaconCustomerMappingRepository.checkBeaconMappingPK(beaconCustomerName, beaconGeography, beaconIou);
			}
			
 
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
                
				if(customerObj!=null)
                {
					
				if(customerObj.isActive())
				{
				
				//CUSTOMER ID
				beacon.setCustomerId(customerObj.getCustomerId());
				
                
				//BEACON CUSTOMER NAME
				if (!StringUtils.isEmpty(beaconCustomerName)) {
					beacon.setBeaconCustomerName(beaconCustomerName);
				} 
				else 
				{
					error.setRowNumber(rowNumber);
					errorMsg.append("BeaconCustomerName Is Mandatory;");
				}
                 
				//BEACON IOU
				if (!StringUtils.isEmpty(beaconIou)
						&& mapOfIouMappingT.containsKey(beaconIou)) {
					beacon.setBeaconIou(beaconIou);
				} else {
					error.setRowNumber(rowNumber);
					errorMsg.append("BeaconIou Is Not Valid;");
				}
				
				// ACTIVE
				String active = data[9];
				if (active!=null) {
					beacon.setActive(Boolean.parseBoolean(active));

				}
				
				//BEACON GEOGRAPHY
				if (!StringUtils.isEmpty(beaconGeography)
						&& mapOfGeographyMappingT.containsKey(beaconGeography)) {
					beacon.setCustomerGeography(beaconGeography);

				} else {
					error.setRowNumber(rowNumber);
					errorMsg.append("BeaconGeography Is Not Valid;");

				}
				}
				else
				{
					error.setRowNumber(rowNumber);
					errorMsg.append("The Beacon Customer is not active to be added;");
				}
                }
                else
                {
                	error.setRowNumber(rowNumber);
                	errorMsg.append("The Customer Is Not Valid;");
                }
                
			} else {
				error.setRowNumber(rowNumber);
				errorMsg.append("This Beacon Customer details already exist;");

			}
		}
		else
		{
			error.setRowNumber(rowNumber);
			errorMsg.append("Beacon mapping id should not be given upon add operation!!");
		}
		
		String errorMessage = errorMsg.toString();
		if(StringUtils.isNotEmpty(errorMessage)) {
			error.setMessage(errorMessage);
		}

		
		logger.debug("End:inside validateBeaconCustomerAdd() of CustomerUploadHelper");
       return error;
	}

	/**
	 * Method to validate the fields before delete
	 * @param data
	 * @param userId
	 * @param beacon
	 * @return
	 */
	public UploadServiceErrorDetailsDTO validateBeaconCustomerDelete(
			String[] data, String userId, BeaconCustomerMappingT beacon) {

		String beaconCustomerMapId = validateAndRectifyValue(data[10]);// retrieving beacon customer id for updation / deletion 

		int rowNumber = Integer.parseInt(data[0]) + 1;	
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		StringBuffer errorMsg = new StringBuffer();
        if(!StringUtils.isEmpty(beaconCustomerMapId)){
			if (beacon == null){
				
				error.setRowNumber(rowNumber);
				errorMsg.append("Beacon customer does not exist for the given beacon map id. Hence it cannot be deleted!");
			}
			else
			{
				beacon.setActive(false);	
			}
		}
		else{
			error.setRowNumber(rowNumber);
			errorMsg.append("Beacon customer map id cannot be empty for deletion!");
		}
        
        String errorMessage = errorMsg.toString();
		if(StringUtils.isNotEmpty(errorMessage)) {
			error.setMessage(errorMessage);
		}

		return error;
	}

	/**
	 * to validate and transform a String to a proper format
	 * so that it can be parsed - to eliminate NumberFormatException
	 * @param value
	 * @return
	 */
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

	/**
	 * Method to validate the fields before update
	 * @param data
	 * @param userId
	 * @param beacon
	 * @return
	 */
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
		String active = data[9];
		String beaconcustomerMapId = validateAndRectifyValue(data[10]);
		StringBuffer errorMsg = new StringBuffer();
		List<BeaconCustomerMappingT> beaconCustomers =new ArrayList<BeaconCustomerMappingT>();


		int rowNumber = Integer.parseInt(data[0]) + 1;		
		CustomerMasterT customer = customerRepository.findByCustomerName(masterCustomerName);
		if((!StringUtils.isEmpty(beaconCustomerName))&&(!StringUtils.isEmpty(beaconGeography))&&(!StringUtils.isEmpty(beaconIou)))
		{
		// to find the uniqueness of the primary key (here composite key)
		beaconCustomers = beaconCustomerMappingRepository.checkBeaconMappingPK(beaconCustomerName, beaconGeography, beaconIou);
		}
		
		if(!StringUtils.isEmpty(beaconcustomerMapId)){
			BeaconCustomerMappingT beaconCustomer = beaconCustomerMappingRepository
					.findOne(Long.parseLong(beaconcustomerMapId));
			if (beaconCustomer == null) {
				error.setRowNumber(rowNumber);
				errorMsg.append("Beacon customer not found for the beacon map id ,hence it cannot be updated");

			} else {
				
					if(beaconCustomer.isActive()){
						if (beaconCustomers.isEmpty()) {
						beacon.setCustomerId(customer.getCustomerId());
						beacon.setBeaconCustomerMapId(Long.parseLong(beaconcustomerMapId));
						if (!StringUtils.isEmpty(beaconCustomerName)) {
							beacon.setBeaconCustomerName(beaconCustomerName);
						} else {
							error.setRowNumber(rowNumber);
							errorMsg.append("beaconCustomerName Is Mandatory; ");
						}

						if (!StringUtils.isEmpty(beaconIou)
								&& mapOfIouMappingT.containsKey(beaconIou)) {
							beacon.setBeaconIou(beaconIou);
						} else {
							error.setRowNumber(rowNumber);
							errorMsg.append("beaconIou Is Mandatory; ");
						}
						if (!StringUtils.isEmpty(beaconGeography)
								&& mapOfGeographyMappingT.containsKey(beaconGeography)) {
							beacon.setCustomerGeography(beaconGeography);
						} else {
							error.setRowNumber(rowNumber);
							errorMsg.append("beaconGeography Is Mandatory; ");
						}
						
						if (!StringUtils.isEmpty(active)){
							beacon.setActive(Boolean.parseBoolean(active));
						} 
						//check for inactive records and log 
						try {
							beaconCustomerUploadService.validateInactiveIndicators(beacon);
						} catch(DestinationException e) {
							error.setRowNumber(rowNumber);
							errorMsg.append(e.getMessage());
						}

					} else {
						error.setRowNumber(rowNumber);
						errorMsg.append("Beacon Customer details already exists");
					}
				}
				else{
					error.setRowNumber(rowNumber);
					errorMsg.append("Beacon Customer is inactive and cannot be updated");
				}
			}
		}else {
			error.setRowNumber(rowNumber);
			errorMsg.append("Beacon Customer map Id cannot be empty for update");
		}
		String errorMessage = errorMsg.toString();
		if(StringUtils.isNotEmpty(errorMessage)) {
			error.setMessage(errorMessage);
		}

		return error;
	}
}
