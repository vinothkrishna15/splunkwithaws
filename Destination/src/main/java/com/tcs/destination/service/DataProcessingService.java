package com.tcs.destination.service;

import static com.tcs.destination.utils.Constants.FILE_DIR_SEPERATOR;
import static com.tcs.destination.utils.Constants.UPLOAD;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.FileManager;

/**
 * This service is used to process the batch requests being provided
 * 
 */
@Service("dataProcessingService")
public class DataProcessingService {
	
	private static final Logger logger = LoggerFactory
			.getLogger(DataProcessingService.class);
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DataProcessingRequestRepository dataProcessingRequestRepository;
	
	@Value("${fileserver.path}")
	private String fileServerPath;

	/**
	 * This method is used to save the upload requests being provided for batch operations
	 * @param file
	 * @param type
	 * @param deleteTo 
	 * @param deleteFrom 
	 * @return
	 * @throws Exception
	 */
	public Status saveUploadRequest(MultipartFile file, int type, String deleteFrom, String deleteTo) throws Exception {
		
		logger.debug("Start:Inside saveUploadRequest method of DataProcessing Service:");
		
		String userId=DestinationUtils.getCurrentUserDetails().getUserId();
		
		Status status = new Status();
		
		String entity = getEntity(type);
		
		String path = new StringBuffer(fileServerPath)
		.append(entity).append(FILE_DIR_SEPERATOR).append(UPLOAD)
		.append(FILE_DIR_SEPERATOR)
		.append(DateUtils.getCurrentDate())
		.append(FILE_DIR_SEPERATOR)
		.append(userId)
		.append(FILE_DIR_SEPERATOR).toString();
		
		FileManager.saveFile(file, path);
		
		DataProcessingRequestT request = new DataProcessingRequestT();
		request.setFileName(file.getOriginalFilename());
		request.setFilePath(path);
		request.setUserT(userRepository.findByUserId(userId));
		request.setStatus(RequestStatus.SUBMITTED.getStatus());
		request.setDeleteFrom(deleteFrom);
		request.setDeleteTo(deleteTo);
		request.setRequestType(type);		
		dataProcessingRequestRepository.save(request);
		
		status.setStatus(Status.SUCCESS, "Upload request is submitted successfully");
		
		logger.debug("End: saveUploadRequest method of DataProcessing Service:");
		
		return status;
	}

	/**
	 * This method is used to fetch the entity type
	 * @param type
	 * @return String
	 */
	public String getEntity(int type) {
		
		String entity = "FOLDER";
		
		switch (type) {
		
		case 1: entity = EntityType.USER.name();
		break;
		case 2: entity = EntityType.CUSTOMER.name();
		break;
		case 3: entity = EntityType.CONNECT.name();
		break;
		case 4: entity = EntityType.OPPORTUNITY.name();
		break;
		case 5: entity = EntityType.ACTUAL_REVENUE.name();
		break;
		case 6: entity = EntityType.CUSTOMER_CONTACT.name();
		break;
		case 7: entity = EntityType.PARTNER.name();
		break;
		case 8: entity = EntityType.PARTNER_CONTACT.name();
		break;
		case 9: entity = EntityType.BEACON.name();
		break;
		case 10: entity = EntityType.USER.name();
		break;
		case 11: entity = EntityType.CUSTOMER.name();
		break;
		case 12: entity = EntityType.CONNECT.name();
		break;
		case 13: entity = EntityType.OPPORTUNITY.name();
		break;
		case 14: entity = EntityType.ACTUAL_REVENUE.name();
		break;
		case 15: entity = EntityType.CUSTOMER_CONTACT.name();
		break;
		case 16: entity = EntityType.PARTNER.name();
		break;
		case 17: entity = EntityType.PARTNER_CONTACT.name();
		break;
		case 18: entity = EntityType.BEACON.name();
		break;
		
		}
		return entity;
	}

	/**
	 * This method is used to save the download requests being provided for batch operations
	 * @param userId
	 * @param type
	 * @return
	 */
	public Status saveDownloadRequest(int type) {
		
		String userId=DestinationUtils.getCurrentUserDetails().getUserId();
		logger.debug("Start :Inside saveDownloadRequest method of DataProcessing Service ");
		
        Status status = new Status();
		
		DataProcessingRequestT request = new DataProcessingRequestT();
		request.setUserT(userRepository.findByUserId(userId));
		request.setStatus(RequestStatus.SUBMITTED.getStatus());
		request.setRequestType(type);
		
		dataProcessingRequestRepository.save(request);
		
		status.setStatus(Status.SUCCESS, "Download request is submitted successfully");
		logger.debug("End :saveDownloadRequest method of DataProcessing Service");
		return status;
	}

}