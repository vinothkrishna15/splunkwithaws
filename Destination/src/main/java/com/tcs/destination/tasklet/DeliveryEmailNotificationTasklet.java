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

@Component("deliveryEmailNotificationTasklet")
public class DeliveryEmailNotificationTasklet implements Tasklet {

    private DestinationMailUtils destinationMailUtils;
	
	private static final Logger logger = LoggerFactory
			.getLogger(DeliveryEmailNotificationTasklet.class);
	
	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		logger.info("Inside DeliveryEmailNotificationTasklet");
		JobParameters jobParameters = chunkContext.getStepContext()
				.getStepExecution().getJobExecution().getJobParameters();
		String entityId = jobParameters.getString("entityId");
		String entityType = jobParameters.getString("EntityType");
		Integer deliveryCenterId = Integer.parseInt(jobParameters.getString("deliveryCentreId"));
		destinationMailUtils.sendDeliveryEmails(entityId,entityType,deliveryCenterId);
		return RepeatStatus.FINISHED;
	}

	public DestinationMailUtils getDestinationMailUtils() {
		return destinationMailUtils;
	}

	public void setDestinationMailUtils(DestinationMailUtils destinationMailUtils) {
		this.destinationMailUtils = destinationMailUtils;
	}
	
	

}
