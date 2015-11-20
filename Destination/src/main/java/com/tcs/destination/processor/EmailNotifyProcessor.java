/**
 * 
 * EmailNotifyProcessor.java 
 *
 * @author TCS
 * @Version 1.0 - 2015
 * 
 * @Copyright 2015 Tata Consultancy 
 */
package com.tcs.destination.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.utils.DestinationMailUtils;

/**
 * This EmailNotifyProcessor class has the functionality to notify the group for pending request for uploads
 * 
 */
@Component("emailNotifyProcessor")
public class EmailNotifyProcessor implements ItemProcessor<DataProcessingRequestT, DataProcessingRequestT> {
	
	private static final Logger logger = LoggerFactory
			.getLogger(EmailNotifyProcessor.class);
	
	@Autowired
	private DestinationMailUtils destinationMailUtils;

	/* (non-Javadoc)
	 * @see org.springframework.batch.item.ItemProcessor#process(java.lang.Object)
	 */
	@Override
	public DataProcessingRequestT process(DataProcessingRequestT item)
			throws Exception {
		
		logger.debug("Inside process method:");
		
		if(destinationMailUtils.sendUploadNotification(item)) {
			item.setStatus(RequestStatus.NOTIFIED.getStatus());
			logger.info("Emailed report for the request Id: {}", item.getProcessRequestId());
		}
		
		return item;
	}

}
