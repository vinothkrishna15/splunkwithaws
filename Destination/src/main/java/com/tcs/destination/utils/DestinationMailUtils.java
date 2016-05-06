package com.tcs.destination.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.DestinationMailMessage;
import com.tcs.destination.bean.OpportunityReopenRequestT;
import com.tcs.destination.bean.OpportunitySalesSupportLinkT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.UserAccessRequestT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.bean.WorkflowCustomerT;
import com.tcs.destination.bean.WorkflowPartnerT;
import com.tcs.destination.bean.WorkflowRequestT;
import com.tcs.destination.bean.WorkflowStepT;
import com.tcs.destination.data.repository.OpportunityReopenRequestRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.OpportunitySalesSupportLinkTRepository;
import com.tcs.destination.data.repository.UserAccessPrivilegesRepository;
import com.tcs.destination.data.repository.UserAccessRequestRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.data.repository.WorkflowCustomerTRepository;
import com.tcs.destination.data.repository.WorkflowPartnerRepository;
import com.tcs.destination.data.repository.WorkflowProcessTemplateRepository;
import com.tcs.destination.data.repository.WorkflowRequestTRepository;
import com.tcs.destination.data.repository.WorkflowStepTRepository;
import com.tcs.destination.enums.EntityTypeId;
import com.tcs.destination.enums.RequestType;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.enums.UserRole;
import com.tcs.destination.enums.WorkflowStatus;
import com.tcs.destination.service.OpportunityService;
import com.tcs.destination.service.UserService;

@Component
public class DestinationMailUtils {

	@Value("${senderEmailId}")
	private String senderEmailId;

	@Value("${dateFormat}")
	private String dateFormatStr;

	@Value("${forgotPasswordTemplateLoc}")
	private String forgotPasswordTemplateLoc;

	@Value("${userAccessTemplateLoc}")
	private String userAccessTemplateLoc;

	@Value("${reopenOpportunityTemplateLoc}")
	private String reopenOpportunityTemplateLoc;

	@Value("${reopenOpportunityProcessedTemplateLoc}")
	private String reopenOpportunityProcessedTemplateLoc;

	@Value("${workflowPendingTemplateLoc}")
	private String workflowPendingTemplateLoc;

	@Value("${workflowCustomerPendingTemplateLoc}")
	private String workflowCustomerPendingTemplateLoc;


	@Value("${workflowApproveOrRejectTemplateLoc}")
	private String workflowApproveOrRejectTemplateLoc;

	@Value("${workflowCustomerApproveOrRejectTemplateLoc}")
	private String workflowCustomerApproveOrRejectTemplateLoc;

	@Value("${upload.template}")
	private String uploadTemplateLoc;

	@Value("${download.template}")
	private String downloadTemplateLoc;

	@Value("${upload.notify.template}")
	private String uploadNotifyTemplateLoc;

	@Value("${daily.download.template}")
	private String dailyDownloadTemplateLoc;

	@Value("${environment.name}")
	private String environmentName;

	@Autowired
	private UserService userService;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private JavaMailSenderImpl sender;

	@Autowired
	private VelocityEngine velocityEngine;

	@Autowired
	UserAccessRequestRepository userAccessRepo;

	@Autowired
	OpportunityReopenRequestRepository oppReopenRepo;

	@Autowired
	OpportunitySalesSupportLinkTRepository opportunitySalesSupportLinkTRepository;

	@Autowired
	WorkflowRequestTRepository workflowRequestRepository;

	@Autowired
	WorkflowStepTRepository workflowStepRepository;

	@Autowired
	WorkflowCustomerTRepository workflowCustomerRepository;

	@Autowired
	UserAccessPrivilegesRepository userAccessPrivilegesRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	WorkflowProcessTemplateRepository workflowProcessTemplateRepository;

	@Autowired
	WorkflowPartnerRepository workflowPartnerRepository;

	@Autowired
	OpportunityRepository opportunityRepository;

	@Autowired
	private OpportunityService oppService;

	@Autowired
	private DestinationMailSender destMailSender;

	private static final Logger logger = LoggerFactory
			.getLogger(DestinationMailUtils.class);

