/**
 * 
 * BeaconDeleteProcessor.java 
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

import com.tcs.destination.bean.BeaconDataT;
import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.data.repository.BeaconDataTRepository;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.utils.DateUtils;

/**
 * This BeaconDeleteProcessor class contains the functionality to process the 
 * Beacon delete request
 * 
 */
@Component("beaconDeleteProcessor")
public class BeaconDeleteProcessor implements Tasklet {

	private static final Logger logger = LoggerFactory
			.getLogger(BeaconDeleteProcessor.class);

	private List<DataProcessingRequestT> requestList = null;

	@Autowired
	private DataProcessingRequestRepository dataProcessingRequestRepository;
	
	@Autowired
	private BeaconDataTRepository beaconDataRepo;

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
			List<String> quartersList = DateUtils.getAllQuartersBetween(deleteFrom, deleteTo);
			if(CollectionUtils.isNotEmpty(quartersList)){
				for(String quarter: quartersList){
					List<BeaconDataT> beaconDataList = (List<BeaconDataT>)beaconDataRepo.findByQuarter(quarter);
					if(CollectionUtils.isNotEmpty(beaconDataList)){
						beaconDataRepo.delete(beaconDataList);
					}
				}
			}
		}

		return RepeatStatus.FINISHED;
	}

}
