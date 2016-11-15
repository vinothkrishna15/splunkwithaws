package com.tcs.destination.service;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tcs.destination.bean.AuditDeliveryMasterT;
import com.tcs.destination.bean.AuditEngagementHistoryDTO;
import com.tcs.destination.bean.AuditEntryDTO;
import com.tcs.destination.bean.AuditHistoryDTO;
import com.tcs.destination.bean.AuditHistoryResponseDTO;
import com.tcs.destination.bean.AuditOpportunityHistoryDTO;
import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.OpportunityTimelineHistoryT;
import com.tcs.destination.bean.WorkflowRequestT;
import com.tcs.destination.data.repository.AuditDeliveryMasterRepository;
import com.tcs.destination.data.repository.OpportunityTimelineHistoryTRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.data.repository.WorkflowRequestTRepository;
import com.tcs.destination.enums.EntityTypeId;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.framework.history.HistoryBuilderHelper;
import com.tcs.destination.framework.history.IHistoryBuilder;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.ErrorConstants;
import com.tcs.destination.utils.PropertyUtil;

/**
 * Services to get the modification history(audit)
 * @author TCS
 *
 */
@Service
public class AuditDetailService {
	
	private static final Logger logger = LoggerFactory
			.getLogger(AuditDetailService.class);

	private static final String KEY_TO_DATE = "TO_DATE";
	private static final String KEY_FROM_DATE = "FROM_DATE";
	private static final String KEY_SALES_CODE = "SALES_CODE";
	private static final String KEY_NEXT_SALES_CODE = "NEXT_SALES_CODE";
	private static final String KEY_USER_ID = "KEY_USER_ID";

	@Autowired
	private WorkflowRequestTRepository workflowRequestRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OpportunityTimelineHistoryTRepository timelineHistoryRepo;

	@Autowired
	private AuditDeliveryMasterRepository aDeliveryRepo;
	
	@Autowired
	private IHistoryBuilder historyBuilder;
	
	@Autowired
	private HistoryBuilderHelper historyBuilderHelper;
	
	/**
	 * service method to retrieve the workflow history 
	 * @param workflowId
	 * @return
	 */
	public AuditHistoryResponseDTO<AuditHistoryDTO> getWorkFlowHistory(String workflowId) {

		logger.info("Entering AuditDetailService :: getWorkFlowHistory");
		if(!NumberUtils.isDigits(workflowId)) {//throw error on invalid id
			throw new DestinationException(HttpStatus.BAD_REQUEST, PropertyUtil.getProperty(ErrorConstants.INVALID_WORKFLOW_ID));
		}
		int wfId = Integer.parseInt(workflowId);
		//fetch workflow using id
		WorkflowRequestT workFlow = workflowRequestRepository.findOne(wfId);
		if(workFlow == null) {
			throw new DestinationException(HttpStatus.BAD_REQUEST, PropertyUtil.getProperty(ErrorConstants.INVALID_WORKFLOW_ID));
		}
		Integer entityTypeId = workFlow.getEntityTypeId();
		String entityId = workFlow.getEntityId();
		
		//fetch audit entries of workflow step using workflowId 
		List<AuditEntryDTO> stepEntries = historyBuilder.getAuditEntries("workflow_steps", wfId);
		
		if(CollectionUtils.isEmpty(stepEntries)) {
			throw new DestinationException(HttpStatus.NOT_FOUND, PropertyUtil.getProperty(ErrorConstants.WORKFLOW_AUDIT_NOT_AVAILABLE));
		}
		
		//fetch audit entries of workflow entity(partner, customer, competitor) using entityId
		List<AuditEntryDTO> entityEntries = getWorkflowEntityEntries(entityTypeId, entityId);
		
		stepEntries.addAll(entityEntries);
		//group the audit entries by date and user
		List<AuditHistoryDTO> auditHistories = historyBuilderHelper.groupAuditHistory(stepEntries, entityTypeId);
		Collections.sort(auditHistories);//sort the list on date
		logger.info("Ends AuditDetailService :: getWorkFlowHistory");
		return new AuditHistoryResponseDTO<AuditHistoryDTO>(auditHistories);
	}
	
	
	/**
	 * Service to retrieve the opportunity history
	 * @param oppId
	 * @return
	 */
	public AuditHistoryResponseDTO<AuditOpportunityHistoryDTO> getOpportunityHistory(String oppId) {
		logger.info("Entering AuditDetailService :: getOpportunityHistory");
		List<AuditOpportunityHistoryDTO> histories = Lists.newArrayList();
		List<OpportunityTimelineHistoryT> timeLineHistories = timelineHistoryRepo.findByOpportunityIdOrderByUpdatedDatetimeAsc(oppId);
		
		List<Map<String, Object>> salesCodeSequenceMap = getSalesCodeSequenceMap(oppId, timeLineHistories);
		List<AuditEntryDTO> entries = historyBuilder.getAuditEntries("opportunity", oppId);
		
		//group entries by sales stage code and date
		Map<String, List<AuditEntryDTO>> salesCodeHistoryMap = groupBySalesCode(entries, salesCodeSequenceMap);
		
		for (Entry<String, List<AuditEntryDTO>> mapEntry : salesCodeHistoryMap.entrySet()) {
			AuditOpportunityHistoryDTO aOppHistory = getAuditOpportunityHistoryDTO(mapEntry, EntityTypeId.OPPORTUNITY.getType(), timeLineHistories);
			histories.add(aOppHistory);
		}
		Collections.sort(histories);
		logger.info("Ends AuditDetailService :: getOpportunityHistory");
		return new AuditHistoryResponseDTO<AuditOpportunityHistoryDTO>(histories);
	}

