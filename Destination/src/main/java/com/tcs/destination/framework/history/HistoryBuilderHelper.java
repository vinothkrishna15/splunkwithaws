package com.tcs.destination.framework.history;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.tcs.destination.bean.AuditEntryDTO;
import com.tcs.destination.bean.AuditHistoryDTO;
import com.tcs.destination.bean.history.AuditHistoryTable;
import com.tcs.destination.bean.history.Field;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.DeliveryCentreRepository;
import com.tcs.destination.data.repository.DeliveryOwnershipRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.DeliveryStage;
import com.tcs.destination.enums.EntityTypeId;
import com.tcs.destination.enums.Operation;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationUtils;

@Component
public class HistoryBuilderHelper {
	
	private static final Logger logger = LoggerFactory
			.getLogger(HistoryBuilderHelper.class);

	private static final String SPAN_START_TAG ="<span class=\"span-closed-won\">";
	private static final String SPAN_END_TAG ="</span>";
	
	
/*	private static final Integer OPERATION_ADD = new Integer(1);
	private static final Integer OPERATION_UPDATE = new Integer(2);*/
	
	@Autowired
	private ApplicationContext appContext;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private DeliveryOwnershipRepository deliveryOwnershipRepo;

	@Autowired
	private DeliveryCentreRepository deliveryCentreRepo;
   
	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private PartnerRepository partnerRepository;
	
	public void fillAuditEntriesFromTable(
			AuditHistoryTable auditHistoryTable, Object entityId, List<AuditEntryDTO> auditEntryDTOs) {
		logger.info("Inside HistoryBuilderHelper:getAuditEntriesFromTable method for :: {}", auditHistoryTable.getRepository());

		List<Field> fields = auditHistoryTable.getFields().getField();
		String modifiedByField = auditHistoryTable.getModifiedByField();
		String modifiedDateField = auditHistoryTable.getModifiedDateField();
		String opTypeField = auditHistoryTable.getOperationTypeField();
		Object repo = appContext.getBean(auditHistoryTable.getRepository());
		List<?> auditRows =  (List<?>) DestinationUtils.invokeMethod(repo, auditHistoryTable.getRepoMethod(), entityId);
		if(CollectionUtils.isNotEmpty(auditRows)) {
			//inner table configs
			List<AuditHistoryTable> innerTables = auditHistoryTable.getAuditHistoryTable();
			Map<String, Set<Object>> idFieldMap = null;
			if(CollectionUtils.isNotEmpty(innerTables)) {
				idFieldMap = Maps.newHashMap();
				for (AuditHistoryTable innerTable : innerTables) {
					idFieldMap.put(innerTable.getIdField(), Sets.newHashSet());
				}
			}

			//iterate all rows
			for (Object rowObj : auditRows) {
				fillIdFieldSet(rowObj, idFieldMap);
				if(auditHistoryTable.getIsChildTable()) {
					auditEntryDTOs.addAll(getAuditEntriesForChild(rowObj,fields,modifiedByField,modifiedDateField, opTypeField));
				} else {
					auditEntryDTOs.addAll(getAuditEntries(rowObj,fields,modifiedByField,modifiedDateField));
				}
			}

			//recursive call for child tables
			if(CollectionUtils.isNotEmpty(innerTables)) {
				for (AuditHistoryTable innerTable : innerTables) {
					Set<Object> idFieldSet = idFieldMap.get(innerTable.getIdField());
					for (Object idFieldValue : idFieldSet) {
						fillAuditEntriesFromTable(innerTable, idFieldValue, auditEntryDTOs);
					}
				}
			}
		}
	}

	private void fillIdFieldSet(Object rowObj,
			Map<String, Set<Object>> idFieldMap) {
		if(idFieldMap != null) {
			for (Entry<String, Set<Object>> idField : idFieldMap.entrySet()) {
				Object filedValue = invokeGetMethod(rowObj, idField.getKey());
				idField.getValue().add(filedValue);
			}
		}
	}
	
	private List<AuditEntryDTO> getAuditEntries(
			Object obj, List<Field> fields, String modifiedByField,
			String modifiedDateField) {
		List<AuditEntryDTO> entryDTOs = Lists.newArrayList();
		String modifiedUserName = getUserName(obj,modifiedByField);
		Date modifiedDate = getDate(obj, modifiedDateField);
		if(StringUtils.isNotEmpty(modifiedUserName) && modifiedDate != null) {
			entryDTOs.addAll(getEntriesFromFields(obj, fields, modifiedUserName, modifiedDate));
		}
		return entryDTOs;
	}
	

