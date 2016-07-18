package com.tcs.destination.helper;

import static com.tcs.destination.utils.DateUtils.ACTUAL_FORMAT;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tcs.destination.bean.AuditBidDetailsT;
import com.tcs.destination.bean.AuditBidOfficeGroupOwnerLinkT;
import com.tcs.destination.bean.AuditConnectSecondaryOwnerLinkT;
import com.tcs.destination.bean.AuditConnectT;
import com.tcs.destination.bean.AuditOpportunitySalesSupportLinkT;
import com.tcs.destination.bean.AuditOpportunityT;
import com.tcs.destination.bean.AuditTaskBdmsTaggedLinkT;
import com.tcs.destination.bean.AuditTaskT;
import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.BidOfficeGroupOwnerLinkT;
import com.tcs.destination.bean.ConnectSecondaryOwnerLinkT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.NotificationEventGroupMappingT;
import com.tcs.destination.bean.OperationEventRecipientMappingT;
import com.tcs.destination.bean.OpportunitySalesSupportLinkT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.Recipient;
import com.tcs.destination.bean.RecipientMessageTemplateMapping;
import com.tcs.destination.bean.SearchKeywordsT;
import com.tcs.destination.bean.TaskBdmsTaggedLinkT;
import com.tcs.destination.bean.TaskT;
import com.tcs.destination.bean.UserNotificationsT;
import com.tcs.destination.data.repository.AuditBidDetailsTRepository;
import com.tcs.destination.data.repository.AuditBidOfficeGroupOwnerLinkTRepository;
import com.tcs.destination.data.repository.AuditConnectSecondaryOwnerLinkTRepository;
import com.tcs.destination.data.repository.AuditConnectTRepository;
import com.tcs.destination.data.repository.AuditOpportunityRepository;
import com.tcs.destination.data.repository.AuditOpportunitySalesSupportLinkTRepository;
import com.tcs.destination.data.repository.AuditTaskBdmsTaggedLinkTRepository;
import com.tcs.destination.data.repository.AuditTaskTRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.NotificationEventGroupMappingTRepository;
import com.tcs.destination.data.repository.NotificationTypeEventMappingRepository;
import com.tcs.destination.data.repository.OperationEventRecipientMappingRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.TaskRepository;
import com.tcs.destination.data.repository.UserNotificationSettingsConditionRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.data.repository.UserTaggedFollowedRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.enums.NotificationSettingEvent;
import com.tcs.destination.enums.OperationType;
import com.tcs.destination.enums.OwnerType;
import com.tcs.destination.enums.RecipientType;
import com.tcs.destination.enums.SalesStageCode;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.utils.Constants;

/**
 * Helper for Notification Batch
 * 
 * @author TCS
 *
 */
@Component("notificationBatchHelper")
public class NotificationBatchHelper {

	private static final Logger logger = LoggerFactory
			.getLogger(NotificationBatchHelper.class);

	private static final Integer OPERATION_INSERT = Integer.valueOf(1);
	private static final Integer OPERATION_UPDATE = Integer.valueOf(2);
	private static final Integer OPERATION_DELETE = Integer.valueOf(0);

	private static final String PATTERN = "\\<(.+?)\\>";

	@Autowired
	OperationEventRecipientMappingRepository operationEventRecipientMappingRepository;

	@Autowired
	OpportunityRepository opportunityRepository;

	@Autowired
	NotificationTypeEventMappingRepository notificationTypeEventMappingRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	ConnectRepository connectRepository;

	@Autowired
	UserTaggedFollowedRepository userTaggedFollowedRepository;

	@Autowired
	UserNotificationSettingsConditionRepository userNotificationSettingsConditionRepository;

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	NotificationEventGroupMappingTRepository notificationEventGroupMappingTRepository;

	@Autowired
	AuditOpportunityRepository auditOpportunityRepository;

	@Autowired
	AuditOpportunitySalesSupportLinkTRepository auditOpportunitySalesSupportLinkTRepository;

	@Autowired
	AuditBidDetailsTRepository auditBidDetailsTRepository;

	@Autowired
	AuditBidOfficeGroupOwnerLinkTRepository auditBidOfficeGroupOwnerLinkTRepository;

	@Autowired
	AuditConnectTRepository auditConnectTRepository;

	@Autowired
	AuditConnectSecondaryOwnerLinkTRepository auditConnectSecondaryOwnerLinkTRepository;

	@Autowired
	AuditTaskTRepository auditTaskTRepository;

	@Autowired
	AuditTaskBdmsTaggedLinkTRepository auditTaskBdmsTaggedLinkTRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	PartnerRepository partnerRepository;

	@Autowired
	VelocityEngine velocityEngine;

	/**
	 * This method returns the events and recipients based on the operation type
	 * given
	 * 
	 * @param operationTypeE
	 * @return
	 */
	public List<OperationEventRecipientMappingT> pullEventsByOperation(
			OperationType operationTypeE) {
		return operationEventRecipientMappingRepository
				.findByOperationType(operationTypeE.ordinal());
	}

	/**
	 * This method is used to get the recipient details
	 * 
	 * @param entityId
	 * @param operationTypeE
	 * @param eventRecipientMapping
	 * @param currentUser
	 * @param auditBidDetailsT
	 * @param auditTaskT
	 * @param auditOpportunitySalesSupportLinkTs
	 * @param auditOpportunityT
	 * @param auditConnectSecondaryOwnerLinkTs
	 * @param auditConnectT
	 * @param auditBidOfficeGroupOwnerLinkTs
	 * @return
	 */
	public List<Recipient> getRecipients(
			String entityId,
			OperationType operationTypeE,
			List<OperationEventRecipientMappingT> eventRecipientMapping,
			String currentUser,
			AuditConnectT auditConnectT,
			List<AuditConnectSecondaryOwnerLinkT> auditConnectSecondaryOwnerLinkTs,
			AuditOpportunityT auditOpportunityT,
			List<AuditOpportunitySalesSupportLinkT> auditOpportunitySalesSupportLinkTs,
			AuditTaskT auditTaskT,
			List<AuditBidOfficeGroupOwnerLinkT> auditBidOfficeGroupOwnerLinkTs) {

		logger.info("Inside getRecipients method");

		List<Recipient> recipients = null;

		// getting applicable events for each recipient type
		Map<RecipientType, List<Integer>> recipientEventMap = getEventsForRecipientType(eventRecipientMapping);

		switch (operationTypeE.getEntityType()) {

		case OPPORTUNITY:
			logger.info("Entity Type : Opportunity");
			recipients = getRecipientsOfOpportunity(entityId,
					recipientEventMap, operationTypeE, auditOpportunityT,
					auditOpportunitySalesSupportLinkTs,
					auditBidOfficeGroupOwnerLinkTs);
			logger.info("Recipients retrieved for opportunity");
			break;
		case CONNECT:
			logger.info("Entity Type : Connect");
			recipients = getRecipientsOfConnect(entityId, recipientEventMap,
					operationTypeE, auditConnectT,
					auditConnectSecondaryOwnerLinkTs);
			logger.info("Recipients retrieved for Connect");
			break;
		case TASK:
			logger.info("Entity Type : Task");
			recipients = getRecipientsOfTask(entityId, recipientEventMap,
					operationTypeE, auditTaskT);
			logger.info("Recipients retrieved for Task");
			break;
		default:
			break;
		}

		return mergeDuplicates(recipients);
	}

	/**
	 * method used to get the recipients of task
	 * 
	 * @param entityId
	 * @param recipientEventMap
	 * @param opType
	 * @param auditTaskT
	 * @return
	 */
	private List<Recipient> getRecipientsOfTask(String entityId,
			Map<RecipientType, List<Integer>> recipientEventMap,
			OperationType opType, AuditTaskT auditTaskT) {
		logger.info("Inside getRecipientsOfTask method");
		TaskT task = taskRepository.findOne(entityId);
		List<Recipient> recipients = Lists.newArrayList();
		List<Recipient> owners = getOwners(task, recipientEventMap);
		recipients.addAll(owners);
		recipients.addAll(getSupervisor(owners, recipientEventMap, opType));
		recipients.addAll(getBDMTagged(task, recipientEventMap, opType));
		if (auditTaskT != null) {
			Recipient removedPrimaryOwner = getRemovedOwner(
					auditTaskT.getOldTaskOwner(), auditTaskT.getNewTaskOwner(),
					OwnerType.PRIMARY_OWNER);
			if (StringUtils.isNotEmpty(removedPrimaryOwner.getId())) {
				recipients.add(removedPrimaryOwner);
			}
		}

		logger.info("End of getRecipientsOfTask method");
		return recipients;
	}

	/**
	 * Method used to get the recipients for connect
	 * 
	 * @param entityId
	 * @param recipientEventMap
	 * @param opType
	 * @param auditConnectSecondaryOwnerLinkTs
	 * @param auditConnectT
	 * @return
	 */
	private List<Recipient> getRecipientsOfConnect(
			String entityId,
			Map<RecipientType, List<Integer>> recipientEventMap,
			OperationType opType,
			AuditConnectT auditConnectT,
			List<AuditConnectSecondaryOwnerLinkT> auditConnectSecondaryOwnerLinkTs) {
		logger.info("Inside getRecipientsOfConnect Method");
		ConnectT connect = connectRepository.findOne(entityId);
		List<Recipient> recipients = Lists.newArrayList();
		// getting owners
		List<Recipient> owners = getOwners(connect, recipientEventMap);
		recipients.addAll(owners);
		// getting supervisors
		recipients.addAll(getSupervisor(owners, recipientEventMap, opType));
		// getting followers
		recipients.addAll(getFollowers(entityId, recipientEventMap, opType));
		// getting condition subscribers
		recipients.addAll(getConditionSubscribers(connect.getCustomerMasterT(),
				connect.getCountry(), null, connect.getSearchKeywordsTs(),
				recipientEventMap, opType));
		if (auditConnectT != null) {
			Recipient removedPrimaryOwner = getRemovedOwner(
					auditConnectT.getOldPrimaryOwner(),
					auditConnectT.getNewPrimaryOwner(), OwnerType.PRIMARY_OWNER);
			if (StringUtils.isNotEmpty(removedPrimaryOwner.getId())) {
				recipients.add(removedPrimaryOwner);
			}
		}
		List<Recipient> removedSecondaryOwners = getRemovedSecondaryOwners(
				null, null, auditConnectSecondaryOwnerLinkTs);
		if (CollectionUtils.isNotEmpty(removedSecondaryOwners)) {
			recipients.addAll(removedSecondaryOwners);
		}
		logger.info("End of getRecipientsOfConnect Method");
		return recipients;
	}

	/**
	 * Method used to get the recipients applicable for Opportunity
	 * 
	 * @param entityId
	 * @param recipientEventMap
	 * @param opType
	 * @param auditBidOfficeGroupOwnerLinkTs
	 * @param auditOpportunitySalesSupportLinkTs
	 * @param auditOpportunityT
	 * @return
	 */
	private List<Recipient> getRecipientsOfOpportunity(
			String entityId,
			Map<RecipientType, List<Integer>> recipientEventMap,
			OperationType opType,
			AuditOpportunityT auditOpportunityT,
			List<AuditOpportunitySalesSupportLinkT> auditOpportunitySalesSupportLinkTs,
			List<AuditBidOfficeGroupOwnerLinkT> auditBidOfficeGroupOwnerLinkTs) {
		// get opportunity
		logger.info("Inside getRecipientsOfOpportunity Method");
		OpportunityT opportunity = opportunityRepository.findOne(entityId);
		List<Recipient> recipients = Lists.newArrayList();
		List<Recipient> owners = getOwners(opportunity, recipientEventMap);
		recipients.addAll(owners);

		recipients.addAll(getSupervisor(owners, recipientEventMap, opType));
		recipients.addAll(getFollowers(entityId, recipientEventMap, opType));
		recipients.addAll(getConditionSubscribers(
				opportunity.getCustomerMasterT(), opportunity.getCountry(),
				opportunity.getDigitalDealValue(),
				opportunity.getSearchKeywordsTs(), recipientEventMap, opType));
		recipients.addAll(getStrategicInitiatives(recipientEventMap, opType));
		// check if owner removed
		if (auditOpportunityT != null) {
			Recipient removedPrimaryOwner = getRemovedOwner(
					auditOpportunityT.getOldOpportunityOwner(),
					auditOpportunityT.getNewOpportunityOwner(),
					OwnerType.PRIMARY_OWNER);
			if (StringUtils.isNotEmpty(removedPrimaryOwner.getId())) {
				recipients.add(removedPrimaryOwner);
			}
		}

		List<Recipient> removedSecondaryOwners = getRemovedSecondaryOwners(
				auditOpportunitySalesSupportLinkTs,
				auditBidOfficeGroupOwnerLinkTs, null);
		if (CollectionUtils.isNotEmpty(removedSecondaryOwners)) {
			recipients.addAll(removedSecondaryOwners);
		}
		logger.info("End of retrieving recipients of Opportunity");
		return recipients;
	}

