package com.tcs.destination.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.parboiled.common.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.DeliveryCentreUnallocationT;
import com.tcs.destination.bean.HealthCardOverallPercentage;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.DeliveryCentreUnallocationRepository;
import com.tcs.destination.data.repository.ServicePracticeRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.utils.Constants;

@Component("unallocationUploadHelper")
public class UnallocationUploadHelper {


	@Autowired
	ServicePracticeRepository servicePracticeRepository;

	@Autowired
	SubSpRepository subSpRepository;

	@Autowired
	DeliveryCentreUnallocationRepository deliveryCentreUnallocationRepository;

	/**
	 *  Method called to set values in overall percentage table
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
	 * 
	 * @param data
	 * @param userId
	 * @param healthCardOverallPercentage
	 * @return
	 * @throws ParseException 
	 */
	public UploadServiceErrorDetailsDTO validateUnallocationDeliveryCentreData(String[] data,
			String userId,
			DeliveryCentreUnallocationT deliveryCentreUnallocationT) throws ParseException {
		

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
			deliveryCentreUnallocationT.setDate(sdf.parse(sdf
					.format(dateUnallocated)));
		} else {
			error.setRowNumber(rowNumber);
			errorMsg.append("Date Format is Invalid " + dateModified);
		}

		// Unallocation Overall Percentage
		if (StringUtils.isNotEmpty(overallPercentage)) {
			deliveryCentreUnallocationT.setJuniorPercentage(new BigDecimal(
					overallPercentage).setScale(5, RoundingMode.HALF_UP));
		}

		// Component Id defaulted to Unallocation - 3
		//deliveryCentreUnallocationT.setComponentId(3);

		return error;
	
	}

}