	/**
	 * construct the list to track the sales stage code and its date range
	 * @param oppId
	 * @return
	 */
	private List<Map<String, Object>> getSalesCodeSequenceMap(String oppId, List<OpportunityTimelineHistoryT> timeLineHistories) {
		List<Map<String, Object>> salesCodeSequenceMap = Lists.newArrayList();
		List<OpportunityTimelineHistoryT> timeLineHistoriesclone = Lists.newArrayList(timeLineHistories);

		if(CollectionUtils.isNotEmpty(timeLineHistoriesclone)) {
			OpportunityTimelineHistoryT previousHistory = timeLineHistoriesclone.remove(0);

			for (OpportunityTimelineHistoryT timeLineHistory : timeLineHistoriesclone) {
				Date toDate = truncateSeconds(timeLineHistory.getUpdatedDatetime());
				Date fromDate = truncateSeconds(previousHistory.getUpdatedDatetime());

				Map<String, Object> map = createMapWith(fromDate,
						previousHistory.getSalesStageCode(), toDate, timeLineHistory.getSalesStageCode(), previousHistory.getUserUpdated());
				salesCodeSequenceMap.add(map);

				previousHistory = timeLineHistory;
			}
			//add current sales stage code also 
			Date fromDate = truncateSeconds(previousHistory.getUpdatedDatetime());
			salesCodeSequenceMap.add(createMapWith(fromDate, previousHistory.getSalesStageCode(), null, null, previousHistory.getUserUpdated()));
		}
		return salesCodeSequenceMap;
	}
	
	/**
	 * construct the list to track the delivery stage and its date range
	 * @param engId
	 * @return
	 */
	private List<Map<String, Object>> getEngagementCodeSequenceMap(String engId) {
		List<AuditDeliveryMasterT> engagementAudits = aDeliveryRepo.getDeliveryCodeChanges(engId);
		
		if(CollectionUtils.isEmpty(engagementAudits)) {
			throw new DestinationException(HttpStatus.NOT_FOUND, PropertyUtil.getProperty(ErrorConstants.ENG_AUDIT_NOT_AVAILABLE));
		}
		List<Map<String, Object>> deliverySequenceMap = Lists.newArrayList();

		AuditDeliveryMasterT previousStage = engagementAudits.remove(0);
		for (AuditDeliveryMasterT deliveryStage : engagementAudits) {
			Date toDate = truncateSeconds(deliveryStage.getCreatedModifiedDatetime());
			Date fromDate = truncateSeconds(previousStage.getCreatedModifiedDatetime());
			Map<String, Object> map = createMapWith(fromDate,
					previousStage.getNewDeliveryStage(), toDate, deliveryStage.getNewDeliveryStage(), previousStage.getCreatedModifiedBy());
			deliverySequenceMap.add(map);
			previousStage = deliveryStage;
		}
		//add current sales stage code also 
		Date fromDate = truncateSeconds(previousStage.getCreatedModifiedDatetime());
		deliverySequenceMap.add(createMapWith(fromDate, previousStage.getNewDeliveryStage(), null, null, previousStage.getCreatedModifiedBy()));
		
		return deliverySequenceMap;
	}


