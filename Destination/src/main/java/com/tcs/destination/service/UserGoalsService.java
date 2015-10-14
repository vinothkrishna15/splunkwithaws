package com.tcs.destination.service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.bean.GoalMappingT;
import com.tcs.destination.bean.IouCustomerMappingT;
import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.bean.UserAccessPrivilegesT;
import com.tcs.destination.bean.UserGoalsT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.data.repository.GoalMappingRepository;
import com.tcs.destination.data.repository.IOURepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.data.repository.UserAccessPrivilegesRepository;
import com.tcs.destination.data.repository.UserGoalsRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.PrivilegeType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.StringUtils;

@Service
public class UserGoalsService {

	private static final Logger logger = LoggerFactory
			.getLogger(UserGoalsService.class);

	@Autowired
	UserGoalsRepository userGoalsRepository;
	
	@Autowired
	GoalMappingRepository goalMappingRepository;
	
	@Autowired
	UserRepository userRepository;

	public boolean insertTarget(UserGoalsT userGoal) throws Exception{
		validateRequest(userGoal);
		if(userGoalsRepository.save(userGoal) != null){
			return true;
		}
		return false;
	}
	
	private void validateRequest(UserGoalsT userGoal) throws DestinationException{
	 UserT currentUser = DestinationUtils.getCurrentUserDetails();
	 String currUser = currentUser.getUserId();
     userGoal.setCreatedModifiedBy(currUser);
	 
     String financialYear = userGoal.getFinancialYear();
     if(StringUtils.isEmpty(financialYear)){
    	 String finYear = DateUtils.getCurrentFinancialYear();
    	 userGoal.setFinancialYear(finYear);
     }
     
     String goalId= userGoal.getGoalId();
     if(StringUtils.isEmpty(goalId)){
    	 logger.error("goalId is null");
		 throw new DestinationException(HttpStatus.BAD_REQUEST,"goalId cannot be empty");
     } else {
    	List<GoalMappingT> goalMappingList = goalMappingRepository.findByFinancialyearAndGoalId(userGoal.getFinancialYear(),goalId);
    	if(goalMappingList == null || goalMappingList.isEmpty()) {
    		logger.error("invalid goalId");
   		 	throw new DestinationException(HttpStatus.BAD_REQUEST,"invalid goalId");
    	}
     }
     
     String userId= userGoal.getUserId();
     if(StringUtils.isEmpty(userId)){
    	 logger.error("userId is null");
		 throw new DestinationException(HttpStatus.BAD_REQUEST,"userId cannot be empty");
     } else {
    	 UserT user = userRepository.findOne(userId);
    	 if(user == null){
    		 logger.error("invalid userId");
    		 throw new DestinationException(HttpStatus.BAD_REQUEST,"invalid userId");
    	 }
     }
     
     BigDecimal targetValue = userGoal.getTargetValue();
     if(targetValue == null){
    	 logger.error("targetValue is null");
		 throw new DestinationException(HttpStatus.BAD_REQUEST,"targetValue cannot be empty");
     } else {
    	 try{
    		 String targetValueStr = targetValue.toPlainString();
    		 BigDecimal val = new BigDecimal(targetValueStr);
    		 if(isMultiplierGoalId(goalId)){
    			 val = val.multiply(new BigDecimal(5));
    			 userGoal.setTargetValue(val);
    		 }
    	 }
    	 catch(Exception e){
    		 logger.error(e.getMessage());
    		 throw new DestinationException(HttpStatus.BAD_REQUEST,e.getMessage());
    	 }
     }
     
	}

	private boolean isMultiplierGoalId(String goalId) {
		if(goalId.equalsIgnoreCase("G5")){
		 return true;
		}
		return false;
	}
}
