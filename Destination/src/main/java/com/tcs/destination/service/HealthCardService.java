package com.tcs.destination.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.ContentDTO;
import com.tcs.destination.bean.DeliveryCentreUnallocationT;
import com.tcs.destination.bean.DeliveryCentreUtilizationT;
import com.tcs.destination.data.repository.DeliveryCentreUnallocationRepository;
import com.tcs.destination.data.repository.DeliveryCentreUtilizationRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.DateUtils;


@Service
public class HealthCardService {
	
	@Autowired
	DeliveryCentreUnallocationRepository deliveryCentreUnallocationRepository;
	
	@Autowired
	DeliveryCentreUtilizationRepository deliveryCentreUtilizationRepository;

	public ContentDTO<DeliveryCentreUnallocationT> getDeliveryCentreUnallocation(Date fromDate,
			Date toDate) {
		ContentDTO<DeliveryCentreUnallocationT> contentDTO = new ContentDTO<DeliveryCentreUnallocationT>();
		Date startDate = fromDate != null ? fromDate : DateUtils
				.getFinancialYrStartDate();
		Date endDate = toDate != null ? toDate : new Date();
		List<DeliveryCentreUnallocationT> unallocationTs = deliveryCentreUnallocationRepository
				.findByDateBetween(startDate, endDate);
		if (CollectionUtils.isNotEmpty(unallocationTs)) {
			contentDTO.setContent(unallocationTs);
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Unallocation Details not found");
		}
		return contentDTO;
	}
	public ContentDTO<DeliveryCentreUtilizationT> getDeliveryCentreUtilization(Date fromDate,
			Date toDate) {
		ContentDTO<DeliveryCentreUtilizationT> contentDTO = new ContentDTO<DeliveryCentreUtilizationT>();
		Date startDate = fromDate != null ? fromDate : DateUtils
				.getFinancialYrStartDate();
		Date endDate = toDate != null ? toDate : new Date();
		List<DeliveryCentreUtilizationT> utilizationTs = deliveryCentreUtilizationRepository
				.findByDateBetween(startDate, endDate);
		if (CollectionUtils.isNotEmpty(utilizationTs)) {
			contentDTO.setContent(utilizationTs);
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Utilization Details not found");
		}
		return contentDTO;
	}
}