	/**
	 * map using to track the sales stage change 
	 * @param lastSalesChangeDate
	 * @param oldSalesCode
	 * @param modifiedDate
	 * @param salesCode
	 * @param string 
	 * @return
	 */
	private Map<String, Object> createMapWith(Date lastSalesChangeDate,
			Integer oldSalesCode, Date modifiedDate, Integer salesCode, String userId) {
		Map<String, Object> map = Maps.newHashMap();
		map.put(KEY_NEXT_SALES_CODE, salesCode);
		map.put(KEY_SALES_CODE, oldSalesCode);
		map.put(KEY_FROM_DATE, lastSalesChangeDate);
		map.put(KEY_TO_DATE, modifiedDate);
		map.put(KEY_USER_ID, userId);
		return map;
	}


	/**
	 * sort and construct the {@link AuditOpportunityHistoryDTO} 
	 * @param mapEntry
	 * @param type
	 * @param timeLineHistories 
	 * @return
	 */
	private AuditOpportunityHistoryDTO getAuditOpportunityHistoryDTO(
			Entry<String, List<AuditEntryDTO>> mapEntry, Integer type, List<OpportunityTimelineHistoryT> timeLineHistories) {
		AuditOpportunityHistoryDTO dto = new AuditOpportunityHistoryDTO();
		//salesCode
		int salesCode = getStageCode(mapEntry.getKey());
		//startDate
		Date startDate = getDate(mapEntry.getKey());
		String userName = getUserName(mapEntry.getKey());
		dto.setUserName(userName);
		
		dto.setSalesStageCode(salesCode);
		dto.setStartDate(startDate);
		List<AuditHistoryDTO> auditHistories;
		List<AuditEntryDTO> entries = mapEntry.getValue();
		if(CollectionUtils.isEmpty(entries)) {
			//get entries from timeline for old opportunities
			entries = getHistoryFromTimeLine(timeLineHistories, salesCode, startDate);
		}
		auditHistories = historyBuilderHelper.groupAuditHistory(entries, EntityTypeId.OPPORTUNITY.getType());
		Collections.sort(auditHistories);
		dto.setHistories(auditHistories);
		return dto;
	}
	
	private AuditEngagementHistoryDTO getAuditEngagmentHistoryDTO(
			Entry<String, List<AuditEntryDTO>> mapEntry) {
		AuditEngagementHistoryDTO dto = new AuditEngagementHistoryDTO();
		//salesCode
		int engStageCode = getStageCode(mapEntry.getKey());
		//startDate
		Date startDate = getDate(mapEntry.getKey());

		String userName = getUserName(mapEntry.getKey());
		dto.setUserName(userName);
		dto.setEngagementStage(engStageCode);
		dto.setStartDate(startDate);
		List<AuditHistoryDTO> auditHistories;
		List<AuditEntryDTO> entries = mapEntry.getValue();
		
		auditHistories = historyBuilderHelper.groupAuditHistory(entries, EntityTypeId.ENGAGEMENT.getType());
		Collections.sort(auditHistories);
		dto.setHistories(auditHistories);
		return dto;
	}