	/**
	 * @param user
	 * @param requestedDateTime
	 * @throws Exception
	 */
	public void sendPasswordAutomatedEmail(String subject, UserT user,
			Date requestedDateTime) throws Exception {
		logger.debug("inside sendPasswordAutomatedEmail method");
		DestinationMailMessage message = new DestinationMailMessage();

		message.setRecipients(Lists.newArrayList(user.getUserEmailId()));

		message.setSubject(formatSubject(subject));

		Map<String, Object> forgotPasswordTemplateDataModel = Maps.newHashMap();
		forgotPasswordTemplateDataModel.put("user", user);
		forgotPasswordTemplateDataModel.put("date", formatDate(requestedDateTime));
		String text = mergeTmplWithData(forgotPasswordTemplateDataModel, forgotPasswordTemplateLoc);

		message.setMessage(text);
		destMailSender.send(message);
		logger.info("Forgot Password : Mail sent");
	}

	/**
	 * @param request
	 * @param subject
	 * @param template
	 * @return
	 * @throws Exception
	 */
	public boolean sendUserRequestResponse(DataProcessingRequestT request,
			List<UserRole> roles) throws Exception {
		logger.debug("inside sendUserRequestResponse method");

		DestinationMailMessage message = new DestinationMailMessage();

		boolean status = false;
		String dateStr = null;

		UserT user = request.getUserT();
		DateFormat df = new SimpleDateFormat(dateFormatStr);
		if (user != null) {
			dateStr = df.format(request.getSubmittedDatetime());
			message.setRecipients(Lists.newArrayList(user.getUserEmailId()));
		}
		if (CollectionUtils.isNotEmpty(roles)) {
			message.setRecipients(listMailIdsFromUserRoles(roles));
			dateStr = df.format(DateUtils.getCurrentTimeStamp());
		}

		String template = null;
		StringBuffer subject = new StringBuffer(environmentName)
		.append(" Admin: ");

		String userName = user.getUserName();
		String entity = null;
		String uploadedFileName = null;
		String attachmentFileName = null;
		String attachmentFilePath = null;
		String requestId = null;

		int requestType = request.getRequestType();
		RequestType reqType = RequestType.getByType(requestType);
		if(reqType != null) {
			subject.append(reqType.getMailSubject());
			entity = WordUtils.capitalize(reqType.getEntityType().name()
					.toLowerCase());
			if(reqType == RequestType.OPPORTUNITY_DAILY_DOWNLOAD) {
				userName = "System Admin/Strategic Group Admin";
			}
		}

		if (requestType > 0 && requestType < 10) { //upload
			template = uploadTemplateLoc;
			requestId = request.getProcessRequestId().toString();
			uploadedFileName = request.getFileName();
			attachmentFilePath = request.getErrorFilePath()
					+ request.getErrorFileName();
			attachmentFileName = request.getErrorFileName();
		} else if (requestType > 9 && requestType < 19) { //download
			template = downloadTemplateLoc;
			attachmentFilePath = request.getFilePath()
					+ request.getFileName();
			attachmentFileName = request.getFileName();
		} else {
			template = dailyDownloadTemplateLoc;
			attachmentFilePath = request.getFilePath()
					+ request.getFileName();
			attachmentFileName = request.getFileName();
		}

		Map<String, Object> userRequestMap = new HashMap<String, Object>();
		userRequestMap.put("userName", userName);
		userRequestMap.put("entity", entity);
		userRequestMap.put("fileName", uploadedFileName);
		userRequestMap.put("submittedDate", dateStr);
		userRequestMap.put("requestId", requestId);

		String text = mergeTmplWithData(userRequestMap, template);

		message.setSubject(subject.toString());
		message.setMessage(text);
		message.setAtchFileName(attachmentFileName);
		message.setAtchFilePath(attachmentFilePath);
		destMailSender.send(message);

		status = true;

		return status;
	}

