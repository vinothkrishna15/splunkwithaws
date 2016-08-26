package com.tcs.destination.helper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.PartnerSubSpMappingT;
import com.tcs.destination.bean.PartnerSubspProductMappingT;
import com.tcs.destination.bean.ProductMasterT;
import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.PartnerSubSpMappingTRepository;
import com.tcs.destination.data.repository.PartnerSubSpProductMappingTRepository;
import com.tcs.destination.data.repository.ProductRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.service.PartnerService;
import com.tcs.destination.utils.StringUtils;

@Component("partnerSubSpProductUploadHelper")
public class PartnerSubSpProductUploadHelper {
	
    @Autowired
	private PartnerRepository partnerRepository;
	
	@Autowired
	private SubSpRepository subSpRepository;
	
    @Autowired
	private  UserRepository userRepository;
	
	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private PartnerService partnerService;
	
	@Autowired
	private PartnerSubSpProductMappingTRepository partnerSubSpProductMappingTRepository;
	
	@Autowired
	PartnerSubSpMappingTRepository partnerSubSpMappingTRepository;
	
	
	
	/**
	 * This method is used to validate partner subsp product data for add
	 * @param data
	 * @param userId
	 * @param partnerSubspProductMappingT
	 * @return
	 * @throws Exception
	 */
	public UploadServiceErrorDetailsDTO validatePartnerSubspProductData(String[] data, String userId, PartnerSubspProductMappingT partnerSubspProductMappingT) throws Exception 
	{

		StringBuffer str = new StringBuffer();
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		List<PartnerSubSpMappingT> partnerSubsp=new ArrayList<PartnerSubSpMappingT>();

		// PARTNER_NAME 
		String partnerName = data[3];
		int rowNumber = Integer.parseInt(data[0]) + 1;
		if(!StringUtils.isEmpty(partnerName))
		{
			PartnerMasterT partner=partnerRepository.findPartnerByName(partnerName);
			if(partner == null)
			{
				error.setRowNumber(rowNumber);
				str.append("Partner name is not found ;");
			}
			else
			{
				//SUBSP
				String subSp = data[4];
				if(!StringUtils.isEmpty(subSp))	{
					SubSpMappingT subSpMappingT = subSpRepository.findBySubSp(subSp);
					if(subSpMappingT != null)	{
						partnerSubsp=partnerSubSpMappingTRepository.findByPartnerIdAndSubSpId(partner.getPartnerId(), subSpMappingT.getSubSpId());
						if(partnerSubsp != null) {
							for(PartnerSubSpMappingT partnerSubSpMapping:partnerSubsp) {
								partnerSubspProductMappingT.setPartnerSubspMappingId(partnerSubSpMapping.getPartnerSubspMappingId());
							}
						} else {
							error.setRowNumber(rowNumber);
							str.append("Partner Name and SubSp Combination is not found; ");
						}
					} else {
						error.setRowNumber(rowNumber);
						str.append("Invalid SubSp;");
					}
				} else {
					error.setRowNumber(rowNumber);
					str.append("SubSp is mandatory;");
				}
			}
		} else {
			error.setRowNumber(rowNumber);
			str.append("Partner name is mandatory; ");
		}

		//PRODUCT 
		String product=data[5];
		if(!StringUtils.isEmpty(product)) {
			ProductMasterT productMasterT=productRepository.findByProductName(product);
			if(productMasterT!=null) {
				partnerSubspProductMappingT.setProductId(productMasterT.getProductId());
			} else {
				error.setRowNumber(rowNumber);
				str.append("Product is not valid;");
			}
		} else {
			error.setRowNumber(rowNumber);
			str.append("Product should not be empty;");
		}

		// CREATED_BY
		partnerSubspProductMappingT.setCreatedBy(userId);

		// MODIFIED_BY
		partnerSubspProductMappingT.setModifiedBy(userId);

		error.setMessage(str.toString());
		return error;
	}