	/**
	 * Retrives the list of removed secondary owners
	 * @param auditOpportunitySalesSupportLinkTs
	 * @param auditBidOfficeGroupOwnerLinkTs
	 * @param auditConnectSecondaryOwnerLinkTs
	 * @return
	 */
	private List<Recipient> getRemovedSecondaryOwners(
			List<AuditOpportunitySalesSupportLinkT> auditOpportunitySalesSupportLinkTs,
			List<AuditBidOfficeGroupOwnerLinkT> auditBidOfficeGroupOwnerLinkTs,
			List<AuditConnectSecondaryOwnerLinkT> auditConnectSecondaryOwnerLinkTs) {
		logger.info("Inside getRemovedSecondaryOwners method");
		List<Recipient> recipients = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(auditOpportunitySalesSupportLinkTs)) {
			for (AuditOpportunitySalesSupportLinkT auditOpportunitySalesSupportLinkT : auditOpportunitySalesSupportLinkTs) {
				if (OPERATION_DELETE.equals(auditOpportunitySalesSupportLinkT
						.getOperationType())
						&& StringUtils
								.isNotEmpty(auditOpportunitySalesSupportLinkT
										.getOldSalesSupportOwner())) {
					recipients.add(getRemovedSecondaryOwner(
							auditOpportunitySalesSupportLinkT
									.getOldSalesSupportOwner(),
							OwnerType.SALES_SUPPORT_OWNER));
				}
			}
		}
		if (CollectionUtils.isNotEmpty(auditConnectSecondaryOwnerLinkTs)) {
			for (AuditConnectSecondaryOwnerLinkT auditConnectSecondaryOwnerLinkT : auditConnectSecondaryOwnerLinkTs) {
				if (OPERATION_DELETE.equals(auditConnectSecondaryOwnerLinkT
						.getOperationType())
						&& StringUtils
								.isNotEmpty(auditConnectSecondaryOwnerLinkT
										.getOldSecondaryOwner())) {
					recipients.add(getRemovedSecondaryOwner(
							auditConnectSecondaryOwnerLinkT
									.getOldSecondaryOwner(),
							OwnerType.SECONDARY_OWNER));
				}
			}
		}
		if (CollectionUtils.isNotEmpty(auditBidOfficeGroupOwnerLinkTs)) {
			for (AuditBidOfficeGroupOwnerLinkT auditBidOfficeGroupOwnerLinkT : auditBidOfficeGroupOwnerLinkTs) {
				if (OPERATION_UPDATE.equals(auditBidOfficeGroupOwnerLinkT
						.getOperationType())
						&& compareStringValueForUpdate(
								auditBidOfficeGroupOwnerLinkT
										.getOldBidOfficeGroupOwner(),
								auditBidOfficeGroupOwnerLinkT
										.getNewBidOfficeGroupOwner())) {
					recipients.add(getRemovedOwner(
							auditBidOfficeGroupOwnerLinkT
									.getOldBidOfficeGroupOwner(),
							auditBidOfficeGroupOwnerLinkT
									.getNewBidOfficeGroupOwner(),
							OwnerType.BID_OFFICE_GROUP_OWNER));
				}
			}
		}
		logger.info("End of getRemovedSecondaryOwners method");
		return recipients;
	}

	/**
	 * Retrieves the removed primary owner
	 * @param oldOwner
	 * @param newOwner
	 * @param ownerType
	 * @return
	 */
	private Recipient getRemovedOwner(String oldOwner, String newOwner,
			OwnerType ownerType) {
		Recipient recipient = new Recipient();
		if (compareStringValueForUpdate(oldOwner, newOwner)) {
			List<Integer> events = notificationTypeEventMappingRepository
					.getNotificationEventIdsForUser(oldOwner);
			if (CollectionUtils.isNotEmpty(events)) {
				if (events.contains(NotificationSettingEvent.OWNER_CHANGE
						.getEventId())) {
					recipient = constructRecipient(oldOwner, ownerType, null,
							RecipientType.REMOVED_USER, true, events);
				}
			}

		}
		return recipient;
	}

	private Recipient getRemovedSecondaryOwner(String oldOwner,
			OwnerType ownerType) {
		Recipient recipient = new Recipient();
		List<Integer> events = notificationTypeEventMappingRepository
				.getNotificationEventIdsForUser(oldOwner);
		if (CollectionUtils.isNotEmpty(events)) {
			if (events.contains(NotificationSettingEvent.OWNER_CHANGE
					.getEventId())) {
				recipient = constructRecipient(oldOwner, ownerType, null,
						RecipientType.REMOVED_USER, true, events);
			}
		}

		return recipient;
	}

	/**
	 * Method used to get the strategic initiatives
	 * 
	 * @param recipientEventMap
	 * @param opType
	 * @return
	 */
	private List<Recipient> getStrategicInitiatives(
			Map<RecipientType, List<Integer>> recipientEventMap,
			OperationType opType) {
		logger.info("Inside getStrategicInitiatives Method");
		List<Recipient> recipients = Lists.newArrayList();
		if (isRecipientRequired(opType, RecipientType.STRATEGIC_INITIATIVE)) {
			List<Integer> strategicInitiativeEvents = recipientEventMap
					.get(RecipientType.STRATEGIC_INITIATIVE);
			List<String> users = userRepository
					.findUserIdByUserGroup(UserGroup.STRATEGIC_INITIATIVES.getValue());
			users.addAll(userRepository.findUserIdByUserGroup(UserGroup.GEO_HEADS.getValue()));
			if (CollectionUtils.isNotEmpty(users)) {
				for (String strategicInitiative : users) {
					recipients.add(constructRecipient(strategicInitiative,
							null, null, RecipientType.STRATEGIC_INITIATIVE,
							false, strategicInitiativeEvents));
				}
				logger.info("Retrieved Strategic Initiatives");
			}

		}
		logger.info("End of getStrategicInitiatives Method");
		return recipients;
	}

	/**
	 * Method used to filter the recipients if the same user coming under more
	 * than one recipient type
	 * 
	 * @param recipients
	 * @return
	 */
	private List<Recipient> mergeDuplicates(List<Recipient> recipients) {
		List<Recipient> filteredRecipients = Lists.newArrayList();
		for (Recipient recipient : recipients) {
			if (!recipient.isRemoved()
					&& filteredRecipients.contains(recipient)) {
				mergeRecipientEventsAndSubords(filteredRecipients, recipient);
			} else {
				filteredRecipients.add(recipient);
			}
		}

		return filteredRecipients;
	}

	/**
	 * merge the events of recipient with the recipient matched in the list and
	 * merge the subordinates if supervisors
	 * 
	 * @param filteredRecipients
	 * @param recipient
	 */
	private void mergeRecipientEventsAndSubords(
			List<Recipient> filteredRecipients, Recipient recipient) {
		for (Recipient item : filteredRecipients) {
			if (item.equals(recipient)) {
				Map<RecipientType, List<Integer>> events = item.getEvents();
				events.putAll(recipient.getEvents());
				item.getSubodinates().addAll(recipient.getSubodinates());
			}
		}
	}

	/**
	 * method used to get all the owners of opportunity
	 * 
	 * @param opportunity
	 * @param recipientEventMap
	 * @return
	 */
	private List<Recipient> getOwners(OpportunityT opportunity,
			Map<RecipientType, List<Integer>> recipientEventMap) {
		logger.info("Inside getOwners method for Opportunity");
		List<Recipient> recipients = new ArrayList<Recipient>();

		List<Integer> ownerEvents = recipientEventMap.get(RecipientType.OWNER);
        //Getting primary owner
		recipients.add(constructRecipient(opportunity.getOpportunityOwner(),
				OwnerType.PRIMARY_OWNER, null, RecipientType.OWNER, false,
				ownerEvents));
       //Getting sales support owner
		if (CollectionUtils.isNotEmpty(opportunity
				.getOpportunitySalesSupportLinkTs())) {
			for (OpportunitySalesSupportLinkT opportunitySalesSupportLinkT : opportunity
					.getOpportunitySalesSupportLinkTs()) {
				recipients.add(constructRecipient(
						opportunitySalesSupportLinkT.getSalesSupportOwner(),
						OwnerType.SALES_SUPPORT_OWNER, null,
						RecipientType.OWNER, false, ownerEvents));
			}
		}
		//Getting bid office group owner
		if (CollectionUtils.isNotEmpty(opportunity.getBidDetailsTs())) {
			for (BidDetailsT bidDetailsT : opportunity.getBidDetailsTs()) {
				if (CollectionUtils.isNotEmpty(bidDetailsT
						.getBidOfficeGroupOwnerLinkTs())) {
					for (BidOfficeGroupOwnerLinkT bidOfficeGroupOwnerLinkT : bidDetailsT
							.getBidOfficeGroupOwnerLinkTs()) {
						recipients.add(constructRecipient(
								bidOfficeGroupOwnerLinkT
										.getBidOfficeGroupOwner(),
								OwnerType.BID_OFFICE_GROUP_OWNER, null,
								RecipientType.OWNER, false, ownerEvents));
					}
				}
			}
		}
		logger.info("End getOwners method for Opportunity");
		return recipients;
	}

	/**
	 * Method used to get the owners of connect
	 * 
	 * @param connect
	 * @param recipientEventMap
	 * @return
	 */
	private List<Recipient> getOwners(ConnectT connect,
			Map<RecipientType, List<Integer>> recipientEventMap) {
		logger.info("End getOwners method for Connect");
		List<Recipient> recipients = new ArrayList<Recipient>();

		List<Integer> ownerEvents = recipientEventMap.get(RecipientType.OWNER);
        //Getting primary owner
		recipients.add(constructRecipient(connect.getPrimaryOwner(),
				OwnerType.PRIMARY_OWNER, null, RecipientType.OWNER, false,
				ownerEvents));
        //Getting Secondary owner
		if (CollectionUtils
				.isNotEmpty(connect.getConnectSecondaryOwnerLinkTs())) {
			for (ConnectSecondaryOwnerLinkT connectSecondaryOwnerLinkT : connect
					.getConnectSecondaryOwnerLinkTs()) {
				recipients.add(constructRecipient(
						connectSecondaryOwnerLinkT.getSecondaryOwner(),
						OwnerType.SECONDARY_OWNER, null, RecipientType.OWNER,
						false, ownerEvents));
			}
		}
		logger.info("End getOwners method for Connect");
		return recipients;
	}

	/**
	 * Retrives the owner of task
	 * @param task
	 * @param recipientEventMap
	 * @return
	 */
	private List<Recipient> getOwners(TaskT task,
			Map<RecipientType, List<Integer>> recipientEventMap) {
		logger.info("Inside getOwners method for Task");
		List<Recipient> recipients = new ArrayList<Recipient>();
		List<Integer> ownerEvents = recipientEventMap.get(RecipientType.OWNER);
		recipients.add(constructRecipient(task.getTaskOwner(),
				OwnerType.PRIMARY_OWNER, null, RecipientType.OWNER, false,
				ownerEvents));
		logger.info("End getOwners method for Opportunity");
		return recipients;
	}

	/**
	 * Method to get the supervisor details of each owner
	 * 
	 * @param owners
	 * @param recipientEventMap
	 * @param opType
	 * @return
	 */
	private List<Recipient> getSupervisor(List<Recipient> owners,
			Map<RecipientType, List<Integer>> recipientEventMap,
			OperationType opType) {
		logger.info("Inside getSupervisor method");
		List<Recipient> recipients = Lists.newArrayList();
		// Check if supervisor is required in the recipient
		if (isRecipientRequired(opType, RecipientType.SUPERVISOR)) {
			List<Integer> supervisorEvents = recipientEventMap
					.get(RecipientType.SUPERVISOR);

			for (Recipient owner : owners) {
				String supervisorId = userRepository
						.getSupervisorUserIdForUser(owner.getId());

				recipients.add(constructRecipient(supervisorId,
						owner.getOwnerType(), owner.getId(),
						RecipientType.SUPERVISOR, false, supervisorEvents));
			}
		}
		logger.info("End of getSupervisor method");
		return recipients;
	}

	/**
	 * Retrieves BDM Tagged for task
	 * @param task
	 * @param recipientEventMap
	 * @param opType
	 * @return
	 */
	private List<Recipient> getBDMTagged(TaskT task,
			Map<RecipientType, List<Integer>> recipientEventMap,
			OperationType opType) {
		logger.info("Inside getBDMTagged method");
		List<Recipient> recipients = new ArrayList<Recipient>();

		// fetch only if the bdm tagged required for the opType
		if (isRecipientRequired(opType, RecipientType.BDM_TAGGED)) {
			List<Integer> bdmTaggedEvents = recipientEventMap
					.get(RecipientType.BDM_TAGGED);
			if (CollectionUtils.isNotEmpty(task.getTaskBdmsTaggedLinkTs())) {
				for (TaskBdmsTaggedLinkT taskBdmsTaggedLinkT : task
						.getTaskBdmsTaggedLinkTs()) {
					recipients.add(constructRecipient(
							taskBdmsTaggedLinkT.getBdmsTagged(),
							OwnerType.SECONDARY, null,
							RecipientType.BDM_TAGGED, false, bdmTaggedEvents));
				}
			}
		}
		logger.info("End of getBDMTagged method");
		return recipients;
	}

	/**
	 * Method is used to get the collaboration condition subscribers
	 * 
	 * @param customerMasterT
	 * @param country
	 * @param digitalDealValue
	 * @param searchKeywords
	 * @param recipientEventMap
	 * @param opType
	 * @return
	 */
	private List<Recipient> getConditionSubscribers(
			CustomerMasterT customerMasterT, String country,
			Integer digitalDealValue, List<SearchKeywordsT> searchKeywords,
			Map<RecipientType, List<Integer>> recipientEventMap,
			OperationType opType) {
		logger.info("Inside getConditionSubscribers method");
		List<Recipient> recipients = Lists.newArrayList();
		if (isRecipientRequired(opType, RecipientType.SUBSCRIBER)) {
			List<String> conditionSubscribers = Lists.newArrayList();
			List<Integer> conditionSubscriberEvents = recipientEventMap
					.get(RecipientType.SUBSCRIBER);
			//Customer name
			conditionSubscribers
					.addAll(userNotificationSettingsConditionRepository
							.findUserIdByConditionIdAndConditionValue(1,
									customerMasterT.getCustomerName()));
			if (CollectionUtils.isNotEmpty(searchKeywords)) {
				for (SearchKeywordsT searchKeywordsT : searchKeywords) {
					conditionSubscribers
							.addAll(userNotificationSettingsConditionRepository.findUserIdByConditionIdAndConditionValue(
									2, searchKeywordsT.getSearchKeywords()));
				}
			}
			conditionSubscribers
					.addAll(userNotificationSettingsConditionRepository
							.findUserIdByConditionIdAndConditionValue(3,
									customerMasterT.getIouCustomerMappingT()
											.getDisplayIou()));
			conditionSubscribers
					.addAll(userNotificationSettingsConditionRepository
							.findUserIdByConditionIdAndConditionValue(4,
									customerMasterT.getGeography()));
			conditionSubscribers
					.addAll(userNotificationSettingsConditionRepository
							.findUserIdByConditionIdAndConditionValue(5,
									country));
			if (digitalDealValue != null) {
				conditionSubscribers
						.addAll(userNotificationSettingsConditionRepository
								.findUserIdByDigitalDealValueGreaterThan(digitalDealValue));

			}
			if (CollectionUtils.isNotEmpty(conditionSubscribers)) {
				for (String conditionSubscriber : conditionSubscribers) {
					recipients.add(constructRecipient(conditionSubscriber,
							null, null, RecipientType.SUBSCRIBER, false,
							conditionSubscriberEvents));
				}
			}
		}
		logger.info("End of getConditionSubscribers method");
		return recipients;
	}

	/**
	 * Method to get the followers of an entity
	 * 
	 * @param entityId
	 * @param recipientEventMap
	 * @param opType
	 * @return
	 */
	private List<Recipient> getFollowers(String entityId,
			Map<RecipientType, List<Integer>> recipientEventMap,
			OperationType opType) {
		List<Recipient> recipients = Lists.newArrayList();
		logger.info("Inside getFollowers method");
		// Check if the Follower is required in the recipient
		if (isRecipientRequired(opType, RecipientType.FOLLOWER)) {

			List<Integer> followerEvents = recipientEventMap
					.get(RecipientType.FOLLOWER);
			List<String> followers = Lists.newArrayList();
			switch (opType.getEntityType()) {
			case OPPORTUNITY:
				followers = userTaggedFollowedRepository
						.getOpportunityFollowers(entityId);
				break;
			case CONNECT:
				followers = userTaggedFollowedRepository
						.getConnectFollowers(entityId);
				break;
			default:
				break;

			}
			if (CollectionUtils.isNotEmpty(followers)) {
				for (String follower : followers) {
					recipients.add(constructRecipient(follower, null, null,
							RecipientType.FOLLOWER, false, followerEvents));
				}
				logger.info("Retrieved followers");
			}
		}
		logger.info("End of getFollowers method");
		return recipients;
	}

	/**
	 * classify the events by recipient type
	 * 
	 * @param eventRecipientMapping
	 * @return map of {@link RecipientType} and corresponding list of events
	 */
	private Map<RecipientType, List<Integer>> getEventsForRecipientType(
			List<OperationEventRecipientMappingT> eventRecipientMapping) {
		List<Integer> ownerEvents = Lists.newArrayList();
		List<Integer> supervisorEvents = Lists.newArrayList();
		List<Integer> followerEvents = Lists.newArrayList();
		List<Integer> subscriberEvents = Lists.newArrayList();
		List<Integer> strategicInitiativesEvents = Lists.newArrayList();
		List<Integer> bdmTaggedEvents = Lists.newArrayList();
		Map<RecipientType, List<Integer>> recipientEventMap = Maps.newHashMap();
		for (OperationEventRecipientMappingT eventRecMaping : eventRecipientMapping) {
			if (eventRecMaping.getOwnerSubscription()) {
				ownerEvents.add(eventRecMaping.getEventId());
			}
			if (eventRecMaping.getSupervisorSubscription()) {
				supervisorEvents.add(eventRecMaping.getEventId());
			}
			if (eventRecMaping.getFollowerSubscription()) {
				followerEvents.add(eventRecMaping.getEventId());
			}
			if (eventRecMaping.getConditionSubscriber()) {
				subscriberEvents.add(eventRecMaping.getEventId());
			}
			if (eventRecMaping.getStrategicInitiatives()) {
				strategicInitiativesEvents.add(eventRecMaping.getEventId());
			}
			if (eventRecMaping.getBdmTagged()) {
				bdmTaggedEvents.add(eventRecMaping.getEventId());
			}
		}
		recipientEventMap.put(RecipientType.OWNER, ownerEvents);
		recipientEventMap.put(RecipientType.SUPERVISOR, supervisorEvents);
		recipientEventMap.put(RecipientType.FOLLOWER, followerEvents);
		recipientEventMap.put(RecipientType.SUBSCRIBER, subscriberEvents);
		recipientEventMap.put(RecipientType.STRATEGIC_INITIATIVE,
				strategicInitiativesEvents);
		recipientEventMap.put(RecipientType.BDM_TAGGED, bdmTaggedEvents);
		return recipientEventMap;
	}

	private Recipient constructRecipient(String recipientId,
			OwnerType ownerType, String subordinateId,
			RecipientType recipientType, boolean isRemoved, List<Integer> events) {
		Recipient recipient = new Recipient();
		Map<RecipientType, List<Integer>> recipientTypeEventMap = Maps
				.newHashMap();
		recipient.setId(recipientId);
		recipient.setOwnerType(ownerType);
		if (!recipientType.equals(RecipientType.REMOVED_USER)) {
			recipient.getSubodinates().add(
					getSubordinate(subordinateId, ownerType));
			List<Integer> subscribedEvents = notificationTypeEventMappingRepository
					.getNotificationEventIdsForUser(recipientId);
			events.retainAll(subscribedEvents);// retain only the subscribed
												// events
		}
		recipientTypeEventMap.put(recipientType, events);
		recipient.setEvents(recipientTypeEventMap);
		recipient.setRemoved(isRemoved);
		return recipient;
	}

	/**
	 * construct the subordinate in Recipient object
	 * 
	 * @param subordinateId
	 * @param subordinateType
	 * @return
	 */
	private Recipient getSubordinate(String subordinateId,
			OwnerType subordinateType) {
		Recipient subordinate = new Recipient();
		subordinate.setId(subordinateId);
		subordinate.setOwnerType(subordinateType);
		return subordinate;
	}

	/**
	 * 
	 * @param opType
	 * @param reciType
	 * @return
	 */
	private boolean isRecipientRequired(OperationType opType,
			RecipientType reciType) {
		boolean isRecipientRequired = false;
		switch (opType) {
		case OPPORTUNITY_CREATE:
		case OPPORTUNITY_EDIT:
			switch (reciType) {
			case OWNER:
			case SUPERVISOR:
			case STRATEGIC_INITIATIVE:
				isRecipientRequired = true;
				break;
			case SUBSCRIBER:
				isRecipientRequired = (opType == OperationType.OPPORTUNITY_CREATE);
			case FOLLOWER:
				isRecipientRequired = (opType == OperationType.OPPORTUNITY_EDIT);
				break;
			default:
				break;
			}
			break;
		case CONNECT_CREATE:
		case CONNECT_EDIT:
			switch (reciType) {
			case OWNER:
			case SUPERVISOR:
				isRecipientRequired = true;
				break;
			case FOLLOWER:
				isRecipientRequired = (opType == OperationType.CONNECT_EDIT);
				break;
			case SUBSCRIBER:
				isRecipientRequired = (opType == OperationType.CONNECT_CREATE);
				break;
			default:
				break;
			}
			break;
		case TASK_CREATE:
		case TASK_EDIT:
			switch (reciType) {
			case OWNER:
			case SUPERVISOR:
			case BDM_TAGGED:
				isRecipientRequired = true;
				break;
			default:
				break;
			}
			break;
		case OPPORTUNITY_FOLLOW:
		case CONNECT_FOLLOW:
			isRecipientRequired = (reciType == RecipientType.OWNER);
			break;
		case OPPORTUNITY_COMMENT:
		case CONNECT_COMMENT:
		case TASK_COMMENT:
			isRecipientRequired = (reciType == RecipientType.OWNER || reciType == RecipientType.SUPERVISOR);
			break;

		default:
			break;
		}

		return isRecipientRequired;
	}

	/**
	 * method used to get all the notifications based on the operation type and
	 * changes occurred
	 * 
	 * @param recipients
	 * @param entityId
	 * @param entityType
	 * @param operationType
	 * @param currentUser
	 * @param auditConnectT
	 * @param auditConnectSecondaryOwnerLinkTs
	 * @param auditOpportunityT
	 * @param auditOpportunitySalesSupportLinkTs
	 * @param auditBidDetailsT
	 * @param auditBidOfficeGroupOwnerLinkTs
	 * @param auditTaskT
	 * @param auditTaskBdmsTaggedLinkTs
	 * @return
	 * @throws Exception
	 */
	public List<UserNotificationsT> getUserNotifications(
			List<Recipient> recipients,
			String entityId,
			String entityType,
			OperationType operationType,
			String currentUser,
			AuditConnectT auditConnectT,
			List<AuditConnectSecondaryOwnerLinkT> auditConnectSecondaryOwnerLinkTs,
			AuditOpportunityT auditOpportunityT,
			List<AuditOpportunitySalesSupportLinkT> auditOpportunitySalesSupportLinkTs,
			AuditBidDetailsT auditBidDetailsT,
			List<AuditBidOfficeGroupOwnerLinkT> auditBidOfficeGroupOwnerLinkTs,
			AuditTaskT auditTaskT,
			List<AuditTaskBdmsTaggedLinkT> auditTaskBdmsTaggedLinkTs)
			throws Exception {
		logger.info("Inside getUserNotifications method");
		List<UserNotificationsT> userNotificationList = null;
		Map<NotificationSettingEvent, RecipientMessageTemplateMapping> eventIdsMap = Maps
				.newHashMap();
		Map<String, String> data = Maps.newHashMap();
		String customerOrPartnerName = null;
		List<NotificationEventGroupMappingT> notificationEventGroupMappingTs = (List<NotificationEventGroupMappingT>) notificationEventGroupMappingTRepository
				.findAll();
		switch (operationType.getEntityType()) {
		case CONNECT:

			// Getting customer/partner details of connect
			ConnectT connect = connectRepository.findOne(entityId);
			if (StringUtils.equals(Constants.CUSTOMER,
					connect.getConnectCategory())) {
				customerOrPartnerName = customerRepository.findOne(
						connect.getCustomerId()).getCustomerName();
			} else if (StringUtils.equals(Constants.PARTNER,
					connect.getConnectCategory())) {
				customerOrPartnerName = partnerRepository.findOne(
						connect.getPartnerId()).getPartnerName();
			}
			data = constructMapWithDefaultValues(EntityType.CONNECT.name(),
					connect.getConnectName(), connect.getConnectCategory()
							.toLowerCase(), customerOrPartnerName, currentUser,
					null, null);

			eventIdsMap = getEventIdsForConnect(entityId, recipients,
					operationType, currentUser, data, auditConnectT,
					auditConnectSecondaryOwnerLinkTs,
					notificationEventGroupMappingTs);
			userNotificationList = getNotifications(eventIdsMap, recipients,
					EntityType.CONNECT.name(), entityId, data,
					notificationEventGroupMappingTs);
			logger.info("End of getting notifications for connect");
			break;
		case OPPORTUNITY:
			OpportunityT opportunity = opportunityRepository.findOne(entityId);
			String customerName = customerRepository.findOne(
					opportunity.getCustomerId()).getCustomerName();

			data = constructMapWithDefaultValues(EntityType.OPPORTUNITY.name(),
					opportunity.getOpportunityName(), Constants.CUSTOMER,
					customerName, currentUser, null, null);

			eventIdsMap = getEventIdsForOpportunity(entityId, recipients,
					operationType, currentUser, data, auditOpportunityT,
					auditOpportunitySalesSupportLinkTs, auditBidDetailsT,
					auditBidOfficeGroupOwnerLinkTs,
					notificationEventGroupMappingTs, opportunity);
			userNotificationList = getNotifications(eventIdsMap, recipients,
					EntityType.OPPORTUNITY.name(), entityId, data,
					notificationEventGroupMappingTs);
			logger.info("End of getting notifications for Opportunity");
			break;
		case TASK:
			TaskT task = taskRepository.findOne(entityId);
			String category = null;
			String parentEntity = null;
			String parentEntityName = null;
			if (task.getEntityReference().equals(EntityType.OPPORTUNITY.name())) {
				OpportunityT opportunityT = opportunityRepository.findOne(task
						.getOpportunityId());
				customerOrPartnerName = customerRepository.findOne(
						opportunityT.getCustomerId()).getCustomerName();
				category = Constants.CUSTOMER;
				parentEntity = Constants.OPPORTUNITY;
				parentEntityName = opportunityT.getOpportunityName();
			} else if (task.getEntityReference().equals(
					EntityType.CONNECT.name())) {
				ConnectT connectT = connectRepository.findOne(task
						.getConnectId());
				parentEntity = Constants.CONNECT;
				parentEntityName = connectT.getConnectName();
				if (StringUtils.equals(Constants.CUSTOMER,
						connectT.getConnectCategory())) {
					customerOrPartnerName = customerRepository.findOne(
							connectT.getCustomerId()).getCustomerName();
					category = Constants.CUSTOMER;

				} else if (StringUtils.equals(Constants.PARTNER,
						connectT.getConnectCategory())) {
					customerOrPartnerName = partnerRepository.findOne(
							connectT.getPartnerId()).getPartnerName();
					category = Constants.PARTNER;
				}
			}
			data = constructMapWithDefaultValues(Constants.TASK,
					task.getTaskDescription(), category, customerOrPartnerName,
					currentUser, parentEntity, parentEntityName);

			eventIdsMap = getEventIdsForTask(entityId, recipients,
					operationType, currentUser, data, auditTaskT,
					auditTaskBdmsTaggedLinkTs, notificationEventGroupMappingTs);
			userNotificationList = getNotifications(eventIdsMap, recipients,
					EntityType.TASK.name(), entityId, data,
					notificationEventGroupMappingTs);
			logger.info("End of getting notifications for Task");
			break;
		default:
			break;
		}

		return userNotificationList;
	}
	
	/**
	 * Method used to retrieve the notifications
	 * @param eventIdsMap
	 * @param recipients
	 * @param entityType
	 * @param entityId
	 * @param data
	 * @param notificationEventGroupMappingTs
	 * @return
	 * @throws Exception
	 */
	private List<UserNotificationsT> getNotifications(
			Map<NotificationSettingEvent, RecipientMessageTemplateMapping> eventIdsMap,
			List<Recipient> recipients, String entityType, String entityId,
			Map<String, String> data,
			List<NotificationEventGroupMappingT> notificationEventGroupMappingTs)
			throws Exception {
		logger.info("Inside getNotifications method");
		List<UserNotificationsT> userNotifications = Lists.newArrayList();
		if (eventIdsMap != null) {
			String templateForRemoveduser = getMessageTemplateByEventId(
					notificationEventGroupMappingTs,
					NotificationSettingEvent.OWNER_CHANGE.getEventId());
			if (CollectionUtils.isNotEmpty(recipients)) {
				for (Recipient recipient : recipients) {
					if (!recipient.isRemoved()) {
						List<Integer> eventIds = removeDuplicateEvents(
								recipient, eventIdsMap);
						if (CollectionUtils.isNotEmpty(eventIds)) {
							for (Integer eventId : eventIds) {
								List<UserNotificationsT> notifications = getNotificationForEventId(
										eventId, eventIdsMap, recipient,
										entityType, entityId);
								if (CollectionUtils.isNotEmpty(notifications)) {
									userNotifications.addAll(notifications);
								}
							}
						}
					} else {
						UserNotificationsT notificationForRemovedOwners = constructNotificationsForRemovedOwners(
								recipient, data, templateForRemoveduser,
								entityType, entityId);
						userNotifications.add(notificationForRemovedOwners);
					}
				}
			}

		}
		logger.info("End of getNotifications method");
		return userNotifications;
	}
	
	/**
	 * Constructs notification for removed owners
	 * @param recipient
	 * @param data
	 * @param templateForRemoveduser
	 * @param entityType
	 * @param entityId
	 * @return
	 * @throws Exception
	 */
	private UserNotificationsT constructNotificationsForRemovedOwners(
			Recipient recipient, Map<String, String> data,
			String templateForRemoveduser, String entityType, String entityId)
			throws Exception {
		String message = constructMessageTemplate(data, null, null,
				Constants.REMOVED, recipient.getOwnerType().getName(),
				templateForRemoveduser, null, null);

		return constructNotification(
				NotificationSettingEvent.OWNER_CHANGE.getEventId(),
				recipient.getId(), entityType, entityId, message);
	}

	/**
	 * 
	 * Remove duplicate events with following scenarios <li>if 1, remove 9, 10,
	 * 11</li> <li>if 8, remove 13</li> <li>if 9 and tmpl
	 * string(salesCode-WIN/LOSS), remove 14</li> refer
	 * {@link NotificationSettingEvent} for the event code
	 * 
	 * @param recipient
	 * @param eventIdsMap
	 * @return
	 */
	private List<Integer> removeDuplicateEvents(
			Recipient recipient,
			Map<NotificationSettingEvent, RecipientMessageTemplateMapping> eventIdsMap) {

		Set<Integer> filteredEvents = Sets.newHashSet();

		for (Entry<RecipientType, List<Integer>> entry : recipient.getEvents()
				.entrySet()) {
			filteredEvents.addAll(entry.getValue());
		}

		if (eventIdsMap.containsKey(NotificationSettingEvent.OWNER_CHANGE)) {
			// if he is a newly added or removed owner, suppress supervisor
			// notification(if supervisor), key changes, and collab conditions
			if (filteredEvents.contains(NotificationSettingEvent.OWNER_CHANGE
					.getEventId())) {
				RecipientMessageTemplateMapping recipientMessageTemplateMapping = eventIdsMap
						.get(NotificationSettingEvent.OWNER_CHANGE);
				List<String> addedUsers = recipientMessageTemplateMapping
						.getUsers();
				if (CollectionUtils.isNotEmpty(addedUsers)) {
					if (addedUsers.contains(recipient.getId())) {
						filteredEvents
								.remove(NotificationSettingEvent.SUBORDINATES_AS_OWNERS
										.getEventId());
						filteredEvents
								.remove(NotificationSettingEvent.KEY_CHANGES
										.getEventId());
						filteredEvents
								.remove(NotificationSettingEvent.COLLAB_CONDITION
										.getEventId());
					}
				}

			} else if (filteredEvents
					.contains(NotificationSettingEvent.KEY_CHANGES.getEventId())) {
				filteredEvents
						.remove(NotificationSettingEvent.SUBORDINATES_AS_OWNERS
								.getEventId());
			}
		}

		if (eventIdsMap.containsKey(NotificationSettingEvent.COMMENT)) {
			// suppress comment for supervisor
			if (filteredEvents.contains(NotificationSettingEvent.COMMENT
					.getEventId())) {
				filteredEvents
						.remove(NotificationSettingEvent.COMMENT_ON_SUBORDINATES_ENTITY
								.getEventId());
			}
		}

		if (eventIdsMap.containsKey(NotificationSettingEvent.KEY_CHANGES)
				&& filteredEvents.contains(NotificationSettingEvent.KEY_CHANGES
						.getEventId())) {
			// If Key changes having sales stage changes either to win or lost,
			// Supervisor notification for win/loss to be suppressed
			RecipientMessageTemplateMapping templMsg = eventIdsMap
					.get(NotificationSettingEvent.KEY_CHANGES);
			List<String> templates = templMsg.getTemplates();
			if (CollectionUtils.isNotEmpty(templates)) {
				String winDescription = SalesStageCode.WIN.getDescription();
				String lostDescription = SalesStageCode.LOST.getDescription();
				for (String template : templates) {
					if (template.contains(winDescription)
							|| template.contains(lostDescription)) {
						filteredEvents
								.remove(NotificationSettingEvent.SUBORDINATES_OPPORTUNITIES_WL
										.getEventId());
						break;
					}
				}
			}

		}

		return Lists.newArrayList(filteredEvents);
	}
	
	/**
	 * constructs the notification for the event id given 
	 * @param eventId
	 * @param eventIdsMap
	 * @param recipient
	 * @param entityType
	 * @param entityId
	 * @return
	 * @throws Exception
	 */
	private List<UserNotificationsT> getNotificationForEventId(
			Integer eventId,
			Map<NotificationSettingEvent, RecipientMessageTemplateMapping> eventIdsMap,
			Recipient recipient, String entityType, String entityId)
			throws Exception {
		logger.info("Inside getNotificationForEventId method");
		NotificationSettingEvent settingEvent = NotificationSettingEvent
				.getByValue(eventId);
		List<UserNotificationsT> userNotificationsTs = Lists.newArrayList();
		if (!eventIdsMap.containsKey(settingEvent)) {

			return null;
		}
		String recipientId = recipient.getId();
		RecipientMessageTemplateMapping recipientMessageTemplateMapping = eventIdsMap
				.get(settingEvent);
		if (recipientMessageTemplateMapping != null) {
			List<String> templates = recipientMessageTemplateMapping
					.getTemplates();
			String template = recipientMessageTemplateMapping.getTemplates()
					.get(0);

			switch (settingEvent) {
			case OWNER_CHANGE:
				UserNotificationsT notificationForOwnerChange = getNotificationForOwnerChangeAndBdmTaggedToTask(
						eventIdsMap, recipient, entityType, entityId, eventId,
						recipientMessageTemplateMapping);
				if (notificationForOwnerChange != null) {
					userNotificationsTs.add(notificationForOwnerChange);
				}

				break;
			case TAG_UPDATES_TASK:
				UserNotificationsT notificationForBdmTagged = getNotificationForOwnerChangeAndBdmTaggedToTask(
						eventIdsMap, recipient, entityType, entityId, eventId,
						recipientMessageTemplateMapping);
				if (notificationForBdmTagged != null) {
					userNotificationsTs.add(notificationForBdmTagged);
				}
				break;
			case FOLLOW_CONNECT_OPPORTUNITY:
				userNotificationsTs.add(constructNotification(eventId,
						recipientId, entityType, entityId, template));
				break;
			case COMMENT:
				userNotificationsTs.add(constructNotification(eventId,
						recipientId, entityType, entityId, template));
				break;
			case SUBORDINATES_AS_OWNERS:
				UserNotificationsT notificationForOwnerChangeToSupervisor = getNotificationForOwnerChangeToSupervisor(
						eventIdsMap, recipient, entityType, entityId, eventId,
						recipientMessageTemplateMapping,
						recipient.getSubodinates());
				if (notificationForOwnerChangeToSupervisor != null) {
					userNotificationsTs
							.add(notificationForOwnerChangeToSupervisor);
				}

				break;
			case COLLAB_CONDITION:
				userNotificationsTs.add(constructNotification(eventId,
						recipientId, entityType, entityId, template));
				break;
			case COMMENT_ON_SUBORDINATES_ENTITY:
				userNotificationsTs.add(constructNotification(eventId,
						recipientId, entityType, entityId, template));
				break;
			case DIGITAL_OPPORTUNITIES:
				userNotificationsTs
						.add(getNotificationForDigitalOpportunitiesAndWonLost(
								eventId, recipientId, entityType, entityId,
								template, recipient.getSubodinates()));
				break;
			case STRATEGIC_OPPORTUNITIES:
				userNotificationsTs
						.add(getNotificationForDigitalOpportunitiesAndWonLost(
								eventId, recipientId, entityType, entityId,
								template, null));
				break;
			case KEY_CHANGES:
				userNotificationsTs.addAll(getNotificationForKeyChanges(
						eventId, recipientId, entityType, entityId, templates));
				break;
			case SUBORDINATES_OPPORTUNITIES_WL:
				userNotificationsTs
						.add(getNotificationForDigitalOpportunitiesAndWonLost(
								eventId, recipientId, entityType, entityId,
								template, recipient.getSubodinates()));
				break;
			default:
				break;
			}
		}
		logger.info("End of getNotificationForEventId method");
		return userNotificationsTs;

	}

	private List<UserNotificationsT> getNotificationForKeyChanges(
			Integer eventId, String recipientId, String entityType,
			String entityId, List<String> templates) {
		List<UserNotificationsT> userNotifications = Lists.newArrayList();
		for (String template : templates) {
			userNotifications.add(constructNotification(eventId, recipientId,
					entityType, entityId, template));
		}
		return userNotifications;
	}

	private UserNotificationsT getNotificationForDigitalOpportunitiesAndWonLost(
			Integer eventId, String recipientId, String entityType,
			String entityId, String template, List<Recipient> subordinates)
			throws Exception {

		String subordinateNames = null;

		if (CollectionUtils.isNotEmpty(subordinates)) {
			List<String> nameList = Lists.newArrayList();
			for (Recipient subordinate : subordinates) {
				String name = getUserNameForUserId(subordinate.getId());
				String nameWithType = getNameWithType(
						subordinate.getOwnerType(), name);
				nameList.add(nameWithType);
			}
			subordinateNames = StringUtils.join(nameList, ", ");
		}

		String templateForDigitalOpportunity = constructMessageTemplate(null,
				null, null, null, null, template, subordinateNames, null);
		return constructNotification(eventId, recipientId, entityType,
				entityId, templateForDigitalOpportunity);
	}

	private String getNameWithType(OwnerType ownerType, String name) {
		String nameWithType = new StringBuffer(name).append("(")
				.append(ownerType.getName()).append(")").toString();
		return nameWithType;
	}

	/**
	 * this method is used to construct the notification details
	 * 
	 * @param eventId
	 * @param recipient
	 * @param entityType
	 * @param entityId
	 * @param messageTemplate
	 * @return
	 */
	private UserNotificationsT constructNotification(Integer eventId,
			String recipient, String entityType, String entityId,
			String messageTemplate) {
		UserNotificationsT userNotificationsT = new UserNotificationsT();
		userNotificationsT.setComments(messageTemplate);
		userNotificationsT.setEventId(eventId);
		userNotificationsT.setRead(Constants.NO);
		userNotificationsT.setRecipient(recipient);
		userNotificationsT.setUserId("System");
		if (StringUtils.equals(entityType, EntityType.CONNECT.name())) {
			userNotificationsT.setEntityType(entityType);
			userNotificationsT.setConnectId(entityId);
		}

		if (StringUtils.equals(entityType, EntityType.OPPORTUNITY.name())) {
			userNotificationsT.setEntityType(entityType);
			userNotificationsT.setOpportunityId(entityId);
		}
		if (StringUtils.equals(entityType, EntityType.TASK.name())) {
			userNotificationsT.setEntityType(entityType);
			userNotificationsT.setTaskId(entityId);
		}

		return userNotificationsT;
	}

	/**
	 * This method is used to get the event Ids and message templates for which
	 * the changes occured in a Task
	 * 
	 * @param entityId
	 * @param recipients
	 * @param operationType
	 * @param currentUser
	 * @param data
	 * @param auditTaskBdmsTaggedLinkTs
	 * @param auditTaskT
	 * @param notificationEventGroupMappingTs
	 * @return
	 * @throws Exception
	 */
	private Map<NotificationSettingEvent, RecipientMessageTemplateMapping> getEventIdsForTask(
			String entityId, List<Recipient> recipients,
			OperationType operationType, String currentUser,
			Map<String, String> data, AuditTaskT auditTaskT,
			List<AuditTaskBdmsTaggedLinkT> auditTaskBdmsTaggedLinkTs,
			List<NotificationEventGroupMappingT> notificationEventGroupMappingTs)
			throws Exception {
		logger.info("Inside getEventIdsForTask method");
		Map<NotificationSettingEvent, RecipientMessageTemplateMapping> eventsMap = Maps
				.newHashMap();
		if (operationType == OperationType.TASK_COMMENT) {
			// event 8 and 13
			eventsMap.putAll(getRecipientMessageTemplateMapping(
					notificationEventGroupMappingTs, 8,
					NotificationSettingEvent.COMMENT, data, null, null));
			eventsMap.putAll(getRecipientMessageTemplateMapping(
					notificationEventGroupMappingTs, 13,
					NotificationSettingEvent.COMMENT_ON_SUBORDINATES_ENTITY,
					data, null, null));
		} else {
			// Added owners
			// Added owners
			List<String> addedOwners = getAddedOwnersOfTask(auditTaskT);
			if (CollectionUtils.isNotEmpty(addedOwners)) {
				eventsMap.putAll(getMapForOwnerChange(data, addedOwners));
			}
			// Key changes
			if (operationType == OperationType.TASK_EDIT) {
				List<String> templatesForKeyChanges = getMessageTemplatesForKeyChangesForTask(
						auditTaskT, data, notificationEventGroupMappingTs);
				if (CollectionUtils.isNotEmpty(templatesForKeyChanges)) {
					RecipientMessageTemplateMapping RecipientMessageTemplateForKeyChanges = constructRecipientMessageTemplateMapping(
							templatesForKeyChanges, null);
					eventsMap.put(NotificationSettingEvent.KEY_CHANGES,
							RecipientMessageTemplateForKeyChanges);
				}

			}

			// tags to follow updates
			if (CollectionUtils.isNotEmpty(auditTaskBdmsTaggedLinkTs)) {
				List<String> bdmsTagged = getBdmTaggedOfTask(auditTaskBdmsTaggedLinkTs);
				if (CollectionUtils.isNotEmpty(bdmsTagged)) {
					eventsMap.putAll(getMapForBdmTAgged(data, bdmsTagged,
							notificationEventGroupMappingTs));
				}
			}
		}
		logger.info("End of getEventIdsForTask method");
		return eventsMap;
	}

	private Map<NotificationSettingEvent, RecipientMessageTemplateMapping> getMapForBdmTAgged(
			Map<String, String> data, List<String> bdmsTagged,
			List<NotificationEventGroupMappingT> notificationEventGroupMappingTs) throws Exception {
		Map<NotificationSettingEvent, RecipientMessageTemplateMapping> eventsMap = Maps
				.newHashMap();
		List<String> templates = Lists.newArrayList();
		String templateForBDMTagged = getMessageTemplateByEventId(
				notificationEventGroupMappingTs, 2);
		templates.add(constructMessageTemplate(data, null, null, null, null, templateForBDMTagged, null, null));
		RecipientMessageTemplateMapping recipientMessageTemplateMapping = constructRecipientMessageTemplateMapping(
				templates, bdmsTagged);
		eventsMap.put(NotificationSettingEvent.TAG_UPDATES_TASK,
				recipientMessageTemplateMapping);
		return eventsMap;
	}

	private List<String> getBdmTaggedOfTask(
			List<AuditTaskBdmsTaggedLinkT> auditTaskBdmsTaggedLinkTs) {
		List<String> bdmsTagged = Lists.newArrayList();
		for (AuditTaskBdmsTaggedLinkT auditTaskBdmsTaggedLinkT : auditTaskBdmsTaggedLinkTs) {
			if (OPERATION_INSERT.equals(auditTaskBdmsTaggedLinkT
					.getOperationType())) {
				bdmsTagged.add(auditTaskBdmsTaggedLinkT.getOldBdmsTagged());
			}
		}
		return bdmsTagged;
	}

	private List<String> getMessageTemplatesForKeyChangesForTask(
			AuditTaskT auditTaskT, Map<String, String> data,
			List<NotificationEventGroupMappingT> notificationEventGroupMappingTs)
			throws Exception {
		logger.info("Inside getMessageTemplatesForKeyChangesForTask method");
		List<String> templates = Lists.newArrayList();
		if (auditTaskT != null) {
			String template = getMessageTemplateByEventId(
					notificationEventGroupMappingTs, 9);
			String templateForUpdate = new StringBuffer(template).append(
					Constants.FROM_TO_STRING).toString();
			if (compareStringValueForUpdate(auditTaskT.getOldTaskOwner(),
					auditTaskT.getNewTaskOwner())) {
				templates.add(constructMessageTemplate(data,
						getUserNameForUserId(auditTaskT.getOldTaskOwner()),
						getUserNameForUserId(auditTaskT.getNewTaskOwner()),
						Constants.UPDATED, Constants.TASK_OWNER_FIELD,
						templateForUpdate, null, null));
			}
			if (compareStringValueForUpdate(auditTaskT.getOldTaskStatus(),
					auditTaskT.getNewTaskStatus())) {
				templates.add(constructMessageTemplate(data,
						auditTaskT.getOldTaskStatus(),
						auditTaskT.getNewTaskStatus(), Constants.UPDATED,
						Constants.TASK_STATUS_FIELD, templateForUpdate, null, null));
			}
			if (compareDateValueForUpdate(
					auditTaskT.getOldTargetDateForCompletion(),
					auditTaskT.getNewTargetDateForCompletion())) {
				templates.add(constructMessageTemplate(data, ACTUAL_FORMAT
						.format(auditTaskT.getOldTargetDateForCompletion()),
						ACTUAL_FORMAT.format(auditTaskT
								.getNewTargetDateForCompletion()),
						Constants.UPDATED,
						Constants.TARGET_DATE_OF_COMPLETION_FIELD, templateForUpdate,
						null, null));
			}
		}
		logger.info("End of getMessageTemplatesForKeyChangesForTask method");
		return templates;
	}

	private List<String> getAddedOwnersOfTask(AuditTaskT auditTaskT) {
		List<String> addedOwners = Lists.newArrayList();
		if (auditTaskT != null) {
			if (OPERATION_INSERT.equals(auditTaskT.getOperationType())
					&& StringUtils.isNotEmpty(auditTaskT.getNewTaskOwner())) {
				addedOwners.add(auditTaskT.getNewTaskOwner());
			} else if (OPERATION_UPDATE.equals(auditTaskT.getOperationType())
					&& !StringUtils.equals(auditTaskT.getOldTaskOwner(),
							auditTaskT.getNewTaskOwner())) {
				addedOwners.add(auditTaskT.getNewTaskOwner());
			}
		}

		return addedOwners;
	}

	/**
	 * This method is used to get the events and message templates for which the
	 * changes occurred in a connect
	 * 
	 * @param entityId
	 * @param recipients
	 * @param operationType
	 * @param currentUser
	 * @param data
	 * @param auditConnectSecondaryOwnerLinkTs
	 * @param auditConnectT
	 * @param notificationEventGroupMappingTs
	 * @return
	 * @throws Exception
	 */
	private Map<NotificationSettingEvent, RecipientMessageTemplateMapping> getEventIdsForConnect(
			String entityId,
			List<Recipient> recipients,
			OperationType operationType,
			String currentUser,
			Map<String, String> data,
			AuditConnectT auditConnectT,
			List<AuditConnectSecondaryOwnerLinkT> auditConnectSecondaryOwnerLinkTs,
			List<NotificationEventGroupMappingT> notificationEventGroupMappingTs)
			throws Exception {
		logger.info("Inside getEventIdsForConnect Method");
		Map<NotificationSettingEvent, RecipientMessageTemplateMapping> eventsMap = Maps
				.newHashMap();
		if (operationType == OperationType.CONNECT_FOLLOW) {
			// opp. follow
			// add event 3 with current user
			eventsMap.putAll(getRecipientMessageTemplateMapping(
					notificationEventGroupMappingTs, 3,
					NotificationSettingEvent.FOLLOW_CONNECT_OPPORTUNITY, data,
					null, null));
		} else if (operationType == OperationType.CONNECT_COMMENT) {
			// opp.comment
			// add event 8 and 13
			eventsMap.putAll(getRecipientMessageTemplateMapping(
					notificationEventGroupMappingTs, 8,
					NotificationSettingEvent.COMMENT, data, null, null));
			eventsMap.putAll(getRecipientMessageTemplateMapping(
					notificationEventGroupMappingTs, 13,
					NotificationSettingEvent.COMMENT_ON_SUBORDINATES_ENTITY,
					data, null, null));

		} else {

			List<String> addedOwners = getAddedOwnersOfConnect(auditConnectT,
					auditConnectSecondaryOwnerLinkTs);
			// Owner addition /Updation
			if (CollectionUtils.isNotEmpty(addedOwners)) {
				logger.info("fetched added owners to connect");
				eventsMap.putAll(getMapForOwnerChange(data, addedOwners));
			}
			// Get message templates for key changes of connect
			if (operationType == OperationType.CONNECT_EDIT) {
				List<String> templatesForKeyChanges = getMessageTemplatesForKeyChangesOfConnect(
						auditConnectT, auditConnectSecondaryOwnerLinkTs, data,
						notificationEventGroupMappingTs);
				if (CollectionUtils.isNotEmpty(templatesForKeyChanges)) {
					RecipientMessageTemplateMapping RecipientMessageTemplateForKeyChanges = constructRecipientMessageTemplateMapping(
							templatesForKeyChanges, null);
					eventsMap.put(NotificationSettingEvent.KEY_CHANGES,
							RecipientMessageTemplateForKeyChanges);
				}
			}

			if (operationType == OperationType.CONNECT_CREATE) {
				// add event Id 10
				eventsMap.putAll(getRecipientMessageTemplateMapping(
						notificationEventGroupMappingTs, 10,
						NotificationSettingEvent.COLLAB_CONDITION, data, null,
						null));
			}

		}
        logger.info("End of getEventIdsForConnect method");
		return eventsMap;
	}

	private RecipientMessageTemplateMapping constructRecipientMessageTemplateMapping(
			List<String> templates, List<String> users) {
		RecipientMessageTemplateMapping mapping = new RecipientMessageTemplateMapping();
		mapping.setTemplates(templates);
		mapping.setUsers(users);
		return mapping;
	}

	/**
	 * constructs map with the default values of an entity
	 * 
	 * @param entityType
	 * @param entityName
	 * @param customerOrPartner
	 * @param customerOrPartnerName
	 * @param currentUser
	 * @param parentEntity
	 * @param parentEntityName
	 * @return
	 */
	private Map<String, String> constructMapWithDefaultValues(
			String entityType, String entityName, String customerOrPartner,
			String customerOrPartnerName, String currentUser,
			String parentEntity, String parentEntityName) {
		Map<String, String> data = Maps.newHashMap();
		data.put("entityType", entityType);
		data.put("entityName", entityName);
		data.put("customerOrPartner", customerOrPartner);
		data.put("custOrPartValue", customerOrPartnerName);
		data.put("user", currentUser);
		data.put("parentEntity", parentEntity);
		data.put("parentEntityName", parentEntityName);
		return data;
	}

	private List<String> getMessageTemplatesForKeyChangesOfConnect(
			AuditConnectT auditConnectT,
			List<AuditConnectSecondaryOwnerLinkT> auditConnectSecondaryOwnerLinkTs,
			Map<String, String> data,
			List<NotificationEventGroupMappingT> notificationEventGroupMappingTs)
			throws Exception {
		logger.info("Inside getMessageTemplatesForKeyChangesOfConnect Method");
		List<String> templates = Lists.newArrayList();
		String template = getMessageTemplateByEventId(
				notificationEventGroupMappingTs, 9);
		String templateForUpdate = new StringBuffer(template).append(
				Constants.FROM_TO_STRING).toString();
		String templateForAdd = new StringBuffer(template).append(
				Constants.ENTITY_NAME_TO_STRING).toString();
		if (auditConnectT != null) {
			// Primary Owner
			if (!StringUtils.equals(auditConnectT.getOldPrimaryOwner(),
					auditConnectT.getNewPrimaryOwner())) {
				templates
						.add(constructMessageTemplate(data,
								getUserNameForUserId(auditConnectT
										.getOldPrimaryOwner()),
								getUserNameForUserId(auditConnectT
										.getNewPrimaryOwner()),
								Constants.UPDATED,
								Constants.PRIMARY_OWNER_FIELD,
								templateForUpdate, null, null));
			}
			// Start date time of connect
			if (!auditConnectT.getOldStartDatetimeOfConnect().equals(
					auditConnectT.getNewStartDatetimeOfConnect())) {
				templates
						.add(constructMessageTemplate(data, auditConnectT
								.getOldStartDatetimeOfConnect().toString(),
								auditConnectT.getNewStartDatetimeOfConnect()
										.toString(), Constants.UPDATED,
								Constants.START_DATE_TIME_OF_CONNECT_FIELD,
								templateForUpdate, null, null));
			}
			// End date time of Connect
			if (!auditConnectT.getOldEndDatetimeOfConnect().equals(
					auditConnectT.getNewEndDatetimeOfConnect())) {
				templates.add(constructMessageTemplate(data, auditConnectT
						.getOldEndDatetimeOfConnect().toString(), auditConnectT
						.getNewEndDatetimeOfConnect().toString(),
						Constants.UPDATED,
						Constants.END_DATE_TIME_OF_CONNECT_FIELD,
						templateForUpdate, null, null));
			}
		}
		// Connect secondary owners
		if (CollectionUtils.isNotEmpty(auditConnectSecondaryOwnerLinkTs)) {
			templates.addAll(getTemplatesForAddRemoveConnectSecondaryOwners(
					auditConnectSecondaryOwnerLinkTs, templateForAdd, data));
		}
		logger.info("End of getMessageTemplatesForKeyChangesOfConnect Method");
		return templates;
	}

	/**
	 * This method is used to construct the message template with the data being
	 * replaced
	 * 
	 * @param data
	 * @param fromValue
	 * @param toValue
	 * @param status
	 * @param fieldName
	 * @param template
	 * @return
	 * @throws Exception
	 */
	private String constructMessageTemplate(Map<String, String> data,
			String fromValue, String toValue, String status, String fieldName,
			String template, String subordinates, String associates)
			throws Exception {
		Map<String, String> dynamicData = Maps.newHashMap();
		if (data != null) {
			dynamicData.putAll(data);
		}
		dynamicData.put("status", status);
		dynamicData.put("from", fromValue);
		dynamicData.put("to", toValue);
		dynamicData.put("fieldName", fieldName);
		dynamicData.put("subordinate", subordinates);
		dynamicData.put("associates", associates);
		return replaceTokens(template, dynamicData);
		// return template + "##";
		// return mergeTmplWithData(dynamicData, template);
	}

	public String getUserNameForUserId(String userId) {
		String userName = userRepository.findUserNameByUserId(userId);
		return userName;
	}
	
	/**
	 * Used to get the added owners of connect
	 * @param auditConnectT
	 * @param auditConnectSecondaryOwnerLinkTs
	 * @return
	 */
	private List<String> getAddedOwnersOfConnect(
			AuditConnectT auditConnectT,
			List<AuditConnectSecondaryOwnerLinkT> auditConnectSecondaryOwnerLinkTs) {
		List<String> addedOwners = Lists.newArrayList();
		if (auditConnectT != null) {
			if (OPERATION_INSERT.equals(auditConnectT.getOperationType())
					&& StringUtils.isNotEmpty(auditConnectT
							.getNewPrimaryOwner())) {
				addedOwners.add(auditConnectT.getNewPrimaryOwner());
			} else if (OPERATION_UPDATE
					.equals(auditConnectT.getOperationType())
					&& compareStringValueForUpdate(
							auditConnectT.getOldPrimaryOwner(),
							auditConnectT.getNewPrimaryOwner())) {
				addedOwners.add(auditConnectT.getNewPrimaryOwner());
			}
		}

		if (CollectionUtils.isNotEmpty(auditConnectSecondaryOwnerLinkTs)) {
			for (AuditConnectSecondaryOwnerLinkT auditConnectSecondaryOwnerLinkT : auditConnectSecondaryOwnerLinkTs) {
				if (OPERATION_INSERT.equals(auditConnectSecondaryOwnerLinkT
						.getOperationType())) {
					addedOwners.add(auditConnectSecondaryOwnerLinkT
							.getOldSecondaryOwner());
				}
			}
		}

		return addedOwners;
	}

	/**
	 * This method is used to get the event Ids and message templates for which
	 * the changes occured in a opportunity
	 * 
	 * @param entityId
	 * @param recipients
	 * @param operationType
	 * @param currentUser
	 * @param data
	 * @param auditBidOfficeGroupOwnerLinkTs
	 * @param auditBidDetailsT
	 * @param auditOpportunitySalesSupportLinkTs
	 * @param auditOpportunityT
	 * @param notificationEventGroupMappingTs
	 * @param opportunity
	 * @return
	 * @throws Exception
	 */
	private Map<NotificationSettingEvent, RecipientMessageTemplateMapping> getEventIdsForOpportunity(
			String entityId,
			List<Recipient> recipients,
			OperationType operationType,
			String currentUser,
			Map<String, String> data,
			AuditOpportunityT auditOpportunityT,
			List<AuditOpportunitySalesSupportLinkT> auditOpportunitySalesSupportLinkTs,
			AuditBidDetailsT auditBidDetailsT,
			List<AuditBidOfficeGroupOwnerLinkT> auditBidOfficeGroupOwnerLinkTs,
			List<NotificationEventGroupMappingT> notificationEventGroupMappingTs,
			OpportunityT opportunity) throws Exception {
		logger.info("Inside getEventIdsForOpportunity method");
		Map<NotificationSettingEvent, RecipientMessageTemplateMapping> eventsMap = Maps
				.newHashMap();
		if (operationType == OperationType.OPPORTUNITY_FOLLOW) {
			// opp. follow
			// add event 3 with current user
			eventsMap.putAll(getRecipientMessageTemplateMapping(
					notificationEventGroupMappingTs, 3,
					NotificationSettingEvent.FOLLOW_CONNECT_OPPORTUNITY, data,
					null, null));

		} else if (operationType == OperationType.OPPORTUNITY_COMMENT) {
			// opp.comment
			// add event 8 and 13
			eventsMap.putAll(getRecipientMessageTemplateMapping(
					notificationEventGroupMappingTs, 8,
					NotificationSettingEvent.COMMENT, data, null, null));
			eventsMap.putAll(getRecipientMessageTemplateMapping(
					notificationEventGroupMappingTs, 13,
					NotificationSettingEvent.COMMENT_ON_SUBORDINATES_ENTITY,
					data, null, null));
		} else {
			// Owner addition
			List<String> addedOwners = getAddedOwnersOfOpportunity(
					auditOpportunityT, auditOpportunitySalesSupportLinkTs,
					auditBidOfficeGroupOwnerLinkTs);

			if (CollectionUtils.isNotEmpty(addedOwners)) {
				eventsMap.putAll(getMapForOwnerChange(data, addedOwners));
			}

			// Key changes 9
			if (operationType == OperationType.OPPORTUNITY_EDIT) {
				List<String> templatesForKeyChanges = getMessageTemplatesForKeyChangesOfOpportunity(
						auditOpportunityT, auditOpportunitySalesSupportLinkTs,
						auditBidOfficeGroupOwnerLinkTs, auditBidDetailsT, data,
						notificationEventGroupMappingTs);
				if (CollectionUtils.isNotEmpty(templatesForKeyChanges)) {
					RecipientMessageTemplateMapping recipientMessageTemplateMapping = constructRecipientMessageTemplateMapping(
							templatesForKeyChanges, null);
					eventsMap.put(NotificationSettingEvent.KEY_CHANGES,
							recipientMessageTemplateMapping);
				}
			}

			if (auditOpportunityT != null) {
				// digital flag
				if ((!StringUtils.equals(auditOpportunityT.getOldDigitalFlag(),
						auditOpportunityT.getNewDigitalFlag()))
						&& (StringUtils.equals(Constants.Y,
								auditOpportunityT.getNewDigitalFlag()))) {
					// add event 15 with empty list
					eventsMap.putAll(getRecipientMessageTemplateMapping(
							notificationEventGroupMappingTs, 15,
							NotificationSettingEvent.DIGITAL_OPPORTUNITIES,
							data, Constants.ADDED, null));
				}
				// strategic flag
				if ((!StringUtils.equals(
						auditOpportunityT.getOldStrategicDeal(),
						auditOpportunityT.getNewStrategicDeal()))
						&& (StringUtils.equals(Constants.YES,
								auditOpportunityT.getNewStrategicDeal()))) {
					// add event 16 with empty list
					String associates = getOwnersOfOpportunity(opportunity);

					eventsMap.putAll(getRecipientMessageTemplateMapping(
							notificationEventGroupMappingTs, 16,
							NotificationSettingEvent.STRATEGIC_OPPORTUNITIES,
							data, Constants.ADDED, associates));
				}

				// won/lost
				Integer newSalesStageCode = auditOpportunityT
						.getNewSalesStageCode();

				if (auditOpportunityT.getOldSalesStageCode() != auditOpportunityT
						.getNewSalesStageCode()) {
					SalesStageCode salesStageCode = SalesStageCode
							.valueOf(newSalesStageCode);
					if (salesStageCode != null
							&& (salesStageCode == SalesStageCode.WIN)) {
						// add event 14 with empty list
						eventsMap
								.putAll(getRecipientMessageTemplateMapping(
										notificationEventGroupMappingTs,
										14,
										NotificationSettingEvent.SUBORDINATES_OPPORTUNITIES_WL,
										data, Constants.WON, null));
					} else if (salesStageCode != null
							&& (salesStageCode == SalesStageCode.LOST)) {
						eventsMap
								.putAll(getRecipientMessageTemplateMapping(
										notificationEventGroupMappingTs,
										14,
										NotificationSettingEvent.SUBORDINATES_OPPORTUNITIES_WL,
										data, Constants.LOST, null));
					}

				}

				// Collab Condition event Id 10
				if (operationType == OperationType.OPPORTUNITY_CREATE) {
					// add event Id 10
					eventsMap.putAll(getRecipientMessageTemplateMapping(
							notificationEventGroupMappingTs, 10,
							NotificationSettingEvent.COLLAB_CONDITION, data,
							null, null));
				}
			}

		}
		logger.info("End of getEventIdsForOpportunity method");
		return eventsMap;
	}

	private String getOwnersOfOpportunity(OpportunityT opportunity) {
		StringBuffer ownerBuffer = new StringBuffer();
		String salesSupportOwners = null;
		String bidOfficeGroupOwners = null;
		List<String> salesSupport = Lists.newArrayList();
		List<String> bidOffice = Lists.newArrayList();
		ownerBuffer
				.append(getUserNameForUserId(opportunity.getOpportunityOwner()))
				.append(Constants.NOTIFICATION_PRIMARY_OWNER)
				.append(Constants.COMMA);
		if (CollectionUtils.isNotEmpty(opportunity
				.getOpportunitySalesSupportLinkTs())) {

			for (OpportunitySalesSupportLinkT opportunitySalesSupportLinkT : opportunity
					.getOpportunitySalesSupportLinkTs()) {
				salesSupport
						.add(getUserNameForUserId(opportunitySalesSupportLinkT
								.getSalesSupportOwner()));
			}
		}

		if (CollectionUtils.isNotEmpty(opportunity.getBidDetailsTs())) {
			for (BidDetailsT bidDetailsT : opportunity.getBidDetailsTs()) {
				if (CollectionUtils.isNotEmpty(bidDetailsT
						.getBidOfficeGroupOwnerLinkTs())) {
					for (BidOfficeGroupOwnerLinkT bidGroupOwnerLinkT : bidDetailsT
							.getBidOfficeGroupOwnerLinkTs()) {
						bidOffice.add(getUserNameForUserId(bidGroupOwnerLinkT
								.getBidOfficeGroupOwner()));
					}
				}
			}
		}
		if (CollectionUtils.isNotEmpty(salesSupport)) {
			salesSupportOwners = StringUtils.join(salesSupport, ",");
			ownerBuffer.append(salesSupportOwners).append(
					Constants.NOTIFICATION_SALES_SUPPORT);
		}
		if (CollectionUtils.isNotEmpty(bidOffice)) {
			bidOfficeGroupOwners = StringUtils.join(bidOffice, ",");
			ownerBuffer.append(bidOfficeGroupOwners).append(
					Constants.NOTIFICATION_BID_OFFICE_GRP_OWNER);
		}

		return ownerBuffer.toString();
	}
	/**
	 * used to get the templates for key changes of opportunity
	 * @param auditOpportunityT
	 * @param auditOpportunitySalesSupportLinkTs
	 * @param auditBidOfficeGroupOwnerLinkTs
	 * @param auditBidDetailsT
	 * @param data
	 * @param notificationEventGroupMappingTs
	 * @return
	 * @throws Exception
	 */
	private List<String> getMessageTemplatesForKeyChangesOfOpportunity(
			AuditOpportunityT auditOpportunityT,
			List<AuditOpportunitySalesSupportLinkT> auditOpportunitySalesSupportLinkTs,
			List<AuditBidOfficeGroupOwnerLinkT> auditBidOfficeGroupOwnerLinkTs,
			AuditBidDetailsT auditBidDetailsT, Map<String, String> data,
			List<NotificationEventGroupMappingT> notificationEventGroupMappingTs)
			throws Exception {
		logger.info("Inside getMessageTemplatesForKeyChangesOfOpportunity method");
		List<String> templates = Lists.newArrayList();
		String template = getMessageTemplateByEventId(
				notificationEventGroupMappingTs, 9);
		String templateForUpdate = new StringBuffer(template).append(
				Constants.FROM_TO_STRING).toString();
		String templateForAdd = new StringBuffer(template).append(
				Constants.ENTITY_NAME_TO_STRING).toString();
		String templateEntity = new StringBuffer(template).append(
				Constants.ENTITY_NAME_STRING).toString();
		// Digital deal value

		if (auditOpportunityT != null) {
			if (compareIntegerValueForAdd(
					auditOpportunityT.getOldDigitalDealValue(),
					auditOpportunityT.getNewDigitalDealValue())) {
				templates.add(constructMessageTemplate(data, null, "",
						Constants.ADDED, Constants.DIGITAL_DEAL_VALUE_FIELD,
						templateForAdd, null, null));
			}
			if (compareIntegerValueForUpdate(
					auditOpportunityT.getOldDigitalDealValue(),
					auditOpportunityT.getNewDigitalDealValue())) {
				templates.add(constructMessageTemplate(data, null, "",
						Constants.UPDATED, Constants.DIGITAL_DEAL_VALUE_FIELD,
						templateEntity, null, null));
			}
			// sales stage code
			SalesStageCode oldSalesStageCode = SalesStageCode
					.valueOf(auditOpportunityT.getOldSalesStageCode());
			SalesStageCode newSalesStageCode = SalesStageCode
					.valueOf(auditOpportunityT.getNewSalesStageCode());
			if (oldSalesStageCode != newSalesStageCode) {
				templates.add(constructMessageTemplate(data,
						oldSalesStageCode.getDescription(),
						newSalesStageCode.getDescription(), Constants.UPDATED,
						Constants.SALES_STAGE_FIELD, templateForUpdate, null,
						null));
			}
			// Opportunity owner
			if (!StringUtils.equals(auditOpportunityT.getOldOpportunityOwner(),
					auditOpportunityT.getNewOpportunityOwner())) {
				templates.add(constructMessageTemplate(data,
						getUserNameForUserId(auditOpportunityT
								.getOldOpportunityOwner()),
						getUserNameForUserId(auditOpportunityT
								.getNewOpportunityOwner()), Constants.UPDATED,
						Constants.PRIMARY_OWNER_FIELD, templateForUpdate, null,
						null));
			}

		}

		// sales suppport owner
		if (CollectionUtils.isNotEmpty(auditOpportunitySalesSupportLinkTs)) {
			templates.addAll(getTemplatesForAddRemoveSalesSupportOwners(
					auditOpportunitySalesSupportLinkTs, templateForAdd, data));
		}
		// Bid Office Group Owner
		if (CollectionUtils.isNotEmpty(auditBidOfficeGroupOwnerLinkTs)) {
			templates.addAll(getTemplatesForAddRemoveBidOfficeGroupOwners(
					auditBidOfficeGroupOwnerLinkTs, templateForAdd, data));
		}
		// Bid Details
		if (auditBidDetailsT != null) {
			templates.addAll(getTemplatesForBidDetailChanges(auditBidDetailsT,
					templateForAdd, templateForUpdate, data));
		}
		logger.info("End of getMessageTemplatesForKeyChangesOfOpportunity method");
		return templates;

	}

	private String getMessageTemplateByEventId(
			List<NotificationEventGroupMappingT> notificationEventGroupMappingTs,
			Integer eventId) {
		String template = null;
		for (NotificationEventGroupMappingT notificationEventGroupMappingT : notificationEventGroupMappingTs) {
			if (notificationEventGroupMappingT.getEventId() == eventId) {
				template = notificationEventGroupMappingT.getMessageTemplate();
				break;
			}
		}
		return template;
	}

	private List<String> getAddedOwnersOfOpportunity(
			AuditOpportunityT auditOpportunityT,
			List<AuditOpportunitySalesSupportLinkT> auditOpportunitySalesSupportLinkTs,
			List<AuditBidOfficeGroupOwnerLinkT> auditBidOfficeGroupOwnerLinkTs) {

		List<String> addedOwners = Lists.newArrayList();
		if (auditOpportunityT != null) {
			// primary owner
			if (OPERATION_INSERT.equals(auditOpportunityT.getOperationType())
					&& compareStringValueForAdd(
							auditOpportunityT.getOldOpportunityOwner(),
							auditOpportunityT.getNewOpportunityOwner())) {
				addedOwners.add(auditOpportunityT.getNewOpportunityOwner());
			} else if (OPERATION_UPDATE.equals(auditOpportunityT
					.getOperationType())
					&& compareStringValueForUpdate(
							auditOpportunityT.getOldOpportunityOwner(),
							auditOpportunityT.getNewOpportunityOwner())) {
				addedOwners.add(auditOpportunityT.getNewOpportunityOwner());
			}
		}

		// sales suppport owner
		if (CollectionUtils.isNotEmpty(auditOpportunitySalesSupportLinkTs)) {
			for (AuditOpportunitySalesSupportLinkT salesSupportLink : auditOpportunitySalesSupportLinkTs) {
				if (OPERATION_INSERT
						.equals(salesSupportLink.getOperationType())) {
					addedOwners.add(salesSupportLink.getOldSalesSupportOwner());
				}
			}
		}

		// bid office group owners
		if (CollectionUtils.isNotEmpty(auditBidOfficeGroupOwnerLinkTs)) {
			for (AuditBidOfficeGroupOwnerLinkT bidOffGrpLink : auditBidOfficeGroupOwnerLinkTs) {
				if (OPERATION_INSERT.equals(bidOffGrpLink.getOperationType())
						&& compareStringValueForAdd(
								bidOffGrpLink.getOldBidOfficeGroupOwner(),
								bidOffGrpLink.getNewBidOfficeGroupOwner())) {
					addedOwners.add(bidOffGrpLink.getNewBidOfficeGroupOwner());
				} else if (OPERATION_UPDATE.equals(bidOffGrpLink
						.getOperationType())
						&& compareStringValueForUpdate(
								bidOffGrpLink.getOldBidOfficeGroupOwner(),
								bidOffGrpLink.getNewBidOfficeGroupOwner())) {
					addedOwners.add(bidOffGrpLink.getNewBidOfficeGroupOwner());
				}
			}
		}
		return addedOwners;
	}

	private Map<NotificationSettingEvent, RecipientMessageTemplateMapping> getMapForOwnerChange(
			Map<String, String> data, List<String> addedOwners)
			throws Exception {

		Map<NotificationSettingEvent, RecipientMessageTemplateMapping> eventsMap = Maps
				.newHashMap();
		String templateForOwnerChange = notificationEventGroupMappingTRepository
				.getMessageTemplateByEventId(1);
		String templateForSubordinateOwners = notificationEventGroupMappingTRepository
				.getMessageTemplateByEventId(11);
		List<String> templatesForOwnerChange = Lists.newArrayList();
		List<String> templatesForSubordinateOwners = Lists.newArrayList();
		templatesForOwnerChange.add(constructMessageTemplate(data, null, null,
				Constants.ADDED, null, templateForOwnerChange, null, null));
		templatesForSubordinateOwners.add(constructMessageTemplate(data, null,
				null, Constants.ADDED, null, templateForSubordinateOwners,
				null, null));
		eventsMap.put(
				NotificationSettingEvent.OWNER_CHANGE,
				constructRecipientMessageTemplateMapping(
						templatesForOwnerChange, addedOwners));
		eventsMap.put(
				NotificationSettingEvent.SUBORDINATES_AS_OWNERS,
				constructRecipientMessageTemplateMapping(
						templatesForSubordinateOwners, addedOwners));
		return eventsMap;

	}

	private List<String> getTemplatesForAddRemoveConnectSecondaryOwners(
			List<AuditConnectSecondaryOwnerLinkT> auditConnectSecondaryOwnerLinkTs,
			String template, Map<String, String> data) throws Exception {
		List<String> templates = Lists.newArrayList();
		List<String> addedSecondaryOwners = Lists.newArrayList();
		List<String> removedSecondaryOwners = Lists.newArrayList();
		for (AuditConnectSecondaryOwnerLinkT auditConnectSecondaryOwnerLinkT : auditConnectSecondaryOwnerLinkTs) {
			if (OPERATION_INSERT.equals(auditConnectSecondaryOwnerLinkT
					.getOperationType())) {
				addedSecondaryOwners
						.add(getUserNameForUserId(auditConnectSecondaryOwnerLinkT
								.getOldSecondaryOwner()));
			} else if (OPERATION_DELETE.equals(auditConnectSecondaryOwnerLinkT
					.getOperationType())) {
				removedSecondaryOwners
						.add(getUserNameForUserId(auditConnectSecondaryOwnerLinkT
								.getOldSecondaryOwner()));

			}
		}
		if (CollectionUtils.isNotEmpty(addedSecondaryOwners)) {
			templates.add(constructMessageTemplate(data, null,
					StringUtils.join(addedSecondaryOwners, ","),
					Constants.ADDED, Constants.SALES_SUPPORT_OWNER_FIELD,
					template, null, null));
		}
		if (CollectionUtils.isNotEmpty(removedSecondaryOwners)) {
			templates.add(constructMessageTemplate(data, null,
					StringUtils.join(removedSecondaryOwners, ","),
					Constants.REMOVED, Constants.SALES_SUPPORT_OWNER_FIELD,
					template, null, null));
		}
		return templates;

	}

	private List<String> getTemplatesForAddRemoveSalesSupportOwners(
			List<AuditOpportunitySalesSupportLinkT> auditOpportunitySalesSupportLinkTs,
			String template, Map<String, String> data) throws Exception {
		List<String> addedSalesSupportOwners = Lists.newArrayList();
		List<String> removedSalesSupportOwners = Lists.newArrayList();
		List<String> templates = Lists.newArrayList();
		for (AuditOpportunitySalesSupportLinkT salesSupportLink : auditOpportunitySalesSupportLinkTs) {
			if (OPERATION_INSERT.equals(salesSupportLink.getOperationType())) {
				addedSalesSupportOwners
						.add(getUserNameForUserId(salesSupportLink
								.getOldSalesSupportOwner()));
			} else if (OPERATION_DELETE.equals(salesSupportLink
					.getOperationType())) {
				removedSalesSupportOwners
						.add(getUserNameForUserId(salesSupportLink
								.getOldSalesSupportOwner()));
			}
		}
		if (CollectionUtils.isNotEmpty(addedSalesSupportOwners)) {
			templates.add(constructMessageTemplate(data, null,
					StringUtils.join(addedSalesSupportOwners, ","),
					Constants.ADDED, Constants.SALES_SUPPORT_OWNER_FIELD,
					template, null, null));
		}
		if (CollectionUtils.isNotEmpty(removedSalesSupportOwners)) {
			templates.add(constructMessageTemplate(data, null,
					StringUtils.join(removedSalesSupportOwners, ","),
					Constants.REMOVED, Constants.SALES_SUPPORT_OWNER_FIELD,
					template, null, null));
		}
		return templates;

	}

	private List<String> getTemplatesForAddRemoveBidOfficeGroupOwners(
			List<AuditBidOfficeGroupOwnerLinkT> auditBidOfficeGroupOwners,
			String template, Map<String, String> data) throws Exception {
		List<String> templates = Lists.newArrayList();
		List<String> addedBidOfficeOwners = Lists.newArrayList();
		for (AuditBidOfficeGroupOwnerLinkT auditBidOfficeGroupOwnerLinkT : auditBidOfficeGroupOwners) {
			if (OPERATION_INSERT.equals(auditBidOfficeGroupOwnerLinkT
					.getOperationType())
					&& StringUtils.isNotEmpty(auditBidOfficeGroupOwnerLinkT
							.getNewBidOfficeGroupOwner())) {
				addedBidOfficeOwners
						.add(getUserNameForUserId(auditBidOfficeGroupOwnerLinkT
								.getNewBidOfficeGroupOwner()));
			} else if (OPERATION_UPDATE.equals(auditBidOfficeGroupOwnerLinkT
					.getOperationType())
					&& compareStringValueForUpdate(
							auditBidOfficeGroupOwnerLinkT
									.getOldBidOfficeGroupOwner(),
							auditBidOfficeGroupOwnerLinkT
									.getNewBidOfficeGroupOwner())) {
				templates.add(constructMessageTemplate(data,
						getUserNameForUserId(auditBidOfficeGroupOwnerLinkT
								.getOldBidOfficeGroupOwner()),
						getUserNameForUserId(auditBidOfficeGroupOwnerLinkT
								.getNewBidOfficeGroupOwner()),
						Constants.UPDATED,
						Constants.BID_OFFICE_GROUP_OWNER_FIELD, template, null,
						null));
			}
		}
		if (CollectionUtils.isNotEmpty(addedBidOfficeOwners)) {
			templates.add(constructMessageTemplate(data, null,
					StringUtils.join(addedBidOfficeOwners, ","),
					Constants.ADDED, Constants.BID_OFFICE_GROUP_OWNER_FIELD,
					template, null, null));
		}
		return templates;

	}

	private List<String> getTemplatesForBidDetailChanges(
			AuditBidDetailsT auditBidDetailsT, String templateForAdd,
			String templateForUpdate, Map<String, String> data)
			throws Exception {

		List<String> templates = Lists.newArrayList();

		// Bid Type
		if (compareStringValueForAdd(auditBidDetailsT.getOldBidRequestType(),
				auditBidDetailsT.getNewBidRequestType())) {
			templates.add(constructMessageTemplate(data, null,
					auditBidDetailsT.getNewBidRequestType(), Constants.ADDED,
					Constants.BID_FIELD, templateForAdd, null, null));
		}

		if (compareStringValueForUpdate(
				auditBidDetailsT.getOldBidRequestType(),
				auditBidDetailsT.getNewBidRequestType())) {
			templates.add(constructMessageTemplate(data,
					auditBidDetailsT.getOldBidRequestType(),
					auditBidDetailsT.getNewBidRequestType(), Constants.UPDATED,
					Constants.BID_FIELD, templateForUpdate, null, null));
		}

		// Actual Bid Submission Date
		if (compareDateValueForAdd(
				auditBidDetailsT.getOldActualBidSubmissionDate(),
				auditBidDetailsT.getNewActualBidSubmissionDate())) {
			templates.add(constructMessageTemplate(data, null, ACTUAL_FORMAT
					.format(auditBidDetailsT.getNewActualBidSubmissionDate()),
					Constants.ADDED,
					Constants.ACTUAL_BID_SUBMISSION_DATE_FIELD, templateForAdd,
					null, null));
		}

		if (compareDateValueForUpdate(
				auditBidDetailsT.getOldActualBidSubmissionDate(),
				auditBidDetailsT.getNewActualBidSubmissionDate())) {
			templates.add(constructMessageTemplate(data, ACTUAL_FORMAT
					.format(auditBidDetailsT.getOldActualBidSubmissionDate()),
					ACTUAL_FORMAT.format(auditBidDetailsT
							.getNewActualBidSubmissionDate()),
					Constants.UPDATED,
					Constants.ACTUAL_BID_SUBMISSION_DATE_FIELD,
					templateForUpdate, null, null));
		}

		// Target Bid submission date
		if (compareDateValueForAdd(
				auditBidDetailsT.getOldTargetBidSubmissionDate(),
				auditBidDetailsT.getNewTargetBidSubmissionDate())) {
			templates.add(constructMessageTemplate(data, null, ACTUAL_FORMAT
					.format(auditBidDetailsT.getNewTargetBidSubmissionDate()),
					Constants.ADDED,
					Constants.TARGET_BID_SUBMISSION_DATE_FIELD, templateForAdd,
					null, null));
		}

		if (compareDateValueForUpdate(
				auditBidDetailsT.getOldTargetBidSubmissionDate(),
				auditBidDetailsT.getNewTargetBidSubmissionDate())) {
			templates.add(constructMessageTemplate(data, ACTUAL_FORMAT
					.format(auditBidDetailsT.getOldTargetBidSubmissionDate()),
					ACTUAL_FORMAT.format(auditBidDetailsT
							.getNewTargetBidSubmissionDate()),
					Constants.UPDATED,
					Constants.TARGET_BID_SUBMISSION_DATE_FIELD,
					templateForUpdate, null, null));
		}

		// Expected Date of outcome
		if (compareDateValueForAdd(
				auditBidDetailsT.getOldExpectedDateOfOutcome(),
				auditBidDetailsT.getNewExpectedDateOfOutcome())) {
			templates.add(constructMessageTemplate(data, null, ACTUAL_FORMAT
					.format(auditBidDetailsT.getNewExpectedDateOfOutcome()),
					Constants.ADDED, Constants.EXPECTED_DATE_OF_OUTCOME_FIELD,
					templateForAdd, null, null));
		}

		if (compareDateValueForUpdate(
				auditBidDetailsT.getOldExpectedDateOfOutcome(),
				auditBidDetailsT.getNewExpectedDateOfOutcome())) {
			templates.add(constructMessageTemplate(data, ACTUAL_FORMAT
					.format(auditBidDetailsT.getOldExpectedDateOfOutcome()),
					ACTUAL_FORMAT.format(auditBidDetailsT
							.getNewExpectedDateOfOutcome()), Constants.UPDATED,
					Constants.EXPECTED_DATE_OF_OUTCOME_FIELD,
					templateForUpdate, null, null));
		}

		return templates;
	}

	private boolean compareStringValueForAdd(String oldValue, String newValue) {
		boolean flag = false;
		if (StringUtils.isEmpty(oldValue) && StringUtils.isNotEmpty(newValue)) {
			flag = true;
		}
		return flag;
	}

	private boolean compareStringValueForUpdate(String oldValue, String newValue) {
		boolean flag = false;
		if (StringUtils.isNotEmpty(oldValue)
				&& !StringUtils.equals(oldValue, newValue)) {
			flag = true;
		}
		return flag;
	}

	private boolean compareDateValueForAdd(Date oldValue, Date newValue) {
		boolean flag = false;
		if (oldValue == null && newValue != null) {
			flag = true;
		}
		return flag;
	}

	private boolean compareDateValueForUpdate(Date oldValue, Date newValue) {
		boolean flag = false;
		if (oldValue != null && oldValue != newValue) {
			flag = true;
		}
		return flag;
	}

	private boolean compareIntegerValueForAdd(Integer oldValue, Integer newValue) {
		boolean flag = false;
		if (oldValue == null && newValue != null) {
			flag = true;
		}
		return flag;
	}

	private boolean compareIntegerValueForUpdate(Integer oldValue,
			Integer newValue) {
		boolean flag = false;
		if (oldValue != null && oldValue != newValue) {
			flag = true;
		}
		return flag;
	}

	private Map<NotificationSettingEvent, RecipientMessageTemplateMapping> getRecipientMessageTemplateMapping(
			List<NotificationEventGroupMappingT> notificationEventGroupMappingTs,
			Integer eventId, NotificationSettingEvent notificationSettingEvent,
			Map<String, String> data, String status, String associates)
			throws Exception {
		List<String> templates = Lists.newArrayList();
		Map<NotificationSettingEvent, RecipientMessageTemplateMapping> eventsMap = Maps
				.newHashMap();
		String template = getMessageTemplateByEventId(
				notificationEventGroupMappingTs, eventId);
		templates.add(constructMessageTemplate(data, null, null, status, null,
				template, null, associates));
		RecipientMessageTemplateMapping recipientMessageTemplate = constructRecipientMessageTemplateMapping(
				templates, null);
		eventsMap.put(notificationSettingEvent, recipientMessageTemplate);
		return eventsMap;
	}

	/**
	 * Method used to get the Notification For Owner Change And BdmTaggedToTask
	 * 
	 * @param eventIdsMap
	 * @param recipient
	 * @param entityType
	 * @param entityId
	 * @param subordinate
	 * @param eventId
	 * @param recipientMessageTemplateMapping
	 * @return
	 * @throws Exception
	 */
	private UserNotificationsT getNotificationForOwnerChangeAndBdmTaggedToTask(
			Map<NotificationSettingEvent, RecipientMessageTemplateMapping> eventIdsMap,
			Recipient recipient, String entityType, String entityId,
			Integer eventId,
			RecipientMessageTemplateMapping recipientMessageTemplateMapping)
			throws Exception {

		UserNotificationsT userNotificationsT = new UserNotificationsT();
		List<String> addedOwnersOrBdmsTagged = recipientMessageTemplateMapping
				.getUsers();
		List<String> templates = recipientMessageTemplateMapping.getTemplates();
		String recipientId = recipient.getId();

		if (addedOwnersOrBdmsTagged.contains(recipientId)) {
			String status = recipient.isRemoved() ? Constants.REMOVED
					: Constants.ADDED;

			userNotificationsT = constructNotification(
					eventId,
					recipient.getId(),
					entityType,
					entityId,
					constructMessageTemplate(null, null, null, status,
							recipient.getOwnerType().getName(),
							templates.get(0), null, null));
			return userNotificationsT;

		}
		return null;
	}

	/**
	 * Method used to get the Notification For Owner Change And BdmTaggedToTask
	 * 
	 * @param eventIdsMap
	 * @param recipient
	 * @param entityType
	 * @param entityId
	 * @param subordinate
	 * @param eventId
	 * @param recipientMessageTemplateMapping
	 * @param subordinates
	 * @return
	 * @throws Exception
	 */
	private UserNotificationsT getNotificationForOwnerChangeToSupervisor(
			Map<NotificationSettingEvent, RecipientMessageTemplateMapping> eventIdsMap,
			Recipient recipient, String entityType, String entityId,
			Integer eventId,
			RecipientMessageTemplateMapping recipientMessageTemplateMapping,
			List<Recipient> subordinates) throws Exception {

		UserNotificationsT userNotificationsT = new UserNotificationsT();
		List<String> addedOwnersOrBdmsTagged = recipientMessageTemplateMapping
				.getUsers();
		List<String> templates = recipientMessageTemplateMapping.getTemplates();
		if (CollectionUtils.isNotEmpty(subordinates)) {
			List<String> addedSubordinates = Lists.newArrayList();
			for (Recipient subordinate : subordinates) {
				if (addedOwnersOrBdmsTagged.contains(subordinate.getId())) {
					addedSubordinates.add(getNameWithType(
							subordinate.getOwnerType(),
							getUserNameForUserId(subordinate.getId())));
				}
			}
			if (CollectionUtils.isNotEmpty(addedSubordinates)) {
				String subordinateNames = StringUtils.join(addedSubordinates,
						", ");
				userNotificationsT = constructNotification(
						eventId,
						recipient.getId(),
						entityType,
						entityId,
						constructMessageTemplate(null, null, null,
								Constants.ADDED, recipient.getOwnerType()
										.name(), templates.get(0),
								subordinateNames, null));
				return userNotificationsT;
			}

		}
		return null;
	}

	/**
	 * method used to remove modified user from the recipients
	 * 
	 * @param currentUser
	 * @param recipients
	 * @return
	 */
	public List<Recipient> removeModifiedByUserFromRecipients(
			String currentUser, List<Recipient> recipients) {

		for (Recipient recipient : recipients) {
			if (StringUtils.equals(currentUser, recipient.getId())) {
				recipients.remove(recipient);
				break;
			}
		}
		return recipients;

	}

	/**
	 * Method used to set notified as true in all the notified audit entities
	 * 
	 * @param auditConnectT
	 * @param auditConnectSecondaryOwnerLinkTs
	 * @param auditOpportunityT
	 * @param auditOpportunitySalesSupportLinkTs
	 * @param auditBidDetailsT
	 * @param auditBidOfficeGroupOwnerLinkTs
	 * @param auditTaskT
	 * @param auditTaskBdmsTaggedLinkTs
	 */
	public void setNotifiedTrueForAuditEntities(
			AuditConnectT auditConnectT,
			List<AuditConnectSecondaryOwnerLinkT> auditConnectSecondaryOwnerLinkTs,
			AuditOpportunityT auditOpportunityT,
			List<AuditOpportunitySalesSupportLinkT> auditOpportunitySalesSupportLinkTs,
			AuditBidDetailsT auditBidDetailsT,
			List<AuditBidOfficeGroupOwnerLinkT> auditBidOfficeGroupOwnerLinkTs,
			AuditTaskT auditTaskT,
			List<AuditTaskBdmsTaggedLinkT> auditTaskBdmsTaggedLinkTs) {
		boolean notified = true;
		if (auditConnectT != null) {
			auditConnectT.setNotified(notified);
			auditConnectTRepository.save(auditConnectT);
		}

		if (auditOpportunityT != null) {
			auditOpportunityT.setNotified(notified);
			auditOpportunityRepository.save(auditOpportunityT);
		}

		if (auditTaskT != null) {
			auditTaskT.setNotified(notified);
			auditTaskTRepository.save(auditTaskT);
		}

		if (auditBidDetailsT != null) {
			auditBidDetailsT.setNotified(notified);
			auditBidDetailsTRepository.save(auditBidDetailsT);
		}

		if (CollectionUtils.isNotEmpty(auditTaskBdmsTaggedLinkTs)) {
			for (AuditTaskBdmsTaggedLinkT auditTaskBdmsTaggedLinkT : auditTaskBdmsTaggedLinkTs) {
				auditTaskBdmsTaggedLinkT.setNotified(notified);
				auditTaskBdmsTaggedLinkTRepository
						.save(auditTaskBdmsTaggedLinkT);
			}
		}

		if (CollectionUtils.isNotEmpty(auditBidOfficeGroupOwnerLinkTs)) {
			for (AuditBidOfficeGroupOwnerLinkT auditBidOfficeGroupOwnerLinkT : auditBidOfficeGroupOwnerLinkTs) {
				auditBidOfficeGroupOwnerLinkT.setNotified(notified);
				auditBidOfficeGroupOwnerLinkTRepository
						.save(auditBidOfficeGroupOwnerLinkT);
			}
		}

		if (CollectionUtils.isNotEmpty(auditOpportunitySalesSupportLinkTs)) {
			for (AuditOpportunitySalesSupportLinkT auditOpportunitySalesSupportLinkT : auditOpportunitySalesSupportLinkTs) {
				auditOpportunitySalesSupportLinkT.setNotified(notified);
				auditOpportunitySalesSupportLinkTRepository
						.save(auditOpportunitySalesSupportLinkT);
			}
		}

		if (CollectionUtils.isNotEmpty(auditConnectSecondaryOwnerLinkTs)) {
			for (AuditConnectSecondaryOwnerLinkT auditConnectSecondaryOwnerLinkT : auditConnectSecondaryOwnerLinkTs) {
				auditConnectSecondaryOwnerLinkT.setNotified(notified);
				auditConnectSecondaryOwnerLinkTRepository
						.save(auditConnectSecondaryOwnerLinkT);
			}
		}
	}

	/**
	 * This method is used to replace tokens in message template framed
	 * 
	 * @param message
	 * @param tokens
	 * @return
	 * @throws Exception
	 */
	private String replaceTokens(String message, Map<String, String> tokens)
			throws Exception {
		logger.debug("Inside replaceTokens() method");
		Pattern pattern = Pattern.compile(PATTERN);
		Matcher matcher = pattern.matcher(message);
		StringBuilder builder = new StringBuilder();
		int i = 0;
		while (matcher.find()) {
			String replacement = tokens.get(matcher.group(1));
			builder.append(message.substring(i, matcher.start()));
			if (replacement == null)
				builder.append(matcher.group(0));
			else
				builder.append(replacement);
			i = matcher.end();
		}
		builder.append(message.substring(i, message.length()));
		return builder.toString();
	}

}