	private List<AuditEntryDTO> getAuditEntriesForChild(
			Object obj, List<Field> fields, String modifiedByField,
			String modifiedDateField, String opTypeField) {
		// TODO Auto-generated method stub
		List<AuditEntryDTO> entryDTOs = Lists.newArrayList();
		String modifiedUserName = getUserName(obj,modifiedByField);
		Date modifiedDate = getDate(obj, modifiedDateField);
		Operation operationType = getOperationType(obj, opTypeField);
		if(StringUtils.isNotEmpty(modifiedUserName) && modifiedDate != null && operationType != null) {
			AuditEntryDTO entry = getEntryByOperation(obj, fields, operationType, modifiedUserName, modifiedDate);
			if(entry != null) {
				entryDTOs.add(entry);
			}
		}
		return entryDTOs;
	}
	
	private Operation getOperationType(Object obj, String opTypeField) {
		Object opType = invokeGetMethod(obj, opTypeField);
		if(opType != null) {
			return Operation.getByCode((Integer)opType);
		}
		return null;
	}

	private Date getDate(Object obj, String modifiedDateField) {
		Object userId = invokeGetMethod(obj, modifiedDateField);
		if(userId != null) {
			return new Date(((Timestamp)userId).getTime());
		}
		return null;
	}

	private String getUserName(Object obj, String modifiedByField) {
		Object userId = invokeGetMethod(obj, modifiedByField);
		if(userId != null) {
			return userRepository.findUserNameByUserId((String)userId);
		}
		return null;
	}
	
	
	
	/**
	 * returns all the entries by traversing the object for the given fields
	 * @param entity
	 * @param fieldArray
	 * @param user
	 * @param date
	 * @param fieldMap - map of fields where 
	 * @return
	 */
	private List<AuditEntryDTO> getEntriesFromFields(
			Object entity, List<Field> fieldList, String user, Date date) {
		List<AuditEntryDTO> entryDTOs = Lists.newArrayList();
		for (Field field : fieldList) {
			AuditEntryDTO entry = getEntry(entity, field, user, date);
			if(entry != null) {
				entryDTOs.add(entry);
			}
		}
		return entryDTOs;
	}
	
	private AuditEntryDTO getEntryByOperation(Object obj, List<Field> fields, Operation operationType,
			String user, Date date) {
		AuditEntryDTO entry = null;
		Field field = fields.get(0);
		Object fieldValue = invokeGetMethod(obj, field.getName());
		if(fieldValue != null && !Constants.EMPTY_STRING.equals(fieldValue)) {
			if(operationType == Operation.ADD || operationType == Operation.UPDATE) {
				entry = getAuditEntry(field, null, fieldValue, user, date);
			} else { //removed
				entry = getAuditEntry(field, fieldValue, null, user, date);
			}
		}
		return entry;
	}

