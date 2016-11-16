package com.tcs.destination.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tcs.destination.bean.AsyncJobRequest;
import com.tcs.destination.bean.DeliveryCentreT;
import com.tcs.destination.bean.DeliveryClusterT;
import com.tcs.destination.bean.DeliveryFulfillment;
import com.tcs.destination.bean.DeliveryIntimatedCentreLinkT;
import com.tcs.destination.bean.DeliveryIntimatedT;
import com.tcs.destination.bean.DeliveryMasterDTO;
import com.tcs.destination.bean.DeliveryMasterManagerLinkT;
import com.tcs.destination.bean.DeliveryMasterT;
import com.tcs.destination.bean.DeliveryRequirementT;
import com.tcs.destination.bean.DeliveryResourcesT;
import com.tcs.destination.bean.DeliveryRgsT;
import com.tcs.destination.bean.EngagementDashboardDTO;
import com.tcs.destination.bean.OpportunityDeliveryCentreMappingT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.PageDTO;
import com.tcs.destination.bean.SearchResultDTO;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.DeliveryCentreRepository;
import com.tcs.destination.data.repository.DeliveryClusterRepository;
import com.tcs.destination.data.repository.DeliveryIntimatedCentreLinkRepository;
import com.tcs.destination.data.repository.DeliveryIntimatedPagingRepository;
import com.tcs.destination.data.repository.DeliveryIntimatedRepository;
import com.tcs.destination.data.repository.DeliveryMasterManagerLinkRepository;
import com.tcs.destination.data.repository.DeliveryMasterPagingRepository;
import com.tcs.destination.data.repository.DeliveryMasterRepository;
import com.tcs.destination.data.repository.DeliveryRequirementRepository;
import com.tcs.destination.data.repository.DeliveryResourcesRepository;
import com.tcs.destination.data.repository.DeliveryRgsTRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.DeliveryStage;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.enums.JobName;
import com.tcs.destination.enums.SmartSearchType;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.PaginationUtils;

/**
 * handle service functionalities for delivery
 * 
 * @author TCS
 *
 */
@Service
public class DeliveryMasterService {

	private static final Logger logger = LoggerFactory.getLogger(DeliveryMasterService.class);

	@Autowired
	DeliveryMasterRepository deliveryMasterRepository;

	@Autowired
	DeliveryMasterPagingRepository deliveryMasterPagingRepository;

	@Autowired
	DeliveryCentreRepository deliveryCentreRepository;

	@Autowired
	DeliveryClusterRepository deliveryClusterRepository;

	@Autowired
	DeliveryResourcesRepository deliveryResourcesRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	DeliveryMasterManagerLinkRepository deliveryMasterManagerLinkRepository;

	@Autowired
	DeliveryRequirementRepository deliveryRequirementRepository;

	@Autowired
	DeliveryRgsTRepository deliveryRgsTRepository;

	@Autowired
	OpportunityService opportunityService;
	
	@Autowired
	DeliveryIntimatedCentreLinkRepository deliveryIntimatedCentreLinkRepository;
	
	@Autowired
	DeliveryIntimatedPagingRepository deliveryIntimatedPagingRepository;
	
	@Autowired
	DeliveryIntimatedRepository deliveryIntimatedRepository;
	
	
	private static final Map<String,String>ATTRIBUTE_MAP;
	static {
		Map<String, String> attributeMap = new HashMap<String, String>();
		attributeMap.put("deliveryMasterId", "deliveryMasterId");
		attributeMap.put("opportunityId", "opportunityId");
		attributeMap.put("customerName", "opportunityT.customerMasterT.customerName");
		attributeMap.put("opportunityName", "opportunityT.opportunityName");
		attributeMap.put("opportunityDescription", "opportunityT.opportunityDescription");
		attributeMap.put("engagementStartDate", "opportunityT.engagementStartDate");
		attributeMap.put("engagementDuration", "opportunityT.engagementDuration");
		attributeMap.put("deliveryOwnership","opportunityT.deliveryOwnershipT.ownership");
		attributeMap.put("stage","deliveryStage");
		attributeMap.put("deliveryCentre","deliveryCentreT.deliveryCentre");
		attributeMap.put("modifiedDatetime","modifiedDatetime");
		attributeMap.put("deliveryIntimatedId", "deliveryIntimatedId");
		ATTRIBUTE_MAP = Collections.unmodifiableMap(attributeMap);
	}