	/**
	 * @param request
	 * @param subject
	 * @param template
	 * @return
	 * @throws Exception
	 */
	public boolean sendUploadNotification(DataProcessingRequestT request)
			throws Exception {
		logger.debug("inside sendUploadNotification method");

		DestinationMailMessage message = new DestinationMailMessage();
		UserT user = request.getUserT();
		DateFormat df = new SimpleDateFormat(dateFormatStr);
		String dateStr = df.format(request.getSubmittedDatetime());
		List<String> recipientMailIds = listMailIdsFromRoles(Lists.newArrayList(user.getUserRole()));

		message.setRecipients(recipientMailIds);

		StringBuffer subject = new StringBuffer(environmentName)
		.append(" Admin: ");

		String userName = user.getUserName();
		String entity = null;
		String fileName = request.getFileName();

		RequestType reqType = RequestType.getByType(request.getRequestType());
		if(reqType != null) {
			subject.append(reqType.getNotifySubject());
			entity = WordUtils.capitalize(reqType.getEntityType().name()
					.toLowerCase());
		}

		Map<String, Object> userRequestMap = new HashMap<String, Object>();
		userRequestMap.put("userName", userName);
		userRequestMap.put("entity", entity);
		userRequestMap.put("fileName", fileName);
		userRequestMap.put("submittedDate", dateStr);
		userRequestMap.put("requestId", request.getProcessRequestId()
				.toString());
		String text = mergeTmplWithData(userRequestMap, uploadNotifyTemplateLoc);

		message.setSubject(subject.toString());
		message.setMessage(text);
		destMailSender.send(message);

		return true;
	}

	/**
	 * send a mail to the system admin, when a user request for access
	 * @param subject - subject of the mail to be sent
	 * @param reqId - request id for new user access
	 * @param requestedDateTime - requested timestamp
	 * @throws Exception
	 */
	public void sendUserAccessAutomatedEmail(String subject, String reqId,
			Date requestedDateTime) throws Exception {
		logger.debug("inside sendUserAccessAutomatedEmail method");
		DestinationMailMessage message = new DestinationMailMessage();

		//add all system admins in "to address"
		List<String> recipientIds = userService.findByUserRole(Constants.SYSTEM_ADMIN);
		message.setRecipients(listMailIdsFromUserIds(recipientIds));

		//cc to the requested user and his supervisor
		UserAccessRequestT userAccessRequest = userAccessRepo.findOne(reqId);
		UserT supervisor = userRepository.findOne(userAccessRequest.getSupervisorId());
		List<String> ccIds = Lists.newArrayList(userAccessRequest.getUserEmailId(), supervisor.getUserEmailId());
		message.setCcList(ccIds);

		message.setSubject(formatSubject(subject));

		String requestedDateStr = formatDate(requestedDateTime);
		logger.info("User Access - Sender : " + senderEmailId);
		Map<String, Object> userAccessTemplateDataModel = Maps.newHashMap();
		userAccessTemplateDataModel.put("request", userAccessRequest);
		userAccessTemplateDataModel.put("date", requestedDateStr);
		String text = mergeTmplWithData(userAccessTemplateDataModel, userAccessTemplateLoc);
		message.setMessage(text);

		destMailSender.send(message);
	}

	/**
	 * @param subject
	 * @param reqId
	 * @param requestedDateTime
	 * @throws Exception
	 */
	public void sendOpportunityReopenAutomatedEmail(String subject,
			String reqId, Date requestedDateTime) throws Exception {
		logger.debug("inside sendUserAccessAutomatedEmail method");
		DestinationMailMessage message = new DestinationMailMessage();

		OpportunityReopenRequestT oppReopenRequest = oppReopenRepo.findOne(reqId);
		UserT user = userService.findByUserId(oppReopenRequest.getRequestedBy());
		UserT supervisor = userService.findByUserId(user.getSupervisorUserId());
		OpportunityT opp = oppService.findOpportunityById(oppReopenRequest.getOpportunityId());

		//add all system admins in "to address"
		List<String> recipientIds = userService.findByUserRole(Constants.SYSTEM_ADMIN);
		message.setRecipients(listMailIdsFromUserIds(recipientIds));

		//cc to the requested user and his supervisor
		List<String> ccIds = Lists.newArrayList(user.getUserEmailId(), supervisor.getUserEmailId());
		message.setCcList(ccIds);

		String dateStr = formatDate(requestedDateTime);
		message.setSubject(formatSubject(subject));

		Map<String, Object> reopenOppTemplateDataModel = Maps.newHashMap();
		reopenOppTemplateDataModel.put("request", oppReopenRequest);
		reopenOppTemplateDataModel.put("user", user);
		reopenOppTemplateDataModel.put("opportunity", opp);
		reopenOppTemplateDataModel.put("date", dateStr);
		String text = mergeTmplWithData(reopenOppTemplateDataModel, reopenOpportunityTemplateLoc);
		message.setMessage(text);

		destMailSender.send(message);
		logger.info("Opportunity Reopen : Mail sent");
	}