	private List<AuditEntryDTO> getHistoryFromTimeLine(
			List<OpportunityTimelineHistoryT> timeLineHistories, int salesCode, Date startDate) {
		
		List<AuditEntryDTO> entries = Lists.newArrayList();
		OpportunityTimelineHistoryT preTimeLineHistory = null;
		OpportunityTimelineHistoryT timeLineHistory = null;
		BidDetailsT preBid = null;
		BidDetailsT currentBid = null;
		for (OpportunityTimelineHistoryT item : timeLineHistories) {
			if(item.getSalesStageCode() == salesCode && startDate.equals(truncateSeconds(item.getUpdatedDatetime()))) {
				timeLineHistory = item;
				break;
			}
			preTimeLineHistory = item;
		}
		
		if(timeLineHistory != null) {
			String user = userRepository.findUserNameByUserId(timeLineHistory.getUserUpdated());
			Date date = new Date(timeLineHistory.getUpdatedDatetime().getTime());
			currentBid = timeLineHistory.getBidDetailsT();
			if(preTimeLineHistory != null) {
				preBid = preTimeLineHistory.getBidDetailsT();
				entries.add(historyBuilderHelper.getAuditEntry("Sales Stage", String.valueOf(preTimeLineHistory.getSalesStageCode()), String.valueOf(timeLineHistory.getSalesStageCode()), user, date));
			} else {
				entries.add(historyBuilderHelper.getAuditEntry("Sales Stage", null, String.valueOf(timeLineHistory.getSalesStageCode()), user, date));
			}

			if(currentBid != null) {
				List<String> fieldArray = Lists.newArrayList("BidRequestReceiveDate", "TargetBidSubmissionDate", 
						"ActualBidSubmissionDate", "ExpectedDateOfOutcome", "WinProbability", 
						"CoreAttributesUsedForWinning", "BidRequestType");
				if(preBid == null) {
					preBid = new BidDetailsT();
				}
				entries.addAll(historyBuilderHelper.getEntry(preBid, currentBid, fieldArray, user, date));
			}
		}
		return entries;
	}


	/**
	 * grouping the entries by sales stage code and date.
	 * @param entries
	 * @param salesCodeSequenceMap
	 * @return
	 */
	private Map<String, List<AuditEntryDTO>> groupBySalesCode(
			List<AuditEntryDTO> entries, List<Map<String, Object>> salesCodeSequenceMap) {
		Map<String, List<AuditEntryDTO>> groupedEntriesMap = mapBySalesCode(salesCodeSequenceMap);

		// groupBySalesCode in order date range
		for (AuditEntryDTO entry : entries) {
			Date date = entry.getDate();
			//generate key in the format "currentSalesCode-NextSalesCode-Date"
			String key = generateKey(date, salesCodeSequenceMap);
			if(StringUtils.isNotEmpty(key)) {
				if (!groupedEntriesMap.containsKey(key)) {
					List<AuditEntryDTO> list = Lists.newArrayList();
					list.add(entry);
					groupedEntriesMap.put(key, list);
				} else {
					groupedEntriesMap.get(key).add(entry);
				}
			}
		}
		return groupedEntriesMap;
	}


	private Map<String, List<AuditEntryDTO>> mapBySalesCode(
			List<Map<String, Object>> salesCodeSequenceMap) {
		
		Map<String, List<AuditEntryDTO>> groupedEntriesMap = Maps.newLinkedHashMap();
		for (Map<String, Object> map : salesCodeSequenceMap) {
			List<AuditEntryDTO> newArrayList = Lists.newArrayList();
			groupedEntriesMap.put(getKey(map), newArrayList);
		}
		
		return groupedEntriesMap;
	}


	/**
	 * generate the key by comparing sales stage code level and date
	 * @param date
	 * @param salesCodeSequenceMap
	 * @return
	 */
	private String generateKey(Date date,
			List<Map<String, Object>> salesCodeSequenceMap) {
		//generate key in the format "currentSalesCode-NextSalesCode-StartDate"
		String key = "";
		for (Map<String, Object> map : salesCodeSequenceMap) {
			Date fromDate = (Date) map.get(KEY_FROM_DATE);
			Object toDate = map.get(KEY_TO_DATE);
			if(date.equals(fromDate) || (date.after(fromDate) && (toDate == null || date.before((Date)toDate)))) {
				key = getKey(map);
				break;
			}
		}
		return key;
	}
	
