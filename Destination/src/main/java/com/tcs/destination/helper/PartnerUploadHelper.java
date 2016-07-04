package com.tcs.destination.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.ConnectTypeRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.data.repository.OfferingRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.data.repository.TimezoneMappingRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.PartnerService;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.StringUtils;

@Component("partnerUploadHelper")
public class PartnerUploadHelper {
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private PartnerRepository partnerRepository;
	
	@Autowired
	private SubSpRepository subSpRepository;
	
	@Autowired
	private OfferingRepository offeringRepository;
	
	@Autowired
	private TimezoneMappingRepository timeZoneMappingRepository;
	
	@Autowired
	private  UserRepository userRepository;
	
	@Autowired
	private  ContactRepository contactRepository;
	
	@Autowired
	private  ConnectTypeRepository connectTypeRepository;
	
	@Autowired
	private GeographyRepository geographyRepository;

	@Autowired
	private PartnerService partnerService;
	
	
	public UploadServiceErrorDetailsDTO validatePartnerData(String[] data, String userId, PartnerMasterT partnerMasterT) throws Exception 
	{
		
			
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		
		        // PARTNER_NAME 
				String partnername = data[3];
				int rowNumber = Integer.parseInt(data[0]) + 1;
				if(!StringUtils.isEmpty(partnername))
				{
					
					partnerMasterT.setPartnerName(partnername);
					//List<PartnerMasterT> partners = partnerRepository.findByPartnerName(partnername);
				}
				else
				{
					error.setRowNumber(rowNumber);
					error.setMessage("Partner Name Is Mandatory; ");
				}
				
				// GEOGRAPHY 
				String geography = data[4];
				if(!StringUtils.isEmpty(geography))
				{
					GeographyMappingT geographyMappingT=new GeographyMappingT();
					geographyMappingT.setGeography(geography);
					partnerMasterT.setGeographyMappingT(geographyMappingT);
				}
				else{
					error.setRowNumber(rowNumber);
					error.setMessage("Geography Is Mandatory; ");
				}
				
				//WEBSITE (Optional)
				String website = data[5];
				if(!StringUtils.isEmpty(website))
				{
					partnerMasterT.setWebsite(website);
				}
				
				
				//FACEBOOK (Optional)
				String facebook = data[6];
				if(!StringUtils.isEmpty(facebook))
				{
					partnerMasterT.setFacebook(facebook);
				}
				
				//CORPORATE_HQ_ADDRESS (Optional)
				String corporate_hq_address = data[7];
				if(!StringUtils.isEmpty(corporate_hq_address))
				{
					partnerMasterT.setCorporateHqAddress(corporate_hq_address);
				}
				
				// CREATED_BY
				partnerMasterT.setCreatedBy(userId);
				
				// MODIFIED_BY
				partnerMasterT.setModifiedBy(userId);
					
				//DOCUMENTS_ATTACHED
				partnerMasterT.setDocumentsAttached(Constants.NO);
				
				//ACTIVE
				partnerMasterT.setActive(true);
				
				//check for inactive records and log 
				try {
					partnerService.validateInactiveIndicators(partnerMasterT);
				} catch(DestinationException e) {
					error.setRowNumber(rowNumber);
					error.setMessage(e.getMessage());
				}

		return error;
	}

	public UploadServiceErrorDetailsDTO validatePartnerDataUpdate(
			String[] data, String userId, PartnerMasterT partner) throws Exception 
	{
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		
		String partnerId=data[2];
	
		int rowNumber = Integer.parseInt(data[0]) + 1;
		if (!StringUtils.isEmpty(partnerId)) {

		PartnerMasterT partnerMasterT = partnerRepository.findByPartnerId(partnerId);
		if(partnerMasterT==null)
		{
			error.setRowNumber(rowNumber);
			error.setMessage("Invalid Partner Id");
			
		}
		}
		else
		{
			error.setRowNumber(rowNumber);
			error.setMessage("Partner id is mandatory");
		}

		String partnerName = data[3];
		
		if (!StringUtils.isEmpty(partnerName)) 
		{
		     partner.setPartnerName(partnerName);
		}
		else
		{
			error.setRowNumber(rowNumber);
			error.setMessage("Partner Name Is Mandatory; ");
			
		}
		
		// GEOGRAPHY 
		String geography = data[4];
		if(!StringUtils.isEmpty(geography))
		{
			GeographyMappingT geographyMappingT=geographyRepository.findByGeography(geography);
			if(geographyMappingT.getGeography()!=null)
			{
			 partner.setGeography(geography);
			}
		}
		else
		{
			error.setRowNumber(rowNumber);
			error.setMessage("Geography Is Mandatory; ");
		}
		
		//WEBSITE (Optional)
		String website = data[5];
		if(!StringUtils.isEmpty(website))
		{
			partner.setWebsite(website);
		}
		
		
		//FACEBOOK (Optional)
		String facebook = data[6];
		if(!StringUtils.isEmpty(facebook))
		{
			partner.setFacebook(facebook);
		}
		
		
		//CORPORATE_HQ_ADDRESS (Optional)
		String corporate_hq_address = data[7];
		if(!StringUtils.isEmpty(corporate_hq_address))
		{
			partner.setCorporateHqAddress(corporate_hq_address);
		}
		
		// CREATED_MODIFIED_BY
		partner.setModifiedBy(userId);
			
		//DOCUMENTS_ATTACHED
		partner.setDocumentsAttached(Constants.NO);
		
		//ACTIVE
		partner.setActive(true);
		
		//check for inactive records and log 
		try {
			partnerService.validateInactiveIndicators(partner);
		} catch(DestinationException e) {
			error.setRowNumber(rowNumber);
			error.setMessage(e.getMessage());
		}
		
		return error;

	}
	
	public UploadServiceErrorDetailsDTO validatePartnerId(String[] data,
			PartnerMasterT partner) {
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		String partnerId = data[2];

		if (StringUtils.isEmpty(partnerId)) {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Partner Id is mandatory ");
		} else {
			PartnerMasterT partnerMasterT = partnerRepository.findByPartnerId(partnerId);
			if (partnerMasterT.getPartnerId() == null) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("Invalid Partner Id ");
			}
			else
			{
				//ACTIVE
				partner.setActive(false);
				
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
	
}
