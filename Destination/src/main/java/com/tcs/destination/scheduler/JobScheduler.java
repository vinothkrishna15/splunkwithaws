package com.tcs.destination.scheduler;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;

public class JobScheduler {
	
	private static final Logger logger = LoggerFactory.getLogger(JobScheduler.class);
	
	@Autowired
    private JobLauncher jobLauncher;

    private Job job;

	  public void run() {
		  
	    logger.debug("Inside JobScheduler: run");

		Format dtFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    	try {

    	    String dateParam = dtFormat.format(new Date());
			JobParameters param = 
			  new JobParametersBuilder().addString("date", dateParam).toJobParameters();
					
			logger.info("Job: {} starting with parameters: {}.", job.getName() ,dateParam );
					
			JobExecution execution = jobLauncher.run(job, param);
			
			logger.info("Job: {} exit status:{}.", job.getName() ,execution.getStatus() );
    	} catch (JobExecutionAlreadyRunningException e) {
    		logger.error("Error while lauching:{}",e);
		} catch (JobRestartException e) {
			logger.error("Error while lauching:{}",e);
		} catch (JobInstanceAlreadyCompleteException e) {
			logger.error("Error while lauching:{}",e);
		} catch (JobParametersInvalidException e) {
			logger.error("Error while lauching:{}",e);
		}

	  }

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}
	  

}