	/**
	 * method to retrieve List of engagements
	 * @param stages
	 * @param orderBy
	 * @param order
	 * @param start
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public PageDTO<DeliveryMasterT> findEngagements(List<Integer> stages,String orderBy,String order,
			int page, int count) throws Exception {
		PageDTO<DeliveryMasterT> deliveryMasterDTO = null;

		logger.debug("Starting findEngagements deliveryMasterService");

		UserT loginUser = DestinationUtils.getCurrentUserDetails();
		String loginUserGroup = loginUser.getUserGroup();

		List<Integer> requiredStages = new ArrayList<Integer>();
		
		Page<DeliveryMasterT> deliveryMasterTs = null;
		Sort sort = null;
		Pageable pageable = null;
		switch (UserGroup.valueOf(UserGroup.getName(loginUserGroup))) {
		case DELIVERY_CENTRE_HEAD:
			requiredStages = getRequiredStages(stages, DeliveryStage.ACCEPTED.getStageCode());
			
			DeliveryCentreT deliveryCentreT = deliveryCentreRepository
					.findByDeliveryCentreHead(loginUser.getUserId());
			if (deliveryCentreT != null) {
				Integer deliveryCentreId = deliveryCentreT
						.getDeliveryCentreId();

				List<Integer> deliveryCentreIds = new ArrayList<Integer>();
				deliveryCentreIds.add(deliveryCentreId);
				deliveryCentreIds.add(-1);

				orderBy = ATTRIBUTE_MAP.get(orderBy);
				sort = getSortFromOrder(order,orderBy);
				pageable = new PageRequest(page, count, sort);

				deliveryMasterTs = deliveryMasterPagingRepository
						.findByDeliveryCentreIdInAndDeliveryStageIn(
								deliveryCentreIds, requiredStages, pageable);

			}
			break;
		case STRATEGIC_INITIATIVES:
			requiredStages = getRequiredStages(stages, DeliveryStage.ACCEPTED.getStageCode());
			
			List<DeliveryCentreT> deliveryCentresSI = (List<DeliveryCentreT>) deliveryCentreRepository.findAll();
			 if(!CollectionUtils.isEmpty(deliveryCentresSI)){
					List<Integer> deliveryCentreIds = new ArrayList<Integer>();
					for (DeliveryCentreT deliveryCentre : deliveryCentresSI) {
						deliveryCentreIds.add(deliveryCentre.getDeliveryCentreId());
					}
					orderBy = ATTRIBUTE_MAP.get(orderBy);
					sort = getSortFromOrder(order,orderBy);
					pageable = new PageRequest(page, count, sort);
					deliveryMasterTs = deliveryMasterPagingRepository
								.findByDeliveryCentreIdInAndDeliveryStageIn(
										deliveryCentreIds, requiredStages, pageable);
						
					
			 }
			 
			 break;
		case DELIVERY_CLUSTER_HEAD:
			
			requiredStages = getRequiredStages(stages, DeliveryStage.ACCEPTED.getStageCode());
			
			DeliveryClusterT deliveryClusterT = deliveryClusterRepository
					.findByDeliveryClusterHead(loginUser.getUserId());
			if(deliveryClusterT!=null){
			List<DeliveryCentreT> deliveryCentres = deliveryCentreRepository
					.findByDeliveryClusterId(deliveryClusterT
							.getDeliveryClusterId());
            if(!CollectionUtils.isEmpty(deliveryCentres)){
			List<Integer> deliveryCentreIds = new ArrayList<Integer>();
			for (DeliveryCentreT deliveryCentre : deliveryCentres) {
				deliveryCentreIds.add(deliveryCentre.getDeliveryCentreId());
			}
			deliveryCentreIds.add(-1);
			
			orderBy = ATTRIBUTE_MAP.get(orderBy);
			sort = getSortFromOrder(order,orderBy);
			pageable = new PageRequest(page, count, sort);
			deliveryMasterTs = deliveryMasterPagingRepository
					.findByDeliveryCentreIdInAndDeliveryStageIn(
							deliveryCentreIds, requiredStages, pageable);
            }
			}
			break;
		case DELIVERY_MANAGER:
			requiredStages = getRequiredStages(stages, DeliveryStage.ASSIGNED.getStageCode());
			
			orderBy = ATTRIBUTE_MAP.get(orderBy);
			sort = getSortFromOrder(order,orderBy);
			pageable = new PageRequest(page, count, sort);
			String managerId = loginUser.getUserId();
			List<DeliveryMasterManagerLinkT> deliveryMasterManagerList = deliveryMasterManagerLinkRepository.findByDeliveryManagerId(managerId);
			if(CollectionUtils.isEmpty(deliveryMasterManagerList)){
				logger.error("NOT_FOUND: Delivery Master details not found");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"Delivery Master details not found");
			} else {
			List<String> deliveryMasterIds = new ArrayList<String>();
			for(DeliveryMasterManagerLinkT deliveryMasterManagerLinkT:deliveryMasterManagerList){
			deliveryMasterIds.add(deliveryMasterManagerLinkT.getDeliveryMasterId());
			}
			deliveryMasterTs = deliveryMasterPagingRepository
			.findByDeliveryMasterIdInAndDeliveryStageIn(
			deliveryMasterIds, requiredStages, pageable);
			}
			break;
		default:
			break;
		}
		deliveryMasterDTO = new PageDTO<DeliveryMasterT>();
		if (deliveryMasterTs != null) {
			deliveryMasterDTO.setContent(deliveryMasterTs.getContent());
			deliveryMasterDTO.setTotalCount(new Long(deliveryMasterTs
					.getTotalElements()).intValue());
		} else {
			logger.error("NOT_FOUND: Delivery Master Details not found:");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Delivery Master not found: ");
		}
		return deliveryMasterDTO;
	}


	private List<Integer> getRequiredStages(List<Integer> stages, Integer stageFrom) {
		 List<Integer> requiredStages = Lists.newArrayList();
		if (stages.contains(new Integer(-1))) {
			for (int i = stageFrom.intValue(); i < DeliveryStage.getTotalNumberOfStages(); i++)
				requiredStages.add(i);
		} else {
			requiredStages.addAll(stages);
		}
		return requiredStages;
	}
	
	
	/**
	 * returns the sort object for given order by column and the order direction
	 * @param order
	 * @param orderBy
	 * @return
	 */
	private Sort getSortFromOrder(String order, String orderBy) {
		Sort sort = null;
		if (order.equalsIgnoreCase("DESC")) {
			sort = new Sort(Direction.DESC, orderBy);
		} else {
			sort = new Sort(Direction.ASC, orderBy);
		}
		return sort;
	}


