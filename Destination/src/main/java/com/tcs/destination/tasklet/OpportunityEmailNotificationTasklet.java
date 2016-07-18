/**
 * 
 * OpportunityEmailNotificationTasklet.java 
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
 * This tasklet sends the email notification for opportunity 
 * @author tcs
 *
 */
@Component("opportunityEmailNotificationTasklet")
public class OpportunityEmailNotificationTasklet implements Tasklet {

	private DestinationMailUtils destinationMailUtils;
	
	private static final Logger logger = LoggerFactory
			.getLogger(OpportunityEmailNotificationTasklet.class);
	
	
	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		      logger.info("Inside OpportunityEmailNotificationTasklet ");
				 JobParameters jobParameters = chunkContext.getStepContext()
							.getStepExecution().getJobExecution().getJobParameters();
					String entityId = jobParameters.getString("entityId");
				Double dealValue = jobParameters.getDouble("dealValue"); 
				destinationMailUtils.sendOpportunityEmailNotification(entityId,dealValue);	
			return RepeatStatus.FINISHED;
	}


	public DestinationMailUtils getDestinationMailUtils() {
		return destinationMailUtils;
	}


	public void setDestinationMailUtils(DestinationMailUtils destinationMailUtils) {
		this.destinationMailUtils = destinationMailUtils;
	}
	
	

}