	/**
	 * This method is used to validate partner subsp product data for update
	 * @param data
	 * @param userId
	 * @param partnerSubspProductMappingT
	 * @return
	 * @throws Exception
	 */
	public UploadServiceErrorDetailsDTO validatePartnerSubspProductDataUpdate(
			String[] data, String userId, PartnerSubspProductMappingT partnerSubspProductMappingT) throws Exception 
	{
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		List<PartnerSubSpMappingT> partnerSubsp=new ArrayList<PartnerSubSpMappingT>();

		String partnerSubSpProductId=data[2];
	
		int rowNumber = Integer.parseInt(data[0]) + 1;
		if (!StringUtils.isEmpty(partnerSubSpProductId)) {

			PartnerSubspProductMappingT partnerSubspProductT=partnerSubSpProductMappingTRepository.findByPartnerSubspProductMappingId(partnerSubSpProductId);
		    if(partnerSubspProductT==null)
		    {
			error.setRowNumber(rowNumber);
			error.setMessage("Invalid PartnerSubSpProductId");
			
		     }
		}
		else
		{
			error.setRowNumber(rowNumber);
			error.setMessage("PartnerSubSpProductId is mandatory");
		}
		
		
		
		

		// PARTNER_NAME 
		String partnerName = data[3];
		if(!StringUtils.isEmpty(partnerName))
		{
			
			PartnerMasterT partner=partnerRepository.findPartnerByName(partnerName);
			if(partner!=null)
			{
			 partnerSubsp=partnerSubSpMappingTRepository.findByPartnerId(partner.getPartnerId());
			}
			else
			{
				error.setRowNumber(rowNumber);
				error.setMessage("Partner name is not found ");
			}
			   //SUBSP
				String subSp = data[4];
				if(!StringUtils.isEmpty(subSp))
				{
					SubSpMappingT subSpMappingT=subSpRepository.findBySubSp(subSp);
					if(subSpMappingT!=null)
					{
						List<PartnerSubSpMappingT> partnerSubSpMappingTs=partnerSubSpMappingTRepository.findBySubSpId(subSpMappingT.getSubSpId());
						if((partnerSubSpMappingTs!=null)&&((partnerSubsp!=null)))
						{
							for(PartnerSubSpMappingT partnerSubSpMapping:partnerSubsp)
							{
						     partnerSubspProductMappingT.setPartnerSubspMappingId(partnerSubSpMapping.getPartnerSubspMappingId());
							}
						}						
					
						else
						{
							error.setRowNumber(rowNumber);
							error.setMessage("Partner Name and SubSp Combination is not found ");
						}

					}
					else
					{
						error.setRowNumber(rowNumber);
						error.setMessage("Invalid SubSp");
						
					}
		           
				}
				else
				{
					error.setRowNumber(rowNumber);
					error.setMessage("SubSp is mandatory");
				}
			}
		else
		{
			error.setRowNumber(rowNumber);
			error.setMessage("Partner name is mandatory; ");
		}
		
		
		//PRODUCT 
		String product=data[5];
		if(!StringUtils.isEmpty(product))
		{
		 ProductMasterT productMasterT=productRepository.findByProductName(product);
		 if(productMasterT!=null)
		 {
			 partnerSubspProductMappingT.setProductId(productMasterT.getProductId());
		 }
		 else
		 {
			 error.setRowNumber(rowNumber);
			 error.setMessage("Product is not valid");
		 }
		}
		else
		{
			 error.setRowNumber(rowNumber);
			 error.setMessage("Product is mandatory");
		}
		
		// CREATED_BY
		partnerSubspProductMappingT.setCreatedBy(userId);
		
		// MODIFIED_BY
		partnerSubspProductMappingT.setModifiedBy(userId);
		
		return error;

	}
	
	/**
	 * This method is used to validate partnerSubspProductMappingT data for delete
	 * @param data
	 * @param partnerSubspProductMappingT
	 * @return
	 */
	public UploadServiceErrorDetailsDTO validatePartnerSubSpProductId(String[] data,
			PartnerSubspProductMappingT partnerSubspProductMappingT) {
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		String partnerSubspProductMappingId = data[2];

		if (StringUtils.isEmpty(partnerSubspProductMappingId)) {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("PartnerSubspProductMapping Id is mandatory ");
		} else {
			PartnerSubspProductMappingT partnerSubspProductT = partnerSubSpProductMappingTRepository.findByPartnerSubspProductMappingId(partnerSubspProductMappingId);
			if (partnerSubspProductT != null) 
			{
				partnerSubspProductMappingT.setPartnerSubspProductMappingId(partnerSubspProductMappingId);
				
			}
		}

		return error;
	}
		
}
