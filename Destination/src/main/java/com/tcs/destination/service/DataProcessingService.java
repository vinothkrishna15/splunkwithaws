package com.tcs.destination.service;

import static com.tcs.destination.utils.Constants.FILE_DIR_SEPERATOR;

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
import com.tcs.destination.utils.FileManager;

@Service
public class DataProcessingService {
	
	private static final Logger logger = LoggerFactory
			.getLogger(DataProcessingService.class);
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private DataProcessingRequestRepository dataProcessingRequestRepository;
	
	@Value("${fileserver.path}")
	private String fileServerPath;

	public Status saveUploadRequest(MultipartFile file, String userId, int type) throws Exception {
		
		logger.debug("Inside saveUploadRequest method:");
		
		Status status = new Status();
		
		String path = fileServerPath + getEntity(type) + FILE_DIR_SEPERATOR + DateUtils.getCurrentDate() + FILE_DIR_SEPERATOR + userId + FILE_DIR_SEPERATOR;
		
		FileManager.saveFile(file, path);
		
		DataProcessingRequestT request = new DataProcessingRequestT();
		request.setFileName(file.getOriginalFilename());
		request.setFilePath(path);
		request.setUserT(userRepository.findByUserId(userId));
		request.setStatus(RequestStatus.SUBMITTED.getStatus());
		request.setRequestType(type);
		
		dataProcessingRequestRepository.save(request);
		
		status.setStatus(Status.SUCCESS, "Upload request is submitted successfully");
		
		return status;
	}

	private String getEntity(int type) {
		String entity = "FOLDER";
	switch (type) {
	    case 1:entity="USER";
	
	    break;
	
		case 2:entity="CUSTOMER";
			
			break;
		case 3:entity="CONNECT";
		
		break;
		case 4:entity="OPPORTUNITY";
		
		break;
		case 5:entity="ACTUAL_REVENUE";
		
		break;
		case 6:entity="CUSTOMER_CONTACT";
		
		break;
		case 7:entity="PARTNER";
		
		break;
		case 8:entity="PARTNER_CONTACT";
		
		break;
		case 9:entity="BEACON";
		
		break;
		
		
		}
		return entity;
	}


	/**
	 * @param userId
	 * @param type
	 * @return
	 */
	public Status saveDownloadRequest(String userId, int type) {
		
		logger.debug("Inside saveDownloadRequest method:");
		
        Status status = new Status();
		
		DataProcessingRequestT request = new DataProcessingRequestT();
		request.setUserT(userRepository.findByUserId(userId));
		request.setStatus(RequestStatus.SUBMITTED.getStatus());
		request.setRequestType(type);
		
		dataProcessingRequestRepository.save(request);
		
		status.setStatus(Status.SUCCESS, "Download request is submitted successfully");
		
		return status;
	}

}