package com.tcs.destination.framework.history;

import java.io.File;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.tcs.destination.bean.AuditEntryDTO;
import com.tcs.destination.bean.history.AuditHistoryEntities;
import com.tcs.destination.bean.history.AuditHistoryEntity;
import com.tcs.destination.bean.history.AuditHistoryTable;
import com.tcs.destination.exception.DestinationException;

@Component
public class HistoryBuilder implements IHistoryBuilder {

	private static final Logger logger = LoggerFactory
			.getLogger(HistoryBuilder.class);
	
	@Autowired
	private AuditHistoryEntities auditHistoryEntities; 
	
	@Autowired
	private HistoryBuilderHelper builderHelper;
	
	@Override
	public List<AuditEntryDTO> getAuditEntries(String type, Object entityId) {
		List<AuditEntryDTO> auditEntryDTOs = Lists.newArrayList();
		if(auditHistoryEntities != null) {
			//Gets the respective configuration for the type given
			AuditHistoryEntity historyEntityConfig = getHistoryEntityConfig(type);
			
			List<AuditHistoryTable> auditHistoryTables = historyEntityConfig.getAuditHistoryTable();
			if(CollectionUtils.isNotEmpty(auditHistoryTables)) {
				for (AuditHistoryTable auditHistoryTable : auditHistoryTables) {
					List<AuditEntryDTO> auditDtos = Lists.newArrayList();
					builderHelper.fillAuditEntriesFromTable(auditHistoryTable, entityId, auditDtos);
					auditEntryDTOs.addAll(auditDtos);
				}
			}
			
		} else {
			throw new DestinationException("Configuration not found");
		}

		return auditEntryDTOs;
	}

	private AuditHistoryEntity getHistoryEntityConfig(String type) {
		AuditHistoryEntity historyEntityConfig = null;
		List<AuditHistoryEntity> auditHistoryEntityList = auditHistoryEntities.getAuditHistoryEntity();
		for (AuditHistoryEntity auditHistoryEntity : auditHistoryEntityList) {
			if(StringUtils.equals(auditHistoryEntity.getType(), type)) {
				historyEntityConfig = auditHistoryEntity;
				break;
			}
		}
		if(historyEntityConfig == null) {
			throw new DestinationException("Configuration not found for the given type");
		} 
		return historyEntityConfig;
	}
	
	@Bean
	public AuditHistoryEntities getAuditHistoryEntities() {
		JAXBContext jaxbContext;
		AuditHistoryEntities auditHistoryEntities = null;
		try {
			jaxbContext = JAXBContext.newInstance(AuditHistoryEntities.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			auditHistoryEntities = (AuditHistoryEntities) jaxbUnmarshaller.
					unmarshal(getClass().getResourceAsStream("/history/history-config.xml"));
		} catch (JAXBException e) {
			logger.error("history XML parsing exception", e);
		}
		return auditHistoryEntities;
	}

}
