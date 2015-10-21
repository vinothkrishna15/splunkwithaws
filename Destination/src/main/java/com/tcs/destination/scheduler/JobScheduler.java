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

import com.tcs.destination.enums.Switch;
import com.tcs.destination.utils.PropertyUtil;

public class JobScheduler {
	
	private static final Logger logger = LoggerFactory.getLogger(JobScheduler.class);
	
	@Autowired
    private JobLauncher jobLauncher;

    private Job job;

	  public void run() {
		  
	    logger.debug("Inside JobScheduler: run");

		Format dtFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    	try {
    		if (Switch.ON.checkSwitch(PropertyUtil.getProperty(job.getName()))) {
    			
	    	    String dateParam = dtFormat.format(new Date());
				JobParameters param = 
				  new JobParametersBuilder().addString("date", dateParam).toJobParameters();
						
				logger.info("Job: {} starting with parameters: {}.", job.getName() ,dateParam );
				
				JobExecution execution = jobLauncher.run(job, param);
				
				logger.info("Job: {} exit status:{}.", job.getName() ,execution.getStatus() );
    		} else {
    			logger.info("Job: {} not started as the switch is OFF.", job.getName());
    		}
    	} catch (JobExecutionAlreadyRunningException e) {
    		logger.error("Error while lauching job:{} - exception:{}",job.getName(), e.getMessage());
		} catch (JobRestartException e) {
			logger.error("Error while lauching job:{} - exception:{}",job.getName(), e.getMessage());
		} catch (JobInstanceAlreadyCompleteException e) {
			logger.error("Error while lauching job:{} - exception:{}",job.getName(), e.getMessage());
		} catch (JobParametersInvalidException e) {
			logger.error("Error while lauching job:{} - exception:{}",job.getName(), e.getMessage());
		}

	  }

	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}
	  

}