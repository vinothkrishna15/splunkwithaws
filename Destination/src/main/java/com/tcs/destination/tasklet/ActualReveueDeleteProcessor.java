/**
 * 
 * ActualReveueDeleteProcessor.java 
 *
 * @author TCS
 * @Version 1.0 - 2015
 * 
 * @Copyright 2015 Tata Consultancy 
 */
package com.tcs.destination.tasklet;

import static com.tcs.destination.utils.Constants.REQUEST;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.parboiled.common.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.ActualRevenuesDataT;
import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.data.repository.ActualRevenuesDataTRepository;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.utils.DateUtils;

/**
 * This ActualReveueDeleteProcessor class contains the functionality to process the 
 * Actual revenue delete request
 * 
 */
@Component("revenueDeleteProcessor")
public class ActualReveueDeleteProcessor implements Tasklet {

	private static final Logger logger = LoggerFactory
			.getLogger(ActualReveueDeleteProcessor.class);

	private List<DataProcessingRequestT> requestList = null;

	@Autowired
	private DataProcessingRequestRepository dataProcessingRequestRepository;
	
	@Autowired
	private ActualRevenuesDataTRepository actualsRepo;

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

		ExecutionContext jobContext = chunkContext.getStepContext()
				.getStepExecution().getJobExecution().getExecutionContext();
		
		DataProcessingRequestT request = (DataProcessingRequestT) jobContext.get(REQUEST);
		String deleteFrom = request.getDeleteFrom();
		String deleteTo = request.getDeleteTo();
		if(!StringUtils.isEmpty(deleteFrom) && !StringUtils.isEmpty(deleteTo)){
			List<String> monthsList = DateUtils.getAllMonthsBetween(deleteFrom, deleteTo);
			if(CollectionUtils.isNotEmpty(monthsList)){
				for(String month: monthsList){
					List<ActualRevenuesDataT> actualDataList = (List<ActualRevenuesDataT>)actualsRepo.findByMonth(month);
					if(CollectionUtils.isNotEmpty(actualDataList)){
						actualsRepo.delete(actualDataList);
					}
				}
			}
		}


		return RepeatStatus.FINISHED;
	}

}
