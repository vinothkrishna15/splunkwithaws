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

/**
 *This Revenue service handles revenue related requests 
 *
 */
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
		 logger.info("Begin:Inside addFinance() of RevenueService");
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
		}
		 logger.info("End:Inside addFinance() of RevenueService");
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
		if(actualRevenueDataToInsert!=null){
			logger.info("begin:Inside addActualRevenue() of RevenueService");
			actualRevenueDataT = new ActualRevenuesDataT();
			actualRevenueDataT.setQuarter(actualRevenueDataToInsert.getQuarter());
			actualRevenueDataT.setMonth(actualRevenueDataToInsert.getMonth());
			actualRevenueDataT.setFinancialYear(actualRevenueDataToInsert.getFinancialYear());
			actualRevenueDataT.setRevenue(actualRevenueDataToInsert.getRevenue());
			actualRevenueDataT.setClientCountry(actualRevenueDataToInsert.getClientCountry());
			actualRevenueDataT.setFinanceGeography(actualRevenueDataToInsert.getFinanceGeography());
			actualRevenueDataT.setSubSp(actualRevenueDataToInsert.getSubSp());
			actualRevenueDataT.setFinanceIou(actualRevenueDataToInsert.getFinanceIou());
			actualRevenueDataT.setFinanceCustomerName(actualRevenueDataToInsert.getFinanceCustomerName());
			actualRevenueDataT = actualRevenuesDataTRepository.save(actualRevenueDataT);
		}
		logger.info("End:Inside addActualRevenue() of RevenueService");
		return actualRevenueDataT;
	}
	
	/**
	 * this method saves the actual revenue data into the repository
	 * @param addList
	 */
	@Transactional
	public void save(List<ActualRevenuesDataT> addList) {
		logger.info("Begin:Inside save() of RevenueService");
		actualRevenuesDataTRepository.save(addList);
		logger.info("End:Inside save() of RevenueService");
	}
	
	/**
	 * 
	 * @param deleteList
	 */
	@Transactional
	public void delete(List<ActualRevenuesDataT> deleteList) {
		logger.info("Begin:Inside delete() of RevenueService");
		actualRevenuesDataTRepository.delete(deleteList);
		logger.info("End:Inside delete() of RevenueService");
	}

}