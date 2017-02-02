package com.tcs.destination.writer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.tcs.destination.utils.Constants.FILE_PATH;
import static com.tcs.destination.utils.Constants.REQUEST;

import javax.servlet.WriteListener;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.DeliveryCentreUtilizationT;
import com.tcs.destination.bean.HealthCardOverallPercentage;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.data.repository.DeliveryCentreRepository;
import com.tcs.destination.data.repository.DeliveryCentreUtilizationRepository;
import com.tcs.destination.data.repository.HealthCardOverallPercentageRepository;

import static com.tcs.destination.enums.DeliveryCentre.BANGALORE;
import static com.tcs.destination.enums.DeliveryCentre.CHENNAI;
import static com.tcs.destination.enums.DeliveryCentre.DELHI;
import static com.tcs.destination.enums.DeliveryCentre.HYDERABAD;
import static com.tcs.destination.enums.DeliveryCentre.KOCHI;
import static com.tcs.destination.enums.DeliveryCentre.KOLKATA;
import static com.tcs.destination.enums.DeliveryCentre.MUMBAI;
import static com.tcs.destination.enums.DeliveryCentre.PUNE;

import com.tcs.destination.enums.HealthCardComponent;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.utils.DateUtils;

public class UtilisationSheetWriter implements ItemWriter<String[]>,
		StepExecutionListener, WriteListener {

	private static final Logger logger = LoggerFactory
			.getLogger(UtilisationSheetWriter.class);

	private DataProcessingRequestRepository dataProcessingRequestRepository;

	private DeliveryCentreUtilizationRepository deliveryCentreUtilizationRepository;

	private Map<Integer, Integer> deliveryCentreMap = null;

	@Autowired
	DeliveryCentreRepository deliveryCentreRepository;

	@Autowired
	HealthCardOverallPercentageRepository healthCardOverallPercentageRepository;

	@Override
	public void write(List<? extends String[]> items) throws Exception {
		logger.info("Inside Write method");
		if (deliveryCentreMap == null) {
			deliveryCentreMap = getDeliveryCentreMap();
		}

		List<DeliveryCentreUtilizationT> deliveryCentreUtilizationTs = Lists
				.newArrayList();
		for (String[] data : items) {

			// get Date from month and year
			String month = data[1];
			String year = data[2];
			String overallPercentString = data[3];
			String category = data[15];
			String dateStr = month + " " + year;
			Date date = DateUtils.parse(dateStr, DateUtils.FORMAT_MMMMM_YYYY);
			HealthCardOverallPercentage overallPercentage = saveOverallPecentage(
					overallPercentString, category, date);
			for (int i = 4; i <= 11; i++) {
				constructUtilization(deliveryCentreUtilizationTs, data[i],
						date, deliveryCentreMap.get(i), category,
						overallPercentage);
			}
			constructUtilizationCluster(deliveryCentreUtilizationTs, data,
					category, overallPercentage, date);
			if (CollectionUtils.isNotEmpty(deliveryCentreUtilizationTs)) {
				deliveryCentreUtilizationRepository
						.save(deliveryCentreUtilizationTs);
				logger.info("Data Saved!!!");
			}
		}
	}

	private void constructUtilizationCluster(
			List<DeliveryCentreUtilizationT> deliveryCentreUtilizationTs,
			String[] data, String category,
			HealthCardOverallPercentage overallPercentage, Date date) {
		constructUtilizationCluster(deliveryCentreUtilizationTs, data[12],
				date, 1, category, overallPercentage);
		constructUtilizationCluster(deliveryCentreUtilizationTs, data[13],
				date, 3, category, overallPercentage);
		constructUtilizationCluster(deliveryCentreUtilizationTs, data[14],
				date, 2, category, overallPercentage);
	}

	private HealthCardOverallPercentage saveOverallPecentage(
			String overallPercentString, String category, Date date) {
		HealthCardOverallPercentage healthCardOverallPercentage = new HealthCardOverallPercentage();
		healthCardOverallPercentage.setComponentId(HealthCardComponent
				.valueOfCategory(category).getCategoryId());
		healthCardOverallPercentage.setDate(date);
		healthCardOverallPercentage.setOverallPercentage(new BigDecimal(
				overallPercentString));
		healthCardOverallPercentageRepository.save(healthCardOverallPercentage);
		return healthCardOverallPercentage;
	}

	private Map<Integer, Integer> getDeliveryCentreMap() {
		Map<Integer, Integer> map = Maps.newHashMap();
		List<String> dc = Lists.newArrayList(MUMBAI.getDeliveryCentre(),
				PUNE.getDeliveryCentre(), BANGALORE.getDeliveryCentre(),
				KOLKATA.getDeliveryCentre(), HYDERABAD.getDeliveryCentre(),
				CHENNAI.getDeliveryCentre(), DELHI.getDeliveryCentre(),
				KOCHI.getDeliveryCentre());
		int i = 4;
		for (String deliveryCentre : dc) {
			map.put(i,
					deliveryCentreRepository.findByDeliveryCentre(
							deliveryCentre).getDeliveryCentreId());
			i++;
		}
		return map;

	}

	private void constructUtilization(
			List<DeliveryCentreUtilizationT> deliveryCentreUtilizationTs,
			String percentageString, Date date, Integer deliveryCentreId,
			String category, HealthCardOverallPercentage overallPercentage) {
		if(percentageString.contains("%")) {
			percentageString = percentageString.substring(0, percentageString.length()-1);
		}
		BigDecimal centrePercentage = new BigDecimal(percentageString);
		deliveryCentreUtilizationTs.add(constructUtilization(centrePercentage,
				deliveryCentreId, date, category,
				overallPercentage.getOverallPercentageId(), null));
	}

	private DeliveryCentreUtilizationT constructUtilization(
			BigDecimal utilizationPercentage, Integer deliveryCentreId,
			Date date, String category, Integer overallPercentageId,
			Integer deliveryClusterId) {
		DeliveryCentreUtilizationT deliveryCentreUtilizationT = new DeliveryCentreUtilizationT();
		if (deliveryCentreId != null) {
			deliveryCentreUtilizationT.setDeliveryCentreId(deliveryCentreId);
		}
		if (deliveryClusterId != null) {
			deliveryCentreUtilizationT.setClusterId(deliveryClusterId);
		}
		deliveryCentreUtilizationT
				.setUtilizationPercentage(utilizationPercentage);
		deliveryCentreUtilizationT.setDate(date);
		deliveryCentreUtilizationT.setCategoryId(HealthCardComponent
				.valueOfCategory(category).getCategoryId());
		deliveryCentreUtilizationT.setOverallPercentageId(overallPercentageId);
		return deliveryCentreUtilizationT;
	}

	private void constructUtilizationCluster(
			List<DeliveryCentreUtilizationT> deliveryCentreUtilizationTs,
			String percentageString, Date date, Integer deliveryClusterId,
			String category, HealthCardOverallPercentage overallPercentage) {
		if(percentageString.contains("%")) {
			percentageString = percentageString.substring(0, percentageString.length()-1);
		}
		BigDecimal centrePercentage = new BigDecimal(percentageString);
		deliveryCentreUtilizationTs.add(constructUtilization(centrePercentage,
				null, date, category,
				overallPercentage.getOverallPercentageId(), deliveryClusterId));
	}

	@Override
	public void onWritePossible() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(Throwable throwable) {
		logger.error("Error while writing the error report: {}", throwable);

	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		try {

			DataProcessingRequestT request = (DataProcessingRequestT) stepExecution
					.getJobExecution().getExecutionContext().get(REQUEST);
			request.setStatus(RequestStatus.INPROGRESS.getStatus());
			dataProcessingRequestRepository.save(request);

		} catch (Exception e) {
			logger.error("Error in before step process: {}", e);
		}

	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		try {

			ExecutionContext jobContext = stepExecution.getJobExecution()
					.getExecutionContext();

			DataProcessingRequestT request = (DataProcessingRequestT) jobContext
					.get(REQUEST);

			request.setStatus(RequestStatus.PROCESSED.getStatus());
			dataProcessingRequestRepository.save(request);

			jobContext.remove(REQUEST);
			jobContext.remove(FILE_PATH);
		} catch (Exception e) {
			logger.error("Error while writing the error report: {}", e);
		}

		return ExitStatus.COMPLETED;
	}

	public DataProcessingRequestRepository getDataProcessingRequestRepository() {
		return dataProcessingRequestRepository;
	}

	public void setDataProcessingRequestRepository(
			DataProcessingRequestRepository dataProcessingRequestRepository) {
		this.dataProcessingRequestRepository = dataProcessingRequestRepository;
	}

	public DeliveryCentreUtilizationRepository getDeliveryCentreUtilizationRepository() {
		return deliveryCentreUtilizationRepository;
	}

	public void setDeliveryCentreUtilizationRepository(
			DeliveryCentreUtilizationRepository deliveryCentreUtilizationRepository) {
		this.deliveryCentreUtilizationRepository = deliveryCentreUtilizationRepository;
	}

	public DeliveryCentreRepository getDeliveryCentreRepository() {
		return deliveryCentreRepository;
	}

	public void setDeliveryCentreRepository(
			DeliveryCentreRepository deliveryCentreRepository) {
		this.deliveryCentreRepository = deliveryCentreRepository;
	}

	public void setDeliveryCentreMap(Map<Integer, Integer> deliveryCentreMap) {
		this.deliveryCentreMap = deliveryCentreMap;
	}

}
