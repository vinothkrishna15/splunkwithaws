package com.tcs.destination.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.GeographyCountryMappingT;
import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.bean.OpportunitySubSpLinkT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.PartnerSubSpMappingT;
import com.tcs.destination.bean.PartnerSubspProductMappingT;
import com.tcs.destination.bean.ProductMasterT;
import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.ConnectTypeRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.GeographyCountryRepository;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.data.repository.OfferingRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.PartnerSubSpMappingTRepository;
import com.tcs.destination.data.repository.PartnerSubSpProductMappingTRepository;
import com.tcs.destination.data.repository.ProductRepository;
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
	private ProductRepository productRepository;
	
	@Autowired
	private GeographyCountryRepository geographyCountryRepository;
	
	@Autowired
	private PartnerSubSpMappingTRepository partnerSubSpMappingTRepository;
	
	@Autowired
	private PartnerSubSpProductMappingTRepository partnerSubSpProductMappingTRepository;

	@Autowired
	private PartnerService partnerService;
	


	
	/**
	 * This method is used to validate partner data for add
	 * @param data
	 * @param userId
	 * @param partnerMasterT
	 * @param childList
	 * @param parentList
	 * @return
	 * @throws Exception
	 */
	
	public UploadServiceErrorDetailsDTO validatePartnerData(String[] data, String userId, PartnerMasterT partnerMasterT,List<PartnerMasterT> childList,List<PartnerMasterT> parentList,Map<String, String> mapOfPartnerAndHqLink) throws Exception 
	{
		
			
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		
		
		        // PARTNER_NAME 
				String partnerName = data[3];
				int rowNumber = Integer.parseInt(data[0]) + 1;
				if(!StringUtils.isEmpty(partnerName))
				{
					
					partnerMasterT.setPartnerName(partnerName);
					
				}
				else
				{
					error.setRowNumber(rowNumber);
					error.setMessage("Partner name is mandatory; ");
				}
				
				//GROUP PARTNER NAME 
				String groupPartnerName = data[4];
				if(!StringUtils.isEmpty(groupPartnerName))
				{
					partnerMasterT.setGroupPartnerName(groupPartnerName);
				}
				else
				{
					error.setRowNumber(rowNumber);
					error.setMessage("Group partner name is mandatory; ");
				}
				
				// GEOGRAPHY 
				String geography = data[5];
				if(!StringUtils.isEmpty(geography))
				{
					GeographyMappingT geographyMappingT=geographyRepository.findByGeography(geography);
					if(geographyMappingT!=null)
					{
					  partnerMasterT.setGeography(geography);
					}
					else
					{
						error.setRowNumber(rowNumber);
						error.setMessage("Geography is invalid ");
					}

				}
				else
				{
					error.setRowNumber(rowNumber);
					error.setMessage("Geography is mandatory; ");
				}
				
				//COUNTRY 
				String country = data[6];
				if(!StringUtils.isEmpty(country))
				{
					GeographyCountryMappingT geographyCountryMappingT=geographyCountryRepository.findByCountry(country);
					if(geographyCountryMappingT!=null)
					{
					    partnerMasterT.setCountry(country);
					}
					else
					{
						error.setRowNumber(rowNumber);
						error.setMessage("Country is invalid ");
					}
				}
				else
				{
					error.setRowNumber(rowNumber);
					error.setMessage("Country is mandatory ");
				}
				
				//CITY 
				String city=data[7];
				if(!StringUtils.isEmpty(city))
				{
					partnerMasterT.setCity(city);
				}
				else
				{
					error.setRowNumber(rowNumber);
					error.setMessage("City is mandatory ");
				}
				
				
				//CORPORATE_HQ_ADDRESS (Optional)
				String corporate_hq_address = data[9];
				if(!StringUtils.isEmpty(corporate_hq_address))
				{
					partnerMasterT.setCorporateHqAddress(corporate_hq_address);
				}
				
				//WEBSITE (Optional)
				String website = data[10];
				if(!StringUtils.isEmpty(website))
				{
					partnerMasterT.setWebsite(website);
				}
				
				
				//FACEBOOK (Optional)
				String facebook = data[11];
				if(!StringUtils.isEmpty(facebook))
				{
					partnerMasterT.setFacebook(facebook);
				}
				
				//TEXT1 (Optional)
				String text1 = data[12];
				if(!StringUtils.isEmpty(text1))
				{
					partnerMasterT.setText1(text1);
				}
				
				//TEXT2 (Optional)
				String text2 = data[13];
				if(!StringUtils.isEmpty(text2))
				{
					partnerMasterT.setText2(text2);
				}
				
				//TEXT3 (Optional)
				String text3 = data[14];
				if(!StringUtils.isEmpty(text3))
				{
					partnerMasterT.setText3(text3);
				}
				
				//NOTES (Optional)
				String notes = data[15];
				if(!StringUtils.isEmpty(notes))
				{
					partnerMasterT.setNotes(notes);
				}
				
				//ACTIVE
				String active = data[16];
				if(!StringUtils.isEmpty(active))
				{
					boolean activeFlag=false;
					if(active.equalsIgnoreCase("true"))
					{
						activeFlag=true;
					}
					
				partnerMasterT.setActive(activeFlag);
				}
				
				
				// CREATED_BY
				partnerMasterT.setCreatedBy(userId);
				
				// MODIFIED_BY
				partnerMasterT.setModifiedBy(userId);
					
				//DOCUMENTS_ATTACHED
				partnerMasterT.setDocumentsAttached(Constants.NO);
				
				
				//HQ_PARTNER_LINK_NAME
				String hqPartnerLinkName = data[8];
				if(!StringUtils.isEmpty(hqPartnerLinkName))
				{
				   mapOfPartnerAndHqLink.put(partnerName, hqPartnerLinkName);
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

	/**
	 * This method is used to validate partner data for update operation
	 * @param data
	 * @param userId
	 * @param partner
	 * @param childList
	 * @param parentList
	 * @return
	 * @throws Exception
	 */
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

		// PARTNER_NAME 
		String partnername = data[3];
		if(!StringUtils.isEmpty(partnername))
		{
			
			partner.setPartnerName(partnername);
			
		}
		else
		{
			error.setRowNumber(rowNumber);
			error.setMessage("Partner name is mandatory; ");
		}
		
		//GROUP PARTNER NAME 
		String groupPartnerName = data[4];
		if(!StringUtils.isEmpty(groupPartnerName))
		{
			partner.setGroupPartnerName(groupPartnerName);
		}
		else
		{
			error.setRowNumber(rowNumber);
			error.setMessage("Group partner name is mandatory; ");
		}
		
		// GEOGRAPHY 
		String geography = data[5];
		if(!StringUtils.isEmpty(geography))
		{
			GeographyMappingT geographyMappingT=geographyRepository.findByGeography(geography);
			if(geographyMappingT!=null)
			{
				partner.setGeography(geography);
			}
			else
			{
				error.setRowNumber(rowNumber);
				error.setMessage("Geography is invalid ");
			}

		}
		else
		{
			error.setRowNumber(rowNumber);
			error.setMessage("Geography is mandatory; ");
		}
		
		//COUNTRY 
		String country = data[6];
		if(!StringUtils.isEmpty(country))
		{
			GeographyCountryMappingT geographyCountryMappingT=geographyCountryRepository.findByCountry(country);
			if(geographyCountryMappingT!=null)
			{
				partner.setCountry(country);
			}
			else
			{
				error.setRowNumber(rowNumber);
				error.setMessage("Country is invalid ");
			}
		}
		else
		{
			error.setRowNumber(rowNumber);
			error.setMessage("Country is mandatory ");
		}
		
		//CITY 
		String city=data[7];
		if(!StringUtils.isEmpty(city))
		{
			partner.setCity(city);
		}
		else
		{
			error.setRowNumber(rowNumber);
			error.setMessage("City is mandatory ");
		}
		
		//CORPORATE_HQ_ADDRESS (Optional)
		String corporate_hq_address = data[9];
		if(!StringUtils.isEmpty(corporate_hq_address))
		{
			partner.setCorporateHqAddress(corporate_hq_address);
		}
		
		//WEBSITE (Optional)
		String website = data[10];
		if(!StringUtils.isEmpty(website))
		{
			partner.setWebsite(website);
		}
		
		
		//FACEBOOK (Optional)
		String facebook = data[11];
		if(!StringUtils.isEmpty(facebook))
		{
			partner.setFacebook(facebook);
		}
		
		//TEXT1 (Optional)
		String text1 = data[12];
		if(!StringUtils.isEmpty(text1))
		{
			partner.setText1(text1);
		}
		
		//TEXT2 (Optional)
		String text2 = data[13];
		if(!StringUtils.isEmpty(text2))
		{
			partner.setText2(text2);
		}
		
		//TEXT3 (Optional)
		String text3 = data[14];
		if(!StringUtils.isEmpty(text3))
		{
			partner.setText3(text3);
		}
		
		//NOTES (Optional)
		String notes = data[15];
		if(!StringUtils.isEmpty(notes))
		{
			partner.setNotes(notes);
		}
		
		//ACTIVE
		String active = data[16];
		if(!StringUtils.isEmpty(active))
		{
			boolean activeFlag=false;
			if(active.equalsIgnoreCase("true"))
			{
				activeFlag=true;
			}
			
			partner.setActive(activeFlag);
		}
		
		
		// CREATED_BY
		partner.setCreatedBy(userId);
		
		// MODIFIED_BY
		partner.setModifiedBy(userId);
			
		//DOCUMENTS_ATTACHED
		partner.setDocumentsAttached(Constants.NO);
		
		
		//HQ_PARTNER_LINK_NAME
		String hqPartnerLinkName = data[8];
		if(!StringUtils.isEmpty(hqPartnerLinkName))
		{
			String hqPartnerLinkId=partnerRepository.findPartnerIdByName(hqPartnerLinkName);
			if(hqPartnerLinkId!=null)
			{
			partner.setHqPartnerLinkId(hqPartnerLinkId);
			childList.add(partner);
			}
			
		}
		else
		{
			partner.setHqPartnerLinkId(null);
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
	
	/**
	 * This method is used to validate partner id for delete operation
	 * @param data
	 * @param partner
	 * @return
	 */
	public UploadServiceErrorDetailsDTO validatePartnerId(String[] data,
			PartnerMasterT partner) {
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		String partnerId = data[2];

		if (StringUtils.isEmpty(partnerId)) {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Partner Id is mandatory ");
		} else {
			PartnerMasterT partnerMasterT = partnerRepository.findByPartnerId(partnerId);
			
			if (partnerMasterT== null) {
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
	
}
