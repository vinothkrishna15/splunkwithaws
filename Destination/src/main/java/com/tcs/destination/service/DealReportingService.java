/**
 * 
 * DealReportingService.java 
 *
 * @author TCS
 * @Version 1.0 - 2016
 * 
 * @Copyright 2016 Tata Consultancy 
 */
package com.tcs.destination.service;


import java.text.ParseException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.destination.bean.DealClosureReportingT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.DealReportingRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.UserRole;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.DestinationUtils;

/**
 * This DealReportingService class holds the services for handling deal closure data
 * 
 */
@Service
public class DealReportingService {

	private static final Logger logger = LoggerFactory.getLogger(DealReportingService.class); 
	
	@Autowired
	DealReportingRepository dealReportingRepository;
	
	@Autowired
	UserRepository userRepository;

	@Transactional
	public void createDealMonthReporting(List<DealClosureReportingT> monthsSelectedList, Status status) throws ParseException {
		UserT user = DestinationUtils.getCurrentUserDetails();
		String userId = user.getUserId();
		UserT userT = userRepository.findByUserId(userId);
		String userRole = userT.getUserRole();
		if(UserRole.contains(userRole)){
			switch (UserRole.valueOf(UserRole.getName(userRole))){
			case SYSTEM_ADMIN:
				dealReportingRepository.updateDealClosureActiveStatus(false);
				if(monthsSelectedList!=null){
					for(DealClosureReportingT monthsSelected : monthsSelectedList){
						monthsSelected.setCreatedBy(userId);
						monthsSelected.setActive(true);
						dealReportingRepository.save(monthsSelected);
					} 
					status.setStatus(Status.SUCCESS, "The reporting Months for deal closure are selected!");
				}
				break;
			default:
				logger.error("NOT_FOUND: Deal Reporting Start Date is empty");
				throw new DestinationException(HttpStatus.FORBIDDEN,
						"User not authorized to set month reporting for deals.");
			}
		}
	}

	/**
	 * Method to retrieve active deal closure data
	 *  
	 * @return List<DealClosureReportingT>
	 */
	public List<DealClosureReportingT> getDealMonthReporting() {
		
		logger.debug("Entering method: getDealMonthReporting");
		
		List<DealClosureReportingT> dealClosureList = dealReportingRepository.findByActiveOrderByDealReportingStartDate(true);
		
		if (CollectionUtils.isEmpty(dealClosureList)) {
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No active deal closure reporting data is available");
		}
		
		logger.debug("Exit method: getDealMonthReporting");
		
		return dealClosureList;
		
	}
}
