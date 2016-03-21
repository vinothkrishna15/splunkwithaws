package com.tcs.destination.listener;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.UserNotificationsT;

@Component("reminderNotifyWriteListener")
public class ReminderNotifyListener implements ItemWriteListener<UserNotificationsT>, ItemProcessListener<Object[], UserNotificationsT> {
	
	private static final Logger logger = LoggerFactory
			.getLogger(ReminderNotifyListener.class);

	@Override
	public void beforeProcess(Object[] item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterProcess(Object[] item, UserNotificationsT result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProcessError(Object[] item, Exception e) {
		logger.error("Error during processing notification:{} ", e.getMessage());
		
	}

	@Override
	public void beforeWrite(List<? extends UserNotificationsT> items) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterWrite(List<? extends UserNotificationsT> items) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onWriteError(Exception exception,
			List<? extends UserNotificationsT> items) {
		for (UserNotificationsT item: items ) {
			logger.error("Error during writing the notification:{} ", exception.getMessage());
		}
		
	}


}
