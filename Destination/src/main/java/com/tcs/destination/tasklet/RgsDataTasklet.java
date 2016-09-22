/**
 * 
 * ActualRevenueDwldPreprocessor.java 
 *
 * @author TCS
 * @Version 1.0 - 2015
 * 
 * @Copyright 2015 Tata Consultancy 
 */
package com.tcs.destination.tasklet;

import static com.tcs.destination.utils.Constants.REQUEST;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.DeliveryRequirementT;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.data.repository.DeliveryRequirementRepository;
import com.tcs.destination.enums.RequestStatus;

/**
 * This RgsDataTasklet class contains the functionality to process the 
 * RgsData request
 * 
 */
@Component("rgsDataTasklet")
public class RgsDataTasklet implements Tasklet{

	private static final Logger logger = LoggerFactory
			.getLogger(RgsDataTasklet.class);

	
	@Autowired
	private DeliveryRequirementRepository requirementRepository;
	
	@Autowired
	private DataProcessingRequestRepository dataProcessingRequestRepository;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.batch.core.step.tasklet.Tasklet#execute(org.
	 * springframework.batch.core.StepContribution,
	 * org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		
		logger.debug("Inside execute method:");
		
		ExecutionContext jobContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
		
		DataProcessingRequestT request = (DataProcessingRequestT) jobContext.get(REQUEST);
		
		@SuppressWarnings("unchecked")
		Map<String,DeliveryRequirementT> rgsIdRequirementMap = 
				(Map<String, DeliveryRequirementT>) jobContext.get("rgsRequirementMap");
		
		for(Map.Entry<String, DeliveryRequirementT> entry : rgsIdRequirementMap.entrySet()){
			DeliveryRequirementT deliveryRequirementT = entry.getValue();
			requirementRepository.save(deliveryRequirementT);
		}
		
		request.setStatus(RequestStatus.PROCESSED.getStatus());
		dataProcessingRequestRepository.save(request);
		
		jobContext.remove("rgsRequirementMap");
		jobContext.remove(REQUEST);
		
		return RepeatStatus.FINISHED;
	}

}