	/**
	 * @param reopenOpportunityProcessedSubject
	 * @param requestId
	 * @param date
	 * @throws Exception
	 */
	public void sendOpportunityReopenProcessedAutomatedEmail(
			String reopenOpportunityProcessedSubject, String requestId,
			Date date) throws Exception {
		logger.debug("inside sendOpportunityReopenProcessedAutomatedEmail method");
		DestinationMailMessage message = new DestinationMailMessage();

		OpportunityReopenRequestT oppReopenRequest = oppReopenRepo.findOne(requestId);
		UserT user = userService.findByUserId(oppReopenRequest.getRequestedBy());
		OpportunityT opp = oppService.findOpportunityById(oppReopenRequest.getOpportunityId());
		CustomerMasterT customer = opp.getCustomerMasterT();

		List<String> recepientIds = Lists.newArrayList(user.getUserEmailId());
		message.setRecipients(recepientIds);

		List<String> ccUserIds = new ArrayList<String>();
		String primaryOwner = opp.getOpportunityOwner();
		List<OpportunitySalesSupportLinkT> opportunitySalesSupportOwners = opportunitySalesSupportLinkTRepository
				.findByOpportunityId(opp.getOpportunityId());
		if (CollectionUtils.isNotEmpty(opportunitySalesSupportOwners)) {
			for (OpportunitySalesSupportLinkT osslt : opportunitySalesSupportOwners) {
				ccUserIds.add(osslt.getSalesSupportOwner());
			}

		}
		ccUserIds.add(primaryOwner);
		ccUserIds.remove(user.getUserId());//remove the user whether he is already added in 'to' address
		message.setCcList(listMailIdsFromUserIds(ccUserIds));

		String dateStr = formatDate(date);
		String sub = formatSubject(reopenOpportunityProcessedSubject);
		message.setSubject(sub);

		logger.info("Opportuity Reopen - Sender : " + senderEmailId);
		Map<String, Object> oppReopenRequestProcessedMap = new HashMap<String, Object>();
		oppReopenRequestProcessedMap.put("userName", user.getUserName());
		oppReopenRequestProcessedMap.put("opportunityName",	opp.getOpportunityName());
		oppReopenRequestProcessedMap.put("customerName", customer.getCustomerName());
		oppReopenRequestProcessedMap.put("submittedDate", dateStr);
		String text = mergeTmplWithData(oppReopenRequestProcessedMap, reopenOpportunityProcessedTemplateLoc);
		message.setMessage(text);

		destMailSender.send(message);
		logger.info("Opportunity Reopen Processed: Mail sent");
	}

