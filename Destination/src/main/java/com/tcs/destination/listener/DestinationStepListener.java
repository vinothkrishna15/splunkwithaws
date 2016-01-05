/**
 * 
 * DestinationStepListener.java 
 *
 * @author TCS
 * @Version 1.0 - 2015
 * 
 * @Copyright 2015 Tata Consultancy 
 */
package com.tcs.destination.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

/**
 * This DestinationStepListener class i used to log the failure in each step in batch execution
 * 
 */
public class DestinationStepListener implements StepExecutionListener {

	private static final Logger logger = LoggerFactory
			.getLogger(DestinationStepListener.class);

	@Override
	public void beforeStep(StepExecution stepExecution) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.StepExecutionListener#afterStep(org.springframework.batch.core.StepExecution)
	 */
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		

		ExitStatus exitStatus = stepExecution.getExitStatus();

		if (!exitStatus.equals(ExitStatus.COMPLETED)) {
			for (Throwable e: stepExecution.getFailureExceptions()) {
				logger.error("Error sending e-mail message: {}", e.getMessage());
			}
		}
		
		logger.info(
				"Step - {} exit with the exit code - {}, exit status - {}",
				stepExecution.getStepName(), exitStatus.getExitCode(),
				exitStatus.getExitDescription());

		return exitStatus;
	}

}
