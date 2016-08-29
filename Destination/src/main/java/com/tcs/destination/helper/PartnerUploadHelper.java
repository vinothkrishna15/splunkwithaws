package com.tcs.destination.helper;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.GeographyCountryMappingT;
import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.bean.PartnerMasterT;
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
	 * @param mapOfPartnerAndRowNumer 
	 * @return
	 * @throws Exception
	 */

	public UploadServiceErrorDetailsDTO validatePartnerData(String[] data, String userId, PartnerMasterT partnerMasterT,List<PartnerMasterT> childList,List<PartnerMasterT> parentList,Map<String, String> mapOfPartnerAndHqLink, Map<String, Integer> mapOfPartnerAndRowNumer) throws Exception 
	{
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();

		StringBuffer errorMessage = new StringBuffer();
		// PARTNER_NAME 
		String partnerName = data[3];
		int rowNumber = Integer.parseInt(data[0]) + 1;
		if(!StringUtils.isEmpty(partnerName))
		{
			List<PartnerMasterT> partnerList = partnerRepository.findByPartnerName(partnerName);
			if(CollectionUtils.isNotEmpty(partnerList)) {
				error.setRowNumber(rowNumber);
				errorMessage.append("Partner is already present in system; ");
			}
			partnerMasterT.setPartnerName(partnerName);
		}
		else
		{
			error.setRowNumber(rowNumber);
			errorMessage.append("Partner name is mandatory; ");
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
			errorMessage.append("Group partner name is mandatory; ");
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
				errorMessage.append("Geography is invalid ");
			}

		}
		else
		{
			error.setRowNumber(rowNumber);
			errorMessage.append("Geography is mandatory; ");
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
				errorMessage.append("Country is invalid ");
			}
		}
		else
		{
			error.setRowNumber(rowNumber);
			errorMessage.append("Country is mandatory ");
		}

		//CITY 
		String city=data[7];
		if(!StringUtils.isEmpty(city))
		{
			partnerMasterT.setCity(city);
		}
		//				else
		//				{
		//					error.setRowNumber(rowNumber);
		//					errorMessage.append("City is mandatory ");
		//				}


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

		try {
			partnerService.validateInactiveIndicators(partnerMasterT);
		} catch(DestinationException e) {
			error.setRowNumber(rowNumber);
			errorMessage.append(e.getMessage());
		}

		//HQ_PARTNER_LINK_NAME

		if(StringUtils.isEmpty(errorMessage.toString())) {
			String hqPartnerLinkName = data[8];
			if(!StringUtils.isEmpty(hqPartnerLinkName))	{
				mapOfPartnerAndHqLink.put(partnerName, hqPartnerLinkName);
				mapOfPartnerAndRowNumer.put(partnerName, rowNumber);
				childList.add(partnerMasterT);
			} else {
				parentList.add(partnerMasterT);
			}
		}

		error.setMessage(errorMessage.toString());
		return error;
	}

	/**
	 * This method is used to validate partner data for update operation
	 * @param data
	 * @param userId
	 * @param partner
	 * @param childList
	 * @param parentList
	 * @param mapOfPartnerAndHqLink 
	 * @param mapOfPartnerAndRowNumer 
	 * @return
	 * @throws Exception
	 */
	public UploadServiceErrorDetailsDTO validatePartnerDataUpdate(
			String[] data, String userId, List<PartnerMasterT> childList,List<PartnerMasterT> parentList, Map<String, String> mapOfPartnerAndHqLink, Map<String, Integer> mapOfPartnerAndRowNumer) throws Exception 
	{
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		StringBuffer errorMessage = new StringBuffer();
		PartnerMasterT partner = new PartnerMasterT();
		PartnerMasterT partnerMasterT = null;
		String partnerId=data[2];

		int rowNumber = Integer.parseInt(data[0]) + 1;
		if (!StringUtils.isEmpty(partnerId)) {

			partnerMasterT = partnerRepository.findByPartnerId(partnerId);
			if(partnerMasterT==null) {
				error.setRowNumber(rowNumber);
				errorMessage.append("Invalid Partner Id");
			}
		} else {
			error.setRowNumber(rowNumber);
			errorMessage.append("Partner id is mandatory");
		}
      
		String partnername = data[3];
		String active = data[16];
		if((partnerMasterT != null && partnerMasterT.isActive() )|| StringUtils.equalsIgnoreCase(active, "true")) {
			// PARTNER_NAME 
			if(!StringUtils.isEmpty(partnername)) {
				partner.setPartnerName(partnername);
			} else {
				error.setRowNumber(rowNumber);
				errorMessage.append("Partner name is mandatory; ");
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
				errorMessage.append("Group partner name is mandatory; ");
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
					errorMessage.append("Geography is invalid ");
				}

			}
			else
			{
				error.setRowNumber(rowNumber);
				errorMessage.append("Geography is mandatory; ");
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
					errorMessage.append("Country is invalid ");
				}
			}
			else
			{
				error.setRowNumber(rowNumber);
				errorMessage.append("Country is mandatory ");
			}

			//CITY 
			String city=data[7];
			if(!StringUtils.isEmpty(city))
			{
				partner.setCity(city);
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
			if(!StringUtils.isEmpty(active))
			{
				boolean activeFlag=false;
				if(active.equalsIgnoreCase("true"))
				{
					activeFlag=true;
				}

				partner.setActive(activeFlag);
			}

			// MODIFIED_BY
			partner.setModifiedBy(userId);

			//check for inactive records and log 
			try {
				partnerService.validateInactiveIndicators(partner);
			} catch(DestinationException e) {
				error.setRowNumber(rowNumber);
				errorMessage.append(e.getMessage());
			}
			
			if(StringUtils.isEmpty(errorMessage.toString())) {
				
				setPartnerFields(partner, partnerMasterT);
				
				String hqPartnerLinkName = data[8];
				if(!StringUtils.isEmpty(hqPartnerLinkName))	{
					mapOfPartnerAndHqLink.put(partnername, hqPartnerLinkName);
					mapOfPartnerAndRowNumer.put(partnername, rowNumber);
					childList.add(partnerMasterT);
				} else {
					parentList.add(partnerMasterT);
					partner.setHqPartnerLinkId(null);
				}
			}
		} else {
			error.setRowNumber(rowNumber);
			errorMessage.append("Partner is inactive.");
		}
		error.setMessage(errorMessage.toString());
		return error;

	}
	
	private void setPartnerFields(PartnerMasterT partner,
			PartnerMasterT partnerMasterT) {
		partnerMasterT.setPartnerName(partner.getPartnerName());
		partnerMasterT.setGroupPartnerName(partner.getGroupPartnerName());
		partnerMasterT.setGeography(partner.getGeography());
		partnerMasterT.setCountry(partner.getCountry());
		partnerMasterT.setCity(partner.getCity());
		partnerMasterT.setCorporateHqAddress(partner.getCorporateHqAddress());
		partnerMasterT.setWebsite(partner.getWebsite());
		partnerMasterT.setFacebook(partner.getFacebook());
		partnerMasterT.setText1(partner.getText1());
		partnerMasterT.setText2(partner.getText2());
		partnerMasterT.setText3(partner.getText3());
		partnerMasterT.setNotes(partner.getNotes());
		partnerMasterT.setActive(partner.isActive());
		partnerMasterT.setModifiedBy(partner.getModifiedBy());
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
		StringBuffer errorMessage = new StringBuffer();
		
		if (StringUtils.isEmpty(partnerId)) {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMessage.append("Partner Id is mandatory ");
		} else {
			PartnerMasterT partnerMasterT = partnerRepository.findByPartnerId(partnerId);
			
			if (partnerMasterT== null) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				errorMessage.append("Invalid Partner Id ");
			}
			else
			{
				//ACTIVE
				partner.setActive(false);
			}
		}
		error.setMessage(errorMessage.toString());
		return error;
	}
	
}
