package com.tcs.destination.tasklet;

import static com.tcs.destination.enums.JobStep.REVENUE_PROCESSING;
import static com.tcs.destination.enums.JobStep.END;
import static com.tcs.destination.enums.RequestStatus.VERIFIED;
import static com.tcs.destination.enums.RequestType.ACTUAL_REVENUE_UPLOAD;
import static com.tcs.destination.utils.Constants.FILE_PATH;
import static com.tcs.destination.utils.Constants.NEXT_STEP;
import static com.tcs.destination.utils.Constants.REQUEST;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;



@Component("revenuePreprocessor")
public class RevenuePreprocessor implements Tasklet{
	
	private static final Logger logger = LoggerFactory
			.getLogger(RevenuePreprocessor.class);
	
	private List<DataProcessingRequestT> requestList = new ArrayList<DataProcessingRequestT>();
	
	@Autowired
	private DataProcessingRequestRepository dataProcessingRequestRepository;

	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		requestList = dataProcessingRequestRepository.findByRequestTypeAndStatus(ACTUAL_REVENUE_UPLOAD.getType(), VERIFIED.getStatus());
		
		logger.debug("Inside execute method:");
		ExecutionContext jobContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
		if (CollectionUtils.isNotEmpty(requestList)) {
			logger.info("before retriving data from repository");
			//requestList = dataProcessingRequestRepository.findByRequestTypeAndStatus(ACTUAL_REVENUE_UPLOAD.getType(), VERIFIED.getStatus());
			logger.info("request list size "+requestList.size());//for testing
		
		
		
		
		
			logger.info("executing the request list");
			//requestList = dataProcessingRequestRepository.findByRequestTypeAndStatus(ACTUAL_REVENUE_UPLOAD.getType(), VERIFIED.getStatus());

			DataProcessingRequestT request = requestList.remove(0);
			String filePath = request.getFilePath() + request.getFileName();
			
			logger.info("request id "+request.getProcessRequestId());
			
			//String userId = request.getUserT().getUserId();
			//Long requestId = request.getProcessRequestId();
			
		    jobContext.put(FILE_PATH,filePath);
		    jobContext.put(REQUEST,request);
		    jobContext.put(NEXT_STEP,REVENUE_PROCESSING);
			
		} else {
			
			logger.info("request list is empty");//for testing
			 jobContext.put(NEXT_STEP, END);
		}
		
		return RepeatStatus.FINISHED;
	}

}
