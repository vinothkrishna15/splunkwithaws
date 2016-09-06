package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.CollaborationCommentT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.DocumentsT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.PageDTO;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.TaskT;
import com.tcs.destination.data.repository.CollaborationCommentsRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.DocumentsTRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.TaskRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.PaginationUtils;
import com.tcs.destination.utils.StringUtils;

/**
 * This service is used to save and delete documents
 * 
 * @author TCS
 *
 */
@Service
public class DocumentsService {

	private static final Logger logger = LoggerFactory
			.getLogger(DocumentsService.class);
	
	@Autowired
	DocumentsTRepository documentsTRepository;
	
	@Autowired
	ConnectRepository connectRepository;
	
	@Autowired
	OpportunityRepository opportunityRepository;
	
	@Autowired
	TaskRepository taskRepository;
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	PartnerRepository partnerRepository;
	
	@Autowired
	CollaborationCommentsRepository commentsRepository;

	/**
	 * This method is used to find the document using id
	 * @param documentId
	 * @return
	 * @throws Exception
	 */
	public DocumentsT findByDocumentId(String documentId) throws Exception{
		logger.debug("Start: Inside  findByDocumentId() of DocumentService");
		DocumentsT docRep = documentsTRepository.findOne(documentId);
		if(docRep==null){
			logger.error("NOT_FOUND: No Relevent Data/document Found in the database");
			throw new DestinationException(HttpStatus.NOT_FOUND,"No Relevent Data/document Found in the database");
		}
		logger.debug("End: Inside  findByDocumentId() of DocumentService");
		return docRep;
	}
	
	/**
	 * Method used to create a document
	 * @param documentsT
	 * @param status
	 * @throws Exception
	 */
	public void createDocument(DocumentsT documentsT, Status status) throws Exception{
      	logger.info("DocumentsService - inside createDocument start");
		validateDocumentsT(documentsT);
		documentsT = documentsTRepository.save(documentsT);
		status.setStatus(Status.SUCCESS, documentsT.getDocumentsId() + " : " + documentsT.getDocName() + " is Saved !!" );	
		logger.info("DocumentsService - inside createDocument end");
	}

	
	/**
	 * Method to validate documents Object
	 * @param documentsT
	 */
	private void validateDocumentsT(DocumentsT documentsT) {
		logger.info("DocumentsService - inside validateDocumentsT start");
		if(documentsT!=null){
			String entityType = documentsT.getEntityType();
			String entityId = documentsT.getEntityId();
			String docType = documentsT.getDocType();
			String docName = documentsT.getDocName();
			byte[] docContent = documentsT.getDocContent();
			String createdBy = DestinationUtils.getCurrentUserDetails().getUserId();
			documentsT.setCreatedBy(createdBy);
			String modifiedBy = DestinationUtils.getCurrentUserDetails().getUserId();
			documentsT.setModifiedBy(modifiedBy);
			
			if(StringUtils.isEmpty(entityType)){
				logger.error("Entity Type is mandatory");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Entity Type is mandatory");
			} else {
				switch (EntityType.valueOf(entityType)) {
				case CONNECT:
					if (entityId == null) {
						logger.error("BAD_REQUEST: Entity ID cannot be Empty");
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"Entity ID(Connect Id) cannot be Empty");
					} else {
						ConnectT connect = connectRepository.findOne(entityId);
						if(connect == null) {
							logger.error("BAD_REQUEST: Invalid EntityId [ConnectId]");
							throw new DestinationException(HttpStatus.BAD_REQUEST,
									"Invalid EntityId [ConnectId]");
						}
					}
					break;
					
				case OPPORTUNITY:
					if (entityId == null) {
						logger.error("BAD_REQUEST: Entity ID cannot be Empty");
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"Entity ID(Opportunity Id) cannot be Empty");
					} else {
						OpportunityT opportunityT = opportunityRepository.findOne(entityId);
						if(opportunityT == null) {
							logger.error("BAD_REQUEST: Invalid EntityId [opportunityId]");
							throw new DestinationException(HttpStatus.BAD_REQUEST,
									"Invalid EntityId [opportunityId]");
						}
					}
					break;
					
				case TASK:
					if (entityId == null) {
						logger.error("BAD_REQUEST: Entity ID cannot be Empty");
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"Entity ID(Task Id) cannot be Empty");
					} else {
						TaskT taskT = taskRepository.findOne(entityId);
						if(taskT == null) {
							logger.error("BAD_REQUEST: Invalid EntityId [taskId]");
							throw new DestinationException(HttpStatus.BAD_REQUEST,
									"Invalid EntityId [taskId]");
						}
					}
					break;
					
				case COMMENT:
					if (entityId == null) {
						logger.error("BAD_REQUEST: Entity ID cannot be Empty");
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"Entity ID(Comment Id) cannot be Empty");
					} else {
						CollaborationCommentT commentsT = commentsRepository.findOne(entityId);
						if(commentsT == null) {
							logger.error("BAD_REQUEST: Invalid EntityId [commentsId]");
							throw new DestinationException(HttpStatus.BAD_REQUEST,
									"Invalid EntityId [commentsId]");
						}
					}
					break;
					
