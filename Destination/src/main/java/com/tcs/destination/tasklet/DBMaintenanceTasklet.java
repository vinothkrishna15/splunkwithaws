/**
 * 
 * BatchMaintenanceTasklet.java 
 *
 * @author TCS
 * @Version 1.0 - 2015
 * 
 * @Copyright 2015 Tata Consultancy 
 */
package com.tcs.destination.tasklet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tcs.destination.data.repository.BatchOpportunityRepository;

/**
 * This BatchMaintenanceTasklet class <description>
 * 
 */
@Component("dbMaintenanceTasklet")
public class DBMaintenanceTasklet implements Tasklet {
	
	private static final Logger logger = LoggerFactory
			.getLogger(DBMaintenanceTasklet.class);
	
	@Value("${batch.table.purge.days}")
	private int batchTablePurgeDays;
	
	@Value("${batch.table.purge.months}")
	private int usrNotiPurgeMonths;
	
	@Autowired
	private BatchOpportunityRepository batchOpportunityRepository;

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.step.tasklet.Tasklet#execute(org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		
		logger.debug("Inside execute method:");
		
		RepeatStatus status = null;
		
		if (batchOpportunityRepository.maintainDBTables(batchTablePurgeDays, usrNotiPurgeMonths) == 1 ) {
			status = RepeatStatus.FINISHED;
		}
		
		return status;
	}

}
