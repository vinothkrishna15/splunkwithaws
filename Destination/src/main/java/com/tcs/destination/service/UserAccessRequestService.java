package com.tcs.destination.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.destination.bean.UserAccessRequestT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.UserAccessRequestRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationMailUtils;
import com.tcs.destination.utils.StringUtils;

@Service
public class UserAccessRequestService {

	private static final Logger logger = LoggerFactory
			.getLogger(UserAccessRequestService.class);

	@Autowired
	UserAccessRequestRepository userAccessReqRepo;

	@Autowired
	DestinationMailUtils mailUtils;

	@Value("${userAccess}")
	private String userAccessSubject;

	@Autowired
	ThreadPoolTaskExecutor mailTaskExecutor;

	@Autowired
	UserRepository userRepository;

	public UserAccessRequestT findUserRequestById(String reqId)
			throws Exception {
		logger.debug("Inside searchforfeedbacksById service");
		UserAccessRequestT newUserRequest = userAccessReqRepo.findOne(reqId);
		if (newUserRequest != null) {
			return newUserRequest;
		} else {
			logger.error("NOT_FOUND: user Request not found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"user Request not found");
		}
	}

	@Transactional
	public boolean insertUserRequest(UserAccessRequestT userAccessRequest)
			throws Exception {
		logger.debug("Inside insertUserRequest Service");
		validateRequest(userAccessRequest, false);
		if (userAccessReqRepo.save(userAccessRequest) != null) {
			logger.debug("User Request Record Inserted");
			// send notification to admin,supervisor and user on saving the
			// request
			sendEmailNotification(userAccessRequest.getRequestId(), new Date());
			return true;
		}
		return false;
	}

	private void sendEmailNotification(String requestId, Date date)
			throws Exception {
		class UserAccessNotificationRunnable implements Runnable {
			String requestId;
			Date date;

			UserAccessNotificationRunnable(String requestId, Date date) {
				this.requestId = requestId;
				this.date = date;
			}

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					mailUtils.sendUserAccessAutomatedEmail(userAccessSubject,
							requestId, date);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage());
				}
			}

		}
		UserAccessNotificationRunnable userAccessNotificationRunnable = new UserAccessNotificationRunnable(
				requestId, date);
		mailTaskExecutor.execute(userAccessNotificationRunnable);
	}

	private void validateRequest(UserAccessRequestT userAccessRequest,
			boolean isUpdate) throws Exception {

		if (StringUtils.isEmpty(userAccessRequest.getUserId())) {
			logger.error("UserId is required");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"UserId is required");
		} else {
			UserT user = userRepository.findByUserId(userAccessRequest
					.getUserId());
			if (user != null) {
				logger.error("User already exists");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"User already exists");
			}
		}

		if (StringUtils.isEmpty(userAccessRequest.getUserEmailId())) {
			logger.error("User Email Id is required");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"User Email Id is required");
		} else {
			UserT user = userRepository.findByUserEmailId(userAccessRequest.getUserEmailId());
			if (user != null) {
				logger.error("EmailId already exists");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"EmailId already exists");
			}
		}

		if (StringUtils.isEmpty(userAccessRequest.getSupervisorId())) {
			logger.error("Supervisor Id is required");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Supervisor Id is required");
		}

		if (StringUtils.isEmpty(userAccessRequest.getSupervisorEmailId())) {
			logger.error("Supervisor Email Id is required");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Supervisor Email Id is required");
		}
		
		// Validate supervisor
	/*	UserT user = userRepository.
				findByUserIdAndUserEmailId(userAccessRequest.getSupervisorId(), userAccessRequest.getSupervisorEmailId());
		if (user == null) {
			logger.error("Supervisor not found");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Supervisor not found");
		}
		*/

		if (StringUtils.isEmpty(userAccessRequest.getReasonForRequest())) {
			logger.error("Reason for request is required");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Reason for request is required");
		}

		if (isUpdate) {
			if (StringUtils.isEmpty(userAccessRequest.getApprovedRejectedBy())) {
				logger.error("Approver User Id is required");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Approver User Id is required");
			}

			if (StringUtils.isEmpty(userAccessRequest.getApprovedRejectedComments())) {
				logger.error("Approver Comments is required");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Approver Comments is required");
			}
			// Set current timestamp
			userAccessRequest.setApprovedRejectedDate(DateUtils.getCurrentTimeStamp());
		}

	}

	@Transactional
	public boolean editUserRequest(UserAccessRequestT userAccessRequest)
			throws Exception {
		logger.debug("Inside editUserRequest Service");
		validateRequest(userAccessRequest, true);
		if (userAccessReqRepo.save(userAccessRequest) != null) {
			logger.debug("userRequest Record edited");
			return true;
		}
		return false;
	}

	public List<UserAccessRequestT> findAllUserAccessRequests() {
		return (List<UserAccessRequestT>) userAccessReqRepo.findAll(new Sort(
				Sort.Direction.DESC, "requestReceivedDate"));
	}
}