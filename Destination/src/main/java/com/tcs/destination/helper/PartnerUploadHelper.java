package com.tcs.destination.helper;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.ConnectTypeMappingT;
import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.bean.OfferingMappingT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.ConnectTypeRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.OfferingRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.data.repository.TimezoneMappingRepository;
import com.tcs.destination.data.repository.UserRepository;
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
	
	
	
	
	public UploadServiceErrorDetailsDTO validatePartnerData(String[] data, String userId, PartnerMasterT partnerMasterT) throws Exception 
	{
		
			
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		
		        // PARTNER_NAME 
				String partnername = data[3];
				if(!StringUtils.isEmpty(partnername))
				{
					
					partnerMasterT.setPartnerName(partnername);
					//List<PartnerMasterT> partners = partnerRepository.findByPartnerName(partnername);
				}
				else
				{
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
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
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
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
				
				// CREATED_MODIFIED_BY
				partnerMasterT.setCreatedModifiedBy(userId);
					
				//DOCUMENTS_ATTACHED
				partnerMasterT.setDocumentsAttached(Constants.NO);

		return error;
	}

	public UploadServiceErrorDetailsDTO validatePartnerDataUpdate(
			String[] data, String userId, PartnerMasterT partner) throws Exception 
	{
		// TODO Auto-generated method stub
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
	

		String partnername = data[3];
	
		if (!StringUtils.isEmpty(partnername)) {

			List<PartnerMasterT> partners = partnerRepository.findByPartnerName(partnername);

			if (!partners.isEmpty()) {
				partner.setPartnerId(partners.get(0)
						.getPartnerId());
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("Invalid Partner Name; ");
			}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Partner Name Is Mandatory; ");
		}
		
		// GEOGRAPHY 
		String geography = data[4];
		if(!StringUtils.isEmpty(geography))
		{
			GeographyMappingT geographyMappingT=new GeographyMappingT();
			geographyMappingT.setGeography(geography);
			partner.setGeographyMappingT(geographyMappingT);
		}
		else{
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
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
		partner.setCreatedModifiedBy(userId);
			
		//DOCUMENTS_ATTACHED
		partner.setDocumentsAttached(Constants.NO);
		
		return error;

	}
	
	public UploadServiceErrorDetailsDTO validatePartnerId(String[] data,
			PartnerMasterT partner) {
		// TODO Auto-generated method stub
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		String partnerId = data[2];

		if (StringUtils.isEmpty(partnerId)) {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Partner Id is mandatory ");
		} else {
			partner = partnerRepository.findByPartnerId(partnerId);
			if (partner.getPartnerId() == null) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("Invalid Partner Id ");
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
