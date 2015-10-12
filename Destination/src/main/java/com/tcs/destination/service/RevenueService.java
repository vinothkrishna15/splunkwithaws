package com.tcs.destination.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.destination.bean.ActualRevenuesDataT;
import com.tcs.destination.bean.RevenueCustomerMappingT;
import com.tcs.destination.bean.RevenueCustomerMappingTPK;
import com.tcs.destination.data.repository.ActualRevenuesDataTRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.RevenueCustomerMappingTRepository;
import com.tcs.destination.exception.DestinationException;

@Service
public class RevenueService {

	private static final Logger logger = LoggerFactory.getLogger(RevenueService.class);

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	RevenueCustomerMappingTRepository revenueRepository;
	
	@Autowired
	ActualRevenuesDataTRepository actualRevenuesDataTRepository;

	/**
	 * This method inserts Revenue customers to the database
	 * @param revenueCustomerToInsert
	 * @return RevenueCustomerMappingT
	 * @throws Exception
	 */
	@Transactional
	public RevenueCustomerMappingT addFinance(RevenueCustomerMappingT revenueCustomerToInsert) throws Exception{
		RevenueCustomerMappingT revenueT = null;
		RevenueCustomerMappingTPK revenueTPK = null;
		 List<RevenueCustomerMappingT> financeCustomers = null;
		if(revenueCustomerToInsert!=null){
			revenueT = new RevenueCustomerMappingT();
			revenueTPK = new RevenueCustomerMappingTPK();
			
			// to find the uniqueness of the primary key (here composite key)
			financeCustomers = revenueRepository.checkRevenueMappingPK(revenueCustomerToInsert.getFinanceCustomerName(),revenueCustomerToInsert.getCustomerGeography(),revenueCustomerToInsert.getFinanceIou());
			
			if (financeCustomers.isEmpty()) 
            {
				revenueT.setCustomerName(revenueCustomerToInsert.getCustomerName());
				revenueTPK.setFinanceCustomerName(revenueCustomerToInsert.getFinanceCustomerName());
				revenueTPK.setCustomerGeography(revenueCustomerToInsert.getCustomerGeography());
				revenueTPK.setFinanceIou(revenueCustomerToInsert.getFinanceIou());
            }
           
			else
            {
                logger.error("EXISTS: Finance Map Already Exist!");
                throw new DestinationException(HttpStatus.CONFLICT,"Finance Map Already Exist!");
            }
			revenueT.setId(revenueTPK);
			revenueT = revenueRepository.save(revenueT);
			logger.info("Finance Mapping Saved .... "+ "finance mapping primary key" + revenueT.getId());
		}
		return revenueT;
	}
	
	/**
	 * This method inserts Actual Revenue Data to the database
	 * @param revenueCustomerToInsert
	 * @return RevenueCustomerMappingT
	 * @throws Exception
	 */
	@Transactional
	public ActualRevenuesDataT addActualRevenue(ActualRevenuesDataT actualRevenueDataToInsert) throws Exception{
		ActualRevenuesDataT actualRevenueDataT = null;
		 List<ActualRevenuesDataT> financeCustomers = null;
		if(actualRevenueDataToInsert!=null){
			actualRevenueDataT = new ActualRevenuesDataT();
			// to find the uniqueness of the primary key (here composite key)
			financeCustomers = actualRevenuesDataTRepository.checkRevenueDataMappingPK(actualRevenueDataToInsert.getQuarter(),actualRevenueDataToInsert.getMonth(),actualRevenueDataToInsert.getFinancialYear(),actualRevenueDataToInsert.getClientCountry(),
			actualRevenueDataToInsert.getFinanceGeography(),actualRevenueDataToInsert.getSubSp(),actualRevenueDataToInsert.getFinanceIou(),actualRevenueDataToInsert.getFinanceCustomerName());
					
			if (financeCustomers.isEmpty()) 
           {
				actualRevenueDataT.setQuarter(actualRevenueDataToInsert.getQuarter());
				actualRevenueDataT.setMonth(actualRevenueDataToInsert.getMonth());
				actualRevenueDataT.setFinancialYear(actualRevenueDataToInsert.getFinancialYear());
				actualRevenueDataT.setRevenue(actualRevenueDataToInsert.getRevenue());
				actualRevenueDataT.setClientCountry(actualRevenueDataToInsert.getClientCountry());
				actualRevenueDataT.setFinanceGeography(actualRevenueDataToInsert.getFinanceGeography());
				actualRevenueDataT.setSubSp(actualRevenueDataToInsert.getSubSp());
				actualRevenueDataT.setFinanceIou(actualRevenueDataToInsert.getFinanceIou());
				actualRevenueDataT.setFinanceCustomerName(actualRevenueDataToInsert.getFinanceCustomerName());
            }
           
			else
            {
                logger.error("EXISTS: Actual Revenue Data Already Exist!");
                throw new DestinationException(HttpStatus.CONFLICT,"Finance Map Already Exist!");
          }
			actualRevenueDataT = actualRevenuesDataTRepository.save(actualRevenueDataT);
			logger.info("Actual Revenue Data Saved .... "+ "Actual Revenue Data primary key" + actualRevenueDataT.getActualRevenuesDataId());
		}
		return actualRevenueDataT;
	}

}