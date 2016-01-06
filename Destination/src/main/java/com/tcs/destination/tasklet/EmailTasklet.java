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

import static com.tcs.destination.enums.JobStep.REVENUE_DWLD_PROCESSING;
import static com.tcs.destination.enums.JobStep.END;
import static com.tcs.destination.enums.RequestStatus.SUBMITTED;
import static com.tcs.destination.enums.RequestType.ACTUAL_REVENUE_DOWNLOAD;
import static com.tcs.destination.utils.Constants.NEXT_STEP;
import static com.tcs.destination.utils.Constants.REQUEST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.enums.UserRole;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.DestinationMailUtils;

/**
 * This ActualRevenueDwldPreprocessor class contains the functionality to process the 
 * Actual revenue download request
 * 
 */
@Component("emailTasklet")
public class EmailTasklet implements Tasklet, StepExecutionListener{

	private static final Logger logger = LoggerFactory
			.getLogger(EmailTasklet.class);

	@Autowired
	private DestinationMailUtils destinationMailUtils;

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
		List<UserRole> roles = Arrays.asList(UserRole.SYSTEM_ADMIN, UserRole.STRATEGIC_GROUP_ADMIN);
		
		if(destinationMailUtils.sendUserRequestResponse(request, roles)) {
			
			logger.info("Emailed opportunity daily report to Admin & Strategic group");
		} else {
			
			logger.info("Unable to email opportunity daily report to Admin & Strategic group");
			throw new DestinationException(HttpStatus.EXPECTATION_FAILED,"Unable to email opportunity daily report to Admin & Strategic group");
		}

		jobContext.remove(REQUEST);
		return RepeatStatus.FINISHED;
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.StepExecutionListener#beforeStep(org.springframework.batch.core.StepExecution)
	 */
	@Override
	public void beforeStep(StepExecution stepExecution) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.StepExecutionListener#afterStep(org.springframework.batch.core.StepExecution)
	 */
	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		logger.info("Unable to email opportunity daily report to Admin & Strategic group");
		ExitStatus status = stepExecution.getExitStatus();
		if (!status.equals(ExitStatus.COMPLETED)) {
			for (Throwable e: stepExecution.getFailureExceptions()) {
				logger.error("Error sending e-mail message: {}", e.getMessage());
			}
		}
		logger.info(
				"Step - {} exit with the exit code - {}, exit status - {}",
				stepExecution.getStepName(), status.getExitCode(),
				status.getExitDescription());
		return status;
	}

}
