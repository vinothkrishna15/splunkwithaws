package com.tcs.destination.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.utils.DestinationMailUtils;

@Component("emailProcessor")
public class EmailProcessor implements ItemProcessor<DataProcessingRequestT, DataProcessingRequestT> {
	
	private static final Logger logger = LoggerFactory
			.getLogger(EmailProcessor.class);
	
	@Autowired
	private DestinationMailUtils destinationMailUtils;

	@Override
	public DataProcessingRequestT process(DataProcessingRequestT item) throws Exception {
		
		logger.debug("Inside process method:");
		
		if(destinationMailUtils.sendUserRequestResponse(item)) {
			item.setStatus(RequestStatus.EMAILED.getStatus());
			logger.info("Emailed report for the request Id: {}", item.getProcessRequestId());
		}
		
		return item;
	}

}