	/**
	 * To fetch delivery master details by delivery master id
	 * 
	 * @param deliveryMasterId
	 * @return
	 * @throws Exception
	 */
	public DeliveryMasterT findByDeliveryMasterId(String deliveryMasterId) throws Exception {
		logger.debug("Inside findByDeliveryMasterId() service");
		DeliveryMasterT deliveryMaster = deliveryMasterRepository.findOne(deliveryMasterId);
		if (deliveryMaster != null) {
			removeCyclicData(deliveryMaster);
			return deliveryMaster;
		} else {
			logger.error("NOT_FOUND: Delivery Master Details not found: {}", deliveryMasterId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Delivery Master not found: " + deliveryMasterId);
		}
	}

	/**
	 * To fetch delivery intimated details by delivery intimated id
	 * 
	 * @param deliveryIntiId
	 * @return
	 * @throws Exception
	 */
	public DeliveryIntimatedT findByDeliveryIntimatedId(String deliveryIntiId) throws Exception {
		logger.debug("Inside findByDeliveryIntimatedId() service");
		DeliveryIntimatedT deliveryIntimated = deliveryIntimatedRepository.findOne(deliveryIntiId);
		if (deliveryIntimated != null) {
			removeCyclicReferenceOfDeliveryIntimated(Lists.newArrayList(deliveryIntimated));
			return deliveryIntimated;
		} else {
			logger.error("NOT_FOUND: Delivery intimated Details not found: {}", deliveryIntiId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Delivery intimated not found: " + deliveryIntiId);
		}
	}

	/**
	 * This method removes the List<DeliveryMasterT> cyclic data 
	 * 
	 * @param deliveryMasterTList
	 */
	private void removeCyclicData(List<DeliveryMasterT> deliveryMasterTList) {
		for(DeliveryMasterT deliveryMasterT: deliveryMasterTList){
			removeCyclicData(deliveryMasterT);
		}
	}

	/**
	 * This method removes the DeliveryMasterT cyclic data 
	 * 
	 * @param deliveryMaster
	 */
	private void removeCyclicData(DeliveryMasterT deliveryMaster) {
		List<DeliveryMasterManagerLinkT> managres = deliveryMaster.getDeliveryMasterManagerLinkTs();
		if(CollectionUtils.isNotEmpty(managres)){
			for (DeliveryMasterManagerLinkT deliveryMasterManagerLinkT : managres) {
				deliveryMasterManagerLinkT.setDeliveryMasterT(null);
			}
		}
		List<DeliveryResourcesT> deliveryResourcesTs = deliveryMaster.getDeliveryResourcesTs();
		if(CollectionUtils.isNotEmpty(deliveryResourcesTs)){
			for(DeliveryResourcesT deliveryResourcesT:deliveryResourcesTs){
				DeliveryRgsT deliveryRgsT = deliveryResourcesT.getDeliveryRgsT();
				if (deliveryRgsT != null) {
					List<DeliveryRequirementT> deliveryRequirementTs = deliveryRgsT
							.getDeliveryRequirementTs();
					if (!CollectionUtils.isEmpty(deliveryRequirementTs)) {
						for (DeliveryRequirementT deliveryRequirementT : deliveryRequirementTs) {
							deliveryRequirementT.setDeliveryRgsT(null);
						}
					}
				}
			}
		}
	}


	@Transactional
	public List<AsyncJobRequest> updateDelivery(DeliveryMasterT deliveryMaster)
			throws Exception {

		List<AsyncJobRequest> asyncJobRequests = Lists.newArrayList();

		logger.debug("Inside updateDelivery() service");
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();

		deliveryMaster.setModifiedBy(DestinationUtils.getCurrentUserDetails()
				.getUserId());

		String deliveryMasterId = deliveryMaster.getDeliveryMasterId();

		if (deliveryMasterId == null) {
			logger.error("BAD_REQUEST: deliveryMasterId is required for update");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"deliveryMasterId is required for update");
		}

		// Check if delivery Master exists
		if (!deliveryMasterRepository.exists(deliveryMasterId)) {
			logger.error(
					"NOT_FOUND: DeliveryMaster Record not found for update: {}",
					deliveryMasterId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Engagement not found for update: " + deliveryMasterId);
		}
		DeliveryMasterT deliveryBeforeEdit = deliveryMasterRepository
				.findOne(deliveryMasterId);

		Integer oldDeliveryStage = deliveryBeforeEdit.getDeliveryStage();
		Integer oldDeliveryCentreId = deliveryBeforeEdit.getDeliveryCentreId();

		// Update database
		DeliveryMasterT deliveryAfterEdit = editDelivery(deliveryMaster,
				deliveryBeforeEdit, userId);
		if (deliveryAfterEdit != null) {
			logger.info("Engagement has been updated successfully: "
					+ deliveryMasterId);
			if ((deliveryAfterEdit.getDeliveryStage() != oldDeliveryStage && deliveryAfterEdit
					.getDeliveryStage() != DeliveryStage.PLANNED.getStageCode())
					|| (deliveryAfterEdit.getDeliveryCentreId() != oldDeliveryCentreId && deliveryAfterEdit
					.getDeliveryCentreId() == Constants.DELIVERY_CENTRE_OPEN)) {
				asyncJobRequests.add(opportunityService
						.constructAsyncJobRequest(
								deliveryAfterEdit.getDeliveryMasterId(),
								EntityType.DELIVERY,
								JobName.deliveryEmailNotification, null,
								deliveryAfterEdit
								.getDeliveryCentreId() == Constants.DELIVERY_CENTRE_OPEN ? 
										oldDeliveryCentreId : deliveryAfterEdit.getDeliveryCentreId()));
			}
		}

		return asyncJobRequests;

	}

	@Transactional
	public DeliveryMasterT editDelivery(DeliveryMasterT deliveryMasterT,DeliveryMasterT deliveryBeforeEdit, String loginUserId)
			throws Exception {

		List<DeliveryResourcesT> deliveryResourcesTs = deliveryMasterT
				.getDeliveryResourcesTs();
		List<DeliveryMasterManagerLinkT> deliveryMasterManagerLinkTs = deliveryMasterT
				.getDeliveryMasterManagerLinkTs();
		validateDeliveryMaster(deliveryMasterT);
		// populateDeliveryMaster(deliveryMasterT);
		try {
			if (deliveryResourcesTs != null) {
				for(DeliveryResourcesT deliveryResourcesT : deliveryResourcesTs){
					deliveryResourcesT.setModifiedBy(loginUserId);
				}
				deliveryResourcesRepository.save(deliveryResourcesTs);
			}

			if (deliveryMasterManagerLinkTs != null) {
				for(DeliveryMasterManagerLinkT deliveryMasterManagerLinkT : deliveryMasterManagerLinkTs){
					deliveryMasterManagerLinkT.setModifiedBy(loginUserId);
				}
				deliveryMasterManagerLinkRepository.save(deliveryMasterManagerLinkTs);
			}

			if (deliveryResourcesTs != null) {
				for(DeliveryResourcesT deliveryResourcesT:deliveryResourcesTs){
					DeliveryRgsT deliveryRgsT = deliveryResourcesT.getDeliveryRgsT();
					if(deliveryRgsT!=null){
						List<DeliveryRequirementT> deliveryRequirementTs = deliveryRgsT.getDeliveryRequirementTs();
						if(!CollectionUtils.isEmpty(deliveryRequirementTs)){
							for(DeliveryRequirementT deliveryRequirementT : deliveryRequirementTs){
								deliveryRequirementT.setModifiedBy(loginUserId);
							}
							deliveryRequirementRepository.save(deliveryRequirementTs);
						}
					}
				}
			}

			return (deliveryMasterRepository.save(deliveryMasterT));
		} catch (Exception e) {
			logger.error("Server Error: Backend Error while processing delivery update ");
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Server Error: Backend Error while processing delivery update ");
		}
	}


	private void validateDeliveryMaster(DeliveryMasterT deliveryMasterT) {

		//validate mandatory fields - deliveryCentreId,deliveryStage
		Integer deliveryCentreId = deliveryMasterT.getDeliveryCentreId();
		if(deliveryCentreId == null) {
			logger.error("BAD_REQUEST: deliveryCentreId is mandatory");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"deliveryCentreId is mandatory");
		}

		Integer deliveryStage = deliveryMasterT.getDeliveryStage();
		if(deliveryStage == null){
			logger.error("BAD_REQUEST: deliveryStage is mandatory");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"deliveryStage is mandatory");			
		}

		String createdBy = deliveryMasterT.getCreatedBy();
		if(StringUtils.isEmpty(createdBy)){
			logger.error("BAD_REQUEST: createdBy is mandatory");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"createdBy is mandatory");			
		}

