package com.tcs.destination.framework.history;

import java.util.List;

import com.tcs.destination.bean.AuditEntryDTO;

public interface IHistoryBuilder {

	List<AuditEntryDTO> getAuditEntries(String type,Object entityId);
}
