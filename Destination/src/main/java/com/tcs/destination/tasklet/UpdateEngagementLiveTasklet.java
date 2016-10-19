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

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.DeliveryMasterManagerLinkT;
import com.tcs.destination.bean.DeliveryMasterT;
import com.tcs.destination.controller.JobLauncherController;
import com.tcs.destination.data.repository.DeliveryMasterManagerLinkRepository;
import com.tcs.destination.data.repository.DeliveryMasterRepository;
import com.tcs.destination.enums.DeliveryStage;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.service.OpportunityService;
import com.tcs.destination.utils.DestinationMailUtils;


/**
 * This tasklet updates the engagements as live 
 * @author tcs
 *
 */
@Component("updateEngagementLiveTasklet")
public class UpdateEngagementLiveTasklet implements Tasklet, StepExecutionListener {

	@Autowired
	private DestinationMailUtils destinationMailUtils;
	
	@Autowired
	DeliveryMasterRepository deliveryMasterRepository;
	
	@Autowired
	OpportunityService opportunityService;
	
	List<DeliveryMasterT> deliveryMastersList;
	
	@Autowired
	private JobLauncherController jobLauncherController;
	
	private static final Logger logger = LoggerFactory
			.getLogger(UpdateEngagementLiveTasklet.class);
	
	
	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		logger.info("Inside UpdateEngagementLiveTasklet ");
		JobParameters jobParameters = chunkContext.getStepContext()
				.getStepExecution().getJobExecution().getJobParameters();
		
		deliveryMastersList = deliveryMasterRepository.findEngagementsPastActualStartDate();
		if (CollectionUtils.isNotEmpty(deliveryMastersList)) {
			for (DeliveryMasterT deliveryMasterT : deliveryMastersList) {
               deliveryMasterT.setDeliveryStage(DeliveryStage.LIVE.getStageCode());
               deliveryMasterT.setModifiedBy("System");
               deliveryMasterRepository.save(deliveryMasterT);
			}
		}
		//		String entityId = jobParameters.getString("entityId");
//		String entityType = jobParameters.getString("entityType");
//		String recipientIds = jobParameters.getString("recipientIds");
//		String sender = jobParameters.getString("sender");
//		String url = jobParameters.getString("url");
//		destinationMailUtils.sendShareEmail(entityId, entityType, recipientIds,
//				sender, url);
		return RepeatStatus.FINISHED;
	}


	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		if (CollectionUtils.isNotEmpty(deliveryMastersList)) { 
		for (DeliveryMasterT deliveryMasterT : deliveryMastersList) {
		 try {
			
			 destinationMailUtils.sendDeliveryEmails(deliveryMasterT.getDeliveryMasterId(), EntityType.DELIVERY.getName(),
         		   deliveryMasterT.getDeliveryCentreId());
            } catch (Exception e){
         	   logger.info("UpdateEngagementLiveTasklet : exception occured while sending mail");
            }
		}
		}
		return stepExecution.getExitStatus();
	}


	@Override
	public void beforeStep(StepExecution arg0) {
		// TODO Auto-generated method stub
		
	}


//	public DestinationMailUtils getDestinationMailUtils() {
//		return destinationMailUtils;
//	}


//	public void setDestinationMailUtils(DestinationMailUtils destinationMailUtils) {
//		this.destinationMailUtils = destinationMailUtils;
//	}
	
	

}
