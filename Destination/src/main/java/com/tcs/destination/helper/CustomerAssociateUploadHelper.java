package com.tcs.destination.helper;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.parboiled.common.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.AssociateT;
import com.tcs.destination.bean.CustomerAssociateT;
import com.tcs.destination.bean.RevenueCustomerMappingT;
import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.AssociateRepository;
import com.tcs.destination.data.repository.DeliveryCentreRepository;
import com.tcs.destination.data.repository.RevenueCustomerMappingTRepository;
import com.tcs.destination.data.repository.ServicePracticeRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.utils.Constants;

@Component("customerAssociateUploadHelper")
public class CustomerAssociateUploadHelper {
	
	@Autowired
	RevenueCustomerMappingTRepository revenueCustomerMappingTRepository;
	
	@Autowired
	ServicePracticeRepository servicePracticeRepository;
	
	@Autowired
	SubSpRepository subSpRepository;
	
	@Autowired
	AssociateRepository associateRepository;
	
	@Autowired
	DeliveryCentreRepository deliveryCentreRepository;
	
	private static final DateFormat dateFormat = new SimpleDateFormat(
			"MM/dd/yy");

	public UploadServiceErrorDetailsDTO validateCustomerAssociateData(
			String[] data, String userId, CustomerAssociateT customerAssociateT) throws ParseException {
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		StringBuffer errorMsg = new StringBuffer();
		int rowNumber = Integer.parseInt(data[0]) + 1;
		String associateId = data[1];
		String associateName = data[2];
		String financeGeo = data[3];
		String clientCountry = data[4];
		String sp = data[5];
		String subSp = data[6];
		String allocationCategory = data[7];
		String financeCustomerName = data[8];
		String financeIou = data[9];
		String employeeBaseCountry = data[11];
		String baseBranch = data[12];
		String deputeBranch = data[13];
		String employeeDeputeCountry = data[14];
		String experienceCategory = data[15];
		String nationality = data[16];
		String gender = data[17];
		String allocationStartDate = data[18];
		String allocationEndDate = data[19];
		String percentageAllocation = data[20];
		String workingSite = data[21];
		String associateType = data[22];
		String reportingDC = data[23];
		String subGroup = data[24];
		String stream = data[25];
		
		//Revenue Customer Map Id
		if(Constants.ALLOCATION_TYPE_WON.equals(allocationCategory) && StringUtils.isNotEmpty(financeCustomerName) && 
				StringUtils.isNotEmpty(financeIou) &&
				StringUtils.isNotEmpty(financeIou)) {
			List<RevenueCustomerMappingT> revenueCustomerMappingts = revenueCustomerMappingTRepository
					.findByFinanceCustomerNameAndCustomerGeographyAndFinanceIouAndActive(
							financeCustomerName, financeGeo, financeIou, true);
			if(CollectionUtils.isNotEmpty(revenueCustomerMappingts)) {
				Long revenueCustMapId = revenueCustomerMappingts.get(0).getRevenueCustomerMapId();
				customerAssociateT.setRevenueCustomerMapId(revenueCustMapId);
			} else {
				error.setRowNumber(rowNumber);
				errorMsg.append("Either of the finance customer name or finance Iou or finance "
						+ "geography is invalid");
			}
		}
		//Client Country
		if(StringUtils.isNotEmpty(clientCountry)) {
			customerAssociateT.setClientCountry(clientCountry);
		}
		
		//SP
		if(StringUtils.isNotEmpty(sp)) {
			customerAssociateT.setSp(sp);
		}
		
		//Sub Sp
		if(StringUtils.isNotEmpty(subSp)) {
			if(Constants.SP_DESS.equals(sp)) {
				SubSpMappingT subSpMappingT = subSpRepository.findByActualSubSp(subSp);
				if(subSpMappingT!=null) {
					customerAssociateT.setSubSp(subSp);
				} else {
					error.setRowNumber(rowNumber);
					errorMsg.append("Invalid SubSp "+subSp);
				}
			} else {
				customerAssociateT.setSubSp(subSp);
			}
		}
		
		//Allocation Category
		if(StringUtils.isNotEmpty(allocationCategory)) {
			customerAssociateT.setAllocationCategory(allocationCategory);
		}
		
		//Depute Branch
		if(StringUtils.isNotEmpty(deputeBranch)) {
			customerAssociateT.setDeputeBranch(deputeBranch);
		}
		//Depute Country
		if(StringUtils.isNotEmpty(employeeDeputeCountry)) {
			customerAssociateT.setDeputeCountry(employeeDeputeCountry);
		}
		
		//Experience Category
		if(StringUtils.isNotEmpty(experienceCategory)) {
			customerAssociateT.setExperienceCategory(experienceCategory);
		}
		
		//Nationality
		if(StringUtils.isNotEmpty(nationality)) {
			customerAssociateT.setNationality(nationality);
		}
		
		//Allocation Start Date
		if(StringUtils.isNotEmpty(allocationStartDate) && !Constants.NA.equals(allocationStartDate)) {
			customerAssociateT.setAllocationStartDate(dateFormat.parse(allocationStartDate));
		}
		
		//Allocation End Date
		if(StringUtils.isNotEmpty(allocationEndDate) && !Constants.NA.equals(allocationStartDate)) {
			customerAssociateT.setAllocationEndDate(dateFormat.parse(allocationEndDate));
		}
		
		//Percentage Allocation
		if(StringUtils.isNotEmpty(percentageAllocation) && !Constants.NA.equals(allocationStartDate)) {
			customerAssociateT.setAllocationPercentage(new BigDecimal(percentageAllocation));
		}
		
		//Working site
		if(StringUtils.isNotEmpty(workingSite)) {
			customerAssociateT.setWorkingSite(workingSite);
		}
		
		//Associate Type
		if(StringUtils.isNotEmpty(associateType)) {
			customerAssociateT.setAssociateType(associateType);
		}
		
		//Reporting DC
		if(StringUtils.isNotEmpty(reportingDC)) {
			customerAssociateT.setReportingDc(reportingDC);
		}
		
		//SubGroup
		if(StringUtils.isNotEmpty(subGroup)) {
			customerAssociateT.setSubGroup(subGroup);
		}
		
		//Stream
		if(StringUtils.isNotEmpty(stream)) {
			customerAssociateT.setStream(stream);
		}
		
		if(StringUtils.isEmpty(errorMsg.toString())) {
			if(associateId.contains(".0")) {
				associateId = associateId.substring(0, associateId.length()-2);
			}
			AssociateT associate = associateRepository.findOne(associateId);
			if(associate!=null) {
				customerAssociateT.setAssociateId(associateId);
			} else {
				AssociateT associateSaved = saveAssociate(
						associateId,associateName,baseBranch,employeeBaseCountry,gender);
				customerAssociateT.setAssociateId(associateSaved.getAssociateId());
			}
		} else {
			error.setMessage(errorMsg.toString());
		}
		
		return error;
	}

	private AssociateT saveAssociate(String associateId, String associateName,
			String baseBranch, String employeeBaseCountry, String gender) {
		AssociateT associateT = new AssociateT();
		associateT.setAssociateId(associateId);
		associateT.setAssociateName(associateName);
		associateT.setBaseBranch(baseBranch);
		associateT.setBaseCountry(employeeBaseCountry);
		associateT.setGender(gender);
		associateRepository.save(associateT);
		return associateT;
	}

}
