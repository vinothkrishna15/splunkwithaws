package com.tcs.destination.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tcs.destination.bean.AuditBidDetailsT;
import com.tcs.destination.bean.AuditBidOfficeGroupOwnerLinkT;
import com.tcs.destination.bean.AuditEntryDTO;
import com.tcs.destination.bean.AuditHistoryDTO;
import com.tcs.destination.bean.AuditHistoryResponseDTO;
import com.tcs.destination.bean.AuditOpportunityCompetitorLinkT;
import com.tcs.destination.bean.AuditOpportunityCustomerContactLinkT;
import com.tcs.destination.bean.AuditOpportunityHistoryDTO;
import com.tcs.destination.bean.AuditOpportunityOfferingLinkT;
import com.tcs.destination.bean.AuditOpportunityPartnerLinkT;
import com.tcs.destination.bean.AuditOpportunitySalesSupportLinkT;
import com.tcs.destination.bean.AuditOpportunitySubSpLinkT;
import com.tcs.destination.bean.AuditOpportunityT;
import com.tcs.destination.bean.AuditOpportunityTcsAccountContactLinkT;
import com.tcs.destination.bean.AuditOpportunityWinLossFactorsT;
import com.tcs.destination.bean.AuditWorkflowCompetitorT;
import com.tcs.destination.bean.AuditWorkflowCustomerT;
import com.tcs.destination.bean.AuditWorkflowPartnerT;
import com.tcs.destination.bean.AuditWorkflowStepT;
import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.OpportunityTimelineHistoryT;
import com.tcs.destination.bean.WorkflowRequestT;
import com.tcs.destination.data.repository.AuditBidDetailsTRepository;
import com.tcs.destination.data.repository.AuditBidOfficeGroupOwnerLinkTRepository;
import com.tcs.destination.data.repository.AuditOpportunityCompetitorLinkTRepository;
import com.tcs.destination.data.repository.AuditOpportunityCustomerContactLinkTRepository;
import com.tcs.destination.data.repository.AuditOpportunityOfferingLinkTRepository;
import com.tcs.destination.data.repository.AuditOpportunityPartnerLinkTRepository;
import com.tcs.destination.data.repository.AuditOpportunityRepository;
import com.tcs.destination.data.repository.AuditOpportunitySalesSupportLinkTRepository;
import com.tcs.destination.data.repository.AuditOpportunitySubSpLinkTRepository;
import com.tcs.destination.data.repository.AuditOpportunityTcsAccountContactLinkTRepository;
import com.tcs.destination.data.repository.AuditOpportunityWinLossFactorsTRepository;
import com.tcs.destination.data.repository.AuditWorkflowCompetitorTRepository;
import com.tcs.destination.data.repository.AuditWorkflowCustomerTRepository;
import com.tcs.destination.data.repository.AuditWorkflowPartnerTRepository;
import com.tcs.destination.data.repository.AuditWorkflowStepTRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.OpportunityTimelineHistoryTRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.data.repository.WorkflowRequestTRepository;
import com.tcs.destination.enums.EntityTypeId;
import com.tcs.destination.enums.Operation;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.ErrorConstants;
import com.tcs.destination.utils.FieldNameMapper;
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
	
	private static final Integer OPERATION_ADD = new Integer(1);
	private static final Integer OPERATION_UPDATE = new Integer(2);

	@Autowired
	private WorkflowRequestTRepository workflowRequestRepository;

	@Autowired
	private AuditWorkflowStepTRepository aWorkflowStepRepository;

	@Autowired
	private AuditWorkflowPartnerTRepository aWorkflowPartnerRepository;

	@Autowired
	private AuditWorkflowCustomerTRepository aWorkflowCustomerRepository;

	@Autowired
	private AuditWorkflowCompetitorTRepository aWorkflowCompetitorRepository;
	
	@Autowired
	private AuditOpportunityRepository aOpportunityRepository;

	@Autowired
	private AuditOpportunityCompetitorLinkTRepository aCompetitorRepo;
	
	@Autowired
	private AuditOpportunityCustomerContactLinkTRepository aCustomerContactRepo;
	
	@Autowired
	private AuditOpportunityOfferingLinkTRepository aOpportunityOfferingRepo;
	
	@Autowired
	private AuditOpportunityPartnerLinkTRepository aOpportunityPartnerRepo;
	
	@Autowired
	private AuditOpportunitySalesSupportLinkTRepository aSalesSupportRepo;
	
	@Autowired
	private AuditOpportunitySubSpLinkTRepository aOpportunitySubSpRepo;
	
	@Autowired
	private AuditOpportunityTcsAccountContactLinkTRepository aTcsAccountContactRepo;

	@Autowired
	private AuditOpportunityWinLossFactorsTRepository aWinLossFactorsRepo;
	
	@Autowired
	private AuditBidDetailsTRepository aBidDetailRepo;
	
	@Autowired
	private AuditBidOfficeGroupOwnerLinkTRepository aBidOfficeGroupOwnerRepo;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PartnerRepository partnerRepository;
	
	@Autowired
	private OpportunityTimelineHistoryTRepository timelineHistoryRepo;
	
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
		List<AuditEntryDTO> stepEntries = getWorkflowStepEntries(wfId);
		
		if(CollectionUtils.isEmpty(stepEntries)) {
			throw new DestinationException(HttpStatus.NOT_FOUND, PropertyUtil.getProperty(ErrorConstants.WORKFLOW_AUDIT_NOT_AVAILABLE));
		}
		
		//fetch audit entries of workflow entity(partner, customer, competitor) using entityId
		List<AuditEntryDTO> entityEntries = getWorkflowEntityEntries(entityTypeId, entityId);
		
		stepEntries.addAll(entityEntries);
		//group the audit entries by date and user
		List<AuditHistoryDTO> auditHistories = groupAuditHistory(stepEntries, entityTypeId);
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
		List<AuditEntryDTO> entries = getOpportunityAudits(oppId);
		
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
//		OpportunityTimelineHistoryT previousHistory = timeLineHistoriesclone.get(0);
		OpportunityTimelineHistoryT previousHistory = timeLineHistoriesclone.remove(0);
		for (OpportunityTimelineHistoryT timeLineHistory : timeLineHistoriesclone) {
			Date toDate = truncateSeconds(timeLineHistory.getUpdatedDatetime());
			Date fromDate = truncateSeconds(previousHistory.getUpdatedDatetime());
			
			Map<String, Object> map = createMapWith(fromDate,
					previousHistory.getSalesStageCode(), toDate, timeLineHistory.getSalesStageCode());
			salesCodeSequenceMap.add(map);
			
			previousHistory = timeLineHistory;
		}
		
		//add current sales stage code also 
		Date fromDate = truncateSeconds(previousHistory.getUpdatedDatetime());
		salesCodeSequenceMap.add(createMapWith(fromDate, previousHistory.getSalesStageCode(), null, null));
		
		return salesCodeSequenceMap;
	}


	/**
	 * map using to track the sales stage change 
	 * @param lastSalesChangeDate
	 * @param oldSalesCode
	 * @param modifiedDate
	 * @param salesCode
	 * @return
	 */
	private Map<String, Object> createMapWith(Date lastSalesChangeDate,
			Integer oldSalesCode, Date modifiedDate, Integer salesCode) {
		Map<String, Object> map = Maps.newHashMap();
		map.put(KEY_NEXT_SALES_CODE, salesCode);
		map.put(KEY_SALES_CODE, oldSalesCode);
		map.put(KEY_FROM_DATE, lastSalesChangeDate);
		map.put(KEY_TO_DATE, modifiedDate);
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
		int salesCode = getSalesCode(mapEntry.getKey());
		//startDate
		Date startDate = getDate(mapEntry.getKey());

		dto.setSalesStageCode(salesCode);
		dto.setStartDate(startDate);
		List<AuditHistoryDTO> auditHistories;
		List<AuditEntryDTO> entries = mapEntry.getValue();
		if(CollectionUtils.isEmpty(entries)) {
			//get entries from timeline for old opportunities
			entries = getHistoryFromTimeLine(timeLineHistories, salesCode, startDate);
		} 
		auditHistories = groupAuditHistory(entries, EntityTypeId.OPPORTUNITY.getType());
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
		
		String user = timeLineHistory.getUserUpdated();
		Date date = new Date(timeLineHistory.getUpdatedDatetime().getTime());
		if(preTimeLineHistory != null) {
			preBid = preTimeLineHistory.getBidDetailsT();
			entries.add(getAuditEntry("Sales Stage", String.valueOf(preTimeLineHistory.getSalesStageCode()), String.valueOf(timeLineHistory.getSalesStageCode()), user, date));
		} else {
			entries.add(getAuditEntry("Sales Stage", null, String.valueOf(timeLineHistory.getSalesStageCode()), user, date));
			currentBid = timeLineHistory.getBidDetailsT();
		}
		
		if(currentBid != null) {
			List<String> fieldArray = Lists.newArrayList("BidRequestReceiveDate", "TargetBidSubmissionDate", 
					"ActualBidSubmissionDate", "ExpectedDateOfOutcome", "WinProbability", 
					"CoreAttributesUsedForWinning", "BidRequestType");
			if(preBid == null) {
				preBid = new BidDetailsT();
			}
			entries.addAll(getEntry(preBid, currentBid, fieldArray, user, date));
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
			if (!groupedEntriesMap.containsKey(key)) {
			    List<AuditEntryDTO> list = Lists.newArrayList();
			    list.add(entry);
			    groupedEntriesMap.put(key, list);
			} else {
				groupedEntriesMap.get(key).add(entry);
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
	 * returns sales stage code by splitting key
	 * @param key
	 * @return
	 */
	private static int getSalesCode(String key) {
		//spilit date from the format "currentSalesCode-NextSalesCode-Date"
		String code = key.split("-")[0];
		return Integer.parseInt(code);
	}

	/**
	 * fetch all available audit entries from opportunity
	 * @param oppId
	 * @return
	 */
	private List<AuditEntryDTO> getOpportunityAudits(String oppId) {
		List<AuditEntryDTO> entries = Lists.newArrayList();
		
		entries.addAll(getOpportunityAudit(oppId));
		entries.addAll(getOppCompetitorAudit(oppId));
		entries.addAll(getCustomerContactAudit(oppId));
		entries.addAll(getOppOfferingAudit(oppId));
		entries.addAll(getOppPartnerAudit(oppId));
		entries.addAll(getSalesSupportAudit(oppId));
		entries.addAll(getOppSubspAudit(oppId));
		entries.addAll(getTcsContactAudit(oppId));
		entries.addAll(getWinLossFactorAudit(oppId));
		entries.addAll(getBidDetailsAudit(oppId));
		return entries;
	}


	/**
	 * get all bid related changes
	 * @param oppId
	 * @return
	 */
	private List<AuditEntryDTO> getBidDetailsAudit(String oppId) {
		List<AuditEntryDTO> entries = Lists.newArrayList();
		List<AuditBidDetailsT> bidDetail = aBidDetailRepo.findByOldOpportunityId(oppId);
		if(CollectionUtils.isNotEmpty(bidDetail)) {
			for (AuditBidDetailsT aBidDetailT : bidDetail) {
				entries.addAll(getBidDetailsAudit(aBidDetailT));
			}
		}
		return entries;
	}

	/**
	 * get all bid related changes
	 * @param aBidDetailT
	 * @return
	 */
	private List<AuditEntryDTO> getBidDetailsAudit(
			AuditBidDetailsT aBidDetailT) {
		
		List<AuditEntryDTO> entryDTOs = Lists.newArrayList();
		String user = userRepository.findUserNameByUserId(aBidDetailT.getCreatedModifiedBy());
		Date date = new Date(aBidDetailT.getCreatedModifiedDatetime().getTime());
		String bidId = aBidDetailT.getBidId();
		List<String> fieldArray = Lists.newArrayList("BidRequestReceiveDate", "TargetBidSubmissionDate", 
				"ActualBidSubmissionDate", "ExpectedDateOfOutcome", "WinProbability", 
				"CoreAttributesUsedForWinning", "BidRequestType");
		
		for (String fieldName : fieldArray) {
			AuditEntryDTO entry = getEntry(aBidDetailT, fieldName, user, date);
			if(entry != null) {
				entryDTOs.add(entry);
			}
		}
		entryDTOs.addAll(getBidOfficeGrpOwnerAudit(bidId));
		
		return entryDTOs;
	}


	/**
	 * get all bid owner related changes
	 * @param bidId
	 * @return
	 */
	private List<AuditEntryDTO> getBidOfficeGrpOwnerAudit(
			String bidId) {
		List<AuditEntryDTO> entries = Lists.newArrayList();
		List<AuditBidOfficeGroupOwnerLinkT> bidOffGrpOwners = aBidOfficeGroupOwnerRepo.findByOldBidId(bidId);
		if(CollectionUtils.isNotEmpty(bidOffGrpOwners)) {
			for (AuditBidOfficeGroupOwnerLinkT aBidOffGrpOwner : bidOffGrpOwners) {
				entries.addAll(getBidOfficeGrpOwnerAudit(aBidOffGrpOwner));
			}
		}
		return entries;
	}


	/**
	 * get all bid owner related changes
	 * @param aBidOffGrpOwner
	 * @return
	 */
	private List<AuditEntryDTO> getBidOfficeGrpOwnerAudit(
			AuditBidOfficeGroupOwnerLinkT aBidOffGrpOwner) {
		List<AuditEntryDTO> entries = Lists.newArrayList();
		String user = userRepository.findUserNameByUserId(aBidOffGrpOwner.getCreatedModifiedBy());
		Date date = new Date(aBidOffGrpOwner.getCreatedModifiedDatetime().getTime());
		AuditEntryDTO entry = getEntry(aBidOffGrpOwner, "BidOfficeGroupOwner", user, date);
		if(entry != null) {
			String fromVal = entry.getFromVal();
			String toVal = entry.getToVal();
			entry.setFromVal(fromVal != null ? userRepository.findUserNameByUserId(fromVal) : null);
			entry.setToVal(toVal != null ? userRepository.findUserNameByUserId(toVal) : null);
			entries.add(entry);
		}
		return entries;
	}

	/**
	 * get all winloss factor related changes
	 * @param oppId
	 * @return
	 */
	private List<AuditEntryDTO> getWinLossFactorAudit(String oppId) {
		//		opportunity_win_loss_factors_t
		List<AuditEntryDTO> entries = Lists.newArrayList();
		List<AuditOpportunityWinLossFactorsT> aWinLossTs = aWinLossFactorsRepo.findByOldOpportunityId(oppId);
		if(CollectionUtils.isNotEmpty(aWinLossTs)) {
			for (AuditOpportunityWinLossFactorsT aWinLossFactorsT : aWinLossTs) {
				entries.addAll(getWinLossFactorAudit(aWinLossFactorsT));
			}
		}
		return entries;
	}


	/**
	 * get all winloss factor related changes
	 * @param aWinLossFactorsT
	 * @return
	 */
	private List<AuditEntryDTO> getWinLossFactorAudit(
			AuditOpportunityWinLossFactorsT aWinLossFactorsT) {
		Operation operationType = Operation.getByCode(aWinLossFactorsT.getOperationType());
		String user = userRepository.findUserNameByUserId(aWinLossFactorsT.getCreatedModifiedBy());
		Date date = new Date(aWinLossFactorsT.getCreatedModifiedDatetime().getTime());
		String winLossFactor = aWinLossFactorsT.getOldWinLossFactor();
		String fieldName = "Win Loss Factor";
		AuditEntryDTO entry = getEntryByOperation(operationType, user, date,
				winLossFactor, fieldName);
		return Lists.newArrayList(entry);
	}


	/**
	 * get all tcs contact related changes
	 * @param oppId
	 * @return
	 */
	private List<AuditEntryDTO> getTcsContactAudit(String oppId) {
		//		opportunity_tcs_account_contact_link_t
		List<AuditEntryDTO> entries = Lists.newArrayList();
		List<AuditOpportunityTcsAccountContactLinkT> aTcsContactTs = aTcsAccountContactRepo.findByOldOpportunityId(oppId);
		if(CollectionUtils.isNotEmpty(aTcsContactTs)) {
			for (AuditOpportunityTcsAccountContactLinkT aTcsContactT : aTcsContactTs) {
				entries.addAll(getTcsContactAudit(aTcsContactT));
			}
		}
		return entries;
	}


	/**
	 * get all tcs contact related changes
	 * @param aTcsContactT
	 * @return
	 */
	private List<AuditEntryDTO> getTcsContactAudit(
			AuditOpportunityTcsAccountContactLinkT aTcsContactT) {
		Operation operationType = Operation.getByCode(aTcsContactT.getOperationType());
		String user = userRepository.findUserNameByUserId(aTcsContactT.getCreatedModifiedBy());
		Date date = new Date(aTcsContactT.getCreatedModifiedDatetime().getTime());
		String contact = contactRepository.findOne(aTcsContactT.getOldContactId()).getContactName();
		String fieldName = "TCS Contact";
		AuditEntryDTO entry = getEntryByOperation(operationType, user, date,
				contact, fieldName);
		return Lists.newArrayList(entry);
	}


	/**
	 * get all subsp related changes
	 * @param oppId
	 * @return
	 */
	private List<AuditEntryDTO> getOppSubspAudit(String oppId) {
		//		opportunity_sub_sp_link_t
		List<AuditEntryDTO> entries = Lists.newArrayList();
		List<AuditOpportunitySubSpLinkT> aSubSpTs = aOpportunitySubSpRepo.findByOldOpportunityId(oppId);
		if(CollectionUtils.isNotEmpty(aSubSpTs)) {
			for (AuditOpportunitySubSpLinkT aSubSpT : aSubSpTs) {
				entries.addAll(getOppSubspAudit(aSubSpT));
			}
		}
		return entries;
	}


	/**
	 * get all subsp related changes
	 * @param aSubSpTs
	 * @return
	 */
	private List<AuditEntryDTO> getOppSubspAudit(
			AuditOpportunitySubSpLinkT aSubSpTs) {
		Operation operationType = Operation.getByCode(aSubSpTs.getOperationType());
		String user = userRepository.findUserNameByUserId(aSubSpTs.getCreatedModifiedBy());
		Date date = new Date(aSubSpTs.getCreatedModifiedDatetime().getTime());
		String subSp = aSubSpTs.getOldSubSp();
		String fieldName = "Sub SP";
		List<AuditEntryDTO> entries = Lists.newArrayList();
		if(operationType == Operation.ADD) {
			entries.add(getAuditEntry(fieldName, null, subSp, user, date));
			if(aSubSpTs.getNewSubspPrimary() != null && aSubSpTs.getNewSubspPrimary().booleanValue()) {
				entries.add( getAuditEntry("Sub SP Primary", null, subSp, user, date));
			}
		} else if(operationType == Operation.UPDATE) {
			entries.add( getAuditEntry("Sub SP Primary", null, subSp, user, date));
		} else {
			entries.add( getAuditEntry(fieldName, subSp, null, user, date));
		}
		return entries;
	}


	/**
	 * get all sales support owner related changes
	 * @param oppId
	 * @return
	 */
	private List<AuditEntryDTO> getSalesSupportAudit(String oppId) {
		//		opportunity_sales_support_link_t
		List<AuditEntryDTO> entries = Lists.newArrayList();
		List<AuditOpportunitySalesSupportLinkT> aSalesSpportTs = aSalesSupportRepo.findByOldOpportunityId(oppId);
		if(CollectionUtils.isNotEmpty(aSalesSpportTs)) {
			for (AuditOpportunitySalesSupportLinkT aSalesSpportT : aSalesSpportTs) {
				entries.addAll(getSalesSupportAudit(aSalesSpportT));
			}
		}
		return entries;
	}


	/**
	 * get all sales support owner related changes
	 * @param aSalesSpportT
	 * @return
	 */
	private List<AuditEntryDTO> getSalesSupportAudit(
			AuditOpportunitySalesSupportLinkT aSalesSpportT) {
		Operation operationType = Operation.getByCode(aSalesSpportT.getOperationType());
		String user = userRepository.findUserNameByUserId(aSalesSpportT.getCreatedModifiedBy());
		Date date = new Date(aSalesSpportT.getCreatedModifiedDatetime().getTime());
		String salesSupport = userRepository.findUserNameByUserId(aSalesSpportT.getOldSalesSupportOwner());
		String fieldName = "Sales Support Owner";
		AuditEntryDTO entry = getEntryByOperation(operationType, user, date,
				salesSupport, fieldName);
		return Lists.newArrayList(entry);
	}


	/**
	 * get all partner related changes
	 * @param oppId
	 * @return
	 */
	private List<AuditEntryDTO> getOppPartnerAudit(String oppId) {
		//		opportunity_partner_link_t
		List<AuditEntryDTO> entries = Lists.newArrayList();
		List<AuditOpportunityPartnerLinkT> aPartnerTs = aOpportunityPartnerRepo.findByOldOpportunityId(oppId);
		if(CollectionUtils.isNotEmpty(aPartnerTs)) {
			for (AuditOpportunityPartnerLinkT aPartnerT : aPartnerTs) {
				entries.addAll(getOppPartnerAudit(aPartnerT));
			}
		}
		return entries;
	}


	/**
	 * get all partner related changes
	 * @param aPartnerT
	 * @return
	 */
	private List<AuditEntryDTO> getOppPartnerAudit(
			AuditOpportunityPartnerLinkT aPartnerT) {
		Operation operationType = Operation.getByCode(aPartnerT.getOperationType());
		String user = userRepository.findUserNameByUserId(aPartnerT.getCreatedModifiedBy());
		Date date = new Date(aPartnerT.getCreatedModifiedDatetime().getTime());
		String partner = partnerRepository.findOne(aPartnerT.getOldPartnerId()).getPartnerName();
		String fieldName = "Partner";
		AuditEntryDTO entry = getEntryByOperation(operationType, user, date,
				partner, fieldName);
		return Lists.newArrayList(entry);
	}


	/**
	 * get all offering related changes
	 * @param oppId
	 * @return
	 */
	private List<AuditEntryDTO> getOppOfferingAudit(String oppId) {
		//opportunity_offering_link_t
		List<AuditEntryDTO> entries = Lists.newArrayList();
		List<AuditOpportunityOfferingLinkT> aOfferingTs = aOpportunityOfferingRepo.findByOldOpportunityId(oppId);
		if(CollectionUtils.isNotEmpty(aOfferingTs)) {
			for (AuditOpportunityOfferingLinkT aOfferingT : aOfferingTs) {
				entries.addAll(getOppOfferingAudit(aOfferingT));
			}
		}
		return entries;
	}


	/**
	 * get all offering related changes
	 * @param aOfferingT
	 * @return
	 */
	private List<AuditEntryDTO> getOppOfferingAudit(
			AuditOpportunityOfferingLinkT aOfferingT) {
		Operation operationType = Operation.getByCode(aOfferingT.getOperationType());
		String user = userRepository.findUserNameByUserId(aOfferingT.getCreatedModifiedBy());
		Date date = new Date(aOfferingT.getCreatedModifiedDatetime().getTime());
		String partner = aOfferingT.getOldOffering();
		String fieldName = "Offering";
		AuditEntryDTO entry = getEntryByOperation(operationType, user, date,
				partner, fieldName);
		return Lists.newArrayList(entry);
	}


	/**
	 * get all customer contact related changes
	 * @param oppId
	 * @return
	 */
	private List<AuditEntryDTO> getCustomerContactAudit(String oppId) {
		//opportunity_customer_contact_link_t
		List<AuditEntryDTO> entries = Lists.newArrayList();
		List<AuditOpportunityCustomerContactLinkT> aCustomerContactTs = aCustomerContactRepo.findByOldOpportunityId(oppId);
		if(CollectionUtils.isNotEmpty(aCustomerContactTs)) {
			for (AuditOpportunityCustomerContactLinkT aCustomerContactT : aCustomerContactTs) {
				entries.addAll(getCustomerContactAudit(aCustomerContactT));
			}
		}
		return entries;
	}


	/**
	 * get all offering related changes
	 * @param aCustomerContactT
	 * @return
	 */
	private List<AuditEntryDTO> getCustomerContactAudit(
			AuditOpportunityCustomerContactLinkT aCustomerContactT) {
		Operation operationType = Operation.getByCode(aCustomerContactT.getOperationType());
		String user = userRepository.findUserNameByUserId(aCustomerContactT.getCreatedModifiedBy());
		Date date = new Date(aCustomerContactT.getCreatedModifiedDatetime().getTime());
		String custContact = contactRepository.findOne(aCustomerContactT.getOldContactId()).getContactName();
		String fieldName = "Customer Contact";
		AuditEntryDTO entry = getEntryByOperation(operationType, user, date,
				custContact, fieldName);
		return Lists.newArrayList(entry);
	}


	/**
	 * get all competitor related changes
	 * @param oppId
	 * @return
	 */
	private List<AuditEntryDTO> getOppCompetitorAudit(String oppId) {
		//opportunity_competitor_link_t
		List<AuditEntryDTO> entries = Lists.newArrayList();
		List<AuditOpportunityCompetitorLinkT> aCompetitorTs = aCompetitorRepo.findByOldOpportunityId(oppId);
		if(CollectionUtils.isNotEmpty(aCompetitorTs)) {
			for (AuditOpportunityCompetitorLinkT aCompetitorT : aCompetitorTs) {
				entries.addAll(getOppCompetitorAudit(aCompetitorT));
			}
		}
		return entries;
	}


	/**
	 * get all competitor related changes
	 * @param aCompetitorT
	 * @return
	 */
	private List<AuditEntryDTO> getOppCompetitorAudit(
			AuditOpportunityCompetitorLinkT aCompetitorT) {
		List<AuditEntryDTO> entries = Lists.newArrayList();
		Operation operationType = Operation.getByCode(aCompetitorT.getOperationType());
		String user = userRepository.findUserNameByUserId(aCompetitorT.getCreatedModifiedBy());
		Date date = new Date(aCompetitorT.getCreatedModifiedDatetime().getTime());
		String compeitor = aCompetitorT.getOldCompetitorName();
		String fieldName = "Competitor";
		if(operationType == Operation.ADD) {
			entries.add(getAuditEntry(fieldName, null, compeitor, user, date));
			if(StringUtils.equals(Constants.Y,aCompetitorT.getNewIncumbentFlag())) {
				entries.add(getAuditEntry("Incumbent Flag", null, compeitor, user, date));
			}
			
		} else if(operationType == Operation.UPDATE) {
			entries.add(getAuditEntry("Incumbent Flag", null, compeitor, user, date));
		} else {
			entries.add(getAuditEntry(fieldName, compeitor, null, user, date));
		}
		return entries;
	}


	/**
	 * get all opportunity master related changes
	 * @param oppId
	 * @return
	 */
	private List<AuditEntryDTO> getOpportunityAudit(String oppId) {
		//opportunity audit
		List<AuditEntryDTO> entryDTOs = Lists.newArrayList();
		
		List<AuditOpportunityT> opportunities = aOpportunityRepository.findByOpportunityId(oppId);
		if(CollectionUtils.isNotEmpty(opportunities))
		for (AuditOpportunityT auditOpportunityT : opportunities) {
			entryDTOs.addAll(getOpportunityAudit(auditOpportunityT));
		}
		return entryDTOs;
	}


	/**
	 * get all opportunity master related changes
	 * @param auditOpportunityT
	 * @return
	 */
	private List<AuditEntryDTO> getOpportunityAudit(
			AuditOpportunityT auditOpportunityT) {
		List<AuditEntryDTO> entryDTOs = Lists.newArrayList();
		String user = userRepository.findUserNameByUserId(auditOpportunityT.getNewModifiedBy());
		Date date = new Date(auditOpportunityT.getNewModifiedDatetime().getTime());
		
		List<String> fieldArray = Lists.newArrayList("CrmId", "OpportunityName", "OpportunityDescription", "StrategicDeal", 
				"DealCurrency", "OverallDealSize", "DigitalDealValue", "DealClosureDate",
				"DescriptionForWinLoss","EngagementDuration","SalesStageCode", 
				"DealType", "Country", "DigitalFlag");
		
		entryDTOs.addAll(getEntriesFromFields(auditOpportunityT, fieldArray, user, date));
		
		AuditEntryDTO entry = getEntry(auditOpportunityT, "OpportunityOwner", user, date);
		if(entry != null) {
			String fromVal = entry.getFromVal();
			String toVal = entry.getToVal();
			entry.setFromVal(fromVal != null ? userRepository.findUserNameByUserId(fromVal) : null);
			entry.setToVal(toVal != null ? userRepository.findUserNameByUserId(toVal) : null);
			entryDTOs.add(entry);
		}
		return entryDTOs;
	}

	/**
	 * return the sub table entry only for addition and updation
	 * @param operationType
	 * @param user
	 * @param date
	 * @param fieldValue
	 * @param fieldName
	 * @return
	 */
	private AuditEntryDTO getEntryByOperation(Operation operationType,
			String user, Date date, String fieldValue, String fieldName) {
		AuditEntryDTO entry;
		if(operationType == Operation.ADD) {
			entry = getAuditEntry(fieldName, null, fieldValue, user, date);
		} else { //removed
			entry = getAuditEntry(fieldName, fieldValue, null, user, date);
		}
		return entry;
	}


	/**
	 * group the entries by date and user
	 * @param entries
	 * @param entityTypeId
	 * @return
	 */
	private List<AuditHistoryDTO> groupAuditHistory(
			List<AuditEntryDTO> entries, Integer entityTypeId) {
		List<AuditHistoryDTO> history = Lists.newArrayList();
		Map<String, List<AuditEntryDTO>> groupEntries= groupEntries(entries);
		
		for (Entry<String, List<AuditEntryDTO>> entryGroup : groupEntries.entrySet()) {
			history.add(constructAuditHistory(entryGroup.getValue(), entityTypeId));
		}
		
		return history;
	}
	
	/**
	 * creates audit history with date, user and list of messages
	 * @param entries
	 * @param entityTypeId
	 * @return
	 */
	private AuditHistoryDTO constructAuditHistory(List<AuditEntryDTO> entries, Integer entityTypeId) {
		AuditHistoryDTO history = new AuditHistoryDTO();
		List<String> messages = Lists.newArrayList();
		if(CollectionUtils.isNotEmpty(entries)) {
			history.setDate(entries.get(0).getDate());
			history.setUserName(entries.get(0).getUser());
			history.setOperation(getOperation(entries.get(0).getOperation()));
			for (AuditEntryDTO entry : entries) {
				messages.add(constructMessage(entry, entityTypeId));
			}

			history.setMessages(messages);
		}
		return history;
	}

	/**
	 * @param operation
	 * @return
	 */
	private String getOperation(int operation) {
		return Operation.getByCode(operation).name();
	}

	/**
	 * constructs the message for a entry
	 * @param entry
	 * @param entityTypeId
	 * @return
	 */
	private String constructMessage(AuditEntryDTO entry, Integer entityTypeId) {
		String message;
		if(entry.isNewEntry()) {
			message = getNewRequestMessage(entityTypeId);
		} else if(entry.getFromVal() == null && entry.getToVal() != null) {
			message = getAddionMessage(entry.getFieldName(), entry.getToVal());
		} else if(entry.getFromVal() != null && entry.getToVal() == null) {
			message = getRemoveMessage(entry.getFieldName(), entry.getFromVal());
		} else {
			message = getUpdateMessage(entry.getFieldName(), entry.getFromVal(), entry.getToVal());
		}
		
		return message;
	}

	/**
	 * returns a new entry message
	 * @param entityTypeId
	 * @return
	 */
	private String getNewRequestMessage(Integer entityTypeId) {//TODO form with template
		StringBuffer sb = new StringBuffer("New ");
		sb.append(EntityTypeId.getFrom(entityTypeId).name()).append(" Request");
		
		return sb.toString();
	}

	/**
	 * returns field addition message with field name and value
	 * @param fieldName
	 * @param toVal
	 * @return
	 */
	private String getAddionMessage(String fieldName, String toVal) {//TODO form with template
		StringBuffer sb = new StringBuffer("Added ");
		sb.append(fieldName).append(": ").append(toVal);
		
		return sb.toString();
	}
	
	/**
	 * returns field removing message
	 * @param fieldName
	 * @param fromVal
	 * @return
	 */
	private String getRemoveMessage(String fieldName, String fromVal) {//TODO form with template
		StringBuffer sb = new StringBuffer("Removed ");
		sb.append(fieldName).append(" ").append(fromVal);
		
		return sb.toString();
	}

	/**
	 * returns field updation message with from and to values
	 * @param fieldName
	 * @param fromVal
	 * @param toVal
	 * @return
	 */
	private String getUpdateMessage(String fieldName, String fromVal,
			String toVal) {//TODO form with template
		StringBuffer sb = new StringBuffer("Updated ");
		sb.append(fieldName).append(" from ").append(fromVal).append(" to ").append(toVal);
		
		return sb.toString();
	}

	/**
	 * group and create the map with entries by date and user
	 * @param entries
	 * @return
	 */
	private Map<String, List<AuditEntryDTO>> groupEntries(
			List<AuditEntryDTO> entries) {
		Map<String, List<AuditEntryDTO>> map = Maps.newHashMap();
		if(CollectionUtils.isNotEmpty(entries)) {
			for (AuditEntryDTO auditEntryDTO : entries) {
				String code = String.valueOf(auditEntryDTO.hashCode());
				if (!map.containsKey(code)) {
					List<AuditEntryDTO> list = Lists.newArrayList();
					list.add(auditEntryDTO);
					map.put(code, list);
				} else {
					map.get(code).add(auditEntryDTO);
				}
			}
		}
		return map;
	}

	/**
	 * get entries based on entity
	 * @param entityTypeId
	 * @param entityId
	 * @return
	 */
	private List<AuditEntryDTO> getWorkflowEntityEntries(Integer entityTypeId,
			String entityId) {
		List<AuditEntryDTO> auditEntryDTOs = null;
		
		switch (EntityTypeId.getFrom(entityTypeId)) {
		case CUSTOMER:
			auditEntryDTOs = getCustomerAudit(entityId);
			break;

		case PARTNER:
			auditEntryDTOs = getPartnerAudit(entityId);
			break;
		
		case COMPETITOR:
			auditEntryDTOs = getCompetitorAudit(entityId);
			break;

		default:
			break;
		}
		return auditEntryDTOs;
	}

	/**
	 * returns all competitor changes
	 * @param entityId
	 * @return
	 */
	private List<AuditEntryDTO> getCompetitorAudit(String entityId) {
		List<AuditEntryDTO> entryDTOs = Lists.newArrayList();
		List<AuditWorkflowCompetitorT> competitorAudit = aWorkflowCompetitorRepository.findByWorkflowCompetitorIdAndOperationType(entityId, 2);

		if(CollectionUtils.isNotEmpty(competitorAudit)) {
			for (AuditWorkflowCompetitorT aWorkflowCompetitorT : competitorAudit) {
				entryDTOs.addAll(getCompetitorAudit(aWorkflowCompetitorT));
			}
		}
		return entryDTOs;
	}

	/**
	 * returns all workflow competitor changes
	 * @param wfCompetitor
	 * @return
	 */
	private List<AuditEntryDTO> getCompetitorAudit(
			AuditWorkflowCompetitorT wfCompetitor) {
		List<AuditEntryDTO> entryDTOs = Lists.newArrayList();
		String user = userRepository.findUserNameByUserId(wfCompetitor.getCreatedModifiedBy());
		Date date = new Date(wfCompetitor.getCreatedModifiedDatetime().getTime());
		
		List<String> fieldArray = Lists.newArrayList("WorkflowCompetitorName", "WorkflowCompetitorWebsite", "WorkflowCompetitorNotes");
		
		entryDTOs.addAll(getEntriesFromFields(wfCompetitor, fieldArray, user, date));
		return entryDTOs;
	}

	/**
	 * returns all workflow partner changes
	 * @param entityId
	 * @return
	 */
	private List<AuditEntryDTO> getPartnerAudit(String entityId) {
		List<AuditEntryDTO> entryDTOs = Lists.newArrayList();
		List<AuditWorkflowPartnerT> partnerAudit = aWorkflowPartnerRepository.findByWorkflowPartnerIdAndOperationType(entityId, 2);

		if(CollectionUtils.isNotEmpty(partnerAudit)) {
			for (AuditWorkflowPartnerT aWorkflowPartnerT : partnerAudit) {
				entryDTOs.addAll(getPartnerAudit(aWorkflowPartnerT));
			}
		}
		return entryDTOs;
	}

	/**
	 * returns all workflow partner changes
	 * @param wfPartner
	 * @return
	 */
	private List<AuditEntryDTO> getPartnerAudit(
			AuditWorkflowPartnerT wfPartner) {
		List<AuditEntryDTO> entryDTOs = Lists.newArrayList();
		String user = userRepository.findUserNameByUserId(wfPartner.getCreatedModifiedBy());
		Date date = new Date(wfPartner.getCreatedModifiedDatetime().getTime());
		
		List<String> fieldArray = Lists.newArrayList("PartnerName", "Website", "Facebook", "CorporateHqAddress", "Iou", "Geography", "Notes");
		
		entryDTOs.addAll(getEntriesFromFields(wfPartner, fieldArray, user, date));
		return entryDTOs;
	}

	/**
	 * returns all workflow customer changes
	 * @param entityId
	 * @return
	 */
	private List<AuditEntryDTO> getCustomerAudit(String entityId) {
		List<AuditEntryDTO> entryDTOs = Lists.newArrayList();
		List<AuditWorkflowCustomerT> customerAudit = aWorkflowCustomerRepository.findByWorkflowCustomerIdAndOperationType(entityId, 2);

		if(CollectionUtils.isNotEmpty(customerAudit)) {
			for (AuditWorkflowCustomerT aWorkflowCustomerT : customerAudit) {
				entryDTOs.addAll(getCustomerAudit(aWorkflowCustomerT));
			}
		}
		return entryDTOs;
	}

	/**
	 * returns all workflow customer changes
	 * @param wfCustomer
	 * @return
	 */
	private List<AuditEntryDTO> getCustomerAudit(
			AuditWorkflowCustomerT wfCustomer) {
		List<AuditEntryDTO> entryDTOs = Lists.newArrayList();
		String user = userRepository.findUserNameByUserId(wfCustomer.getCreatedModifiedBy());
		Date date = new Date(wfCustomer.getCreatedModifiedDatetime().getTime());
		
		List<String> fieldArray = Lists.newArrayList("GroupCustomerName", "CustomerName", "Website", "Facebook", 
				"CorporateHqAddress", "Iou", "Geography", "Remarks");
		
		entryDTOs.addAll(getEntriesFromFields(wfCustomer, fieldArray, user, date));
		return entryDTOs;
	}

	/**
	 * creates a {@link AuditEntryDTO},comparing old and new by calling 'getOld' and 'getNew' methods for the provided field name
	 * using reflection to call getter methods
	 * @param obj
	 * @param fieldName
	 * @param user
	 * @param date
	 * @return
	 */
	private AuditEntryDTO getEntry(Object obj, String fieldName, String user, Date date) {
		try {
			Method oldGetter = obj.getClass().getDeclaredMethod("getOld"+fieldName);
			Method newGetter = obj.getClass().getDeclaredMethod("getNew"+fieldName);
			Object oldVal = oldGetter.invoke(obj);
			Object newVal = newGetter.invoke(obj);
			if(!ObjectUtils.equals(oldVal, newVal)) {
				return getAuditEntry(fieldName, (oldVal != null )? String.valueOf(oldVal): null, 
						(newVal != null )? String.valueOf(newVal): null, user, date);
			}

		} catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException e) {
			logger.error("AuditDetailService :: error on getter method reflection -> {}", e.getMessage());
		}

		return null;
	}
	
	/**
	 * 
	 * compares the old and new objects and returns the list of entries
	 * @param oldObj
	 * @param newObj
	 * @param fieldNames
	 * @param user
	 * @param date
	 * @return
	 */
	private List<AuditEntryDTO> getEntry(Object oldObj, Object newObj, List<String> fieldNames, String user, Date date) {

		List<AuditEntryDTO> entries = Lists.newArrayList();
		for (String fieldName : fieldNames) {

			try {
				Method oldGetter = oldObj.getClass().getDeclaredMethod("get"+fieldName);
				Method newGetter = newObj.getClass().getDeclaredMethod("get"+fieldName);
				Object oldVal = oldGetter.invoke(oldObj);
				Object newVal = newGetter.invoke(newObj);
				if(!ObjectUtils.equals(oldVal, newVal)) {
					entries.add(getAuditEntry(fieldName, (oldVal != null )? String.valueOf(oldVal): null, 
							(newVal != null )? String.valueOf(newVal): null, user, date));
				}

			} catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException e) {
				logger.error("AuditDetailService :: error on getter method reflection -> {}", e.getMessage());
			}
		}
		return entries;
	}

	/**
	 * get all audits in workflow steps
	 * @param workflowId
	 * @return
	 */
	private List<AuditEntryDTO> getWorkflowStepEntries(Integer workflowId) {
		List<AuditEntryDTO> auditEntryDTOs = Lists.newArrayList();
		List<AuditWorkflowStepT> aWorkflowSteps = aWorkflowStepRepository.findByRequestIdAndStatus(workflowId);
		if(CollectionUtils.isNotEmpty(aWorkflowSteps)) {
			for (AuditWorkflowStepT auditWorkflowStepT : aWorkflowSteps) {
				auditEntryDTOs.addAll(getWorkflowStepEntries(auditWorkflowStepT));
			}
		}
		return auditEntryDTOs;
	}

	/**
	 * get all audits in workflow steps
	 * @param auditWorkflowStepT
	 * @return
	 */
	private List<AuditEntryDTO> getWorkflowStepEntries(AuditWorkflowStepT auditWorkflowStepT) {
		List<AuditEntryDTO> auditEntryDTOs = null;
		if(OPERATION_ADD.equals(auditWorkflowStepT.getOperationType())) {
			auditEntryDTOs = Lists.newArrayList(getWorkFlowNewEntry(auditWorkflowStepT));
		} else if(OPERATION_UPDATE.equals(auditWorkflowStepT.getOperationType())) {
			auditEntryDTOs = getWorkflowUpdationEntries(auditWorkflowStepT);
		}
		return auditEntryDTOs;
	}

	/**
	 * returns workflow new request entry
	 * @param auditWorkflowStepT
	 * @return
	 */
	private AuditEntryDTO getWorkFlowNewEntry(
			AuditWorkflowStepT auditWorkflowStepT) {
		return getAuditEntry(userRepository.findUserNameByUserId(auditWorkflowStepT.getCreatedModifiedBy()),auditWorkflowStepT.getCreatedModifiedDatetime());
	}

	/**
	 * returns workflow update entry
	 * @param auditWorkflowStepT
	 * @return
	 */
	private List<AuditEntryDTO> getWorkflowUpdationEntries(
			AuditWorkflowStepT auditWorkflowStepT) {
		List<AuditEntryDTO> auditEntryDTOs = Lists.newArrayList();
		
		String createdModifiedBy = userRepository.findUserNameByUserId(auditWorkflowStepT.getCreatedModifiedBy());
		Timestamp createdModifiedDatetime = auditWorkflowStepT.getCreatedModifiedDatetime();
		auditEntryDTOs.add(getAuditEntry("Status", auditWorkflowStepT.getOldStepStatus(), 
				auditWorkflowStepT.getNewStepStatus(), createdModifiedBy, 
				createdModifiedDatetime));
		
		if(StringUtils.isNotEmpty(auditWorkflowStepT.getNewComments())) {
			auditEntryDTOs.add(getAuditEntry("Comments", null, auditWorkflowStepT.getNewComments(), createdModifiedBy, createdModifiedDatetime));
		}
		
		return auditEntryDTOs;
	}
	

	/**
	 * returns all the entries by traversing the object for the given fields
	 * @param entity
	 * @param fieldArray
	 * @param user
	 * @param date
	 * @return
	 */
	private List<AuditEntryDTO> getEntriesFromFields(
			Object entity, List<String> fieldArray, String user, Date date) {
		List<AuditEntryDTO> entryDTOs = Lists.newArrayList();
		for (String fieldName : fieldArray) {
			AuditEntryDTO entry = getEntry(entity, fieldName, user, date);
			if(entry != null) {
				entryDTOs.add(entry);
			}
		}
		return entryDTOs;
	}

	
	/**
	 * creates AuditEntryDTO with operation-NEW 
	 * @param fieldName
	 * @param fromVal
	 * @param toVal
	 * @param user
	 * @param date
	 * @return
	 */
	private AuditEntryDTO getAuditEntry(String fieldName, String fromVal, String toVal,
			String user, Date date) {
		return new AuditEntryDTO(getFieldLabel(fieldName), fromVal, toVal, user, DateUtils.truncateSeconds(date));
	}

	
	/**
	 * creates AuditEntryDTO with operation-NEW 
	 * @param user
	 * @param date
	 * @return
	 */
	private AuditEntryDTO getAuditEntry(String user, Date date) {
		return new AuditEntryDTO(user, DateUtils.truncateSeconds(date));
	}
	
	/**
	 * get the field Label for the given filed name
	 * @param fieldName
	 * @return
	 */
	private String getFieldLabel(String fieldName) {
		return FieldNameMapper.getFieldLabel(fieldName);
	}
	
	private Date truncateSeconds(Timestamp timestamps) {
		return DateUtils.truncateSeconds(new Date(timestamps.getTime()));
	}
}