	/**
	 * creates a {@link AuditEntryDTO},comparing old and new by calling 'getOld' and 'getNew' methods for the provided field name
	 * using reflection to call getter methods
	 * @param obj
	 * @param fieldName
	 * @param user
	 * @param date
	 * @param fieldType 
	 * @return
	 */
	public AuditEntryDTO getEntry(Object obj, Field field, String user, Date date) {
		
		String fieldName = field.getName();
		Object oldVal = DestinationUtils.invokeMethod(obj, "getOld"+fieldName);
		Object newVal = DestinationUtils.invokeMethod(obj, "getNew"+fieldName);

		if(!ObjectUtils.equals(oldVal, newVal)) {
			return getAuditEntry(field, oldVal,	newVal, user, date);
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
	public List<AuditEntryDTO> getEntry(Object oldObj, Object newObj, List<String> fieldNames, String user, Date date) {
		List<AuditEntryDTO> entries = Lists.newArrayList();
		for (String fieldName : fieldNames) {
			Object oldVal = invokeGetMethod(oldObj, fieldName);
			Object newVal = invokeGetMethod(newObj, fieldName);
			if(!ObjectUtils.equals(oldVal, newVal)) {
				entries.add(getAuditEntry(fieldName, (oldVal != null )? String.valueOf(oldVal): null, 
						(newVal != null )? String.valueOf(newVal): null, user, date));
			}

		}
		return entries;
	}

	
	private AuditEntryDTO getAuditEntry(Field field, Object oldVal, Object newVal, 
			String user, Date date) {
		String dispOldVal = null;
		String dispNewVal = null;
		
		String fieldType = field.getType();
		if(fieldType != null) {
			switch (FieldType.getByName(fieldType)) {
			case USER_ID:
				dispOldVal = oldVal != null ? userRepository.findUserNameByUserId(String.valueOf(oldVal)) : null;
				dispNewVal = newVal != null ? userRepository.findUserNameByUserId(String.valueOf(newVal)) : null;
				break;
			case DELIVERY_CENTRE:
				dispOldVal = oldVal != null ? deliveryCentreRepo.findByDeliveryCentreId((Integer)oldVal).getDeliveryCentre() : null;
				dispNewVal = newVal != null ? deliveryCentreRepo.findByDeliveryCentreId((Integer)newVal).getDeliveryCentre() : null;
				break;
			case DELIVERY_STAGE:
				dispOldVal = oldVal != null ? DeliveryStage.byStageCode((Integer) oldVal).getStageName() : null;
				dispNewVal = newVal != null ? DeliveryStage.byStageCode((Integer) newVal).getStageName() : null;
				break;
			case OWNERSHIP_ID:
				Integer n2 = (Integer) newVal;
				dispOldVal = oldVal != null ? deliveryOwnershipRepo.findOne((Integer) oldVal).getOwnership() : null;
				dispNewVal = newVal != null ? deliveryOwnershipRepo.findOne(n2).getOwnership() : null;
				break;
			case CONTACT_ID:
				dispOldVal = oldVal != null ? contactRepository.findOne(String.valueOf(oldVal)).getContactName() : null;
				dispNewVal = newVal != null ? contactRepository.findOne(String.valueOf(newVal)).getContactName() : null;
				break;
			case PARTNER_ID:
				dispOldVal = oldVal != null ? partnerRepository.findOne(String.valueOf(oldVal)).getPartnerName() : null;
				dispNewVal = newVal != null ? partnerRepository.findOne(String.valueOf(newVal)).getPartnerName() : null;
				break;

			default:
				dispOldVal = (oldVal != null ) ? String.valueOf(oldVal): null;
				dispNewVal = (newVal != null ) ? String.valueOf(newVal): null;
			}
		} else {
			dispOldVal = (oldVal != null ) ? String.valueOf(oldVal): null;
			dispNewVal = (newVal != null ) ? String.valueOf(newVal): null;
		}
		
		
		return getAuditEntry(field.getLabel(), dispOldVal, dispNewVal, user, date);
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
	public AuditEntryDTO getAuditEntry(String fieldLable, String fromVal, String toVal,
			String user, Date date) {
		return new AuditEntryDTO(fieldLable, fromVal, toVal, user, DateUtils.truncateSeconds(date));
	}

	private Object invokeGetMethod(Object obj, String fieldName) {
		return DestinationUtils.invokeMethod(obj, "get" + fieldName);
	}

	
	/**
	 * group the entries by date and user
	 * @param entries
	 * @param entityTypeId
	 * @return
	 */
	public List<AuditHistoryDTO> groupAuditHistory(
			List<AuditEntryDTO> entries) {
		List<AuditHistoryDTO> history = Lists.newArrayList();
		Map<String, List<AuditEntryDTO>> groupEntries= groupEntries(entries);
		
		for (Entry<String, List<AuditEntryDTO>> entryGroup : groupEntries.entrySet()) {
			history.add(constructAuditHistory(entryGroup.getValue()));
		}
		
		return history;
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
	 * creates audit history with date, user and list of messages
	 * @param entries
	 * @param entityTypeId
	 * @return
	 */
	private AuditHistoryDTO constructAuditHistory(List<AuditEntryDTO> entries) {
		AuditHistoryDTO history = new AuditHistoryDTO();
		List<String> messages = Lists.newArrayList();
		if(CollectionUtils.isNotEmpty(entries)) {
			history.setDate(entries.get(0).getDate());
			history.setUserName(entries.get(0).getUser());
			history.setOperation(getOperation(entries.get(0).getOperation()));
			for (AuditEntryDTO entry : entries) {
				messages.add(constructMessage(entry));
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
	private String constructMessage(AuditEntryDTO entry) {
		String message;
		if(entry.getFromVal() == null && entry.getToVal() != null) {
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
		StringBuffer sb = new StringBuffer();
		sb.append(EntityTypeId.getFrom(entityTypeId).getDisplayName()).append(" Request");
		
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
		sb.append(fieldName).append(": ").append(SPAN_START_TAG).append(toVal).append(SPAN_END_TAG);
		
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
		sb.append(fieldName).append(" : ").append(SPAN_START_TAG).append(fromVal).append(SPAN_END_TAG);
		
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
		sb.append(fieldName).append(" from ").append(SPAN_START_TAG).append(fromVal).append(SPAN_END_TAG).append(" to ").append(SPAN_START_TAG).append(toVal).append(SPAN_END_TAG);
		
		return sb.toString();
	}
}
