package com.tcs.destination.tasklet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.utils.DestinationMailUtils;


/**
 * This tasklet sends the email notification on opportunity won or lost
 * @author bnpp
 *
 */
@Component("opportunityWonLostEmailNotificationTasklet")
public class OpportunityWonLostEmailNotificationTasklet implements Tasklet {

	private DestinationMailUtils destinationMailUtils;
	
	private static final Logger logger = LoggerFactory
			.getLogger(OpportunityWonLostEmailNotificationTasklet.class);
	
	
	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
				String entityId = chunkContext.getStepContext()
				.getStepExecution().getJobExecution().getJobParameters().getString("entityId");
				logger.info("Inside OpportunityWonLostEmailNotificationTasklet");
				destinationMailUtils.sendOpportunityWonLostNotification(entityId);	
				return RepeatStatus.FINISHED;
	}


	public DestinationMailUtils getDestinationMailUtils() {
		return destinationMailUtils;
	}


	public void setDestinationMailUtils(DestinationMailUtils destinationMailUtils) {
		this.destinationMailUtils = destinationMailUtils;
	}
	
	

}