		Timestamp createdDatetime = deliveryMasterT.getCreatedDatetime();
		if(createdDatetime == null){
			logger.error("BAD_REQUEST: createdDatetime is mandatory");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"createdDatetime is mandatory");			
		}

		List<DeliveryMasterManagerLinkT> deliveryMasterManagerLinkTs = deliveryMasterT.getDeliveryMasterManagerLinkTs();
		if(!CollectionUtils.isEmpty(deliveryMasterManagerLinkTs)){
			for(DeliveryMasterManagerLinkT deliveryMasterManagerLinkT:deliveryMasterManagerLinkTs){
				String deliveryManagerId = deliveryMasterManagerLinkT.getDeliveryManagerId();
				if(StringUtils.isEmpty(deliveryManagerId)){
					logger.error("BAD_REQUEST: deliveryManagerId is mandatory");
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"deliveryManagerId is mandatory");
				} else {
					if(!userRepository.exists(deliveryManagerId)){
						logger.error("BAD_REQUEST: invalid deliveryManagerId");
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"invalid deliveryManagerId");
					} else {
						UserT user = userRepository.findOne(deliveryManagerId);
						String userGroup = user.getUserGroup();
						if(!UserGroup.DELIVERY_MANAGER.getValue().equalsIgnoreCase(userGroup)){
							logger.error("BAD_REQUEST: invalid deliveryManagerId");
							throw new DestinationException(HttpStatus.BAD_REQUEST,
									"invalid deliveryManagerId");
						}
					}
				}
				String deliveryMasterId = deliveryMasterManagerLinkT.getDeliveryMasterId();
				if(deliveryMasterId==null){
					logger.error("BAD_REQUEST: deliveryMasterId is mandatory");
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"deliveryMasterId is mandatory");
				} else {
					if(!deliveryMasterRepository.exists(deliveryMasterId)){
						logger.error("BAD_REQUEST: invalid deliveryMasterId");
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"invalid deliveryMasterId");
					}
				}

				String masterManagerCreatedBy = deliveryMasterManagerLinkT.getCreatedBy();
				if(StringUtils.isEmpty(masterManagerCreatedBy)){
					logger.error("BAD_REQUEST: createdBy is mandatory in deliveryMasterManagerLinkT");
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"createdBy is mandatory in deliveryMasterManagerLinkT");			
				}

				Timestamp masterManagerCreatedDatetime = deliveryMasterManagerLinkT.getCreatedDatetime();
				if(masterManagerCreatedDatetime == null){
					logger.error("BAD_REQUEST: createdDatetime is mandatory in deliveryMasterManagerLinkT");
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"createdDatetime is mandatory in deliveryMasterManagerLinkT");			
				}

			}
		}

		List<DeliveryResourcesT> deliveryResourcesTs = deliveryMasterT.getDeliveryResourcesTs();
		if(!CollectionUtils.isEmpty(deliveryResourcesTs)){
			for(DeliveryResourcesT deliveryResourcesT:deliveryResourcesTs){
				String deliveryResourcesCreatedBy = deliveryResourcesT.getCreatedBy();
				if(StringUtils.isEmpty(deliveryResourcesCreatedBy)){
					logger.error("BAD_REQUEST: createdBy is mandatory in deliveryResourcesT");
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"createdBy is mandatory in deliveryResourcesT");
				}

				Timestamp deliveryResourcesCreatedDatetime = deliveryResourcesT.getCreatedDatetime();
				if(deliveryResourcesCreatedDatetime == null){
					logger.error("BAD_REQUEST: createdDatetime is mandatory in deliveryResourcesT");
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"createdDatetime is mandatory in deliveryResourcesT");			
				}

				String deliveryMasterId = deliveryResourcesT.getDeliveryMasterId();
				if(deliveryMasterId==null){
					logger.error("BAD_REQUEST: deliveryMasterId is mandatory");
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"deliveryMasterId is mandatory in deliveryResourcesT");
				} else {
					if(!deliveryMasterRepository.exists(deliveryMasterId)){
						logger.error("BAD_REQUEST: invalid deliveryMasterId");
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"invalid deliveryMasterId in deliveryResourcesT");
					}
				}

				String role = deliveryResourcesT.getRole();
				if(StringUtils.isEmpty(role)){
					logger.error("BAD_REQUEST: role is mandatory in deliveryResourcesT");
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"role is mandatory in deliveryResourcesT");
				}

				String skill = deliveryResourcesT.getSkill();
				if(StringUtils.isEmpty(skill)){
					logger.error("BAD_REQUEST: skill is mandatory in deliveryResourcesT");
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"skill is mandatory in deliveryResourcesT");
				}

				String requirementFulfillment = deliveryResourcesT.getRequirementFulfillment();
				if(StringUtils.isEmpty(requirementFulfillment)){
					logger.error("BAD_REQUEST: requirementFulfillment is mandatory in deliveryResourcesT");
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"requirementFulfillment is mandatory in deliveryResourcesT");
				}

				DeliveryRgsT deliveryRgsT = deliveryResourcesT.getDeliveryRgsT();
				if(deliveryRgsT!=null){
					List<DeliveryRequirementT> deliveryRequirementTs = deliveryRgsT.getDeliveryRequirementTs();
					if(!CollectionUtils.isEmpty(deliveryRequirementTs)){
						for(DeliveryRequirementT deliveryRequirementT : deliveryRequirementTs){
							//String employeeId = deliveryRequirementT.getEmployeeId();
							//String employeeName = deliveryRequirementT.getEmployeeName();
							String createdBy2 = deliveryRequirementT.getCreatedBy();

							if(StringUtils.isEmpty(createdBy2)){
								logger.error("BAD_REQUEST: createdBy is mandatory in deliveryRequirementT");
								throw new DestinationException(HttpStatus.BAD_REQUEST,
										"createdBy is mandatory in deliveryRequirementT");
							}
							
							if(deliveryResourcesCreatedDatetime == null){
								logger.error("BAD_REQUEST: createdDatetime is mandatory in deliveryRequirementT");
								throw new DestinationException(HttpStatus.BAD_REQUEST,
										"createdDatetime is mandatory in deliveryRequirementT");			
							}

							String experience = deliveryRequirementT.getExperience();
							if(StringUtils.isEmpty(experience)){
								logger.error("BAD_REQUEST: experience is mandatory in deliveryRequirementT");
								throw new DestinationException(HttpStatus.BAD_REQUEST,
										"experience is mandatory in deliveryRequirementT");
							}

							String location = deliveryRequirementT.getLocation();
							if(StringUtils.isEmpty(location)){
								logger.error("BAD_REQUEST: location is mandatory in deliveryRequirementT");
								throw new DestinationException(HttpStatus.BAD_REQUEST,
										"location is mandatory in deliveryRequirementT");
							}

							String requirementId = deliveryRequirementT.getRequirementId();
							if(StringUtils.isEmpty(requirementId)){
								logger.error("BAD_REQUEST: requirementId is mandatory in deliveryRequirementT");
								throw new DestinationException(HttpStatus.BAD_REQUEST,
										"requirementId is mandatory in deliveryRequirementT");
							}

							String role2 = deliveryRequirementT.getRole();
							if(StringUtils.isEmpty(role2)){
								logger.error("BAD_REQUEST: role is mandatory in deliveryRequirementT");
								throw new DestinationException(HttpStatus.BAD_REQUEST,
										"role is mandatory in deliveryRequirementT");
							}

							String status = deliveryRequirementT.getStatus();
							if(StringUtils.isEmpty(status)){
								logger.error("BAD_REQUEST: status is mandatory in deliveryRequirementT");
								throw new DestinationException(HttpStatus.BAD_REQUEST,
										"status is mandatory in deliveryRequirementT");
							}
						}
					}
				}
			}
		}

	}


	/**
	 * This method is used to save the delivery master details for each delivery centre 
	 * 
	 * @param opportunity
	 * @param opportunityDeliveryCentreMappingT
	 */
	public void createDeliveryMaster(OpportunityT opportunity,
			OpportunityDeliveryCentreMappingT opportunityDeliveryCentreMappingT) {
		logger.info("Inside saveDeliveryMaster() method");
		DeliveryMasterT deliveryMasterT= new DeliveryMasterT();
		deliveryMasterT.setOpportunityId(opportunity.getOpportunityId());
		deliveryMasterT.setDeliveryCentreId(opportunityDeliveryCentreMappingT.getDeliveryCentreId());
		deliveryMasterT.setDeliveryStage(0);
		deliveryMasterT.setCreatedBy(Constants.SYSTEM_USER);
		deliveryMasterT.setModifiedBy(Constants.SYSTEM_USER);
		deliveryMasterRepository.save(deliveryMasterT);
	}

	public List<String> searchByDeliveryRgsId(String idLike, int limitNum) throws Exception {
		logger.debug("Inside searchByDeliveryRgsId() service");
		List<String> response = deliveryRgsTRepository.findByRgsIdPattern(idLike + '%', limitNum);
		if (response != null && response.size()>0) {
			return response;
		} else {
			logger.error("NOT_FOUND: Delivery Rgs Id not found for pattern:", idLike);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Delivery Rgs Id not found for pattern: " + idLike);
		}
	}

	/**
	 * Service to retrieve all the delivery managers under provided delivery centers
	 * 
	 * @param deliveryCentres
	 * @param nameWith
	 * @return
	 */
	public Set<UserT> findDeliveryCentreUserList(List<Integer> deliveryCentres,String nameWith) {
		Set<UserT> usersForDeliveryCentre = new HashSet<UserT>();

		List<DeliveryCentreT> deliveryCentresList = deliveryCentreRepository
				.findByDeliveryCentreIdIn(deliveryCentres);

		if (CollectionUtils.isNotEmpty(deliveryCentresList)) {
			List<String> deliveryHeads = Lists.newArrayList();
			for (DeliveryCentreT deliveryCentre : deliveryCentresList) {
				String deliveryCentreHead = deliveryCentre.getDeliveryCentreHead();
				// get all delivery managers for a delivery centre head
				if (StringUtils.isNotEmpty(deliveryCentreHead)) {
					deliveryHeads.add(deliveryCentreHead);
				} else {
					deliveryHeads.add(deliveryCentre.getDeliveryClusterT()
							.getDeliveryClusterHead());
				}
			}
			// retrieve users under this delivery centre head whose user group
			// is delivery manager
			usersForDeliveryCentre = userRepository
					.findBySupervisorUserIdInAndUserGroupAndUserNameIgnoreCaseContaining(
							deliveryHeads, Constants.DELIVERY_MANAGER, nameWith);
		}
		else {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"The given Delivery Centre not found");
		}
		return usersForDeliveryCentre;
	}


	/**
	 * This method is used to fetch delivery master details 
	 * 
	 * @param smartSearchType
	 * @param term
	 * @param getAll
	 * @param page
	 * @param count
	 * @param user
	 * @param stages 
	 * @return
	 */
	public PageDTO<SearchResultDTO<DeliveryMasterT>> deliveryMasterSmartSearch(
			SmartSearchType smartSearchType, String term, boolean getAll,
			int page, int count, UserT user, List<Integer> stages) {
		logger.info("DeliveryMasterService::smartSearch type {}", smartSearchType);
		Set<DeliveryMasterT> deliveryMasterSet = Sets.newHashSet();
		List<DeliveryMasterT> deliveryMasterTs = Lists.newArrayList();
		PageDTO<SearchResultDTO<DeliveryMasterT>> res = new PageDTO<SearchResultDTO<DeliveryMasterT>>();
		List<SearchResultDTO<DeliveryMasterT>> resList = Lists.newArrayList();
		SearchResultDTO<DeliveryMasterT> searchResultDTO = new SearchResultDTO<DeliveryMasterT>();
		if (smartSearchType != null) {

			switch (smartSearchType) {
			case ALL:
				deliveryMasterSet.addAll(getDeliveryMasterById(term, getAll, user, stages));
				deliveryMasterSet.addAll(getDeliveryMasterByCustName(term, getAll, user, stages));
				deliveryMasterSet.addAll(getDeliveryMasterByDeliveryCentres(term, getAll, user, stages));
				deliveryMasterTs.addAll(deliveryMasterSet);
				break;
			case ID:
				deliveryMasterTs = getDeliveryMasterById(term, getAll, user, stages);
				break;
			case CUSTOMER:
				deliveryMasterTs = getDeliveryMasterByCustName(term, getAll, user, stages);
				break;
			case DELIVERY_CENTRE:
				deliveryMasterTs = getDeliveryMasterByDeliveryCentres(term, getAll, user, stages);
				break;
			default:
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid search type");
			}

			// paginate the result if it is fetching entire record(ie.getAll=true)
			List<DeliveryMasterT> records = PaginationUtils.paginateList(
					page, count, deliveryMasterTs);
			if (CollectionUtils.isNotEmpty(records)) {
				removeCyclicData(records);
			}
			searchResultDTO.setValues(records);
			searchResultDTO.setSearchType(smartSearchType);
			res.setTotalCount(deliveryMasterTs.size());
			resList.add(searchResultDTO);
		}
		res.setContent(resList);
		return res;
	}


	/**
	 * This method is used to fetch delivery master details for the delivery centres
	 * 
	 * @param term
	 * @param getAll
	 * @param user
	 * @param stages 
	 * @return
	 */
	private List<DeliveryMasterT> getDeliveryMasterByDeliveryCentres(
			String term, boolean getAll, UserT user, List<Integer> stages) {
		logger.info("Inside getDeliveryMasterById() Method");

		List<DeliveryMasterT> records = null;
		String userGroup = user.getUserGroup();
		if (userGroup.equals(UserGroup.DELIVERY_CLUSTER_HEAD.getValue())) {
			
			records = deliveryMasterRepository.searchDeliveryClusterDetailsByDeliveryCentres("%" + term + "%", getAll, user.getUserId(), stages);
		
		} else if(userGroup.equals(UserGroup.DELIVERY_CENTRE_HEAD.getValue())){
			
			records = deliveryMasterRepository.searchDeliveryCentreDetailsByDeliveryCentres("%" + term + "%", getAll, user.getUserId(), stages);
		
		} else if(userGroup.equals(UserGroup.DELIVERY_MANAGER.getValue())){
			
			records = deliveryMasterRepository.searchDeliveryManagerDetailsByDeliveryCentres("%" + term + "%", getAll, user.getUserId(), stages);

		} else if(userGroup.equals(UserGroup.STRATEGIC_INITIATIVES.getValue())) {
			
			records = deliveryMasterRepository.searchForSIDetailsByCentres("%" + term + "%", getAll, stages);
					
		} else {
			logger.info("HttpStatus.UNAUTHORIZED, Access Denied");
			throw new DestinationException(HttpStatus.UNAUTHORIZED, "Access Denied");
		}
		return records;
	}

	/**
	 * This method is used to fetch delivery master details for the given customer name
	 * 
	 * @param term
	 * @param getAll
	 * @param user
	 * @param stages 
	 * @return
	 */
	private List<DeliveryMasterT> getDeliveryMasterByCustName(
			String term, boolean getAll, UserT user, List<Integer> stages) {
		logger.info("Inside getDeliveryMasterById() Method");
		List<DeliveryMasterT> records = null;
		String userGroup = user.getUserGroup();
		if (userGroup.equals(UserGroup.DELIVERY_CLUSTER_HEAD.getValue())) {
			
			records = deliveryMasterRepository.searchDeliveryClusterDetailsByCustomerName("%" + term + "%", getAll, user.getUserId(), stages);
		
		} else if(userGroup.equals(UserGroup.DELIVERY_CENTRE_HEAD.getValue())){
			
			records = deliveryMasterRepository.searchDeliveryCentreDetailsByCustomerName("%" + term + "%", getAll, user.getUserId(), stages);
		
		} else if(userGroup.equals(UserGroup.DELIVERY_MANAGER.getValue())){
			
			records = deliveryMasterRepository.searchDeliveryManagerDetailsByCustomerName("%" + term + "%", getAll, user.getUserId(), stages);

		} else if(userGroup.equals(UserGroup.STRATEGIC_INITIATIVES.getValue())) {
			
			records = deliveryMasterRepository.searchForSIDetailsByCustomerName("%" + term + "%", getAll, stages);

		} else {
			logger.info("HttpStatus.UNAUTHORIZED, Access Denied");
			throw new DestinationException(HttpStatus.UNAUTHORIZED, "Access Denied");
		}
		return records;
	}

	/**
	 * This method is used to fetch delivery master details for the given opportunity id
	 * 
	 * @param term
	 * @param getAll
	 * @param user
	 * @param stages 
	 * @return
	 */
	private List<DeliveryMasterT> getDeliveryMasterById(String term,
			boolean getAll, UserT user, List<Integer> stages) {
		logger.info("Inside getDeliveryMasterById() Method");
		List<DeliveryMasterT> records = null;
		String userGroup = user.getUserGroup();
		if (userGroup.equals(UserGroup.DELIVERY_CLUSTER_HEAD.getValue())) {
			
			records = deliveryMasterRepository.searchDeliveryClusterDetailsById("%" + term + "%", getAll, user.getUserId(), stages);
		
		} else if(userGroup.equals(UserGroup.DELIVERY_CENTRE_HEAD.getValue())){
			
			records = deliveryMasterRepository.searchDeliveryCentreDetailsById("%" + term + "%", getAll, user.getUserId(), stages);
		
		} else if(userGroup.equals(UserGroup.DELIVERY_MANAGER.getValue())){
			
			records = deliveryMasterRepository.searchDeliveryManagerDetailsById("%" + term + "%", getAll, user.getUserId(), stages);

		} else if(userGroup.equals(UserGroup.STRATEGIC_INITIATIVES.getValue())) {
			
			records = deliveryMasterRepository.searchForSIDetailsById("%" + term + "%", getAll, stages);

		} else {
			logger.info("HttpStatus.UNAUTHORIZED, Access Denied");
			throw new DestinationException(HttpStatus.UNAUTHORIZED, "Access Denied");
		}
		return records;
	}


	/**
	 * This method is used to fetch the delivery engagements for dash board based on the parameter viewBy
	 * @param stage
	 * @param viewBy
	 */
	public DeliveryMasterDTO findEngagements(Integer stage, String viewBy) {
		logger.debug("Starting findEngagements deliveryMasterService");
		DeliveryMasterDTO deliveryDashboardDTO = null;
		UserT loginUser = DestinationUtils.getCurrentUserDetails();
		String loginUserGroup = loginUser.getUserGroup();
		List<Integer> deliveryCentreIds = null;
		List<Integer> stages = new ArrayList<Integer>();
		List<String> deliveryMasterIds = new ArrayList<String>();
		Integer acceptedStageCode = DeliveryStage.ACCEPTED.getStageCode();
		switch (UserGroup.valueOf(UserGroup.getName(loginUserGroup))) {
		case DELIVERY_CENTRE_HEAD:
			if (stage == -1) {
				for (int i = acceptedStageCode; i < DeliveryStage.getTotalNumberOfStages(); i++)
					stages.add(i);
			} else {
				stages.add(stage);
			}

			DeliveryCentreT deliveryCentreT = deliveryCentreRepository
					.findByDeliveryCentreHead(loginUser.getUserId());
			if (deliveryCentreT != null) {
				deliveryCentreIds = new ArrayList<Integer>();
				deliveryCentreIds.add(deliveryCentreT
						.getDeliveryCentreId());
				deliveryCentreIds.add(-1);
			}
			deliveryDashboardDTO = retrieveEngagementsBasedOnViewBy(viewBy, deliveryCentreIds, stages, deliveryMasterIds);
			break;
		case STRATEGIC_INITIATIVES:
			if (stage == -1) {
				for (int i = acceptedStageCode; i < DeliveryStage.getTotalNumberOfStages(); i++)
					stages.add(i);
			} else {
				stages.add(stage);
			}

			List<DeliveryCentreT> deliveryCentresSI = (List<DeliveryCentreT>) deliveryCentreRepository.findAll();
			if(!CollectionUtils.isEmpty(deliveryCentresSI)){
				deliveryCentreIds = new ArrayList<Integer>();
				for (DeliveryCentreT deliveryCentre : deliveryCentresSI) {
					deliveryCentreIds.add(deliveryCentre.getDeliveryCentreId());
				}
			}
			deliveryDashboardDTO = retrieveEngagementsBasedOnViewBy(viewBy, deliveryCentreIds, stages, deliveryMasterIds);
			break;
		case DELIVERY_CLUSTER_HEAD:

			if (stage == -1) {
				
				for (int i = acceptedStageCode; i < DeliveryStage.getTotalNumberOfStages(); i++)
					stages.add(i);
			} else {
				stages.add(stage);
			}

			DeliveryClusterT deliveryClusterT = deliveryClusterRepository
					.findByDeliveryClusterHead(loginUser.getUserId());
			if(deliveryClusterT!=null){
				List<DeliveryCentreT> deliveryCentres = deliveryCentreRepository
						.findByDeliveryClusterId(deliveryClusterT
								.getDeliveryClusterId());
				if(!CollectionUtils.isEmpty(deliveryCentres)){
					deliveryCentreIds = new ArrayList<Integer>();
					for (DeliveryCentreT deliveryCentre : deliveryCentres) {
						deliveryCentreIds.add(deliveryCentre.getDeliveryCentreId());
					}
					deliveryCentreIds.add(-1);
				}
			}
			deliveryDashboardDTO = retrieveEngagementsBasedOnViewBy(viewBy, deliveryCentreIds, stages, deliveryMasterIds);
			break;
		case DELIVERY_MANAGER:
			if (stage == -1) {
				for (int i = DeliveryStage.ASSIGNED.getStageCode(); i < DeliveryStage.getTotalNumberOfStages(); i++)
					stages.add(i);
			} else {
				stages.add(stage);
			}
			String managerId = loginUser.getUserId();
			List<DeliveryMasterManagerLinkT> deliveryMasterManagerList = deliveryMasterManagerLinkRepository.findByDeliveryManagerId(managerId);
			if(CollectionUtils.isEmpty(deliveryMasterManagerList)){
				logger.error("NOT_FOUND: Delivery Master details not found");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"Delivery Master details not found");
			} else {
				for(DeliveryMasterManagerLinkT deliveryMasterManagerLinkT:deliveryMasterManagerList){
					deliveryMasterIds.add(deliveryMasterManagerLinkT.getDeliveryMasterId());
				}
			}
			deliveryDashboardDTO = retrieveEngagementsBasedOnViewBy(viewBy, deliveryCentreIds, stages, deliveryMasterIds);
			break;
		default:
			break;
		}
		if (deliveryDashboardDTO != null) {

		} else {
			logger.error("NOT_FOUND: Delivery Master Details not found:");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Delivery Master not found: ");
		}
		return deliveryDashboardDTO;
	}

	/**
	 * This method retrieves the engagements for dash board based on delivery stage, geography, subsp
	 * @param viewBy
	 * @param deliveryCentreIds
	 * @param stages
	 * @return
	 */
	private DeliveryMasterDTO retrieveEngagementsBasedOnViewBy(String viewBy,
			List<Integer> deliveryCentreIds, List<Integer> stages, List<String> deliveryMasterIds) {
		List<Object[]> deliveryMasterTs = null;
		DeliveryMasterDTO deliveryDashboardDTO = null;
		EngagementDashboardDTO engagementDashboardDTO = null;

		switch (viewBy) {
		case Constants.ENGAGEMENT_BY_GEOGRAPHY:
			if (deliveryMasterIds.size() > 0) {
				deliveryMasterTs = deliveryMasterRepository.findEngagementByGeographyForDM(deliveryMasterIds,stages);
			} else {
				deliveryMasterTs = deliveryMasterRepository.findEngagementByGeography(deliveryCentreIds,stages);
			}
			break;
		case Constants.ENGAGEMENT_BY_SUBSP :
			if (deliveryMasterIds.size() > 0) {
				deliveryMasterTs = deliveryMasterRepository.findEngagementBySubspForDM(deliveryMasterIds,stages);

			} else {
				deliveryMasterTs = deliveryMasterRepository.findEngagementBySubsp(deliveryCentreIds,stages);
			}
			break;
		case Constants.ENGAGEMENT_BY_STATUS:
		default:
			if (deliveryMasterIds.size() > 0) {
				deliveryMasterTs = deliveryMasterRepository.findEngagementByDeliveryStageForDM(deliveryMasterIds,stages);
			} else {
				deliveryMasterTs = deliveryMasterRepository.findEngagementByDeliveryStage(deliveryCentreIds,stages);
			}
			break;
		}
		if (deliveryMasterTs.size() > 0) {
			deliveryDashboardDTO = new DeliveryMasterDTO();
			List<EngagementDashboardDTO> engagementList = new ArrayList<EngagementDashboardDTO>();
			deliveryDashboardDTO.setViewEngagementBy(viewBy);
			for (Object[] deliveryMaster : deliveryMasterTs) {
				engagementDashboardDTO = new EngagementDashboardDTO();
				engagementDashboardDTO.setEngagementGroupedBy(deliveryMaster[0].toString());
				engagementDashboardDTO.setEngagementCount(deliveryMaster[1].toString());
				engagementList.add(engagementDashboardDTO);
			}
			deliveryDashboardDTO.setEngagementList(engagementList);
		}
		return deliveryDashboardDTO;
	}


	/**
	 * Returns the delivery fulfilled and open count
	 * @param monthStartDate
	 * @param subSp
	 * @return
	 */
	public List<DeliveryFulfillment> getDeliveryFulfillmentGraph(
			Date monthStartDate, String subSp) {
		logger.info("Inside getDeliveryFulfillmentGraph method");
		Date currentDate = new Date();
		List<DeliveryFulfillment> deliveryFulfillment = Lists.newArrayList();
		//getting no of weeks in a month
		Integer noOfWeeksInMonth = DateUtils
				.getNumberOfWeeksInMonth(monthStartDate);
		for (int weekNumber = 1; weekNumber <= noOfWeeksInMonth; weekNumber++) {
			//getting week start date and end date for each week
			Map<String, Date> weekDateMap = DateUtils.getWeekDates(
					monthStartDate, weekNumber);
			Date weekStartDate = weekDateMap.get(DateUtils.WEEK_START_DATE);
			Date weekEndDate = weekDateMap.get(DateUtils.WEEK_END_DATE);
			if (weekEndDate.before(currentDate)) {
				// gets only fulfilled requirement
				List<DeliveryRequirementT> fulfilledRequirement = deliveryRequirementRepository
						.getFulfilledRequirement(subSp, weekStartDate,
								weekEndDate);
				deliveryFulfillment.add(constructDeliveryFulfillment(
						fulfilledRequirement.size(), 0, weekNumber, false));

			} else if (weekStartDate.after(currentDate)) {
				// gets only open requirement
				List<DeliveryRequirementT> openRequirement = deliveryRequirementRepository
						.getOpenRequirement(subSp, weekStartDate, weekEndDate);
				deliveryFulfillment.add(constructDeliveryFulfillment(0,
						openRequirement.size(), weekNumber, false));

			} else {
				//gets both the fulfilled and open requirement if the current date falls within the week start date
				//and week end date
				List<DeliveryRequirementT> fulfilledRequirement = deliveryRequirementRepository
						.getFulfilledRequirement(subSp, weekStartDate,
								weekEndDate);
				List<DeliveryRequirementT> openRequirement = deliveryRequirementRepository
						.getOpenRequirement(subSp, weekStartDate, weekEndDate);
				deliveryFulfillment.add(constructDeliveryFulfillment(
						fulfilledRequirement.size(), openRequirement.size(),
						weekNumber, true));
			}
		}
		return deliveryFulfillment;
	}


	private DeliveryFulfillment constructDeliveryFulfillment(int fullfilledCount,
			int openCount, int weekNumber, boolean isCurrentWeek) {
		DeliveryFulfillment deliveryFulfillment = new DeliveryFulfillment();
		deliveryFulfillment.setCurrentWeek(isCurrentWeek);
		deliveryFulfillment.setFulfilledCount(fullfilledCount);
		deliveryFulfillment.setOpenCount(openCount);
		deliveryFulfillment.setWeekNumber(weekNumber);
		return deliveryFulfillment;
	}


	public PageDTO<DeliveryIntimatedT> getDeliveryIntimated(String orderBy, String order, int page,
			int count) {
		PageDTO<DeliveryIntimatedT> deliveryIntimatedDTO = null;

		logger.debug("Starting getDeliveryIntimated deliveryMasterService");

		UserT loginUser = DestinationUtils.getCurrentUserDetails();
		String loginUserGroup = loginUser.getUserGroup();

		Page<DeliveryIntimatedT> deliveryIntimatedTs = null;
		Sort sort = null;
		Pageable pageable = null;
		switch (UserGroup.valueOf(UserGroup.getName(loginUserGroup))) {
		case STRATEGIC_INITIATIVES:
			List<DeliveryCentreT> deliveryCentresSI = (List<DeliveryCentreT>) deliveryCentreRepository
					.findAll();
			if (!CollectionUtils.isEmpty(deliveryCentresSI)) {
				List<Integer> deliveryCentreIds = new ArrayList<Integer>();
				List<String> deliveryIntimatedIds = Lists.newArrayList();
				for (DeliveryCentreT deliveryCentre : deliveryCentresSI) {
					deliveryCentreIds.add(deliveryCentre.getDeliveryCentreId());
				}
				deliveryCentreIds.add(-1);
				deliveryIntimatedIds = deliveryIntimatedCentreLinkRepository
						.getDeliveryIntimatedIdsByCentreIds(deliveryCentreIds);
				orderBy = ATTRIBUTE_MAP.get(orderBy);
				sort = getSortFromOrder(order, orderBy);
				pageable = new PageRequest(page, count, sort);
				if(CollectionUtils.isNotEmpty(deliveryIntimatedIds)) {
					deliveryIntimatedTs = deliveryIntimatedPagingRepository
							.findByDeliveryIntimatedIdIsInAndAcceptedFalse(deliveryIntimatedIds,
									pageable);
				}
			}

			break;
		case DELIVERY_CLUSTER_HEAD:

			DeliveryClusterT deliveryClusterT = deliveryClusterRepository
					.findByDeliveryClusterHead(loginUser.getUserId());
			if (deliveryClusterT != null) {
				List<DeliveryCentreT> deliveryCentres = deliveryCentreRepository
						.findByDeliveryClusterId(deliveryClusterT
								.getDeliveryClusterId());
				if (!CollectionUtils.isEmpty(deliveryCentres)) {
					List<Integer> deliveryCentreIds = new ArrayList<Integer>();
					List<String> deliveryIntimatedIds = Lists.newArrayList();
					for (DeliveryCentreT deliveryCentre : deliveryCentres) {
						deliveryCentreIds.add(deliveryCentre
								.getDeliveryCentreId());
					}
					deliveryCentreIds.add(-1);
					deliveryIntimatedIds = deliveryIntimatedCentreLinkRepository
							.getDeliveryIntimatedIdsByCentreIds(deliveryCentreIds);
					orderBy = ATTRIBUTE_MAP.get(orderBy);
					sort = getSortFromOrder(order, orderBy);
					pageable = new PageRequest(page, count, sort);
					if(CollectionUtils.isNotEmpty(deliveryIntimatedIds)) {
						deliveryIntimatedTs = deliveryIntimatedPagingRepository
								.findByDeliveryIntimatedIdIsInAndAcceptedFalse(deliveryIntimatedIds,
										pageable);
					}
				}
			}
			break;
		default:
			throw new DestinationException(HttpStatus.FORBIDDEN,
					"You are not authorised to use this service");
		}
		deliveryIntimatedDTO = new PageDTO<DeliveryIntimatedT>();
		if (deliveryIntimatedTs != null) {
			
			removeCyclicReferenceOfDeliveryIntimated(deliveryIntimatedTs.getContent());
			deliveryIntimatedDTO.setContent(deliveryIntimatedTs.getContent());
			deliveryIntimatedDTO.setTotalCount(new Long(deliveryIntimatedTs
					.getTotalElements()).intValue());
		} else {
			logger.error("NOT_FOUND: Intimated Deliveries not found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Intimated Deliveries not found");
		}
		return deliveryIntimatedDTO;
	}


	private void removeCyclicReferenceOfDeliveryIntimated(
			List<DeliveryIntimatedT> deliveryIntimatedTs) {
		for (DeliveryIntimatedT deliveryIntimatedT : deliveryIntimatedTs) {
			if(CollectionUtils.isNotEmpty(deliveryIntimatedT.getDeliveryMasterTs())) {
				for (DeliveryMasterT deliveryMasterT : deliveryIntimatedT.getDeliveryMasterTs()) {
					deliveryMasterT.setDeliveryIntimatedT(null);
				}
			}
			
			for(DeliveryIntimatedCentreLinkT deliveryIntimatedCentreLinkT : deliveryIntimatedT.getDeliveryIntimatedCentreLinkTs()) {
				deliveryIntimatedCentreLinkT.setDeliveryIntimatedT(null);
				deliveryIntimatedCentreLinkT.getDeliveryCentreT().setDeliveryIntimatedCentreLinkTs(null);
			}
		}
	}
	public void createDeliveryIntimated(OpportunityT opportunity,
			Map<Integer, List<Integer>> deliveryCentreMap, String userId) {
		for(Integer clusterId : deliveryCentreMap.keySet()) {
			saveDeliveryIntimated(opportunity,deliveryCentreMap.get(clusterId),userId);
		}
	}


	private void saveDeliveryIntimated(OpportunityT opportunity,
			List<Integer> deliveryIntimatedCentreIds, String userId) {
		DeliveryIntimatedT deliveryIntimated = new DeliveryIntimatedT();
		deliveryIntimated.setCreatedBy(Constants.SYSTEM_USER);
		deliveryIntimated.setDeliveryStage(DeliveryStage.INTIMATED.getStageCode());
		deliveryIntimated.setModifiedBy(Constants.SYSTEM_USER);
		deliveryIntimated.setAccepted(false);
		deliveryIntimated.setOpportunityId(opportunity.getOpportunityId());
		deliveryIntimatedRepository.save(deliveryIntimated);
		saveDeliveryIntimatedCentreLink(deliveryIntimated,deliveryIntimatedCentreIds,userId);
	}


	private void saveDeliveryIntimatedCentreLink(
			DeliveryIntimatedT deliveryIntimated,
			List<Integer> deliveryIntimatedCentreIds, String userId) {
		for(Integer deliveryCentreId : deliveryIntimatedCentreIds) {
			saveDeliveryIntimatedCentreLink(deliveryCentreId,deliveryIntimated,userId);
		}
	}


	private void saveDeliveryIntimatedCentreLink(Integer deliveryCentreId,
			DeliveryIntimatedT deliveryIntimated, String userId) {
		DeliveryIntimatedCentreLinkT deliveryIntimatedCentreLinkT = new DeliveryIntimatedCentreLinkT();
		deliveryIntimatedCentreLinkT.setCreatedBy(Constants.SYSTEM_USER);
		deliveryIntimatedCentreLinkT.setDeliveryCentreId(deliveryCentreId);
		deliveryIntimatedCentreLinkT.setDeliveryIntimatedId(deliveryIntimated.getDeliveryIntimatedId());
		deliveryIntimatedCentreLinkRepository.save(deliveryIntimatedCentreLinkT);
		
	}

	/**
	 * Gets the cluster Id and their respective centre Details
	 * @param userId
	 * @param opportunityDeliveryCentreIds
	 * @return
	 */
	public Map<Integer, List<Integer>> getDeliveryCentreForCluster(
			String userId,List<Integer> opportunityDeliveryCentreIds) {
		Map<Integer, List<Integer>> deliveryCentreMap = Maps
				.newHashMap();
		List<Integer> deliveryIntimatedCentreLinkTs;
		List<DeliveryClusterT> deliveryClusters = (List<DeliveryClusterT>) deliveryClusterRepository
				.findAll();
		for (DeliveryClusterT cluster : deliveryClusters) {
			deliveryIntimatedCentreLinkTs = Lists.newArrayList();
			for(DeliveryCentreT deliveryCentreT : cluster.getDeliveryCentreTs()) {
				if(opportunityDeliveryCentreIds.contains(deliveryCentreT.getDeliveryCentreId())) {
					deliveryIntimatedCentreLinkTs.add(deliveryCentreT.getDeliveryCentreId());
				}
			}
			if(CollectionUtils.isNotEmpty(deliveryIntimatedCentreLinkTs)) {
				deliveryCentreMap.put(cluster.getDeliveryClusterId(),
						deliveryIntimatedCentreLinkTs);
			}
		}
		return deliveryCentreMap;
	}

}

