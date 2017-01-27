package com.tcs.destination.decider;

import static com.tcs.destination.utils.Constants.NEXT_STEP;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import com.tcs.destination.enums.JobStep;

public class CustomerAssociateDecider implements JobExecutionDecider {

	private static final Logger logger = LoggerFactory
			.getLogger(CustomerAssociateDecider.class);
	
	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution,
			StepExecution stepExecution) {
		logger.debug("Inside decide method:");
		
		FlowExecutionStatus status = new FlowExecutionStatus(JobStep.END.name());
		
		if (jobExecution.getExecutionContext().get(NEXT_STEP).equals(JobStep.CUSTOMER_ASSOCIATE_UPLOAD_PROCESSING)) {
			status = new FlowExecutionStatus(JobStep.CUSTOMER_ASSOCIATE_UPLOAD_PROCESSING.name());
        } else if (jobExecution.getExecutionContext().get(NEXT_STEP).equals(JobStep.PREPROCESS)) {
        	status = new FlowExecutionStatus(JobStep.PREPROCESS.name());
        } 
		logger.debug("Decider next step:" + status.getName());
        return status;
	}

}
