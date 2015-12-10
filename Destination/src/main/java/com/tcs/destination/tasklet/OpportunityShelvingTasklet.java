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
 * This tasklet gives the functionality to update the opportunity sales stage to shelved.
 *
 */
@Component("opportunityShelvingTasklet")
public class OpportunityShelvingTasklet implements Tasklet {
	private static final Logger logger = LoggerFactory
			.getLogger(OpportunityShelvingTasklet.class);

	@Value("${opportunity.shelve.days}")
	private int shelveDays;

	@Value("${opportunity.shelve.updated.days}")
	private int shelveUpdateDays;

	@Autowired
	private BatchOpportunityRepository batchOpportunityRepository;

	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		logger.debug("Inside Execute method");

		RepeatStatus status = null;
		if (batchOpportunityRepository.updateOpportunityToShelve(shelveDays,
				shelveUpdateDays) == 1) {
			status = RepeatStatus.FINISHED;
		}
		return status;
	}

}
