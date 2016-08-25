package com.tcs.destination.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.PartnerSubSpMappingT;
import com.tcs.destination.bean.PartnerSubspProductMappingT;
import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.PartnerSubSpMappingTRepository;
import com.tcs.destination.data.repository.PartnerSubSpProductMappingTRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.service.PartnerService;
import com.tcs.destination.utils.StringUtils;

@Component("partnerSubSpUploadHelper")
public class PartnerSubSpUploadHelper {
	
	private static final Logger logger = LoggerFactory
			.getLogger(PartnerSubSpUploadHelper.class);

	@Autowired
	private PartnerRepository partnerRepository;

	@Autowired
	private SubSpRepository subSpRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PartnerService partnerService;

	@Autowired
	PartnerSubSpMappingTRepository partnerSubSpMappingTRepository;

	@Autowired
	PartnerSubSpProductMappingTRepository partnerSubSpProductMappingTRepository;
	
	private Map<String, PartnerMasterT> mapOfPartnerT = null;

	/**
	 * This method is used to validate partner subsp data for add
	 * 
	 * @param data
	 * @param userId
	 * @param partnerSubSpMappingT
	 * @return
	 * @throws Exception
	 */

	public UploadServiceErrorDetailsDTO validatePartnerSubSpData(String[] data,
			String userId, PartnerSubSpMappingT partnerSubSpMappingT)
					throws Exception {

		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		StringBuffer sb = new StringBuffer();
		// PARTNER_NAME
		String partnerName = data[3];
		int rowNumber = Integer.parseInt(data[0]) + 1;
		if (!StringUtils.isEmpty(partnerName)) {
			PartnerMasterT partner = partnerRepository
					.findPartnerByName(partnerName);
			if (partner != null) {
				partnerSubSpMappingT.setPartnerId(partner.getPartnerId());
			} else {
				error.setRowNumber(rowNumber);
				sb.append("Partner name is not found ");
			}

		} else {
			error.setRowNumber(rowNumber);
			sb.append("Partner name is mandatory; ");
		}

		// SUBSP
		String subSp = data[4];
		if (!StringUtils.isEmpty(subSp)) {
			SubSpMappingT subSpMappingT = subSpRepository.findBySubSp(subSp);
			if (subSpMappingT != null) {
				partnerSubSpMappingT.setSubSpId(subSpMappingT.getSubSpId());
			} else {
				error.setRowNumber(rowNumber);
				sb.append("Subsp is not valid ");
			}
		} else {
			error.setRowNumber(rowNumber);
			sb.append("Subsp is mandatory; ");
		}

		// CREATED_BY
		partnerSubSpMappingT.setCreatedBy(userId);

		// MODIFIED_BY
		partnerSubSpMappingT.setModifiedBy(userId);
		
		error.setMessage(sb.toString());
		return error;
	}

	/**
	 * This method is used for partner subsp update
	 * 
	 * @param data
	 * @param userId
	 * @param partnerSubSpMappingT
	 * @return
	 * @throws Exception
	 */
	public UploadServiceErrorDetailsDTO validatePartnerSubSpDataUpdate(
			String[] data, String userId,
			PartnerSubSpMappingT partnerSubSpMappingT) throws Exception {
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();

		String partnerSubspMappingId = data[2];

		int rowNumber = Integer.parseInt(data[0]) + 1;
		if (!StringUtils.isEmpty(partnerSubspMappingId)) {

			PartnerSubSpMappingT partnerSubSp = partnerSubSpMappingTRepository
					.findByPartnerSubspMappingId(partnerSubspMappingId);
			if (partnerSubSp == null) {
				error.setRowNumber(rowNumber);
				error.setMessage("Invalid Partner SubSp Mapping Id");
			}
		} else {
			error.setRowNumber(rowNumber);
			error.setMessage("Partner SubSp Mapping Id is mandatory");
		}
		
		// PARTNER_NAME
		String partnerName = data[3];
		if (!StringUtils.isEmpty(partnerName)) {
			PartnerMasterT partner = partnerRepository
					.findPartnerByName(partnerName);
			if (partner != null) {
				partnerSubSpMappingT.setPartnerId(partner.getPartnerId());
			} else {
				error.setRowNumber(rowNumber);
				error.setMessage("Partner name is not found ");
			}

		} else {
			error.setRowNumber(rowNumber);
			error.setMessage("Partner name is mandatory; ");
		}

		// SUBSP
		String subSp = data[4];
		if (!StringUtils.isEmpty(subSp)) {
			SubSpMappingT subSpMappingT = subSpRepository.findBySubSp(subSp);
			if (subSpMappingT != null) {
				partnerSubSpMappingT.setSubSpId(subSpMappingT.getSubSpId());
			} else {
				error.setRowNumber(rowNumber);
				error.setMessage("Subsp is not valid ");
			}
		} else {
			error.setRowNumber(rowNumber);
			error.setMessage("Subsp is mandatory; ");
		}

		// CREATED_BY
		partnerSubSpMappingT.setCreatedBy(userId);

		// MODIFIED_BY
		partnerSubSpMappingT.setModifiedBy(userId);

		return error;

	}

	/**
	 * This method is used to validate partner subsp mapping id for delete
	 * operation
	 * 
	 * @param data
	 * @param partner
	 * @return
	 */
	public UploadServiceErrorDetailsDTO validatePartnerSubspId(String[] data,PartnerSubSpMappingT partner,boolean deleteFlag) {
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		String partnerSubspMappingId = data[2];
		List<PartnerSubspProductMappingT> partnerSubSpProductMappingTs = new ArrayList<PartnerSubspProductMappingT>();
		int rowNumber = Integer.parseInt(data[0]) + 1;

		StringBuffer sb = new StringBuffer();
		if (StringUtils.isEmpty(partnerSubspMappingId)) {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			sb.append("Partner SubSp Mapping Id is mandatory ;");
		} else {
			PartnerSubSpMappingT partnerSubSp = partnerSubSpMappingTRepository
					.findByPartnerSubspMappingId(partnerSubspMappingId);
			if (partnerSubSp == null) {
				error.setRowNumber(rowNumber);
				sb.append("Invalid Partner SubSp Mapping Id;");
			} else {

				List<PartnerSubspProductMappingT> partnerSubSpProductList = partnerSubSpProductMappingTRepository
						.findByPartnerSubspMappingId(partnerSubSp
								.getPartnerSubspMappingId());
				if(partnerSubSpProductList!=null)
				{
					for(PartnerSubspProductMappingT partnerSubspProductMappingT :partnerSubSpProductList)
					{
						partnerSubSpProductMappingTs.add(partnerSubspProductMappingT);
					}
					partner.setPartnerSubspProductMappingTs(partnerSubSpProductMappingTs);
					deleteFlag=true;
				}
				else
				{
					logger.debug("No partner subsp product records found!");
				}

				partner.setPartnerSubspMappingId(partnerSubspMappingId);

			}


		}
		error.setMessage(sb.toString());
		return error;
	}
	
	/**
	 * Method to return partner details as map
	 * @return  Map<String, PartnerMasterT>
	 */
	public Map<String, PartnerMasterT> getPartnerMasterT() {
		List<PartnerMasterT> listOfPartnerMasterT = null;
		listOfPartnerMasterT = (List<PartnerMasterT>) partnerRepository.findAll();
		Map<String, PartnerMasterT> partnerMap = new HashMap<String, PartnerMasterT>();
		for (PartnerMasterT partnerT : listOfPartnerMasterT) {
			partnerMap.put(partnerT.getPartnerName(), partnerT);
		}
		return partnerMap;
	}
}