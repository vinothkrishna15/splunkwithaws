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
import com.tcs.destination.bean.MobileDashboardT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.dto.DeliveryCentreUnallocationDTO;
import com.tcs.destination.bean.dto.DeliveryCentreUtilizationDTO;
import com.tcs.destination.bean.dto.DeliveryClusterDTO;
import com.tcs.destination.data.repository.DeliveryCentreUnallocationRepository;
import com.tcs.destination.data.repository.DeliveryCentreUtilizationRepository;
import com.tcs.destination.data.repository.DeliveryClusterRepository;
import com.tcs.destination.data.repository.MobileDashboardRepository;
import com.tcs.destination.enums.HealthCardComponent;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationUtils;


@Service
public class HealthCardService {
	
	@Autowired
	DeliveryCentreUnallocationRepository deliveryCentreUnallocationRepository;
	
	@Autowired
	DeliveryCentreUtilizationRepository deliveryCentreUtilizationRepository;
	
	@Autowired
	MobileDashboardRepository mobileDashboardRepository;
	
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

	/**
	 * Main method called to insert new component into the health card for
	 * mobile dashboard
	 * 
	 * @param componentId
	 * @return status - containing status and description
	 */
	public Status insertNewComponentByuserID(int componentId) {
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		Status status = new Status();
		Long orderNumer = mobileDashboardRepository.countByUserId(userId);
		List<Integer> availableComponentsByUserId = mobileDashboardRepository
				.getComponentsByUserId(userId);
		if (availableComponentsByUserId.isEmpty()
				|| !(availableComponentsByUserId.toString().contains(Integer
						.toString(componentId).toString()))) {
			setMobileDashboardValues(componentId, userId, orderNumer);
			status.setStatus(Status.SUCCESS, "Component Successfully added");
		} else {
			status.setStatus(Status.FAILED, "Component Already Exist");

		}
		return status;
	}

	/**
	 * 
	 * Refactored method to set the table values.
	 * @param componentId
	 * @param userId
	 * @param orderNumer
	 */
	private void setMobileDashboardValues(int componentId, String userId,
			Long orderNumer) {
		MobileDashboardT mobileDashboardT = new MobileDashboardT();
		mobileDashboardT.setUserId(userId);
		mobileDashboardT.setComponentId(componentId);
		mobileDashboardT.setDashboardCategory(1);
		mobileDashboardT.setOrderNumber(orderNumer.intValue() + 1);
		mobileDashboardRepository.save(mobileDashboardT);
	}
}