	private String getKey(Map<String, Object> map) {
		StringBuffer sb = new StringBuffer();
		sb.append(map.get(KEY_SALES_CODE));
		sb.append("-");
		sb.append(map.get(KEY_NEXT_SALES_CODE));
		sb.append("-");
		String dateStr = DateUtils.format((Date) map.get(KEY_FROM_DATE), DateUtils.AUDIT_HISTORY_FORMAT);//remove time from date
		sb.append(dateStr);
		sb.append("-");
		sb.append(map.get(KEY_USER_ID));
		return sb.toString();
	}

	/**
	 * returns the date by splitting the key 
	 * @param key
	 * @return
	 */
	private static Date getDate(String key) {
		//spilit date from the format "currentSalesCode-NextSalesCode-Date"
		String dateStr = key.split("-")[2];
		return DateUtils.parse(dateStr, DateUtils.AUDIT_HISTORY_FORMAT);
	}
	
	/**
	 * returns the user name by splitting the key 
	 * @param key
	 * @return
	 */
	private String getUserName(String key) {
		//spilit date from the format "currentSalesCode-NextSalesCode-Date"
		String userId = key.split("-")[3];
		return userRepository.findUserNameByUserId(userId);
	}

	/**
	 * returns sales stage code by splitting key
	 * @param key
	 * @return
	 */
	private static int getStageCode(String key) {
		//spilit date from the format "currentSalesCode-NextSalesCode-Date"
		String code = key.split("-")[0];
		return Integer.parseInt(code);
	}


	/**
	 * get entries based on entity
	 * @param entityTypeId
	 * @param entityId
	 * @return
	 */
	private List<AuditEntryDTO> getWorkflowEntityEntries(Integer entityTypeId,
			String entityId) {
		List<AuditEntryDTO> auditEntryDTOs = Lists.newArrayList();
		
		switch (EntityTypeId.getFrom(entityTypeId)) {
		case CUSTOMER:
			auditEntryDTOs = historyBuilder.getAuditEntries("workflow_customer", entityId);
			break;

		case PARTNER:
			auditEntryDTOs = historyBuilder.getAuditEntries("workflow_partner", entityId);
			break;
		
		case COMPETITOR:
			auditEntryDTOs = historyBuilder.getAuditEntries("workflow_competitor", entityId);
			break;

		default:
			break;
		}
		return auditEntryDTOs;
	}

	private Date truncateSeconds(Timestamp timestamps) {
		return DateUtils.truncateSeconds(new Date(timestamps.getTime()));
	}


	/**
	 * the service method - to get engagement history
	 * @param engId
	 * @return
	 */
	public AuditHistoryResponseDTO<AuditEngagementHistoryDTO> getEngagementHistory(
			String engId) {
		logger.info("Entering AuditDetailService :: getEngagementHistory");
		List<AuditEngagementHistoryDTO> histories = Lists.newArrayList();
		
		//List<AuditDeliveryMasterT> aDeliveryList = aDeliveryRepo.findByOldDeliveryMasterId(engId);
		
		List<Map<String, Object>> engStageSequenceMap = getEngagementCodeSequenceMap(engId);
		List<AuditEntryDTO> entries = historyBuilder.getAuditEntries("engagement", engId);
		
		//group entries by sales stage code and date
		Map<String, List<AuditEntryDTO>> engStageHistoryMap = groupBySalesCode(entries, engStageSequenceMap);
		
		for (Entry<String, List<AuditEntryDTO>> mapEntry : engStageHistoryMap.entrySet()) {
			AuditEngagementHistoryDTO aOppHistory = getAuditEngagmentHistoryDTO(mapEntry);
			histories.add(aOppHistory);
		}
		Collections.sort(histories);
		logger.info("Ends AuditDetailService :: getOpportunityHistory");
		return new AuditHistoryResponseDTO<AuditEngagementHistoryDTO>(histories);
	}

}
