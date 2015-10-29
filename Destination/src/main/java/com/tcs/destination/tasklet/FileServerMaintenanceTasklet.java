/**
 * 
 * FileServerMaintenanceTasklet.java 
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tcs.destination.utils.FileManager;

/**
 * This FileServerMaintenanceTasklet class provide the functionality for purging the file server, as per the business requirements
 * 
 */
@Component("filerServerMaintenanceTasklet")
public class FileServerMaintenanceTasklet implements Tasklet {
	
	private static final Logger logger = LoggerFactory
			.getLogger(FileServerMaintenanceTasklet.class);
	
	@Value("${batch.file.server.purge.days}")
	private int filePurgeDays;
	
	@Value("${fileserver.path}")
	private String rootPath;
	
	/* (non-Javadoc)
	 * @see org.springframework.batch.core.step.tasklet.Tasklet#execute(org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		
		logger.debug("Inside execute method:");
		
		FileManager.purgeOldFile(filePurgeDays, rootPath);
		
		return RepeatStatus.FINISHED;
	}

}
