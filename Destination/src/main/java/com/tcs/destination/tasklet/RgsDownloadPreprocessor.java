package com.tcs.destination.tasklet;

import static com.tcs.destination.enums.JobStep.END;
import static com.tcs.destination.enums.JobStep.RGS_DWLD_PROCESSING;
import static com.tcs.destination.enums.RequestStatus.SUBMITTED;
import static com.tcs.destination.enums.RequestType.RGS_DOWNLOAD;
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

/**
 * Preprocessor For RGS Download-> Checks the RGS Download request and put it in
 * the job context
 * 
 * @author TCS
 *
 */
@Component("rgsDownloadPreprocessor")
public class RgsDownloadPreprocessor implements Tasklet {

	private static final Logger logger = LoggerFactory
			.getLogger(RgsDownloadPreprocessor.class);

	private List<DataProcessingRequestT> requestList = null;

	@Autowired
	private DataProcessingRequestRepository dataProcessingRequestRepository;

	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		logger.debug("Inside execute method:");
		if (requestList == null) {
			requestList = dataProcessingRequestRepository
					.findByRequestTypeAndStatus(RGS_DOWNLOAD.getType(),
							SUBMITTED.getStatus());
		}

		ExecutionContext jobContext = chunkContext.getStepContext()
				.getStepExecution().getJobExecution().getExecutionContext();

		if (CollectionUtils.isNotEmpty(requestList)) {
			DataProcessingRequestT request = requestList.remove(0);
			jobContext.put(REQUEST, request);
			jobContext.put(NEXT_STEP, RGS_DWLD_PROCESSING);
		} else {
			jobContext.put(NEXT_STEP, END);
			requestList = null;
		}
		return RepeatStatus.FINISHED;
	}

}
