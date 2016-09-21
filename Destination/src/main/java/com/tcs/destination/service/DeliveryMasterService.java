package com.tcs.destination.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
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

import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.DeliveryCentreT;
import com.tcs.destination.bean.DeliveryClusterT;
import com.tcs.destination.bean.DeliveryMasterManagerLinkT;
import com.tcs.destination.bean.DeliveryMasterT;
import com.tcs.destination.bean.DeliveryRequirementT;
import com.tcs.destination.bean.DeliveryResourcesT;
import com.tcs.destination.bean.DeliveryRgsT;
import com.tcs.destination.bean.OpportunityDeliveryCentreMappingT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.PageDTO;
import com.tcs.destination.bean.PaginatedResponse;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.DeliveryCentreRepository;
import com.tcs.destination.data.repository.DeliveryClusterRepository;
import com.tcs.destination.data.repository.DeliveryMasterManagerLinkRepository;
import com.tcs.destination.data.repository.DeliveryMasterPagingRepository;
import com.tcs.destination.data.repository.DeliveryMasterRepository;
import com.tcs.destination.data.repository.DeliveryResourcesRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.PaginationUtils;
import com.tcs.destination.utils.StringUtils;

/**
 * handle service functionalities for delivery
 * 
 * @author TCS
 *
 */
@Service
public class DeliveryMasterService {

	private static final Logger logger = LoggerFactory.getLogger(DeliveryMasterService.class);

