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
import com.tcs.destination.utils.DestinationMailUtils;

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

		if (userAccessRequest.getUserId() == null) {
			logger.error("Missing UserId");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Missing UserId");
		} else {
			UserT user = userRepository.findByUserId(userAccessRequest
					.getUserId());
			if (user != null)
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"You already have access to the system");
		}

		if (userAccessRequest.getUserEmailId() == null) {
			logger.error("Missing User Email Id");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Missing User Email Id");
		}

		if (userAccessRequest.getSupervisorId() == null) {
			logger.error("Missing Supervisor Id");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Missing Supervisor Id");
		}

		if (userAccessRequest.getSupervisorId() == null) {
			logger.error("Missing Supervisor Email Id");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Missing Supervisor Email Id");
		}

		if (userAccessRequest.getReasonForRequest() == null) {
			logger.error("Missing Request Reason");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Missing Request Reason");
		}

		if (isUpdate) {
			if (userAccessRequest.getApprovedRejectedBy() == null) {
				logger.error("Missing Approver Id");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Missing Approver Id");
			}

			if (userAccessRequest.getApprovedRejectedComments() == null) {
				logger.error("Missing Approver Comments");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Missing Approver Comments");
			}

			if (userAccessRequest.getApprovedRejectedDate() == null) {
				logger.error("Missing Approved Date");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Missing Approved Date");
			}

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