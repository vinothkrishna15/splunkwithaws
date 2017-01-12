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
import com.tcs.destination.bean.DeliveryCentreT;
import com.tcs.destination.bean.DeliveryCentreUnallocationT;
import com.tcs.destination.bean.DeliveryCentreUtilizationT;
import com.tcs.destination.bean.DeliveryClusterT;
import com.tcs.destination.bean.dto.DeliveryCentreUnallocationDTO;
import com.tcs.destination.bean.dto.DeliveryCentreUtilizationDTO;
import com.tcs.destination.bean.dto.DeliveryClusterDTO;
import com.tcs.destination.data.repository.DeliveryCentreUnallocationRepository;
import com.tcs.destination.data.repository.DeliveryCentreUtilizationRepository;
import com.tcs.destination.data.repository.DeliveryClusterRepository;
import com.tcs.destination.enums.HealthCardComponent;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;


@Service
public class HealthCardService {
	
	@Autowired
	DeliveryCentreUnallocationRepository deliveryCentreUnallocationRepository;
	
	@Autowired
	DeliveryCentreUtilizationRepository deliveryCentreUtilizationRepository;
	
	@Autowired
	DeliveryClusterRepository clusterRepo;
	
	@Autowired
	DozerBeanMapper beanMapper;

	public ContentDTO<DeliveryClusterDTO> getDeliveryCentreUnallocation(Date fromDate,
			Date toDate) {
		List<DeliveryClusterDTO> dtos = Lists.newArrayList();
		
		Date startDate = fromDate != null ? fromDate : DateUtils.getFinancialYrStartDate();
		Date endDate = toDate != null ? toDate : new Date();
		
		List<DeliveryClusterT> clusterTs = clusterRepo.findAllExceptOpen();
		for (DeliveryClusterT clusterT : clusterTs) {
			for (DeliveryCentreT deliveryCenter : clusterT.getDeliveryCentreTs()) {
				List<DeliveryCentreUnallocationT> unallocationTs = deliveryCentreUnallocationRepository.findByDeliveryCentreIdAndDateBetween(deliveryCenter.getDeliveryCentreId(), startDate, endDate);
				deliveryCenter.setUnallocationTs(unallocationTs);
			}
			DeliveryClusterDTO dto = beanMapper.map(clusterT, DeliveryClusterDTO.class, Constants.CLUSTER_UNALLOCATION_MAP);
			dtos.add(dto);
		}
		if (CollectionUtils.isEmpty(dtos)) {
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Unallocation Details not found");
		}
		return new ContentDTO<DeliveryClusterDTO>(dtos);
	}
	
	public ContentDTO<DeliveryCentreUtilizationDTO> getDeliveryCentreUtilization(Date fromDate,
			Date toDate) {
		List<DeliveryCentreUtilizationDTO> dtos = Lists.newArrayList();
		ContentDTO<DeliveryCentreUtilizationDTO> contentDTO = new ContentDTO<DeliveryCentreUtilizationDTO>();
		Date startDate = fromDate != null ? fromDate : DateUtils
				.getFinancialYrStartDate();
		Date endDate = toDate != null ? toDate : new Date();
		List<DeliveryCentreUtilizationT> utilizationTs = deliveryCentreUtilizationRepository
				.findByDateBetweenAndCategoryId(startDate, endDate,HealthCardComponent.UTILIZATION.getCategoryId());
		for (DeliveryCentreUtilizationT deliveryCentreUtilizationT : utilizationTs) {
			DeliveryCentreUtilizationDTO dto = beanMapper.map(deliveryCentreUtilizationT, DeliveryCentreUtilizationDTO.class, Constants.DELIVERY_UTILIZATION_MAP);
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
