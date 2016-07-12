package com.tcs.destination.helper;

import java.util.ArrayList;
import java.util.List;

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
	
	
	public UploadServiceErrorDetailsDTO validatePartnerData(String[] data, String userId, PartnerMasterT partnerMasterT,List<PartnerMasterT> childList,List<PartnerMasterT> parentList) throws Exception 
	{
		
			
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		
		
		        // PARTNER_NAME 
				String partnername = data[3];
				int rowNumber = Integer.parseInt(data[0]) + 1;
				if(!StringUtils.isEmpty(partnername))
				{
					
					partnerMasterT.setPartnerName(partnername);
					
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
					
					partnerMasterT.setGeography(geography);

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
				String active = data[8];
				if(!StringUtils.isEmpty(active))
				{
					boolean activeFlag=false;
					if(active.equalsIgnoreCase("true"))
					{
						activeFlag=true;
					}
					
				partnerMasterT.setActive(activeFlag);
				}
				
				//COUNTRY (Optional)
				String country = data[9];
				if(!StringUtils.isEmpty(country))
				{
					partnerMasterT.setCountry(country);
				}
				
				//CITY (Optional)
				String city=data[10];
				if(!StringUtils.isEmpty(city))
				{
					partnerMasterT.setCity(city);
				}
				
				//TEXT1 (Optional)
				String text1 = data[11];
				if(!StringUtils.isEmpty(text1))
				{
					partnerMasterT.setText1(text1);
				}
				
				//TEXT2 (Optional)
				String text2 = data[12];
				if(!StringUtils.isEmpty(text2))
				{
					partnerMasterT.setText2(text2);
				}
				
				//TEXT3 (Optional)
				String text3 = data[13];
				if(!StringUtils.isEmpty(text3))
				{
					partnerMasterT.setText3(text3);
				}
				
				//GROUP PARTNER NAME (Optional)
				String groupPartnerName = data[14];
				if(!StringUtils.isEmpty(groupPartnerName))
				{
					partnerMasterT.setGroupPartnerName(groupPartnerName);
				}
				
				//NOTES (Optional)
				String notes = data[15];
				if(!StringUtils.isEmpty(notes))
				{
					partnerMasterT.setNotes(notes);
				}
				
				//HQ_PARTNER_LINK_NAME
				String hqPartnerLinkName = data[16];
				if(!StringUtils.isEmpty(hqPartnerLinkName))
				{
					String hqPartnerLinkId=partnerRepository.findPartnerIdByName(hqPartnerLinkName);
					partnerMasterT.setHqPartnerLinkId(hqPartnerLinkId);
					childList.add(partnerMasterT);
					
				}
				else
				{
					parentList.add(partnerMasterT);
				}
				
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
			String[] data, String userId, PartnerMasterT partner,List<PartnerMasterT> childList,List<PartnerMasterT> parentList) throws Exception 
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
		
		//COUNTRY (Optional)
		String country = data[9];
		if(!StringUtils.isEmpty(country))
		{
			partner.setCountry(country);
		}
		
		//CITY (Optional)
		String city=data[10];
		if(!StringUtils.isEmpty(city))
		{
			partner.setCity(city);
		}
		
		//TEXT1 (Optional)
		String text1 = data[11];
		if(!StringUtils.isEmpty(text1))
		{
			partner.setText1(text1);
		}
		
		//TEXT2 (Optional)
		String text2 = data[12];
		if(!StringUtils.isEmpty(text2))
		{
			partner.setText2(text2);
		}
		
		//TEXT3 (Optional)
		String text3 = data[13];
		if(!StringUtils.isEmpty(text3))
		{
			partner.setText3(text3);
		}
		
		//GROUP PARTNER NAME (Optional)
		String groupPartnerName = data[14];
		if(!StringUtils.isEmpty(groupPartnerName))
		{
			partner.setGroupPartnerName(groupPartnerName);
		}
		
		//NOTES (Optional)
		String notes = data[15];
		if(!StringUtils.isEmpty(notes))
		{
			partner.setNotes(notes);
		}
		
		//HQ_PARTNER_LINK_NAME
		String hqPartnerLinkName = data[16];
		if(!StringUtils.isEmpty(hqPartnerLinkName))
		{
			String hqPartnerLinkId=partnerRepository.findPartnerIdByName(hqPartnerLinkName);
			partner.setHqPartnerLinkId(hqPartnerLinkId);
			childList.add(partner);
			
		}
		else
		{
			parentList.add(partner);
		}
		
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
