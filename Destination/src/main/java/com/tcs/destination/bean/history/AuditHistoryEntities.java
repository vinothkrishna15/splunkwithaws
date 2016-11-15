package com.tcs.destination.bean.history;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AuditHistoryEntities {

	private List<AuditHistoryEntity> auditHistoryEntity;

	@XmlElement
	public List<AuditHistoryEntity> getAuditHistoryEntity() {
		return auditHistoryEntity;
	}

	public void setAuditHistoryEntity(List<AuditHistoryEntity> auditHistoryEntity) {
		this.auditHistoryEntity = auditHistoryEntity;
	}

/*	public static void main(String[] args) {
		File file = new File("/Users/PocCoe/Desktop/Raz/code/nP1Check/Destination/src/main/resources/history/history-config.xml");
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(AuditHistoryEntities.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			AuditHistoryEntities auditHistoryEntities = (AuditHistoryEntities) jaxbUnmarshaller.
					unmarshal(file);
		System.out.println("printedd "+auditHistoryEntities.getAuditHistoryEntity());
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}*/
	
}
