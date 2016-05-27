package com.tcs.destination.service;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.DealClosureReportingT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.DealReportingRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.UserRole;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.DestinationUtils;

@Service
public class DealReportingService {

	private static final Logger logger = LoggerFactory.getLogger(DealReportingService.class); 
	
	@Autowired
	DealReportingRepository DealReportingRepository;
	
	@Autowired
	UserRepository userRepository;

	public void createDealMonthReporting(List<DealClosureReportingT> monthsSelectedList, Status status) throws ParseException {
		UserT user = DestinationUtils.getCurrentUserDetails();
		String userId = user.getUserId();
		UserT userT = userRepository.findByUserId(userId);
		String userRole = userT.getUserRole();
		//Date DatesOfMonth[] = new Date[2]; 
		int i=0;
	//	List<DealClosureReportingT> monthsSelectedList = new ArrayList<DealClosureReportingT>();
		if(UserRole.contains(userRole)){
			switch (UserRole.valueOf(UserRole.getName(userRole))){
			case SYSTEM_ADMIN:
				if(monthsSelectedList!=null){
					for(DealClosureReportingT monthsSelected : monthsSelectedList){
						//if(validateDealReporting(dealClosureReportingt)){
						monthsSelected.setCreatedBy(userId);
						//monthsSelected.setDealReportingStartDate(monthsSelected.getDealReportingStartDate());
						//monthsSelected.setDealReportingEndDate(monthsSelected.getDealReportingEndDate());
						System.out.println("iteration" + i);
						System.out.println("deal start date" + monthsSelected.getDealReportingStartDate());
						System.out.println("deal start date" +monthsSelected.getDealReportingEndDate());
						i++;
						DealReportingRepository.save(monthsSelected);
						status.setStatus(Status.SUCCESS, "The reporting Months for deal closure are selected!");
					}
					
				}
				break;
			default:
				logger.error("NOT_FOUND: Deal Reporting Start Date is empty");
				throw new DestinationException(HttpStatus.UNAUTHORIZED,
						"User not authorized to set month reporting for deals.");
			}
		}
	}

	private boolean validateDealReporting(
			DealClosureReportingT dealClosureReportingt) {
		if(dealClosureReportingt.getDealReportingStartDate() == null){
			logger.error("NOT_FOUND: Deal Reporting Start Date is empty");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Deal Reporting Start Date is empty");
		}

		if(dealClosureReportingt.getDealReportingEndDate() == null){
			logger.error("NOT_FOUND: Deal Reporting End Date is empty");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Deal Reporting End Date is empty");
		}
		return true;
	}
}
