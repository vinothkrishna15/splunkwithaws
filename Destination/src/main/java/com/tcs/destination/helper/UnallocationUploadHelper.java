package com.tcs.destination.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.parboiled.common.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.DeliveryCentreUtilizationT;
import com.tcs.destination.bean.HealthCardOverallPercentage;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.DeliveryCentreRepository;
import com.tcs.destination.data.repository.DeliveryCentreUtilizationRepository;
import com.tcs.destination.data.repository.DeliveryClusterRepository;
import com.tcs.destination.data.repository.HealthCardOverallPercentageRepository;
import com.tcs.destination.utils.Constants;

@Component("unallocationUploadHelper")
public class UnallocationUploadHelper {

	@Autowired
	DeliveryCentreUtilizationRepository deliveryCentreUtilizationRepository;

	@Autowired
	HealthCardOverallPercentageRepository healthCardOverallPercentageRepository;

	@Autowired
	DeliveryClusterRepository deliveryClusterRepository;

	@Autowired
	DeliveryCentreRepository deliveryCentreRepository;

	/**
	 * Method called to set values in overall percentage table
	 * 
	 * @param data
	 * @param userId
	 * @param healthCardOverallPercentage
	 * @return
	 * @throws ParseException
	 */
	public UploadServiceErrorDetailsDTO validateUnallocationData(String[] data,
			String userId,
			HealthCardOverallPercentage healthCardOverallPercentage)
			throws ParseException {
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		StringBuffer errorMsg = new StringBuffer();
		int rowNumber = Integer.parseInt(data[0]) + 1;

		String date = data[1] + "-" + data[2];
		String dateModified = date.replace(".0", "");
		String overallPercentage = data[3];

		String deliveryDate = "01-" + dateModified;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy");

		Date dateUnallocated = sdf.parse(deliveryDate);
		sdf = new SimpleDateFormat("MM/dd/yyyy");

		// Unallocation Date
		if (StringUtils.isNotEmpty(deliveryDate)
				&& !Constants.NA.equals(deliveryDate)) {
			healthCardOverallPercentage.setDate(sdf.parse(sdf
					.format(dateUnallocated)));
		} else {
			error.setRowNumber(rowNumber);
			errorMsg.append("Date Format is Invalid " + dateModified);
		}

		// Unallocation Overall Percentage
		if (StringUtils.isNotEmpty(overallPercentage)) {
			healthCardOverallPercentage.setOverallPercentage(new BigDecimal(
					overallPercentage).setScale(5, RoundingMode.HALF_UP));
		}

		// Component Id defaulted to Unallocation - 3
		healthCardOverallPercentage.setComponentId(3);

		return error;
	}

	/**
	 * To set the values in delivery centre utilisation table from the excel uploaded.
	 * @param data
	 * @param userId
	 * @param healthCardOverallPercentage
	 * @return
	 * @throws ParseException
	 */
	public UploadServiceErrorDetailsDTO validateUnallocationDeliveryCentreData(
			String[] data, String userId,
			DeliveryCentreUtilizationT deliveryCentreUtilizationT)
			throws ParseException {

		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		List<Object[]> healthCardData = new ArrayList<Object[]>();
		healthCardData = healthCardOverallPercentageRepository
				.getOverallPercentageIdAndDate();

		List<Object[]> deliveryCluster = deliveryClusterRepository
				.findDeliveryCluster();
		List<Object[]> deliveryCentre = deliveryCentreRepository
				.findDeliveryCentre();

		StringBuffer errorMsg = new StringBuffer();
		int rowNumber = Integer.parseInt(data[0]) + 1;

		String date = data[1] + "-" + data[2];
		String dateModified = date.replace(".0", "");
		String juniorPercentage = data[6];
		String seniorPercentage = data[5];
		String traineePercentage = data[4];
		String unallocationPercentage = data[3];
		String centreOrDelivery = data[7];

		String deliveryDate = "01-" + dateModified;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy");

		Date dateUnallocated = sdf.parse(deliveryDate);
		sdf = new SimpleDateFormat("MM/dd/yyyy");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		// Unallocation Date
		if (StringUtils.isNotEmpty(deliveryDate)
				&& !Constants.NA.equals(deliveryDate)) {
			deliveryCentreUtilizationT.setDate(sdf.parse(sdf
					.format(dateUnallocated)));
		} else {
			error.setRowNumber(rowNumber);
			errorMsg.append("Date Format is Invalid " + dateModified);
		}

		// Delivery Centre Id

		for (Object[] deliveryCent : deliveryCentre) {
			if (deliveryCent != null && deliveryCent[1] != null
					&& centreOrDelivery != null) {
				if (centreOrDelivery.equalsIgnoreCase(deliveryCent[1]
						.toString())) {
					deliveryCentreUtilizationT.setDeliveryCentreId(Integer
							.parseInt(deliveryCent[0].toString()));
				}
			}
		}

		// Cluster Id
		for (Object[] deliveryClust : deliveryCluster) {
			if (deliveryClust != null && deliveryClust[1] != null
					&& centreOrDelivery != null) {
				if (centreOrDelivery.equalsIgnoreCase(deliveryClust[1]
						.toString())) {
					deliveryCentreUtilizationT.setClusterId(Integer
							.parseInt(deliveryClust[0].toString()));
				}
			}
		}

		// Category Id Defaulted to 3 for Unallocation
		deliveryCentreUtilizationT.setCategoryId(3);

		// Overall Percentage Id
		for (Object[] healthCardvalue : healthCardData) {
			if (healthCardvalue != null && healthCardvalue[1] != null) {

				if (df.format(sdf.parse(sdf.format(dateUnallocated)))
						.equalsIgnoreCase(healthCardvalue[1].toString())) {
					deliveryCentreUtilizationT.setOverallPercentageId(Integer
							.parseInt(healthCardvalue[0].toString()));
				}
			}
		}

		// Junior Percentage
		if (StringUtils.isNotEmpty(juniorPercentage)) {
			deliveryCentreUtilizationT.setJuniorPercentage(new BigDecimal(
					juniorPercentage).setScale(5, RoundingMode.HALF_UP));
		} 

		// Senior Percentage
		if (StringUtils.isNotEmpty(seniorPercentage)) {
			deliveryCentreUtilizationT.setSeniorPercentage(new BigDecimal(
					seniorPercentage).setScale(5, RoundingMode.HALF_UP));
		}

		// Trainee Percentage
		if (StringUtils.isNotEmpty(traineePercentage)) {
			deliveryCentreUtilizationT.setTraineePercentage(new BigDecimal(
					traineePercentage).setScale(5, RoundingMode.HALF_UP));
		}

		// Unallocation Overall Percentage
		if (StringUtils.isNotEmpty(unallocationPercentage)) {
			deliveryCentreUtilizationT.setUtilizationPercentage(new BigDecimal(
					unallocationPercentage).setScale(5, RoundingMode.HALF_UP));
		} else {
			deliveryCentreUtilizationT
					.setUtilizationPercentage(BigDecimal.ZERO);
			error.setRowNumber(rowNumber);
			errorMsg.append("unallocationPercentage should ne be null "
					+ unallocationPercentage);
		}

		return error;

	}

}
