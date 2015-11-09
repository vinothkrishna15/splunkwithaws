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
import com.tcs.destination.enums.RequestType;
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
		String path = null;
		Status status = new Status();
		
		if(type == RequestType.CONNECT_UPLOAD.getType()){
		path = fileServerPath + EntityType.CONNECT.name() + FILE_DIR_SEPERATOR + DateUtils.getCurrentDate() + FILE_DIR_SEPERATOR + userId + FILE_DIR_SEPERATOR;
		}
		else if(type == RequestType.OPPORTUNITY_UPLOAD.getType()){
		path = fileServerPath + EntityType.OPPORTUNITY.name() + FILE_DIR_SEPERATOR + DateUtils.getCurrentDate() + FILE_DIR_SEPERATOR + userId + FILE_DIR_SEPERATOR;
		}
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