				case CUSTOMER:
					if (entityId == null) {
						logger.error("BAD_REQUEST: Entity ID cannot be Empty");
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"Entity ID(Customer Id) cannot be Empty");
					} else {
						CustomerMasterT customer = customerRepository.findOne(entityId);
						if(customer == null) {
							logger.error("BAD_REQUEST: Invalid EntityId [customerId]");
							throw new DestinationException(HttpStatus.BAD_REQUEST,
									"Invalid EntityId [customerId]");
						}
					}
					break;
					
				case PARTNER:
					if (entityId == null) {
						logger.error("BAD_REQUEST: Entity ID cannot be Empty");
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"Entity ID(Partner Id) cannot be Empty");
					} else {
						PartnerMasterT partner = partnerRepository.findOne(entityId);
						if(partner == null) {
							logger.error("BAD_REQUEST: Invalid EntityId [partnerId]");
							throw new DestinationException(HttpStatus.BAD_REQUEST,
									"Invalid EntityId [partnerId]");
						}
					}
					break;
					
				default: {
					logger.error("BAD_REQUEST: Invalid Entity Type");
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Invalid Entity Type");
				}
				
				}
			}
			
			if(StringUtils.isEmpty(docType)){
				logger.error("Document Type is mandatory");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Document Type is mandatory");
			}
			
			if(StringUtils.isEmpty(docName)){
				logger.error("Document Name is mandatory");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Document Name is mandatory");
			} else {
				// under the assumption that same file's next version will be saved under same name
				List<DocumentsT> documentsTList = documentsTRepository.findByDocName(docName);
			    documentsT.setVersion(documentsTList.size()+1);
			}
			
			if(docContent == null){
				logger.error("Document Content is mandatory");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Document Content is mandatory");
			}
			
			
		} else {
			logger.error("Invalid Request");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Request");
		}
			
		  
		  logger.info("DocumentsService - inside validateDocumentsT end");
	}
	
	/**
	 * This service is used to retrieve the document list of the logged in user
	 * @param page
	 * @param count
	 * @return
	 */
	public PageDTO<DocumentsT> getMyDocumentList(int page, int count) throws DestinationException {
		
		logger.debug("getMyDocumentList:Start of retrieving document list");
		PageDTO<DocumentsT> documentResponse = new PageDTO<DocumentsT>();
		List<DocumentsT> documents = new ArrayList<DocumentsT>();
	    documents= (List<DocumentsT>)documentsTRepository.findByEntityTypeNotIn("Weekly Report");
	    documents = paginateDocumentList(page, count, documents);
	    documentResponse.setTotalCount(documents.size());
		documentResponse.setContent(documents);
		logger.debug("getMyDocumentList:End of retrieving document list");
		return documentResponse;
	
	}
	
	/**
	 * This method performs pagination for the getMyDocumentList service
	 * 
	 * @param page
	 * @param count
	 * @param documents
	 * @return
	 */
	private List<DocumentsT> paginateDocumentList(int page, int count,
			List<DocumentsT> documents) {
		if (PaginationUtils.isValidPagination(page, count, documents.size())) {
			int fromIndex = PaginationUtils.getStartIndex(page, count,
					documents.size());
			int toIndex = PaginationUtils.getEndIndex(page, count,
					documents.size()) + 1;
			documents = documents.subList(fromIndex, toIndex);
			logger.debug("My documents after pagination size is "
					+ documents.size());
		} else {
			documents = null;
		}
		return documents;
	}


	
	/**
	 * Method used to download file
	 * @param documentsId
	 * @return
	 */
	public DocumentsT downloadFile(String documentsId) {
		
		if(!StringUtils.isEmpty(documentsId)){
		DocumentsT documentsT = documentsTRepository.findOne(documentsId);
		 if(documentsT == null){
			 logger.error("Invalid document Id");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid document Id");
		 } else {
            return documentsT;			 
		 }
		} else {
			logger.error("Document Id is mandatory");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Document Id is mandatory");
		}
		
	}

}
