/**
 * 
 * ShareEmailNotificationTasklet.java 
 *
 * @author TCS
 * @Version 1.0 - 2016
 * 
 * @Copyright 2016 Tata Consultancy 
 */
package com.tcs.destination.tasklet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.utils.DestinationMailUtils;


/**
 * This tasklet sends the email notification for share 
 * @author tcs
 *
 */
@Component("shareEmailNotificationTasklet")
public class ShareEmailNotificationTasklet implements Tasklet {

	private DestinationMailUtils destinationMailUtils;
	
	private static final Logger logger = LoggerFactory
			.getLogger(ShareEmailNotificationTasklet.class);
	
	
	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		logger.info("Inside shareEmailNotificationTasklet ");
		JobParameters jobParameters = chunkContext.getStepContext()
				.getStepExecution().getJobExecution().getJobParameters();
		String entityId = jobParameters.getString("entityId");
		String entityType = jobParameters.getString("entityType");
		String recipientIds = jobParameters.getString("recipientIds");
		String sender = jobParameters.getString("sender");
		String url = jobParameters.getString("url");
		destinationMailUtils.sendShareEmail(entityId, entityType, recipientIds,
				sender, url);
		return RepeatStatus.FINISHED;
	}


	public DestinationMailUtils getDestinationMailUtils() {
		return destinationMailUtils;
	}


	public void setDestinationMailUtils(DestinationMailUtils destinationMailUtils) {
		this.destinationMailUtils = destinationMailUtils;
	}
	
	

}
