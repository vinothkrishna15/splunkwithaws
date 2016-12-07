package com.tcs.destination.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.tcs.destination.bean.ContentDTO;
import com.tcs.destination.bean.DeliveryCentreUnallocationT;
import com.tcs.destination.bean.DeliveryCentreUtilizationT;
import com.tcs.destination.bean.dto.DeliveryCentreUnallocationDTO;
import com.tcs.destination.bean.dto.DeliveryCentreUtilizationDTO;
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
	
	@Autowired
	DozerBeanMapper beanMapper;

	public ContentDTO<DeliveryCentreUnallocationDTO> getDeliveryCentreUnallocation(Date fromDate,
			Date toDate) {
		List<DeliveryCentreUnallocationDTO> dtos = Lists.newArrayList();
		ContentDTO<DeliveryCentreUnallocationDTO> contentDTO = new ContentDTO<DeliveryCentreUnallocationDTO>();
		Date startDate = fromDate != null ? fromDate : DateUtils
				.getFinancialYrStartDate();
		Date endDate = toDate != null ? toDate : new Date();
		List<DeliveryCentreUnallocationT> unallocationTs = deliveryCentreUnallocationRepository
				.findByDateBetween(startDate, endDate);
		for (DeliveryCentreUnallocationT deliveryCentreUnallocationT : unallocationTs) {
			DeliveryCentreUnallocationDTO dto = beanMapper.map(deliveryCentreUnallocationT, DeliveryCentreUnallocationDTO.class, "delivery-unallocation-map");
			dtos.add(dto);
		}
		if (CollectionUtils.isNotEmpty(dtos)) {
			contentDTO.setContent(dtos);
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Unallocation Details not found");
		}
		return contentDTO;
	}
	public ContentDTO<DeliveryCentreUtilizationDTO> getDeliveryCentreUtilization(Date fromDate,
			Date toDate) {
		List<DeliveryCentreUtilizationDTO> dtos = Lists.newArrayList();
		ContentDTO<DeliveryCentreUtilizationDTO> contentDTO = new ContentDTO<DeliveryCentreUtilizationDTO>();
		Date startDate = fromDate != null ? fromDate : DateUtils
				.getFinancialYrStartDate();
		Date endDate = toDate != null ? toDate : new Date();
		List<DeliveryCentreUtilizationT> utilizationTs = deliveryCentreUtilizationRepository
				.findByDateBetween(startDate, endDate);
		for (DeliveryCentreUtilizationT deliveryCentreUtilizationT : utilizationTs) {
			DeliveryCentreUtilizationDTO dto = beanMapper.map(deliveryCentreUtilizationT, DeliveryCentreUtilizationDTO.class, "delivery-utilization-map");
			dtos.add(dto);
		}
		
		if (CollectionUtils.isNotEmpty(dtos)) {
			contentDTO.setContent(dtos);
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Utilization Details not found");
		}
		return contentDTO;
	}
}
