package com.tcs.destination.tasklet;

import static com.tcs.destination.enums.JobStep.CONNECT_PROCESSING;
import static com.tcs.destination.enums.JobStep.END;
import static com.tcs.destination.enums.RequestStatus.SUBMITTED;
import static com.tcs.destination.enums.RequestType.CONNECT_UPLOAD;
import static com.tcs.destination.utils.Constants.FILE_PATH;
import static com.tcs.destination.utils.Constants.NEXT_STEP;
import static com.tcs.destination.utils.Constants.REQUEST;

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



@Component("connectPreprocessor")
public class ConnectPreprocessor implements Tasklet{
	
	private static final Logger logger = LoggerFactory
			.getLogger(ConnectPreprocessor.class);
	
	private List<DataProcessingRequestT> requestList = null;
	
	@Autowired
	private DataProcessingRequestRepository dataProcessingRequestRepository;

	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		
		logger.debug("Inside execute method:");
		
		if (requestList == null) {
			requestList = dataProcessingRequestRepository.findByRequestTypeAndStatus(CONNECT_UPLOAD.getType(), SUBMITTED.getStatus());
		}
		
		ExecutionContext jobContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
		
		if (CollectionUtils.isNotEmpty(requestList)) {
			
			DataProcessingRequestT request = requestList.remove(0);
			String filePath = request.getFilePath() + request.getFileName();
		   
		    jobContext.put(FILE_PATH,filePath);
		    jobContext.put(REQUEST,request);
		    jobContext.put(NEXT_STEP, CONNECT_PROCESSING);
			
		} else {
			 jobContext.put(NEXT_STEP, END);
		}
		
		return RepeatStatus.FINISHED;
	}

}
