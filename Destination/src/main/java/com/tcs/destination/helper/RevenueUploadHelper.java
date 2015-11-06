package com.tcs.destination.helper;

import java.math.BigDecimal;
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

		// Get List of IOU from DB for validating the IOU which comes from the
		// sheet
		mapOfSubSpMappingT = mapOfSubSpMappingT != null ? mapOfSubSpMappingT
				: commonHelper.getSubSpMappingT();
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
		} else {

			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Quarter Is Mandatory; ");

		}

		// MONTH
		if (!StringUtils.isEmpty(month)) {
			actualRevenueT.setMonth(month);
		} else {

			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("GL MONTH Is Mandatory; ");

		}

		// FINANCIAL YEAR
		if (!StringUtils.isEmpty(financialYear)) {
			actualRevenueT.setFinancialYear(financialYear);
		} else {

			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("financialYear Is Mandatory; ");

		}

		// REVENUE AMOUNT
		if (!StringUtils.isEmpty(revenueAmount)) {
			BigDecimal target = new BigDecimal(revenueAmount);
			actualRevenueT.setRevenue((target));
		} else {

			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("revenueAmount Is Mandatory; ");

		}

		// CLIENT COUNTRY NAME
		if (!StringUtils.isEmpty(clientCountryName)) {
			actualRevenueT.setClientCountry(clientCountryName);
		} else {

			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("clientCountryName Is Mandatory; ");

		}

		// SUB_SP
		if (!StringUtils.isEmpty(subSp)) {
			if (mapOfSubSpMappingT.containsKey(subSp)) {
				actualRevenueT.setSubSp(subSp);
			} else {

				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("sub sp not found in database");
			}
		} else {

			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("SUB SP Is Mandatory; ");

		}

		List<RevenueCustomerMappingT> revenueCustomerData = null;

		// to find whether finance_geography, finance_iou, finance_customer_name
		// and (composite key) has foreign key existence in
		// revenue_customer_mapping_t
		revenueCustomerData = revenueCustomerMappingTRepository
				.checkRevenueMappingPK(financeCustomerName, customerGeography,
						financeIou);
		logger.info("revenueCustomerData.size() "
				+ Integer.toString(revenueCustomerData.size()));
		if ((!revenueCustomerData.isEmpty())
				&& (revenueCustomerData.size() == 1)) {

			// FINANACE GEOGRAPHY
			if (!StringUtils.isEmpty(customerGeography)) {
				actualRevenueT.setFinanceGeography(customerGeography);
			} else {

				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("geography Is Mandatory; ");

			}

			// END CUSTOMER NAME
			if (!StringUtils.isEmpty(financeCustomerName)) {
				actualRevenueT.setFinanceCustomerName(financeCustomerName);
			} else {

				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("end customer name Is Mandatory; ");

			}

			// IOU
			if (!StringUtils.isEmpty(financeIou)) {
				if (mapOfIouCustomerMappingT.containsKey(financeIou)) {
					actualRevenueT.setFinanceIou(financeIou);
				} else {

					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					error.setMessage("Iou not found in database");
				}
			} else {

				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("Iou Is Mandatory; ");

			}
		} else {

			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("either of these values i.e finance_geography,finance_iou,finance_customer_name is empty");
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

		// Get List of IOU from DB for validating the IOU which comes from the
		// sheet
		mapOfSubSpMappingT = mapOfSubSpMappingT != null ? mapOfSubSpMappingT
				: commonHelper.getSubSpMappingT();
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
				.checkRevenueMappingPK(financeCustomerName, customerGeography,
						financeIou);
		logger.debug("size " + revenueCustomerData.size());
		if ((!revenueCustomerData.isEmpty())
				&& (revenueCustomerData.size() == 1)) {

			// FINANACE GEOGRAPHY
			if (!StringUtils.isEmpty(customerGeography)) {
				actualRevenueT.setFinanceGeography(customerGeography);
			}

			// END CUSTOMER NAME
			if (!StringUtils.isEmpty(financeCustomerName)) {
				actualRevenueT.setFinanceCustomerName(financeCustomerName);
			}

			// IOU
			if (!StringUtils.isEmpty(financeIou)) {
				if (mapOfIouCustomerMappingT.containsKey(financeIou)) {
					actualRevenueT.setFinanceIou(financeIou);
				} else {

					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					error.setMessage("Iou not found in database");
				}
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

		// Get List of IOU from DB for validating the IOU which comes from the
		// sheet
		mapOfSubSpMappingT = mapOfSubSpMappingT != null ? mapOfSubSpMappingT
				: commonHelper.getSubSpMappingT();
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
				.checkRevenueMappingPK(financeCustomerName, customerGeography,
						financeIou);

		if ((!revenueCustomerData.isEmpty())
				&& (revenueCustomerData.size() == 1)) {

			// FINANACE GEOGRAPHY
			if (!StringUtils.isEmpty(customerGeography)) {
				revenueT.setFinanceGeography(customerGeography);
			}

			// END CUSTOMER NAME
			if (!StringUtils.isEmpty(financeCustomerName)) {
				revenueT.setFinanceCustomerName(financeCustomerName);
			}

			// IOU
			if (!StringUtils.isEmpty(financeIou)) {
				if (mapOfIouCustomerMappingT.containsKey(financeIou)) {
					revenueT.setFinanceIou(financeIou);
				} else {

					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					error.setMessage("Iou not found in database");
				}
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