	/**
	 * This method is used to send mail notification to whom the request is
	 * pending
	 * 
	 * @param workflowCustomerPendingSubject
	 * @param requestId
	 * @param date
	 * @throws Exception
	 */
	public void sendWorkflowPendingMail(Integer requestId, Date date,
			Integer entityTypeId) throws Exception {
		logger.info("Inside sendWorkflowPendingMail method");

		DestinationMailMessage message = new DestinationMailMessage();

		List<String> recepientIds = new ArrayList<String>();
		List<String> ccIds = new ArrayList<String>();
		String userGroupOrUserRoleOrUserId = null;
		String workflowEntity = null;
		String workflowEntityName = null;
		String geography = null;
		String userName = null;
		String notes = "NA";
		String operation = null;
		String reason = "";
		String pmoValue = "%" + Constants.PMO_KEYWORD + "%";
		String dateStr = formatDate(date);
		StringBuffer subject = new StringBuffer(environmentName);
		logger.info("sendWorkflowPendingMail :: RequestId" +requestId);
		WorkflowRequestT workflowRequestT = workflowRequestRepository
				.findOne(requestId);
		if(workflowRequestT != null) {
			String entityId = workflowRequestT.getEntityId();
			logger.debug("Request fetched:");
			logger.debug("EntityId:" +entityId );
			switch (EntityTypeId.valueOf(EntityTypeId.getName(entityTypeId))) {
			case CUSTOMER:
				workflowEntity = Constants.WORKFLOW_CUSTOMER;
				WorkflowCustomerT workflowCustomerT = workflowCustomerRepository
						.findOne(entityId);
				if(!StringUtils.isEmpty(workflowCustomerT.getNotes())){
					notes = workflowCustomerT.getNotes();
				}
				workflowEntityName = workflowCustomerT.getCustomerName();
				geography = workflowCustomerT.getGeography();
				userName = userRepository.findUserNameByUserId(workflowCustomerT
						.getCreatedBy());
				subject.append(Constants.WORKFLOW_CUSTOMER_PENDING_SUBJECT)
				.append(" ").append(Constants.FROM).append(" ")
				.append(userName);
				operation = Constants.WORKFLOW_OPERATION_CREATION_TEMPLATE;
				break;
			case PARTNER:
				workflowEntity = Constants.WORKFLOW_PARTNER;
				WorkflowPartnerT workflowPartnerT = workflowPartnerRepository
						.findOne(entityId);
				workflowEntityName = workflowPartnerT.getPartnerName();
				geography = workflowPartnerT.getGeography();
				userName = userRepository.findUserNameByUserId(workflowPartnerT
						.getCreatedBy());
				subject.append(Constants.WORKFLOW_PARTNER_PENDING_SUBJECT)
				.append(" ").append(Constants.FROM).append(" ")
				.append(userName);
				operation = Constants.WORKFLOW_OPERATION_CREATION_TEMPLATE;
				break;
				//			case COMPETITOR:
				//				workflowEntity = Constants.WORKFLOW_COMPETITOR;
				//				WorkflowCompetitorT workflowCompetitor = workflowCompetitorRepository
				//						.findOne(entityId);
				//				workflowEntityName = workflowCompetitor.getWorkflowCompetitorName();
				//				userName = userRepository.findUserNameByUserId(workflowCompetitor
				//						.getCreatedBy());
				//				subject.append(Constants.WORKFLOW_COMPETITOR_PENDING_SUBJECT)
				//						.append(" ").append(Constants.FROM).append(" ")
				//						.append(userName);
				//				operation = Constants.WORKFLOW_OPERATION_CREATION_TEMPLATE;
				//				break;
			case OPPORTUNITY:
				workflowEntity = Constants.WORKFLOW_OPPORTUNITY_REOPEN;
				OpportunityT opportunity = opportunityRepository.findOne(entityId);
				workflowEntityName = opportunity.getOpportunityName();
				geography = opportunity.getCustomerMasterT().getGeography();
				userName = userRepository.findUserNameByUserId(workflowRequestT
						.getCreatedBy());
				subject.append(
						Constants.WORKFLOW_OPPORTUNITY_REOPEN_PENDING_SUBJECT)
						.append(" ").append(Constants.FROM).append(" ")
						.append(userName);
				operation = Constants.WORKFLOW_OPERATION_REOPEN_TEMPLATE;
				WorkflowStepT workflowSubmittedStep = workflowStepRepository
						.findByRequestIdAndStepStatus(requestId,
								WorkflowStatus.SUBMITTED.getStatus());
				reason = new StringBuffer(Constants.WORKFLOW_REOPEN_PREFIX)
				.append(" ").append(workflowSubmittedStep.getComments())
				.toString();
				logger.info("Subject :"+subject);
				break;
			default:
				break;
			}
			WorkflowStepT workflowStepPending = workflowStepRepository
					.findByRequestIdAndStepStatus(requestId,
							WorkflowStatus.PENDING.getStatus());
			if (workflowStepPending.getUserGroup() != null) {
				switch (workflowStepPending.getUserGroup()) {
				case Constants.WORKFLOW_GEO_HEADS:
					recepientIds.addAll(userAccessPrivilegesRepository
							.findUserIdsForWorkflowUserGroup(geography,
									Constants.Y,
									UserGroup.GEO_HEADS.getValue()));
					logger.debug("recepient Ids for GEO Heads :" +recepientIds);
					userGroupOrUserRoleOrUserId = Constants.WORKFLOW_GEO_HEADS;
					ccIds.addAll(userAccessPrivilegesRepository
							.findUserIdsForWorkflowPMO(geography,
									Constants.Y, pmoValue));
					logger.debug("CCIds for PMO :"+ccIds);
					break;
				case Constants.WORKFLOW_PMO:
					recepientIds.addAll(userAccessPrivilegesRepository
							.findUserIdsForWorkflowPMO(geography,
									Constants.Y, pmoValue));
					userGroupOrUserRoleOrUserId = Constants.WORKFLOW_PMO;
				default:
				}
			}
			if (workflowStepPending.getUserRole() != null) {
				recepientIds.addAll(userRepository
						.findUserIdByUserRole(workflowStepPending
								.getUserRole()));
				userGroupOrUserRoleOrUserId = workflowStepPending
						.getUserRole();

			}
			if (workflowStepPending.getUserId() != null) {
				String[] workflowUserIds = workflowStepPending.getUserId()
						.split(",");
				List<String> workflowUserIdList = Arrays
						.asList(workflowUserIds);
				List<String> userNames = userRepository
						.findUserNamesByUserIds(workflowUserIdList);
				userGroupOrUserRoleOrUserId = StringUtils.join(userNames, ",");
				recepientIds.addAll(workflowUserIdList);
			}
			message.setRecipients(listMailIdsFromUserIds(recepientIds));
			message.setCcList(listMailIdsFromUserIds(ccIds));

			Map<String, Object> workflowMap = new HashMap<String, Object>();
			workflowMap.put("userGroupOrUserRole", userGroupOrUserRoleOrUserId);
			workflowMap.put("workflowEntity", workflowEntity);
			workflowMap.put("workflowEntityName", workflowEntityName);
			workflowMap.put("submittedDate", dateStr);
			workflowMap.put("userName", userName);
			workflowMap.put("notes", notes);
			workflowMap.put("operation", operation);
			workflowMap.put("reason", reason);
			String tmpl;
			if(EntityTypeId.valueOf(EntityTypeId.getName(entityTypeId)).equals(EntityTypeId.CUSTOMER)){
				tmpl = workflowCustomerPendingTemplateLoc;
			}
			else{
				tmpl = workflowPendingTemplateLoc;
			}
			String text = mergeTmplWithData(workflowMap, tmpl);

			logger.info("framed text for mail :" + text);
			message.setSubject(subject.toString());
			message.setMessage(text);

			destMailSender.send(message);
			logger.info("Mail Sent for request" +workflowRequestT.getRequestId());

		} else {
			logger.error("request not fetched");
		}
	}


