package com.tcs.destination.service;

import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.UserNotificationsT;
import com.tcs.destination.data.repository.UserNotificationsRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DestinationUtils;

@Service
public class UserNotificationsService {

	private static final Logger logger = LoggerFactory
			.getLogger(UserNotificationsService.class);

	@Autowired
	UserNotificationsRepository userNotificationsRepository;
	
	public List<UserNotificationsT> getNotifications(String userId,
			String read, long fromTime, long toTime)
			throws DestinationException {
		try {
			if (!DestinationUtils.getCurrentUserDetails().getUserId()
					.equalsIgnoreCase(userId))
				throw new DestinationException(HttpStatus.FORBIDDEN,
						"User Id and Login User Detail does not match");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<UserNotificationsT> userNotificationsTs = null;

		if (read.equals("")) {
			userNotificationsTs = userNotificationsRepository
					.getOptedPortalNotifications(userId,new Timestamp(fromTime), new Timestamp(toTime));
		} else {
			userNotificationsTs = userNotificationsRepository
					.getOptedPortalNotificationsWithRead(userId,new Timestamp(fromTime), new Timestamp(toTime),read);
		}
		if(userNotificationsTs==null||userNotificationsTs.size()==0)
			throw new DestinationException(HttpStatus.NOT_FOUND, "No Notification is available.");
		return userNotificationsTs;

	}
	
	/**
	 * This service updates the read status
	 * 
	 * @param userNotificationIds
	 * @param read
	 * @return String
	 * @throws Exception
	 */
	public String updateReadStatus(List<String> userNotificationIds, String read
			) throws Exception{

		String status = "";
		String message = "No User Notification Id provided";
		
		if(userNotificationIds!=null&&userNotificationIds.size()!=0) {
			if(read.equalsIgnoreCase(Constants.NO)) {
				status = Constants.YES;
				message = "Marked as read";
			} else if(read.equalsIgnoreCase(Constants.YES)) {
				status = Constants.NO;
				message = "Marked as unread";
			} else {
				logger.error("BAD_REQUEST - Invalid read parameter provided");
				throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid read parameter provided");
			}
			
			int updateCount = userNotificationsRepository.updateReadStatus(userNotificationIds, status);
			if(updateCount==0){
				message = "Invalid User Notification Id Provided";
			} else if(updateCount>0){
				message = updateCount+" "+message;
			}
		}
		
		return message;
    }
}


