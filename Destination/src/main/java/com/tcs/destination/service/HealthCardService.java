package com.tcs.destination.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.tcs.destination.bean.CentreList;
import com.tcs.destination.bean.ClusterList;
import com.tcs.destination.bean.ContentDTO;
import com.tcs.destination.bean.DeliveryCentreT;
import com.tcs.destination.bean.DeliveryCentreUnallocationT;
import com.tcs.destination.bean.DeliveryCentreUtilizationT;
import com.tcs.destination.bean.DeliveryClusterT;
import com.tcs.destination.bean.HealthCardOverallPercentage;
import com.tcs.destination.bean.HealthCardValues;
import com.tcs.destination.bean.MobileDashboardT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.UnallocationAssociate;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.bean.dto.DeliveryCentreUtilizationDTO;
import com.tcs.destination.bean.dto.DeliveryClusterDTO;
import com.tcs.destination.data.repository.DeliveryCentreUnallocationRepository;
import com.tcs.destination.data.repository.DeliveryCentreUtilizationRepository;
import com.tcs.destination.data.repository.DeliveryClusterRepository;
import com.tcs.destination.data.repository.HealthCardOverallPercentageRepository;
import com.tcs.destination.data.repository.MobileDashboardRepository;
import com.tcs.destination.enums.DeliveryCentre;
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
	
	@Autowired
	HealthCardOverallPercentageRepository healthCardOverallPercentageRepository;

	@Autowired
	private DashBoardService dashBoardService;

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
	public Status insertNewComponentByuserID(List<Integer> componentId) {
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		Status status = new Status();
		Long orderNumer = mobileDashboardRepository.countByUserId(userId);
		List<Integer> availableComponentsByUserId = mobileDashboardRepository
				.getComponentsByUserId(userId);
		for (Integer cmpId : componentId) {
			if (availableComponentsByUserId.isEmpty()
					|| !(availableComponentsByUserId.toString()
							.contains(Integer.toString(cmpId).toString()))) {
				setMobileDashboardValues(cmpId, userId, orderNumer);
				status.setStatus(Status.SUCCESS, "Metrics added successfully");
				orderNumer++;
			}
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
	
	public ContentDTO<HealthCardValues> getHealthCardValues(Date fromDate, Date toDate,
			int type) {
		List<HealthCardValues> healthCardValues = Lists.newArrayList();
		ContentDTO<HealthCardValues> content = new ContentDTO<HealthCardValues>();
		Date startDate = fromDate != null ? fromDate : DateUtils
				.getFinancialYrStartDate();
		Date endDate = toDate != null ? toDate : new Date();
		List<HealthCardOverallPercentage> overallPercentages = healthCardOverallPercentageRepository
				.findByDateBetweenAndComponentIdOrderByDateDesc(startDate, endDate,type);
		List<DeliveryClusterT> clusterTs = clusterRepo.findAllExceptOpen();
		
		for (HealthCardOverallPercentage healthCardOverallPercentage : overallPercentages) {
			healthCardValues.add(constructHealthCardValues(healthCardOverallPercentage,type,clusterTs));
		}
		content.setContent(healthCardValues);
		return content;
	}

	private HealthCardValues constructHealthCardValues(
			HealthCardOverallPercentage healthCardOverallPercentage, int type, List<DeliveryClusterT> clusterTs) {
		HealthCardValues healthCardValues= new HealthCardValues();
		healthCardValues.setDate(healthCardOverallPercentage.getDate());
		healthCardValues.setOverallPercentage(DestinationUtils.
				scaleToTwoDigits(healthCardOverallPercentage.getOverallPercentage(), true));
		healthCardValues.setCategory(HealthCardComponent.getCategoryName(type));
		healthCardValues.setUploadedDate(healthCardOverallPercentage.getCreatedDatetime());
		List<ClusterList> clusterPercentage = Lists.newArrayList();
		for(DeliveryClusterT deliveryCluster : clusterTs) {
			List<DeliveryCentreUtilizationT> utilizationClusters = deliveryCentreUtilizationRepository.
					findByClusterIdAndOverallPercentageId(deliveryCluster.getDeliveryClusterId(),healthCardOverallPercentage.getOverallPercentageId());
			List<DeliveryCentreUtilizationT> utilizationCentres = Lists.newArrayList();
			List<CentreList> centrePercentage = Lists.newArrayList();
			for(DeliveryCentreT centre : deliveryCluster.getDeliveryCentreTs()) {
				utilizationCentres = deliveryCentreUtilizationRepository
						.findBydeliveryCentreIdAndOverallPercentageId(centre.getDeliveryCentreId(), healthCardOverallPercentage.getOverallPercentageId());
				for(DeliveryCentreUtilizationT deliveCentreUtilizationT : utilizationCentres) {
					centrePercentage.add(constructCentrePercentage(deliveCentreUtilizationT,type));
				}
			}
			if(CollectionUtils.isNotEmpty(utilizationClusters)) {
				clusterPercentage.add(constructClusterPercentage(
						utilizationClusters.get(0),type,deliveryCluster,centrePercentage));
			}
		}
		healthCardValues.setClusterList(clusterPercentage);
		return healthCardValues;
	}

	private ClusterList constructClusterPercentage(
			DeliveryCentreUtilizationT deliveryCentreUtilizationT, int type, DeliveryClusterT deliveryCluster, List<CentreList> centrePercentages) {
		ClusterList clusterPercentage = new ClusterList();
		clusterPercentage.setClusterPercentage(DestinationUtils.
				scaleToTwoDigits(deliveryCentreUtilizationT.getUtilizationPercentage(), true));
		clusterPercentage.setDeliveryCluster(deliveryCluster.getDeliveryCluster());
		clusterPercentage.setDeliveryClusterHead(deliveryCluster.getDeliveryClusterHeadUser().getUserName());
		clusterPercentage.setCentreList(centrePercentages);
		if(type==HealthCardComponent.UNALLOCATION.getCategoryId()) {
			clusterPercentage.setUnallocationAssociate(constructUnallocationAsssociate(deliveryCentreUtilizationT));
		}
		return clusterPercentage;
	}

	private CentreList constructCentrePercentage(
			DeliveryCentreUtilizationT deliveryCentreUtilizationT, int type) {
		CentreList centrePercentage = new CentreList();
		centrePercentage.setDeliveryCentreId(deliveryCentreUtilizationT.getDeliveryCentreId());
		centrePercentage.setDeliveryCentre(DeliveryCentre.getCentreNameFromCentreId(deliveryCentreUtilizationT.getDeliveryCentreId()));
		UserT deliveryCentreHeadUser = deliveryCentreUtilizationT.getDeliveryCentreT().getDeliveryCentreHeadUser();
		centrePercentage.setDeliveryCentreHead(deliveryCentreHeadUser!=null ? deliveryCentreHeadUser.getUserName(): null);
		centrePercentage.setDeliveryCentrePercentage(DestinationUtils.scaleToTwoDigits(deliveryCentreUtilizationT.getUtilizationPercentage(),true));
		if(type==HealthCardComponent.UNALLOCATION.getCategoryId()) {
			centrePercentage.setUnallocationAssociate(constructUnallocationAsssociate(deliveryCentreUtilizationT));
		}
		return centrePercentage;
	}

	private UnallocationAssociate constructUnallocationAsssociate(
			DeliveryCentreUtilizationT deliveCentreUtilizationT) {
		UnallocationAssociate unallocationAssociatePercentage = new UnallocationAssociate();
		unallocationAssociatePercentage.setJuniorPercentage(DestinationUtils.scaleToTwoDigits(deliveCentreUtilizationT.getJuniorPercentage(), true));
		unallocationAssociatePercentage.setSeniorPercentage(DestinationUtils.scaleToTwoDigits(deliveCentreUtilizationT.getSeniorPercentage(), true));
		unallocationAssociatePercentage.setTraineePercentage(DestinationUtils.scaleToTwoDigits(deliveCentreUtilizationT.getTraineePercentage(), true));
		return unallocationAssociatePercentage;
	}

	@Transactional
	public Status removeComponentInHealthCard(int componentId) {
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		Status status = new Status();
		List<MobileDashboardT> list = mobileDashboardRepository.findByUserIdAndDashboardCategoryOrderByOrderNumberAsc(userId, 1);
		List<MobileDashboardT> updatedList = Lists.newArrayList();
		
		int orderNumber = 1;
		for (MobileDashboardT mobileDashboardT : list) {
			// collect all component except the removing component
			if(mobileDashboardT.getComponentId().intValue() != componentId) { 
				MobileDashboardT updatedComp = new MobileDashboardT();
				updatedComp.setComponentId(mobileDashboardT.getComponentId());
				updatedComp.setDashboardCategory(mobileDashboardT.getDashboardCategory());
				updatedComp.setOrderNumber(orderNumber);
				updatedComp.setUserId(userId);
				updatedList.add(updatedComp);
				orderNumber++;
			}
		}
		
		if(CollectionUtils.isNotEmpty(updatedList)) {
			dashBoardService.updateMobileDashboard(new ContentDTO<MobileDashboardT>(updatedList));
			status.setStatus(Status.SUCCESS, "Component Successfully added");
		} else {
			status.setStatus(Status.FAILED, "Component Already Exist");
		}
		
		return status;
	}	
}
