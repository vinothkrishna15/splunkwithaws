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

/**
 * This Service handles the requests related to UserAccessRequest service
 */
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

	/**
	 * To find the user based on the request id
	 * @param reqId
	 * @return
	 * @throws Exception
	 */
	public UserAccessRequestT findUserRequestById(String reqId)
			throws Exception {
		logger.debug("Begin:Inside findUserRequestById() UserAccessRequestService");
		UserAccessRequestT newUserRequest = userAccessReqRepo.findOne(reqId);
		if (newUserRequest != null) {
			logger.debug("End:Inside findUserRequestById() UserAccessRequestService");
			return newUserRequest;
		} else {
			logger.error("NOT_FOUND: user Request not found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"user Request not found");
		}
	}

	/**
	 * to validate and insert the user access request
	 * @param userAccessRequest
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public boolean insertUserRequest(UserAccessRequestT userAccessRequest)
			throws Exception {
		logger.debug("Begin:Inside insertUserRequest() UserAccessRequestService");
		validateRequest(userAccessRequest, false);
		if (userAccessReqRepo.save(userAccessRequest) != null) {
			// send notification to admin,supervisor and user on saving the
			// request
			sendEmailNotification(userAccessRequest.getRequestId(), new Date());
			logger.debug("End:Inside insertUserRequest() UserAccessRequestService");
			return true;
		}
		return false;
	}

	/**
	 * This method handles the operations 
	 * related to sending the email notifications
	 * @param requestId
	 * @param date
	 * @throws Exception
	 */
	private void sendEmailNotification(String requestId, Date date)
			throws Exception {
		logger.debug("Begin:Inside sendEmailNotification() UserAccessRequestService");
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
		logger.debug("End:Inside sendEmailNotification() UserAccessRequestService");
	}
	/**
	 * This method validates the request
	 * @param userAccessRequest
	 * @param isUpdate
	 * @throws Exception
	 */
	private void validateRequest(UserAccessRequestT userAccessRequest,
			boolean isUpdate) throws Exception {
		logger.debug("Begin:Inside validateRequest() UserAccessRequestService");
		if (StringUtils.isEmpty(userAccessRequest.getUserId())) {
			logger.error("BAD_REQUEST: UserId is required");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"UserId is required");
		} else {
			UserT user = userRepository.findByUserId(userAccessRequest
					.getUserId());
			if (user != null) {
				logger.error("BAD_REQUEST: User already exists");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"User already exists");
			}

			// Check if any existing pending requests
			if (!isUpdate) {
				if (userAccessReqRepo.findByUserIdAndApprovedRejectedByIsNull(
						userAccessRequest.getUserId()) != null) {
					logger.error("BAD_REQUEST: User has already requested for access which is still pending action");
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"User has already requested for access which is still pending action");
				}
			}
		}

		if (StringUtils.isEmpty(userAccessRequest.getUserEmailId())) {
			logger.error("BAD_REQUEST: User Email Id is required");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"User Email Id is required");
		} else {
			UserT user = userRepository.findFirstByUserEmailIdAndActiveTrue(userAccessRequest.getUserEmailId());
			if (user != null) {
				logger.error("BAD_REQUEST: EmailId already exists");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"EmailId already exists");
			}
		}

		if (StringUtils.isEmpty(userAccessRequest.getSupervisorId())) {
			logger.error("BAD_REQUEST: Supervisor Id is required");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Supervisor Id is required");
		} else {
			UserT user = userRepository.findOne(userAccessRequest.getSupervisorId());
			if(user==null){
				logger.error("BAD_REQUEST: Invalid Supervisor Id");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid Supervisor Id");
			} else {
				if(!user.getUserEmailId().equals(userAccessRequest.getSupervisorEmailId())){
					logger.error("BAD_REQUEST: Supervisor Id and Email Id mismatch");
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Supervisor Id and Email Id mismatch");
				}
			}
		}

		if (StringUtils.isEmpty(userAccessRequest.getSupervisorEmailId())) {
			logger.error("BAD_REQUEST: Supervisor Email Id is required");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Supervisor Email Id is required");
		}else {
			UserT user = userRepository.findFirstByUserEmailIdAndActiveTrue(userAccessRequest.getSupervisorEmailId());
			if(user==null){
				logger.error("BAD_REQUEST: Invalid Supervisor Email Id");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid Supervisor Email Id");
			}
		}

		if (StringUtils.isEmpty(userAccessRequest.getReasonForRequest())) {
			logger.error("BAD_REQUEST: Reason for request is required");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Reason for request is required");
		}

		if (isUpdate) {
			if (StringUtils.isEmpty(userAccessRequest.getApprovedRejectedBy())) {
				logger.error("BAD_REQUEST: Approver User Id is required");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Approver User Id is required");
			}

			if (StringUtils.isEmpty(userAccessRequest.getApprovedRejectedComments())) {
				logger.error("BAD_REQUEST: Approver Comments is required");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Approver Comments is required");
			}
			// Set current timestamp
			userAccessRequest.setApprovedRejectedDate(DateUtils.getCurrentTimeStamp());
		}
		logger.debug("End:Inside validateRequest() UserAccessRequestService");
	}

	/**
	 * To edit and save the user access request
	 * @param userAccessRequest
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public boolean editUserRequest(UserAccessRequestT userAccessRequest)
			throws Exception {
		logger.debug("Begin:Inside editUserRequest() UserAccessRequestService");
		validateRequest(userAccessRequest, true);
		if (userAccessReqRepo.save(userAccessRequest) != null) {
			logger.debug("End:Inside editUserRequest() UserAccessRequestService");
			return true;
		}
		return false;
	}

	/**
	 * this method finds all the user access requests
	 * @return
	 */
	public List<UserAccessRequestT> findAllUserAccessRequests() {
		logger.debug("Inside findAllUserAccessRequests() UserAccessRequestService");
		return (List<UserAccessRequestT>) userAccessReqRepo.findAll(new Sort(
				Sort.Direction.DESC, "requestReceivedDate"));
	}
}