	/**
	 * This method is used to send the mail on approval of a workflow entity
	 * @param workflowCustomerApprovedOrRejectSubject
	 * @param requestId
	 * @param date
	 * @param entityTypeId
	 * @throws Exception
	 */
	public void sendWorkflowApprovedOrRejectMail(
			String workflowCustomerApprovedOrRejectSubject, Integer requestId,
			Date date, Integer entityTypeId) throws Exception {
		logger.info("Inside sendWorkflowApprovedOrRejectMail method");
		DestinationMailMessage message = new DestinationMailMessage();

		Set<String> ccIds = new HashSet<String>();
		List<String> recepientIds = new ArrayList<String>();
		String dateStr = formatDate(date);
		String approvedOrRejectedUserName = null;
		String entity = null;
		String operation = null;
		String entityName = null;
		String userName = null;
		String geography = null;
		String pmoValue = "%"
				+ Constants.PMO_KEYWORD + "%";
		String subject = formatSubject(workflowCustomerApprovedOrRejectSubject);
		WorkflowRequestT workflowRequestT = workflowRequestRepository
				.findOne(requestId);
		String entityId = workflowRequestT.getEntityId();
		String notes = "NA";
		WorkflowStepT workflowStepSubmitted = workflowStepRepository
				.findByRequestIdAndStepStatus(requestId,
						WorkflowStatus.SUBMITTED.getStatus());
		if (workflowStepSubmitted != null) {
			switch (EntityTypeId.valueOf(EntityTypeId.getName(entityTypeId))) {
			case CUSTOMER :
				entity = Constants.WORKFLOW_CUSTOMER;
				WorkflowCustomerT workflowCustomerT = workflowCustomerRepository
						.findOne(entityId);
				if(!StringUtils.isEmpty(workflowCustomerT.getNotes())){
					notes = workflowCustomerT.getNotes();
				}
				entityName = workflowCustomerT.getCustomerName();
				geography = workflowCustomerT.getGeography();
				userName = userRepository
						.findUserNameByUserId(workflowCustomerT
								.getCreatedBy());
				operation = Constants.WORKFLOW_OPERATION_CREATE;
				recepientIds.add(workflowCustomerT.getCreatedBy());
				break;
			case PARTNER:
				entity = Constants.WORKFLOW_PARTNER;
				WorkflowPartnerT workflowPartnerT = workflowPartnerRepository
						.findOne(entityId);
				entityName = workflowPartnerT.getPartnerName();
				geography = workflowPartnerT.getGeography();
				userName = userRepository
						.findUserNameByUserId(workflowPartnerT
								.getCreatedBy());
				operation = Constants.WORKFLOW_OPERATION_CREATE;
				recepientIds.add(workflowPartnerT.getCreatedBy());
				break;
				//				case COMPETITOR:
				//					entity = Constants.WORKFLOW_COMPETITOR;
				//					WorkflowCompetitorT workflowCompetitor = workflowCompetitorRepository
				//							.findOne(entityId);
				//					entityName = workflowCompetitor.getWorkflowCompetitorName();
				//					userName = userRepository
				//							.findUserNameByUserId(workflowCompetitor
				//									.getCreatedBy());
				//					operation = Constants.WORKFLOW_OPERATION_CREATE;
				//					recepientIds.add(workflowCompetitor.getCreatedBy());
				//					break;
			case OPPORTUNITY:
				entity = Constants.WORKFLOW_OPPORTUNITY_REOPEN;
				OpportunityT opportunity = opportunityRepository.findOne(entityId);
				geography = opportunity.getCustomerMasterT().getGeography();
				entityName = opportunity.getOpportunityName();
				userName = userRepository.findUserNameByUserId(workflowRequestT.getCreatedBy());
				operation = Constants.WORKFLOW_OPERATION_REOPEN;
				recepientIds.add(workflowRequestT.getCreatedBy());
				break;
			default:
				break;
			}

			List<WorkflowStepT> workflowStepforCcIds = new ArrayList<WorkflowStepT>();

			List<WorkflowStepT> workflowStepsBelowMaximumStep = workflowStepRepository
					.findWorkflowTemplateBelowMaximumStep(requestId);
			if (CollectionUtils.isNotEmpty(workflowStepsBelowMaximumStep)) {
				for (WorkflowStepT workflowStep : workflowStepsBelowMaximumStep) {
					if (workflowStep.getStepStatus().equals(
							WorkflowStatus.APPROVED.getStatus())) {
						workflowStepforCcIds.add(workflowStep);
					}
				}
			}
			if (CollectionUtils.isNotEmpty(workflowStepforCcIds)) {
				for (WorkflowStepT workflowStep : workflowStepforCcIds) {
					if (workflowStep.getUserGroup() != null) {
						switch (workflowStep.getUserGroup()) {
						case Constants.WORKFLOW_GEO_HEADS:

							ccIds.addAll(userAccessPrivilegesRepository
									.findUserIdsForWorkflowUserGroup(
											geography, Constants.Y,
											UserGroup.GEO_HEADS.getValue()));
							ccIds.addAll(userAccessPrivilegesRepository
									.findUserIdsForWorkflowPMO(
											geography, Constants.Y,
											pmoValue));
						case Constants.WORKFLOW_PMO:
							ccIds.addAll(userAccessPrivilegesRepository
									.findUserIdsForWorkflowPMO(
											geography, Constants.Y,
											pmoValue));
						default:

						}
					}
					if (workflowStep.getUserRole() != null) {
						ccIds.addAll(userRepository
								.findUserIdByUserRole(workflowStep
										.getUserRole()));
					}
					if (workflowStep.getUserId() != null) {
						String[] workflowUserIds = workflowStep
								.getUserId().split(",");
						List<String> workflowUserIdList = Arrays
								.asList(workflowUserIds);
						ccIds.addAll(workflowUserIdList);
					}
				}
			}
			String comment = "";
			message.setRecipients(listMailIdsFromUserIds(recepientIds));

			if (CollectionUtils.isNotEmpty(ccIds)) {
				message.setCcList(listMailIdsFromUserIds(Lists.newArrayList(ccIds)));
			}

			message.setSubject(subject);

			Map<String, Object> workflowMap = new HashMap<String, Object>();
			workflowMap.put("userName", userName);
			workflowMap.put("entity", entity);
			workflowMap.put("entityName", entityName);
			workflowMap.put("operation", operation);
			workflowMap.put("submittedDate", dateStr);
			String text = "";
			if (workflowRequestT.getStatus().equals(
					WorkflowStatus.APPROVED.getStatus())) {
				WorkflowStepT workflowStepForFinalApproval = workflowStepRepository
						.findWorkflowStepForFinalApproval(requestId);
				if (workflowStepForFinalApproval.getComments() != null) {
					comment = new StringBuffer(Constants.WORKFLOW_COMMENTS)
					.append(" ")
					.append(workflowStepForFinalApproval
							.getComments()).toString();
				}
				approvedOrRejectedUserName = userRepository
						.findUserNameByUserId(workflowStepForFinalApproval
								.getUserId());
				workflowMap.put("status", "approved");
				workflowMap.put("approvedOrRejectedUserName",
						approvedOrRejectedUserName);
				workflowMap.put("comment", comment);
				workflowMap.put("notes", notes);
				workflowMap.put("geography", geography);
			} else {
				WorkflowStepT workflowStepRejected = workflowStepRepository
						.findByRequestIdAndStepStatus(requestId,
								WorkflowStatus.REJECTED.getStatus());
				approvedOrRejectedUserName = userRepository
						.findUserNameByUserId(workflowStepRejected
								.getUserId());
				comment = new StringBuffer(Constants.WORKFLOW_COMMENTS)
				.append(" ")
				.append(workflowStepRejected.getComments())
				.toString();
				workflowMap.put("approvedOrRejectedUserName",
						approvedOrRejectedUserName);
				workflowMap.put("status", "rejected");
				workflowMap.put("comment", comment);
				workflowMap.put("notes", notes);
				workflowMap.put("geography", geography);
			}

			String tmpl;
			if(EntityTypeId.valueOf(EntityTypeId.getName(entityTypeId)).equals(EntityTypeId.CUSTOMER)){
				tmpl = workflowCustomerApproveOrRejectTemplateLoc;
			}
			else{
				tmpl = workflowApproveOrRejectTemplateLoc;
			}
			text = mergeTmplWithData(workflowMap, tmpl);
			logger.info("framed text for mail :" + text);
			message.setMessage(text);
			logger.info("before sending mail");
			destMailSender.send(message);
			logger.info("Mail Sent for request" +workflowRequestT.getRequestId());
		}

	}


