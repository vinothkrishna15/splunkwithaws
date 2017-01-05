package com.tcs.destination.helper;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.ActualRevenuesDataT;
import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.bean.IouCustomerMappingT;
import com.tcs.destination.bean.RevenueCustomerMappingT;
import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.ActualRevenuesDataTRepository;
import com.tcs.destination.data.repository.CustomerIOUMappingRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.data.repository.RevenueCustomerMappingTRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.PropertyUtil;
import com.tcs.destination.utils.StringUtils;

@Component("revenueUploadHelper")
public class RevenueUploadHelper {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	GeographyRepository geographyRepository;

	@Autowired
	RevenueCustomerMappingTRepository revenueCustomerMappingTRepository;

	@Autowired
	CustomerIOUMappingRepository iouCustomerMappingRepository;

	@Autowired
	SubSpRepository subSpRepository;

	@Autowired
	ActualRevenuesDataTRepository actualRevenuesDataTRepository;

	@Autowired
	CommonHelper commonHelper;

	Map<String, String> mapOfCustomerNamesT = null;
	Map<String, SubSpMappingT> mapOfSubSpMappingT = null;
	Map<String, IouCustomerMappingT> mapOfIouCustomerMappingT = null;
	Map<String, GeographyMappingT> mapOfGeographyMappingT = null;
	Map<String, IouCustomerMappingT> mapOfIouMappingT = null;

	private static final Logger logger = LoggerFactory
			.getLogger(RevenueUploadHelper.class);

	public UploadServiceErrorDetailsDTO validateRevenueAdd(String[] data,
			String userId, ActualRevenuesDataT actualRevenueT) {
		
		
		
		// Get List of IOU from DB for validating the IOU which comes from the
		// sheet
		mapOfIouCustomerMappingT = mapOfIouCustomerMappingT != null ? mapOfIouCustomerMappingT
				: commonHelper.getIouCustomerMappingT();

		// Get List of actual subsp from DB for validating the subsp which comes from the
		// sheet
		mapOfSubSpMappingT = mapOfSubSpMappingT != null ? mapOfSubSpMappingT
				: commonHelper.getSubSpMappingT(false);
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();

		StringBuffer errorMsg = new StringBuffer("");
		
		
		//String quarter = data[6];
		String month = null;
		if(data[4]!=null){
		 month = data[4].trim();
		}
		
		String financeCustomerName = data[11];
		String customerGeography = data[9];
		String financeIou = data[12];
		String revenueAmount = data[7];
		String clientCountryName = data[8];
		String subSp = data[10];
		String category = data[13];
		
		if(StringUtils.isEmpty(category)) {
			error.setRowNumber(Integer.parseInt(data[0])+1);
			errorMsg.append(" Category is empty ");
		} else {
			actualRevenueT.setCategory(category);
		}

					if (!StringUtils.isEmpty(month)) {

						try {
							String[] strArr = DateUtils.formatUploadDateData(
									month,
									PropertyUtil.getProperty("upload.month.db.format"),
									PropertyUtil.getProperty("upload.month.format"));
							actualRevenueT.setMonth(strArr[0].toUpperCase());
							actualRevenueT.setQuarter(strArr[1]);
							actualRevenueT.setFinancialYear(strArr[2]);

						} catch (ParseException e) {
							error.setRowNumber(Integer.parseInt(data[0])+1);
							errorMsg.append(" Invalid month format. ");
						}

					} else {
						error.setRowNumber(Integer.parseInt(data[0])+1);
						errorMsg.append(" Month is empty ");
					}


		// REVENUE AMOUNT
		if (!StringUtils.isEmpty(revenueAmount)) {
			try {
				BigDecimal target = new BigDecimal(revenueAmount);
				actualRevenueT.setRevenue((target));
			} catch (Exception e){
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				errorMsg.append(" invalid revenueAmount; ");
			}
		} else {

			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" revenueAmount Is Mandatory; ");
		}

