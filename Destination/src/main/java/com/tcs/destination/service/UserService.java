package com.tcs.destination.service;

import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.LoginHistoryT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.controller.UserDetailsController;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.data.repository.LoginHistoryRepository;;

@Component
public class UserService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	UserRepository userRepository;  
	
	@Autowired
	LoginHistoryRepository loginHistoryRepository; 
	
	public List<UserT> findByUserName(String nameWith) throws Exception{
		logger.debug("Inside findByUserName Service");
		List<UserT> users = (List<UserT>) userRepository.findByUserNameIgnoreCaseLike("%"+nameWith+"%");
		
		if (users.isEmpty())
		{
			logger.error("NOT_FOUND: No matching user found");
			throw new DestinationException(HttpStatus.NOT_FOUND,"No matching user found");
		}			
		
		return users;
	}
	
	public Timestamp getUserNotification(String userId) {
		logger.debug("Inside getUserNotification Service");
		LoginHistoryT userLastLoginHistory=loginHistoryRepository.findLastUpdatedTime(userId);
		Timestamp lastLogin=userLastLoginHistory.getLoginDatetime();
		return lastLogin;
	}
	
	public boolean adduser(UserT user) throws Exception
	{		
		 return userRepository.save(user) != null;
	}

	public boolean addLoginHistory(LoginHistoryT loginHistory) {
		logger.debug("Inside addLoginHistory Service");
		LoginHistoryT loginHistory1=loginHistoryRepository.save(loginHistory);
		if(loginHistory1 == null)
		    return false;
		else 
			return true;
	}

}