	private static final int numDeliveryStages = 6;
	
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
		attributeMap.put("deliveryOwnership","deliveryOwnershipT.ownership");
		attributeMap.put("stage","deliveryStage");
		attributeMap.put("deliveryCentre","deliveryCentreT.deliveryCentre");
		ATTRIBUTE_MAP = Collections.unmodifiableMap(attributeMap);
	}
	
	/**
	 * method to retrieve List of engagements
	 * @param stage
	 * @param orderBy
	 * @param order
	 * @param start
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public PageDTO findEngagements(Integer stage,String orderBy,String order,
			int page, int count) throws Exception {
		PageDTO deliveryMasterDTO = null;

		logger.debug("Starting findEngagements deliveryMasterService");

		UserT loginUser = DestinationUtils.getCurrentUserDetails();
		String loginUserGroup = loginUser.getUserGroup();

		List<Integer> stages = new ArrayList<Integer>();
		if (stage == -1) {
			for (int i = 0; i < numDeliveryStages; i++)
				stages.add(i);
		} else {
			stages.add(stage);
		}
		Page<DeliveryMasterT> deliveryMasterTs = null;
		Sort sort = null;
		Pageable pageable = null;
		switch (UserGroup.valueOf(UserGroup.getName(loginUserGroup))) {
		case DELIVERY_CENTRE_HEAD:
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
								deliveryCentreIds, stages, pageable);

			}
			break;
		case DELIVERY_CLUSTER_HEAD:
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
							deliveryCentreIds, stages, pageable);
            }
			}
			break;
		case DELIVERY_MANAGER:
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
			List<Integer> deliveryMasterIds = new ArrayList<Integer>();
			for(DeliveryMasterManagerLinkT deliveryMasterManagerLinkT:deliveryMasterManagerList){
			deliveryMasterIds.add(deliveryMasterManagerLinkT.getDeliveryMasterId());
			}
			deliveryMasterTs = deliveryMasterPagingRepository
			.findByDeliveryMasterIdInAndDeliveryStageIn(
			deliveryMasterIds, stages, pageable);
			}
			break;
		default:
			break;
		}
		deliveryMasterDTO = new PageDTO();
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
	public DeliveryMasterT findByDeliveryMasterId(Integer deliveryMasterId) throws Exception {
		logger.debug("Inside findByDeliveryMasterId() service");
		DeliveryMasterT deliveryMaster = deliveryMasterRepository.findOne(deliveryMasterId);
		if (deliveryMaster != null) {
			setData(deliveryMaster);
			return deliveryMaster;
		} else {
			logger.error("NOT_FOUND: Delivery Master Details not found: {}", deliveryMasterId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Delivery Master not found: " + deliveryMasterId);
		}
	}

	private void setData(DeliveryMasterT deliveryMaster) {
		List<DeliveryResourcesT> deliveryResourcesTs = deliveryMaster.getDeliveryResourcesTs();
	    if(!CollectionUtils.isEmpty(deliveryResourcesTs)){
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
	public boolean updateDelivery(DeliveryMasterT deliveryMaster) throws Exception {
		
		
			logger.debug("Inside updateDelivery() service");
			String userId = DestinationUtils.getCurrentUserDetails().getUserId();
			
			deliveryMaster.setModifiedBy(DestinationUtils.getCurrentUserDetails()
					.getUserId());
			
			Integer deliveryMasterId = deliveryMaster.getDeliveryMasterId();
			
			if(deliveryMasterId == null){
				logger.error("BAD_REQUEST: deliveryMasterId is required for update");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"deliveryMasterId is required for update");
			}
			
			// Check if delivery Master exists
			if (!deliveryMasterRepository.exists(deliveryMasterId)) {
				logger.error("NOT_FOUND: DeliveryMaster Record not found for update: {}",
						deliveryMasterId);
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"Engagement not found for update: " + deliveryMasterId);
			}
			
			// Load db object before update with lazy collections populated for auto
			// comments
			// for edit access
			UserT user = userRepository.findByUserId(userId);
			String userGroup = user.getUserGroup();
			DeliveryMasterT deliveryBeforeEdit = deliveryMasterRepository.findOne(deliveryMasterId);

//			if (!isEditAccessRequiredForConnect(connectBeforeEdit, userGroup,
//					userId)) {
//				throw new DestinationException(HttpStatus.FORBIDDEN,
//						"User is not authorized to edit this Connect");
//			}
//			ConnectT beforeConnect = loadDbConnectWithLazyCollections(connectId);
//			// Copy the db object as the above object is managed by current
//			// hibernate session
//			ConnectT oldObject = (ConnectT) DestinationUtils.copy(beforeConnect);

			// Update database
			DeliveryMasterT deliveryAfterEdit = editDelivery(deliveryMaster);

			if (deliveryAfterEdit != null) {
				logger.info("Engagement has been updated successfully: " + deliveryMasterId);
//				//			// Invoke Asynchronous Auto Comments Thread
//				processAutoComments(connectId, oldObject);
//				//			// Invoke Asynchronous Notifications Thread
//				//			processNotifications(connectId, oldObject);
				return true;
    		}
			return false;
		
	}
	
	public DeliveryMasterT editDelivery(DeliveryMasterT deliveryMasterT)
			throws Exception {
		
		List<DeliveryResourcesT> deliveryResourcesTs = deliveryMasterT.getDeliveryResourcesTs();
		
		validateDeliveryMaster(deliveryMasterT);
		
		populateDeliveryMaster(deliveryMasterT);
		
		if(deliveryResourcesTs!=null){
			deliveryResourcesRepository.save(deliveryResourcesTs);
		}
		
		return (deliveryMasterRepository.save(deliveryMasterT));
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
		
	}


	private void populateDeliveryMaster(DeliveryMasterT deliveryMasterT) {
		Integer deliveryMasterId = deliveryMasterT.getDeliveryMasterId();
		
		DeliveryMasterT deliveryFromDB = deliveryMasterRepository.findOne(deliveryMasterId);
		
		setData(deliveryMasterT,deliveryFromDB);
		
	}


	private void setData(DeliveryMasterT deliveryMasterSource,
			DeliveryMasterT deliveryMasterDestination) {
		Integer new_deliveryMasterId = deliveryMasterSource
				.getDeliveryMasterId();
		Date new_actualStartDate = deliveryMasterSource.getActualStartDate();
		Integer new_deliveryCentreId = deliveryMasterSource
				.getDeliveryCentreId();
		String new_deliveryPartnerId = deliveryMasterSource
				.getDeliveryPartnerId();
		String new_deliveryPartnerName = deliveryMasterSource
				.getDeliveryPartnerName();
		List<DeliveryResourcesT> new_deliveryResourcesTs = deliveryMasterSource
				.getDeliveryResourcesTs();
		Integer new_deliveryStage = deliveryMasterSource.getDeliveryStage();
		String new_engagementName = deliveryMasterSource.getEngagementName();
		Date new_expectedEndDate = deliveryMasterSource.getExpectedEndDate();
		String new_glId = deliveryMasterSource.getGlId();
		String new_glName = deliveryMasterSource.getGlName();
		String new_odc = deliveryMasterSource.getOdc();
		String new_opportunityId = deliveryMasterSource.getOpportunityId();
		String new_plId = deliveryMasterSource.getPlId();
		String new_plName = deliveryMasterSource.getPlName();
		Date new_scheduledStartDate = deliveryMasterSource
				.getScheduledStartDate();
		String new_wonNum = deliveryMasterSource.getWonNum();

		Integer old_deliveryMasterId = deliveryMasterDestination
				.getDeliveryMasterId();
		Date old_actualStartDate = deliveryMasterDestination
				.getActualStartDate();
		Integer old_deliveryCentreId = deliveryMasterDestination
				.getDeliveryCentreId();
		String old_deliveryPartnerId = deliveryMasterDestination
				.getDeliveryPartnerId();
		String old_deliveryPartnerName = deliveryMasterDestination
				.getDeliveryPartnerName();
		List<DeliveryResourcesT> old_deliveryResourcesTs = deliveryMasterDestination
				.getDeliveryResourcesTs();
		Integer old_deliveryStage = deliveryMasterDestination
				.getDeliveryStage();
		String old_engagementName = deliveryMasterDestination
				.getEngagementName();
		Date old_expectedEndDate = deliveryMasterDestination
				.getExpectedEndDate();
		String old_glId = deliveryMasterDestination.getGlId();
		String old_glName = deliveryMasterDestination.getGlName();
		String old_odc = deliveryMasterDestination.getOdc();
		String old_opportunityId = deliveryMasterDestination.getOpportunityId();
		String old_plId = deliveryMasterDestination.getPlId();
		String old_plName = deliveryMasterDestination.getPlName();
		Date old_scheduledStartDate = deliveryMasterDestination
				.getScheduledStartDate();
		String old_wonNum = deliveryMasterDestination.getWonNum();

		if (new_actualStartDate != null) {
			if (old_actualStartDate != null) {
				if (new_actualStartDate.after(old_actualStartDate)
						|| new_actualStartDate.before(old_actualStartDate)) {
					deliveryMasterDestination
							.setActualStartDate(new_actualStartDate);
				}
			} else {
				deliveryMasterDestination
						.setActualStartDate(new_actualStartDate);
			}
		}

		if (new_expectedEndDate != null) {
			if (old_expectedEndDate != null) {
				if (new_expectedEndDate.after(old_expectedEndDate)
						|| new_expectedEndDate.before(old_expectedEndDate)) {
					deliveryMasterDestination
							.setExpectedEndDate(new_expectedEndDate);
				}
			} else {
				deliveryMasterDestination
						.setExpectedEndDate(new_expectedEndDate);
			}
		}

		if (new_scheduledStartDate != null) {
			if (old_scheduledStartDate != null) {
				if (new_scheduledStartDate.after(old_scheduledStartDate)
						|| new_scheduledStartDate
								.before(old_scheduledStartDate)) {
					deliveryMasterDestination
							.setScheduledStartDate(new_scheduledStartDate);
				}
			} else {
				deliveryMasterDestination
						.setScheduledStartDate(new_scheduledStartDate);
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
}

