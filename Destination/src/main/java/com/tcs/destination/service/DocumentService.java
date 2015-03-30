package com.tcs.destination.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.DocumentRepositoryT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.DocumentRepository;
import com.tcs.destination.exception.DestinationException;

@Component
public class DocumentService {

	@Autowired 
	DocumentRepository documentRepository;
	
	@Value("${fileBaseDir}")
	private String fileBasePath;
	
//	public int save(DocumentRepositoryT docrep) 
//	{
//		DocumentRepositoryT documentrep=documentRepository.save(docrep);
//		if(documentrep != null)
//		    return 1;
//		else
//			return 0;
//	}

	public DocumentRepositoryT findByDocumentId(String documentId) throws Exception{
		DocumentRepositoryT docRep = documentRepository.findByDocumentId(documentId);
		if(docRep==null){
			throw new DestinationException(HttpStatus.NOT_FOUND,"No Relevent Data/document Found in the database");
		}
		return docRep;
	}
	
	public String saveDocument(String documentName,
			 String documentType,
			String entityType, String parentEntity, String parentEntityId,
			String commentId, String connectId, String customerId,
			String opportunityId, String partnerId, String taskId,
			String uploadedBy, MultipartFile file) throws IOException {
			DocumentRepositoryT document=new DocumentRepositoryT();
			document.setCommentId(commentId);
			document.setConnectId(connectId);
			document.setCustomerId(customerId);
			document.setDocumentName(documentName);
			//document.setDocumentSearchKeywords(documentSearchKeywords);
			document.setDocumentType(documentType);
			document.setEntityType(entityType);
			document.setFileReference(fileBasePath);
			document.setOpportunityId(opportunityId);
			document.setParentEntity(parentEntity);
			document.setParentEntityId(parentEntityId);
			document.setPartnerId(partnerId);
			document.setTaskId(taskId);
			UserT user=new UserT();
			user.setUserId(uploadedBy);
			document.setUserT(user);
			document.setUploadedDatetime(new Timestamp(Calendar.getInstance().getTimeInMillis()));

			//document.setFileReference(file.getBytes());
			/**/
			
			String docId = "";
			if(documentRepository.save(document)!=null){
				docId = document.getDocumentId();
				String entityId = getEntityId(connectId,opportunityId,taskId);
				String saveDirLoc = getPathFromForm(entityType,entityId);
				if (!file.isEmpty()) {
					saveFile(file,saveDirLoc,docId);
					String fileName = file.getOriginalFilename();
					String fileExtension = fileName.substring(fileName.lastIndexOf("."), fileName.length());
					document.setFileReference(saveDirLoc + docId + fileExtension);
					if(documentRepository.save(document)!=null){
						return document.getDocumentId();
					}
				} else {
		            return "FAILED";
		        }
			}
			return "FAILED";
			
	}

	
	private void saveFile(MultipartFile file, String saveDirLoc, String docId) throws IOException {
		try {
			byte[] bytes = file.getBytes();
			File dir = new File(saveDirLoc);
			if (!dir.exists())
	            dir.mkdirs();
			
			String fileName = file.getOriginalFilename();
			String saveFileName = docId;
            String fileExtension = fileName.substring(fileName.lastIndexOf("."), fileName.length());
			
			File serverFile = new File(dir.getAbsolutePath()
                    + File.separator+saveFileName+fileExtension);
            BufferedOutputStream stream = new BufferedOutputStream(
                    new FileOutputStream(serverFile));
            stream.write(bytes);
            stream.close();
            
		} catch (IOException e) {
			throw e;
		}
		
		
		
	}

	private String getEntityId(String connectId, String opportunityId,
			String taskId) {
		
		if(!connectId.isEmpty()){
			return connectId;
		} else if(!opportunityId.isEmpty()){
			return opportunityId;
		} else {
			return taskId;
		}
		
		//return null;
	}

	private String getPathFromForm(String entityType,String entityId){
		StringBuffer saveDir = new StringBuffer("");
		saveDir.append(fileBasePath);
		saveDir.append(File.separator);
		saveDir.append(entityType);
		saveDir.append(File.separator);
		saveDir.append(entityId);
		saveDir.append(File.separator);
		return saveDir.toString();
	}

	@Transactional
	public String deleteDocRecords(String[] docIds){
		StringBuffer deletedRecords = new StringBuffer("");
		int index = 0;
		for(String docId : docIds){
			index++;
			DocumentRepositoryT docRep = documentRepository.findByDocumentId(docId);
			//getDocumentPath(connectDoc.getDocumentId());
			if(docRep!=null){
			String fullPath = docRep.getFileReference();
			documentRepository.delete(docRep);
			deleteFile(fullPath);
			deletedRecords.append(docId);
			if(index < docIds.length){
			deletedRecords.append(",");
			} 
			}
		}
		return deletedRecords.toString();
	}
		
		public void deleteFile(String fullPath) {
	    	try{
	    		 //String fullPath = fileBasePath+File.separator+"my.pdf";
	    		    File file = new File(fullPath);
	    		    if(file.delete()){
	        			System.out.println(file.getName() + " is deleted!");
	        		}else{
	        			System.out.println("Delete operation is failed.");
	        		}
	    	    } catch (Exception ex) {
	    	      //logger.info("Error writing file to output stream. Filename was '{}'", "", ex);
	    	      throw new RuntimeException("Delete operation is failed.");
	    	    }
	    }
	
	
	
}