		// CLIENT COUNTRY NAME
		if (!StringUtils.isEmpty(clientCountryName)) {
			actualRevenueT.setClientCountry(clientCountryName);
		} else {

			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" clientCountryName Is Mandatory; ");

		}

		// SUB_SP
		if (!StringUtils.isEmpty(subSp)) {
			if (mapOfSubSpMappingT.containsKey(subSp)) {
				actualRevenueT.setSubSp(subSp);
			} else {

				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				errorMsg.append(" invalid subsp; ");
			}
		} else {

			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" subsp is mandatory; ");

		}
		
		// CUSTOMER GEOGRAPHY
		if(StringUtils.isEmpty(customerGeography)) {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" geography is mandatory; ");
		}

		// END CUSTOMER NAME
		if (StringUtils.isEmpty(financeCustomerName)) {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" Customer Name is Empty ");
		}

		// IOU
		if (StringUtils.isEmpty(financeIou)) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				errorMsg.append(" Finance Iou is Empty ");
		}
		
        
		//check if there are no errors
		if(StringUtils.isEmpty(errorMsg.toString())){
		List<RevenueCustomerMappingT> revenueCustomerMap = null;
		

		// to find whether finance_geography, finance_iou, finance_customer_name
		// and (composite key) has foreign key existence in
		// revenue_customer_mapping_t
		revenueCustomerMap = revenueCustomerMappingTRepository
				.findByFinanceCustomerNameAndCustomerGeographyAndFinanceIouAndActive(financeCustomerName, customerGeography,
						financeIou,true);
		logger.info("revenueCustomerData.size() "
				+ Integer.toString(revenueCustomerMap.size()));
		if ((!revenueCustomerMap.isEmpty())
				&& (revenueCustomerMap.size() == 1)) {
			
			RevenueCustomerMappingT revenueCustomerMappingT=revenueCustomerMap.get(0);
			Long revenueCustomerMapId=revenueCustomerMappingT.getRevenueCustomerMapId();

			            
						
						if(revenueCustomerMapId!=null)
						{
							actualRevenueT.setRevenueCustomerMapId(revenueCustomerMapId);
						}

			} 
			
		else {

			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" either of these values i.e finance_geography,finance_iou,finance_customer_name is invalid/inactive ");
			//error.setMessage("either of these values i.e finance_geography,finance_iou,finance_customer_name is empty/inactive");
		}
		}
		if(!StringUtils.isEmpty(errorMsg.toString())){
			error.setMessage(errorMsg.toString());
		}
		
		return error;
	}

	public UploadServiceErrorDetailsDTO validateRevenueUpdate(String[] data,
			String userId, ActualRevenuesDataT actualRevenueT) {
		System.out.println("into validateRevenueUpdate ");
		// Get List of IOU from DB for validating the IOU which comes from the
		// sheet
		mapOfIouCustomerMappingT = mapOfIouCustomerMappingT != null ? mapOfIouCustomerMappingT
				: commonHelper.getIouCustomerMappingT();

		// Get List of actual sub sp from DB for validating the sub sp which comes from the
		// sheet
		mapOfSubSpMappingT = mapOfSubSpMappingT != null ? mapOfSubSpMappingT
				: commonHelper.getSubSpMappingT(false);
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();

		String quarter = data[6];
		String month = data[5];
		String financeCustomerName = data[12];
		String customerGeography = data[10];
		String financeIou = data[13];
		String financialYear = data[7];
		String revenueAmount = data[8];
		String clientCountryName = data[9];
		String subSp = data[11];
		// QUARTER

		if (!StringUtils.isEmpty(quarter)) {
			actualRevenueT.setQuarter(quarter);
		}

		// MONTH
		if (!StringUtils.isEmpty(month)) {
			actualRevenueT.setMonth(month);
		}

		// FINANCIAL YEAR
		if (!StringUtils.isEmpty(financialYear)) {
			actualRevenueT.setFinancialYear(financialYear);
		}

		// REVENUE AMOUNT
		if (!StringUtils.isEmpty(revenueAmount)) {
			BigDecimal target = new BigDecimal(revenueAmount);
			actualRevenueT.setRevenue((target));
		}

		// CLIENT COUNTRY NAME
		if (!StringUtils.isEmpty(clientCountryName)) {
			actualRevenueT.setClientCountry(clientCountryName);
		}

		// SUB_SP
		if (!StringUtils.isEmpty(subSp)) {
			if (mapOfSubSpMappingT.containsKey(subSp)) {
				actualRevenueT.setSubSp(subSp);
			} else {

				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("sub sp not found in database");
			}
		}

		List<RevenueCustomerMappingT> revenueCustomerData = null;

		// to find whether finance_geography, finance_iou, finance_customer_name
		// and (composite key) has foreign key existence in
		// revenue_customer_mapping_t
		revenueCustomerData = revenueCustomerMappingTRepository
				.findByFinanceCustomerNameAndCustomerGeographyAndFinanceIouAndActive(financeCustomerName, customerGeography,
						financeIou, true);
		logger.debug("size " + revenueCustomerData.size());
		if ((!revenueCustomerData.isEmpty())
				&& (revenueCustomerData.size() == 1)) {
			
			RevenueCustomerMappingT revenueCustomerMappingT=revenueCustomerData.get(0);
			Long revenueCustomerMapId=revenueCustomerMappingT.getRevenueCustomerMapId();

			            // CUSTOMER GEOGRAPHY
						if(StringUtils.isEmpty(customerGeography)) {
							error.setRowNumber(Integer.parseInt(data[0]) + 1);
							error.setMessage("Customer Geography is Empty");
			                //revenueT.setFinanceGeography(customerGeography);
						}

						// END CUSTOMER NAME
						if (StringUtils.isEmpty(financeCustomerName)) {
							error.setRowNumber(Integer.parseInt(data[0]) + 1);
							error.setMessage("Customer Name is Empty");
							//revenueCustomerMappingT.setFinanceCustomerName(financeCustomerName);
							//revenueT.setFinanceCustomerName(financeCustomerName);
						}

						// IOU
						if (StringUtils.isEmpty(financeIou)) {
						//	if (mapOfIouCustomerMappingT.containsKey(financeIou)) {
								error.setRowNumber(Integer.parseInt(data[0]) + 1);
								error.setMessage("Finance Iou is Empty");
								//revenueCustomerMappingT.setFinanceIou(financeIou);
								//revenueT.setFinanceIou(financeIou);
						//	} 
						}
						if(revenueCustomerMapId!=null)
						{
							actualRevenueT.setRevenueCustomerMapId(revenueCustomerMapId);
						}

		} else {

			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("the combination of these values i.e CLIENT GEOGRAPHY FINAL,IOU and END CUSTOMER NAME is wrong");
		}
		List<ActualRevenuesDataT> actualRevenue = actualRevenuesDataTRepository
				.checkRevenueDataMappingPK(quarter, month, financialYear,
						clientCountryName, customerGeography, subSp,
						financeIou, financeCustomerName);
		if (actualRevenue.isEmpty()) {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("unable to delete, requested entry not found in repository");
		} else {
			actualRevenueT.setActualRevenuesDataId(actualRevenue.get(0)
					.getActualRevenuesDataId());
			logger.debug("actual revenue data id to be updated: "
					+ actualRevenue.get(0).getActualRevenuesDataId());
		}

		return error;
	}

	public UploadServiceErrorDetailsDTO validateRevenueDelete(String[] data,
			String userId, ActualRevenuesDataT revenueT) {
		mapOfIouCustomerMappingT = mapOfIouCustomerMappingT != null ? mapOfIouCustomerMappingT
				: commonHelper.getIouCustomerMappingT();

		// Get List of actual sub sp from DB for validating the sub sp which comes from the
		// sheet
		mapOfSubSpMappingT = mapOfSubSpMappingT != null ? mapOfSubSpMappingT
				: commonHelper.getSubSpMappingT(false);
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();

		String quarter = data[6];
		String month = data[5];
		String financeCustomerName = data[12];
		String customerGeography = data[10];
		String financeIou = data[13];
		String financialYear = data[7];
		String revenueAmount = data[8];
		String clientCountryName = data[9];
		String subSp = data[11];
		// QUARTER

		if (!StringUtils.isEmpty(quarter)) {
			revenueT.setQuarter(quarter);
		}

		// MONTH
		if (!StringUtils.isEmpty(month)) {
			revenueT.setMonth(month);
		}

		// FINANCIAL YEAR
		if (!StringUtils.isEmpty(financialYear)) {
			revenueT.setFinancialYear(financialYear);
		}

		// REVENUE AMOUNT
		if (!StringUtils.isEmpty(revenueAmount)) {
			BigDecimal target = new BigDecimal(revenueAmount);
			revenueT.setRevenue((target));
		}

		// CLIENT COUNTRY NAME
		if (!StringUtils.isEmpty(clientCountryName)) {
			revenueT.setClientCountry(clientCountryName);
		}

		// SUB_SP
		if (!StringUtils.isEmpty(subSp)) {
			if (mapOfSubSpMappingT.containsKey(subSp)) {
				revenueT.setSubSp(subSp);
			} else {

				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("sub sp not found in database");
			}
		}

		List<RevenueCustomerMappingT> revenueCustomerData = null;

		// to find whether finance_geography, finance_iou, finance_customer_name
		// and (composite key) has foreign key existence in
		// revenue_customer_mapping_t
		revenueCustomerData = revenueCustomerMappingTRepository
				.findByFinanceCustomerNameAndCustomerGeographyAndFinanceIou(financeCustomerName, customerGeography,
						financeIou);

		if ((!revenueCustomerData.isEmpty())
				&& (revenueCustomerData.size() == 1)) {
			
			RevenueCustomerMappingT revenueCustomerMappingT=revenueCustomerData.get(0);
			Long revenueCustomerMapId=revenueCustomerMappingT.getRevenueCustomerMapId();
			
			// CUSTOMER GEOGRAPHY
			if(StringUtils.isEmpty(customerGeography)) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("Customer Geography is Empty");
                //revenueT.setFinanceGeography(customerGeography);
			}

			// END CUSTOMER NAME
			if (StringUtils.isEmpty(financeCustomerName)) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("Customer Name is Empty");
				//revenueCustomerMappingT.setFinanceCustomerName(financeCustomerName);
				//revenueT.setFinanceCustomerName(financeCustomerName);
			}

			// IOU
			if (StringUtils.isEmpty(financeIou)) {
			//	if (mapOfIouCustomerMappingT.containsKey(financeIou)) {
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					error.setMessage("Finance Iou is Empty");
					//revenueCustomerMappingT.setFinanceIou(financeIou);
					//revenueT.setFinanceIou(financeIou);
			//	} 
			}
			if(revenueCustomerMapId!=null)
			{
				revenueT.setRevenueCustomerMapId(revenueCustomerMapId);
			}

		} else {

			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("the combination of these values i.e CLIENT GEOGRAPHY FINAL,IOU and END CUSTOMER NAME is wrong");
		}
		List<ActualRevenuesDataT> actualRevenue = actualRevenuesDataTRepository
				.checkRevenueDataMappingPK(quarter, month, financialYear,
						clientCountryName, customerGeography, subSp,
						financeIou, financeCustomerName);
		if (actualRevenue.isEmpty()) {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("unable to delete, requested entry not found in repository");
		} else {
			revenueT.setActualRevenuesDataId(actualRevenue.get(0)
					.getActualRevenuesDataId());
			logger.debug("actual revenue data id to be deleted: "
					+ actualRevenue.get(0).getActualRevenuesDataId());
		}

		return error;
	}

}