	/**
	 * @param roles
	 * @return emails Id's
	 */
	private List<String> listMailIdsFromUserRoles(List<UserRole> roles) {

		logger.debug("Inside method: getMailIdsFromRoles");

		List<String> values = new ArrayList<String>(roles.size());
		for (UserRole role : roles) {
			values.add(role.getValue());
		}
		return listMailIdsFromRoles(values);
	}

	/**
	 * 
	 * @param roles
	 * @return
	 */
	private List<String> listMailIdsFromRoles(List<String> roles) {

		List<String> mailIds = new ArrayList<String>();
		List<UserT> users = userService.getByUserRoles(roles);
		for (UserT user : users) {
			mailIds.add(user.getUserEmailId());
		}
		return mailIds;
	}

	/**
	 * merge the data in the given template
	 * @param data
	 * @param tmpl
	 * @return
	 */
	private String mergeTmplWithData(
			Map<String, Object> data, String tmpl) {
		return VelocityEngineUtils.mergeTemplateIntoString(
				velocityEngine, tmpl,
				Constants.UTF8, data);
	}


	/**
	 * format the given date to predefined destination date-format
	 * @param requestedDateTime
	 * @return
	 */
	private String formatDate(final Date requestedDateTime) {
		DateFormat df = new SimpleDateFormat(dateFormatStr);
		String dateStr = df.format(requestedDateTime);
		return dateStr;
	}


	/**
	 * format the subject with environment name
	 * @param subject
	 * @return
	 */
	private String formatSubject(String subject) {
		String sub = new StringBuffer(environmentName).append(" ")
				.append(subject).toString();
		return sub;
	}

	private List<String> listMailIdsFromUserIds(List<String> recipientIdList) {
		List<String> emailIds = Lists.newArrayList();
		if(CollectionUtils.isNotEmpty(recipientIdList)) {
			emailIds = userRepository.findUserMailIdsFromUserId(recipientIdList);
		}
		return emailIds;		
	}

}
