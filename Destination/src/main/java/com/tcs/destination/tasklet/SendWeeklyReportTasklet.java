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
 * Tasklet used to send weekly report
 * @author TCS
 *
 */
@Component("sendWeeklyReportTasklet")
public class SendWeeklyReportTasklet implements Tasklet{

private DestinationMailUtils destinationMailUtils;
	
private static final Logger logger = LoggerFactory.getLogger(SendWeeklyReportTasklet.class);
	
	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		logger.info("Inside SendWeeklyReportTasklet");
		destinationMailUtils.sendWeeklyReport();
		return RepeatStatus.FINISHED;
	}

	public DestinationMailUtils getDestinationMailUtils() {
		return destinationMailUtils;
	}

	public void setDestinationMailUtils(DestinationMailUtils destinationMailUtils) {
		this.destinationMailUtils = destinationMailUtils;
	}
	
	

}
