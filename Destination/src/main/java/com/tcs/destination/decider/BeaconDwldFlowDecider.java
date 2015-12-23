package com.tcs.destination.decider;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.tcs.destination.utils.Constants.NEXT_STEP;

import com.tcs.destination.enums.JobStep;

/**
 * 
 * This class acts as the decider for Beacon Download processing in batch
 *
 */
public class BeaconDwldFlowDecider implements JobExecutionDecider {
	
	private static final Logger logger = LoggerFactory
			.getLogger(BeaconDwldFlowDecider.class);

	/**
	 * 
	 * This method acts as the decider for Beacon Download processing in batch
	 *
	 */
	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution,
			StepExecution stepExecution) {
		
		logger.debug("Inside decide method:");
		
		FlowExecutionStatus status = new FlowExecutionStatus(JobStep.END.name());
		
		if (jobExecution.getExecutionContext().get(NEXT_STEP).equals(JobStep.BEACON_DWLD_PROCESSING)) {
        	status = new FlowExecutionStatus(JobStep.BEACON_DWLD_PROCESSING.name());
        }else if (jobExecution.getExecutionContext().get(NEXT_STEP).equals(JobStep.BEACON_DWLD_PROCESSING)) {
        	status = new FlowExecutionStatus(JobStep.BEACON_DWLD_PROCESSING.name());}
        else if (jobExecution.getExecutionContext().get(NEXT_STEP).equals(JobStep.PREPROCESS)) {
        	status = new FlowExecutionStatus(JobStep.PREPROCESS.name());
        }
		
		logger.debug("Decider next step:" + status.getName());
		
        return status;
	}

}
