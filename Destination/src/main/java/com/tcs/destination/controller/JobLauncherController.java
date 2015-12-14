package com.tcs.destination.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.enums.JobName;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.DataProcessingService;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * Controller to handle contact details search requests.
 * 
 */
@RestController
@RequestMapping("/job")
public class JobLauncherController {

	private static final Logger logger = LoggerFactory
			.getLogger(JobLauncherController.class);

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private JobRegistry jobRegistry;

	@RequestMapping(value = "/launch", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> jobLaunch(
			@RequestBody JobName jobName,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.info("Inside Job Laucher Controller: Launching job"
				+ jobName.getJob());
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		Job job;
		JobExecution execution;
		try {
			job = jobRegistry.getJob(jobName.getJob());
			execution = jobLauncher.run(job, getJobParameter(job));

			logger.info("Job: {} exit status:{}.", job.getName(),
					execution.getStatus());

			if (execution.getStatus().equals(BatchStatus.COMPLETED)) {
				status.setStatus(Status.SUCCESS, "Job:" + job
						+ " completed successfully.");
			}

		} catch (NoSuchJobException e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"No such job available for the job name:"
							+ jobName.getJob());
		} catch (JobExecutionAlreadyRunningException e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Error lauching the job as an job instance is already running:");
		} catch (JobRestartException e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Unable to restart the job:" + jobName.getJob());
		} catch (JobInstanceAlreadyCompleteException e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Error lauching the job as a job instance is already completed:");
		} catch (JobParametersInvalidException e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Error lauching the job due to invalid job parameters:"
							+ jobName.getJob());
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Error lauching the job:" + jobName.getJob());
		}

		logger.info("Inside controller: Job launch complete");
		return new ResponseEntity<String>(
				ResponseConstructors.filterJsonForFieldAndViews(fields, view,
						status), HttpStatus.OK);
	}

	private JobParameters getJobParameter(Job job) {
		String dateParam = DateUtils.getCurrentDateForBatch();
		logger.info("Job: {} starting with parameters: {}.", job.getName(),
				dateParam);
		return new JobParametersBuilder().addString("date", dateParam)
				.toJobParameters();
	}

}
