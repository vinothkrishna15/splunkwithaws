package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.tcs.destination.bean.DeliveryMasterT;
import com.tcs.destination.bean.DeliveryResourcesT;
import com.tcs.destination.bean.PageDTO;
import com.tcs.destination.bean.PaginatedResponse;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.DeliveryCentreRepository;
import com.tcs.destination.data.repository.DeliveryClusterRepository;
import com.tcs.destination.data.repository.DeliveryMasterPagingRepository;
import com.tcs.destination.data.repository.DeliveryMasterRepository;
import com.tcs.destination.data.repository.DeliveryResourcesRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.exception.DestinationException;
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
			int start, int count) throws Exception {
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
				if (order.equalsIgnoreCase("DESC")) {
					sort = new Sort(Direction.DESC, orderBy);
				} else {
					sort = new Sort(Direction.ASC, orderBy);
				}

				pageable = new PageRequest(0, 20, sort);

				deliveryMasterTs = deliveryMasterPagingRepository
						.findByDeliveryCentreIdInAndDeliveryStageIn(
								deliveryCentreIds, stages, pageable);

			}
			break;
		case DELIVERY_CLUSTER_HEAD:
			DeliveryClusterT deliveryClusterT = deliveryClusterRepository
					.findByDeliveryClusterHead(loginUser.getUserId());
			List<DeliveryCentreT> deliveryCentres = deliveryCentreRepository
					.findByDeliveryClusterId(deliveryClusterT
							.getDeliveryClusterId());

			List<Integer> deliveryCentreIds = new ArrayList<Integer>();
			for (DeliveryCentreT deliveryCentre : deliveryCentres) {
				deliveryCentreIds.add(deliveryCentre.getDeliveryCentreId());
			}
			deliveryCentreIds.add(-1);
			
			orderBy = ATTRIBUTE_MAP.get(orderBy);
			if (order.equalsIgnoreCase("DESC")) {
				sort = new Sort(Direction.DESC, orderBy);
			} else {
				sort = new Sort(Direction.ASC, orderBy);
			}
			pageable = new PageRequest(0, 20, sort);
			deliveryMasterTs = deliveryMasterPagingRepository
					.findByDeliveryCentreIdInAndDeliveryStageIn(
							deliveryCentreIds, stages, pageable);
			break;
		case DELIVERY_MANAGER:
			orderBy = ATTRIBUTE_MAP.get(orderBy);
			if (order.equalsIgnoreCase("DESC")) {
				sort = new Sort(Direction.DESC, orderBy);
			} else {
				sort = new Sort(Direction.ASC, orderBy);
			}
			pageable = new PageRequest(0, 20, sort);
			deliveryMasterTs = deliveryMasterPagingRepository
					.findByDeliveryManagerIdAndDeliveryStageIn(
							loginUser.getUserId(), stages, pageable);
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
			return deliveryMaster;
		} else {
			logger.error("NOT_FOUND: Delivery Master Details not found: {}", deliveryMasterId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Delivery Master not found: " + deliveryMasterId);
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
		
		
	}


	private void populateDeliveryMaster(DeliveryMasterT deliveryMasterT) {
		
		
	}
	
}

