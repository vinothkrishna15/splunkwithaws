package com.tcs.destination.decider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;

import static com.tcs.destination.utils.Constants.NEXT_STEP;

import com.tcs.destination.enums.JobStep;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

public class UserUploadDecider implements JobExecutionDecider {
	
	private static final Logger logger = LoggerFactory
			.getLogger(UserUploadDecider.class);

	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution,
			StepExecution stepExecution) {
		
		logger.debug("Inside decide method:");
		
		FlowExecutionStatus status = new FlowExecutionStatus(JobStep.END.name());
		
		if (jobExecution.getExecutionContext().get(NEXT_STEP).equals(JobStep.USER_PROCESSING)) {
			status = new FlowExecutionStatus(JobStep.USER_PROCESSING.name());
        } else if (jobExecution.getExecutionContext().get(NEXT_STEP).equals(JobStep.PREPROCESS)) {
        	status = new FlowExecutionStatus(JobStep.PREPROCESS.name());
        } 		
		logger.debug("Decider next step:" + status.getName());
		
        return status;
	}

}
