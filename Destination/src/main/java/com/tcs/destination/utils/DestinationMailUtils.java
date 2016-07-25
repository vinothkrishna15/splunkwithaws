package com.tcs.destination.utils;

import static com.tcs.destination.utils.DateUtils.ACTUAL_FORMAT;

import java.math.BigDecimal;
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

import javax.mail.MessagingException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.DestinationMailMessage;
import com.tcs.destination.bean.OpportunityCompetitorLinkT;
import com.tcs.destination.bean.OpportunityReopenRequestT;
import com.tcs.destination.bean.OpportunitySalesSupportLinkT;
import com.tcs.destination.bean.OpportunitySubSpLinkT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.OpportunityWinLossFactorsT;
import com.tcs.destination.bean.UserAccessRequestT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.bean.WorkflowCompetitorT;
import com.tcs.destination.bean.WorkflowCustomerT;
import com.tcs.destination.bean.WorkflowPartnerT;
import com.tcs.destination.bean.WorkflowRequestT;
import com.tcs.destination.bean.WorkflowStepT;
import com.tcs.destination.data.repository.BidDetailsTRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.OpportunityReopenRequestRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.OpportunitySalesSupportLinkTRepository;
import com.tcs.destination.data.repository.OpportunitySubSpLinkTRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.data.repository.UserAccessPrivilegesRepository;
import com.tcs.destination.data.repository.UserAccessRequestRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.data.repository.WorkflowCompetitorTRepository;
import com.tcs.destination.data.repository.WorkflowCustomerTRepository;
import com.tcs.destination.data.repository.WorkflowPartnerRepository;
import com.tcs.destination.data.repository.WorkflowProcessTemplateRepository;
import com.tcs.destination.data.repository.WorkflowRequestTRepository;
import com.tcs.destination.data.repository.WorkflowStepTRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.enums.EntityTypeId;
import com.tcs.destination.enums.RequestType;
import com.tcs.destination.enums.SalesStageCode;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.enums.UserRole;
import com.tcs.destination.enums.WorkflowStatus;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.NumericUtil;
import com.tcs.destination.service.OpportunityDownloadService;
import com.tcs.destination.service.OpportunityService;
import com.tcs.destination.service.UserService;

@Component
public class DestinationMailUtils {

	@Value("${senderEmailId}")
	private String senderEmailId;

	@Value("${opportunityWonLostGroupMailId}")
	private String opportunityWonLostGroupMailId;

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

	@Value("${opportunityRFPSubmittedTemplate}")
	private String opportunityRFPSubmittedTemplateLoc;

	@Value("${opportunityShortlistedTemplate}")
	private String opportunityShortlistedTemplateLoc;

	@Value("${opportunitySelectedTemplate}")
	private String opportunitySelectedTemplateLoc;

	@Value("${opportunityContractNegotiationTemplate}")
	private String opportunityContractNegotiationTemplateLoc;

	@Value("${opportunityWonTemplate}")
	private String opportunityWonTemplateLoc;

	@Value("${opportunityLostTemplate}")
	private String opportunityLostTemplateLoc;

	@Value("${environment.name}")
	private String environmentName;

	@Value("${destinationUrl}")
	private String destinationUrl;

	@Value("${defaultPasswordTemplateLoc}")
	private String defaultPasswordTemplateLoc;

	@Value("${mail.environment.name}")
	private String mailSubjectAppendEnvName;

	@Value("${rfp.mail.subject.highvalue}")
	private String rfpHighValueMailSub;

	@Value("${shortlisted.mail.subject.highvalue}")
	private String shortlistedHighValueMailSub;

	@Value("${selected.mail.subject.highvalue}")
	private String selectedHighValueMailSub;

	@Value("${contractNegotiation.mail.subject.highvalue}")
	private String contractNegotiationHighValueMailSub;
	
	//collaboration link share changes
	@Value("${shareConnectTemplate}")
	private String shareConnectTemplate;
	
	@Value("${shareOpportunityTemplate}")
	private String shareOpportunityTemplate;
	
	@Value("${shareOpportunitySubject}")
	private String shareOpportunitySubject;
	
	@Value("${shareConnectSubject}")
	private String shareConnectSubject;
	
	@Autowired
	private UserService userService;

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
	WorkflowCompetitorTRepository workflowCompetitorRepository;

	@Autowired
	OpportunityRepository opportunityRepository;

	@Autowired
	BidDetailsTRepository bidDetailsTRepository;

	@Autowired
	ContactRepository contactRepository;

	@Autowired
	OpportunityDownloadService opportunityDownloadService;
	
	@Autowired
	OpportunitySubSpLinkTRepository opportunitySubSpLinkTRepository;

	@Autowired
	private OpportunityService oppService;

	@Autowired
	SubSpRepository subSpRepository;
	
	@Autowired
	ConnectRepository connectRepository;

	@Autowired
	private DestinationMailSender destMailSender;

	@Value("${userDetailsApprovalSubject}")
	private String userDetailsApprovalSubject;

	@Value("${userDetailsApprovalTemplate}")
	private String userDetailsApprovalTemplate;

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

		if (user == null || !user.isActive()) {
			logger.warn(
					"DestinationMailUtils :: Cann't process the mail - The user {} is not found or inactive",
					user.getUserId());
			return;
		}

		DestinationMailMessage message = new DestinationMailMessage();
		message.setRecipients(Lists.newArrayList(user.getUserEmailId()));
		message.setSubject(formatSubject(subject));

		Map<String, Object> forgotPasswordTemplateDataModel = Maps.newHashMap();
		forgotPasswordTemplateDataModel.put("user", user);
		forgotPasswordTemplateDataModel.put("date",
				formatDate(requestedDateTime));
		String text = mergeTmplWithData(forgotPasswordTemplateDataModel,
				forgotPasswordTemplateLoc);

		message.setMessage(text);
		destMailSender.send(message);
		logger.info("Forgot Password : Mail sent");

	}

	public boolean sendDefaultPasswordAutomatedEmail(String subject, UserT user)
			throws Exception {
		logger.debug("inside sendDefaultPasswordAutomatedEmail method");

		if (user != null && !user.isActive()) {
			logger.warn(
					"DestinationMailUtils :: Can't process the mail - The user {} is inactive",
					user.getUserId());
			return false;
		}

		DestinationMailMessage message = new DestinationMailMessage();
		message.setRecipients(Lists.newArrayList(user.getUserEmailId()));
		message.setSubject(formatSubject(subject));

		Map<String, Object> defaultPasswordTemplateDataModel = Maps
				.newHashMap();
		defaultPasswordTemplateDataModel.put("user", user);
		defaultPasswordTemplateDataModel.put("destinationUrl", destinationUrl);
		String text = mergeTmplWithData(defaultPasswordTemplateDataModel,
				defaultPasswordTemplateLoc);
		message.setMessage(text);
		try {
			destMailSender.send(message);
		} catch (Exception e) {
			return false;
		}
		return true;
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
		if (user != null && user.isActive()) {
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

		String userName = null;
		String entity = null;
		String uploadedFileName = null;
		String attachmentFileName = null;
		String attachmentFilePath = null;
		String requestId = null;

		int requestType = request.getRequestType();
		RequestType reqType = RequestType.getByType(requestType);
		if (reqType != null) {
			subject.append(reqType.getMailSubject());
			entity = WordUtils.capitalize(reqType.getEntityType().name()
					.toLowerCase());
			if (reqType == RequestType.OPPORTUNITY_DAILY_DOWNLOAD) {
				userName = "System Admin/Strategic Group Admin";
			} else if(user != null) {
				userName = user.getUserName();
			}
		}

		if (requestType > 0 && requestType < 10) { // upload
			template = uploadTemplateLoc;
			requestId = request.getProcessRequestId().toString();
			uploadedFileName = request.getFileName();
			attachmentFilePath = request.getErrorFilePath()
					+ request.getErrorFileName();
			attachmentFileName = request.getErrorFileName();
		} else if (requestType > 9 && requestType < 19) { // download
			template = downloadTemplateLoc;
			attachmentFilePath = request.getFilePath() + request.getFileName();
			attachmentFileName = request.getFileName();
		} else {
			template = dailyDownloadTemplateLoc;
			attachmentFilePath = request.getFilePath() + request.getFileName();
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
		List<String> recipientMailIds = listMailIdsFromRoles(Lists
				.newArrayList(user.getUserRole()));

		message.setRecipients(recipientMailIds);

		StringBuffer subject = new StringBuffer(environmentName)
				.append(" Admin: ");

		String userName = user.getUserName();
		String entity = null;
		String fileName = request.getFileName();

		RequestType reqType = RequestType.getByType(request.getRequestType());
		if (reqType != null) {
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
	 * 
	 * @param subject
	 *            - subject of the mail to be sent
	 * @param reqId
	 *            - request id for new user access
	 * @param requestedDateTime
	 *            - requested timestamp
	 * @throws Exception
	 */
	public void sendUserAccessAutomatedEmail(String subject, String reqId,
			Date requestedDateTime) throws Exception {
		logger.debug("inside sendUserAccessAutomatedEmail method");
		DestinationMailMessage message = new DestinationMailMessage();

		// add all system admins in "to address"
		// List<String> recipientIds =
		// userService.findByUserRole(Constants.SYSTEM_ADMIN);
		// message.setRecipients(listMailIdsFromUserIds(recipientIds));
		message.setRecipients(listMailIdsFromRoles(Lists
				.newArrayList(Constants.SYSTEM_ADMIN)));

		// cc to the requested user and his supervisor
		UserAccessRequestT userAccessRequest = userAccessRepo.findOne(reqId);
		UserT supervisor = userRepository.findOne(userAccessRequest
				.getSupervisorId());
		if (supervisor != null && supervisor.isActive()) {
			List<String> ccIds = Lists.newArrayList(
					userAccessRequest.getUserEmailId(),
					supervisor.getUserEmailId());
			message.setCcList(ccIds);
		}

		message.setSubject(formatSubject(subject));

		String requestedDateStr = formatDate(requestedDateTime);
		logger.info("User Access - Sender : " + senderEmailId);
		Map<String, Object> userAccessTemplateDataModel = Maps.newHashMap();
		userAccessTemplateDataModel.put("request", userAccessRequest);
		userAccessTemplateDataModel.put("date", requestedDateStr);
		String text = mergeTmplWithData(userAccessTemplateDataModel,
				userAccessTemplateLoc);
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

		OpportunityReopenRequestT oppReopenRequest = oppReopenRepo
				.findOne(reqId);
		UserT user = userService
				.findByUserId(oppReopenRequest.getRequestedBy());
		UserT supervisor = userService.findByUserId(user.getSupervisorUserId());
		OpportunityT opp = oppService.findOpportunityById(oppReopenRequest
				.getOpportunityId());

		// add all system admins in "to address"
		// List<String> recipientIds =
		// userService.findByUserRole(Constants.SYSTEM_ADMIN);
		// message.setRecipients(listMailIdsFromUserIds(recipientIds));
		message.setRecipients(listMailIdsFromRoles(Lists
				.newArrayList(Constants.SYSTEM_ADMIN)));

		// cc to the requested user and his supervisor
		if (user != null && user.isActive()) {
			List<String> ccIds = Lists.newArrayList(user.getUserEmailId(),
					supervisor.getUserEmailId());
			message.setCcList(ccIds);
		}

		String dateStr = formatDate(requestedDateTime);
		message.setSubject(formatSubject(subject));

		Map<String, Object> reopenOppTemplateDataModel = Maps.newHashMap();
		reopenOppTemplateDataModel.put("request", oppReopenRequest);
		reopenOppTemplateDataModel.put("user", user);
		reopenOppTemplateDataModel.put("opportunity", opp);
		reopenOppTemplateDataModel.put("date", dateStr);
		String text = mergeTmplWithData(reopenOppTemplateDataModel,
				reopenOpportunityTemplateLoc);
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

		OpportunityReopenRequestT oppReopenRequest = oppReopenRepo
				.findOne(requestId);
		UserT user = userService
				.findByUserId(oppReopenRequest.getRequestedBy());
		OpportunityT opp = oppService.findOpportunityById(oppReopenRequest
				.getOpportunityId());
		CustomerMasterT customer = opp.getCustomerMasterT();

		if (user == null || !user.isActive()) {
			logger.warn(
					"DestinationMailUtils :: Cann't process the mail - The user {} is not found or inactive",
					user.getUserId());
			return;
		}

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
		ccUserIds.remove(user.getUserId());// remove the user whether he is
											// already added in 'to' address
		message.setCcList(listMailIdsFromUserIds(ccUserIds));

		String dateStr = formatDate(date);
		String sub = formatSubject(reopenOpportunityProcessedSubject);
		message.setSubject(sub);

		logger.info("Opportuity Reopen - Sender : " + senderEmailId);
		Map<String, Object> oppReopenRequestProcessedMap = new HashMap<String, Object>();
		oppReopenRequestProcessedMap.put("userName", user.getUserName());
		oppReopenRequestProcessedMap.put("opportunityName",
				opp.getOpportunityName());
		oppReopenRequestProcessedMap.put("customerName",
				customer.getCustomerName());
		oppReopenRequestProcessedMap.put("submittedDate", dateStr);
		String text = mergeTmplWithData(oppReopenRequestProcessedMap,
				reopenOpportunityProcessedTemplateLoc);
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
		String remarks = "NA";
		String operation = null;
		String reason = "";
		String dateStr = formatDate(date);
		StringBuffer subject = new StringBuffer(environmentName);
		logger.info("sendWorkflowPendingMail :: RequestId" + requestId);
		WorkflowRequestT workflowRequestT = workflowRequestRepository
				.findOne(requestId);
		if (workflowRequestT != null) {
			String entityId = workflowRequestT.getEntityId();
			logger.debug("Request fetched:");
			logger.debug("EntityId:" + entityId);
			switch (EntityTypeId.valueOf(EntityTypeId.getName(entityTypeId))) {
			case CUSTOMER:
				workflowEntity = Constants.WORKFLOW_CUSTOMER;
				WorkflowCustomerT workflowCustomerT = workflowCustomerRepository
						.findOne(entityId);
				if (!StringUtils.isEmpty(workflowCustomerT.getRemarks())) {
					remarks = workflowCustomerT.getRemarks();
				}
				workflowEntityName = workflowCustomerT.getCustomerName();
				geography = workflowCustomerT.getGeography();
				userName = userRepository
						.findUserNameByUserId(workflowCustomerT.getCreatedBy());
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
			case COMPETITOR:
				workflowEntity = Constants.WORKFLOW_COMPETITOR;
				WorkflowCompetitorT workflowCompetitor = workflowCompetitorRepository
						.findOne(entityId);
				workflowEntityName = workflowCompetitor
						.getWorkflowCompetitorName();
				userName = userRepository
						.findUserNameByUserId(workflowCompetitor.getCreatedBy());
				subject.append(Constants.WORKFLOW_COMPETITOR_PENDING_SUBJECT)
						.append(" ").append(Constants.FROM).append(" ")
						.append(userName);
				operation = Constants.WORKFLOW_OPERATION_CREATION_TEMPLATE;
				break;
			case OPPORTUNITY:
				workflowEntity = Constants.WORKFLOW_OPPORTUNITY_REOPEN;
				OpportunityT opportunity = opportunityRepository
						.findOne(entityId);
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
						.append(" ")
						.append(workflowSubmittedStep.getComments()).toString();
				logger.info("Subject :" + subject);
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
					recepientIds
							.addAll(userAccessPrivilegesRepository
									.findUserIdsForWorkflowUserGroup(geography,
											Constants.Y,
											UserGroup.GEO_HEADS.getValue()));
					logger.debug("recepient Ids for GEO Heads :" + recepientIds);
					userGroupOrUserRoleOrUserId = Constants.WORKFLOW_GEO_HEADS;
					ccIds.addAll(userAccessPrivilegesRepository
							.findUserIdsForWorkflowUserGroup(geography,
									Constants.Y, UserGroup.PMO.getValue()));
					logger.debug("CCIds for PMO :" + ccIds);
					break;
				case Constants.WORKFLOW_PMO:
					recepientIds.addAll(userAccessPrivilegesRepository
							.findUserIdsForWorkflowUserGroup(geography,
									Constants.Y, UserGroup.PMO.getValue()));
					userGroupOrUserRoleOrUserId = Constants.WORKFLOW_PMO;
				default:
					break;
				}
			}
			if (workflowStepPending.getUserRole() != null) {
				recepientIds
						.addAll(userRepository
								.findUserIdByUserRole(workflowStepPending
										.getUserRole()));
				userGroupOrUserRoleOrUserId = workflowStepPending.getUserRole();

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
			workflowMap.put("remarks", remarks);
			workflowMap.put("geography", geography);
			workflowMap.put("operation", operation);
			workflowMap.put("reason", reason);
			String tmpl;
			if (EntityTypeId.valueOf(EntityTypeId.getName(entityTypeId))
					.equals(EntityTypeId.CUSTOMER)) {
				tmpl = workflowCustomerPendingTemplateLoc;
			} else {
				tmpl = workflowPendingTemplateLoc;
			}
			String text = mergeTmplWithData(workflowMap, tmpl);

			logger.info("framed text for mail :" + text);
			message.setSubject(subject.toString());
			message.setMessage(text);

			destMailSender.send(message);
			logger.info("Mail Sent for request"
					+ workflowRequestT.getRequestId());

		} else {
			logger.error("request not fetched");
		}
	}

	/**
	 * This method is used to send the mail on approval of a workflow entity
	 * 
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
		String subject = formatSubject(workflowCustomerApprovedOrRejectSubject);
		WorkflowRequestT workflowRequestT = workflowRequestRepository
				.findOne(requestId);
		String entityId = workflowRequestT.getEntityId();
		String remarks = "NA";
		WorkflowStepT workflowStepSubmitted = workflowStepRepository
				.findByRequestIdAndStepStatus(requestId,
						WorkflowStatus.SUBMITTED.getStatus());
		if (workflowStepSubmitted != null) {
			switch (EntityTypeId.valueOf(EntityTypeId.getName(entityTypeId))) {
			case CUSTOMER:
				entity = Constants.WORKFLOW_CUSTOMER;
				WorkflowCustomerT workflowCustomerT = workflowCustomerRepository
						.findOne(entityId);
				if (!StringUtils.isEmpty(workflowCustomerT.getRemarks())) {
					remarks = workflowCustomerT.getRemarks();
				}
				entityName = workflowCustomerT.getCustomerName();
				geography = workflowCustomerT.getGeography();
				userName = userRepository
						.findUserNameByUserId(workflowCustomerT.getCreatedBy());
				operation = Constants.WORKFLOW_OPERATION_CREATE;
				recepientIds.add(workflowCustomerT.getCreatedBy());
				break;
			case PARTNER:
				entity = Constants.WORKFLOW_PARTNER;
				WorkflowPartnerT workflowPartnerT = workflowPartnerRepository
						.findOne(entityId);
				entityName = workflowPartnerT.getPartnerName();
				geography = workflowPartnerT.getGeography();
				userName = userRepository.findUserNameByUserId(workflowPartnerT
						.getCreatedBy());
				operation = Constants.WORKFLOW_OPERATION_CREATE;
				recepientIds.add(workflowPartnerT.getCreatedBy());
				break;
			case COMPETITOR:
				entity = Constants.WORKFLOW_COMPETITOR;
				WorkflowCompetitorT workflowCompetitor = workflowCompetitorRepository
						.findOne(entityId);
				entityName = workflowCompetitor.getWorkflowCompetitorName();
				userName = userRepository
						.findUserNameByUserId(workflowCompetitor.getCreatedBy());
				operation = Constants.WORKFLOW_OPERATION_CREATE;
				recepientIds.add(workflowCompetitor.getCreatedBy());
				break;
			case OPPORTUNITY:
				entity = Constants.WORKFLOW_OPPORTUNITY_REOPEN;
				OpportunityT opportunity = opportunityRepository
						.findOne(entityId);
				geography = opportunity.getCustomerMasterT().getGeography();
				entityName = opportunity.getOpportunityName();
				userName = userRepository.findUserNameByUserId(workflowRequestT
						.getCreatedBy());
				operation = Constants.WORKFLOW_OPERATION_REOPEN;
				recepientIds.add(workflowRequestT.getCreatedBy());
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
									.findUserIdsForWorkflowUserGroup(geography,
											Constants.Y,
											UserGroup.GEO_HEADS.getValue()));
							ccIds.addAll(userAccessPrivilegesRepository
									.findUserIdsForWorkflowUserGroup(geography,
											Constants.Y,
											UserGroup.PMO.getValue()));
							break;
						case Constants.WORKFLOW_PMO:
							ccIds.addAll(userAccessPrivilegesRepository
									.findUserIdsForWorkflowUserGroup(geography,
											Constants.Y,
											UserGroup.PMO.getValue()));
							break;
						default:
							break;

						}
					}
					if (workflowStep.getUserRole() != null) {
						ccIds.addAll(userRepository
								.findUserIdByUserRole(workflowStep
										.getUserRole()));
					}
					if (workflowStep.getUserId() != null) {
						String[] workflowUserIds = workflowStep.getUserId()
								.split(",");
						List<String> workflowUserIdList = Arrays
								.asList(workflowUserIds);
						ccIds.addAll(workflowUserIdList);
					}
				}
			}
			String comment = "";
			message.setRecipients(listMailIdsFromUserIds(recepientIds));

			if (CollectionUtils.isNotEmpty(ccIds)) {
				message.setCcList(listMailIdsFromUserIds(Lists
						.newArrayList(ccIds)));
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
							.append(workflowStepForFinalApproval.getComments())
							.toString();
				}
				approvedOrRejectedUserName = userRepository
						.findUserNameByUserId(workflowStepForFinalApproval
								.getUserId());
				workflowMap.put("status", "approved");
				workflowMap.put("approvedOrRejectedUserName",
						approvedOrRejectedUserName);
				workflowMap.put("comment", comment);
				workflowMap.put("remarks", remarks);
				workflowMap.put("geography", geography);
			} else {
				WorkflowStepT workflowStepRejected = workflowStepRepository
						.findByRequestIdAndStepStatus(requestId,
								WorkflowStatus.REJECTED.getStatus());
				approvedOrRejectedUserName = userRepository
						.findUserNameByUserId(workflowStepRejected.getUserId());
				comment = new StringBuffer(Constants.WORKFLOW_COMMENTS)
						.append(" ").append(workflowStepRejected.getComments())
						.toString();
				workflowMap.put("approvedOrRejectedUserName",
						approvedOrRejectedUserName);
				workflowMap.put("status", "rejected");
				workflowMap.put("comment", comment);
				workflowMap.put("remarks", remarks);
				workflowMap.put("geography", geography);
			}

			String tmpl;
			if (EntityTypeId.valueOf(EntityTypeId.getName(entityTypeId))
					.equals(EntityTypeId.CUSTOMER)) {
				tmpl = workflowCustomerApproveOrRejectTemplateLoc;
			} else {
				tmpl = workflowApproveOrRejectTemplateLoc;
			}
			text = mergeTmplWithData(workflowMap, tmpl);
			logger.info("framed text for mail :" + text);
			message.setMessage(text);
			logger.info("before sending mail");
			destMailSender.send(message);
			logger.info("Mail Sent for request"
					+ workflowRequestT.getRequestId());
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
			if (user.isActive()) {
				mailIds.add(user.getUserEmailId());
			}
		}
		return mailIds;
	}

	private List<String> listMailIdsFromUserIds(List<String> recipientIdList) {
		List<String> emailIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(recipientIdList)) {
			emailIds = userRepository
					.findUserMailIdsFromActiveUsers(recipientIdList);
		}
		return emailIds;
	}

	/**
	 * merge the data in the given template
	 * 
	 * @param data
	 * @param tmpl
	 * @return
	 */
	private String mergeTmplWithData(Map<String, Object> data, String tmpl) {
		return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
				tmpl, Constants.UTF8, data);
	}

	/**
	 * format the given date to predefined destination date-format
	 * 
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
	 * 
	 * @param subject
	 * @return
	 */
	private String formatSubject(String subject) {
		String sub = new StringBuffer(environmentName).append(" ")
				.append(subject).toString();
		return sub;
	}

	/**
	 * This method is used to send email to the System Admins, the Details
	 * captured for changing the supervisor details
	 * 
	 * @param userDetailsEmailAppendString
	 * @param userT
	 * @throws Exception
	 */
	public void sendEscalateUserDetailsAutomatedEmail(
			String existingSupervisorDetails, String newSupervisorDetails,
			UserT userT) throws Exception {

		DestinationMailMessage message = new DestinationMailMessage();
		List<String> recipientIds = userService
				.findByUserRole(Constants.SYSTEM_ADMIN);
		message.setRecipients(listMailIdsFromUserIds(recipientIds));

		List<String> ccList = listMailIdsFromUserIds(Lists.newArrayList(
				userT.getUserId(), userT.getSupervisorUserId()));
		message.setCcList(ccList);

		DateFormat df = new SimpleDateFormat(dateFormatStr);
		String dateStr = df.format(new Date());
		String subject = formatSubject(userDetailsApprovalSubject);

		message.setSubject(subject);

		Map<String, Object> userDetailsApprovalMap = Maps.newHashMap();
		userDetailsApprovalMap.put("username", userT.getUserName());
		userDetailsApprovalMap.put("date", dateStr);
		userDetailsApprovalMap.put("existingSupervisorDetails",
				existingSupervisorDetails);
		userDetailsApprovalMap
				.put("newSupervisorDetails", newSupervisorDetails);
		String text = mergeTmplWithData(userDetailsApprovalMap,
				userDetailsApprovalTemplate);
		message.setMessage(text);
		destMailSender.send(message);

	}

	
//	 /**
//		 * This method is used to send the email notification to group of users on 
//		 * opportunity won or lost 
//		 * @param entityId
//		 */
//		 public void sendOpportunityWonLostNotification(String entityId) throws Exception {
//			 logger.info("Inside sendOpportunityWonLostNotification method");
//			 OpportunityT opportunity = opportunityRepository.findOne(entityId);
//			 List<String> recepientIds = new ArrayList<String>();
//			 String templateLoc = null;
//			 StringBuffer subject = new StringBuffer(mailSubjectAppendEnvName);
//			 if (opportunity != null) {
//				 String opportunityName = opportunity.getOpportunityName();
//				 logger.info("OpportunityId :" + entityId + ", Opportunity Name : "
//						 + opportunityName);
//				 String customerName = opportunity.getCustomerMasterT()
//						 .getCustomerName();
//				 String opportunityOwner = userRepository
//						 .findUserNameByUserId(opportunity.getOpportunityOwner());
//				 String dealValueUSDInNumberScale = "";
//				 Integer digitalBidValue = opportunity.getDigitalDealValue();
//				 logger.info("digital Bid Value : "+digitalBidValue);
//				 if(digitalBidValue!=null) {
//					 String currencyType = opportunity.getDealCurrency();
//					 //Converting deal value to USD
//					 BigDecimal digitalBidValueUSD = opportunityDownloadService.convertCurrencyToUSD(currencyType, digitalBidValue);
//					 logger.info("digitalBidValueUSD : "+digitalBidValueUSD);
//					 //Converting the USD value in number scale
//					 dealValueUSDInNumberScale = NumericUtil.toUSDinNumberScale(digitalBidValueUSD);
//					 logger.info("dealValueUSDInNumberScale : "+dealValueUSDInNumberScale);
//				 }
//				 
//				 DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
//				 Date dealClosureDate = opportunity.getDealClosureDate();
//				 String dealClosureDateStr = df.format(dealClosureDate);
//				 String opportunityDescription = StringUtils.isNotEmpty(opportunity
//						 .getOpportunityDescription()) ? opportunity
//								 .getOpportunityDescription() : Constants.NOT_AVAILABLE;
//				 List<String> winLossFactors = new ArrayList<String>();
//				 List<String> opportunitySalesSupportOwners = new ArrayList<String>();
//				 List<String> opportunitySubSps = new ArrayList<String>();
//				 List<String> opportunityCompetitors = new ArrayList<String>();
//				 List<String> opportunityIncumbentCompetitors = new ArrayList<String>();
//				 List<String> customerContact = new ArrayList<String>();
//				 String competitorNames = Constants.NOT_AVAILABLE;
//				 String incumbentCompetitorNames = Constants.NOT_AVAILABLE;
//	             String customerContacts = Constants.NOT_AVAILABLE;
//				 String salesSupportOwners = "";
//				 String factorsForWinLoss = "";
//				 String subSpsStr = "";
//				 String withSupportFrom = "";
//				 String displaySubSp = null;
//				 //getting subsps
//				 for(OpportunitySubSpLinkT opportunitySubSpLinkT : opportunity.getOpportunitySubSpLinkTs()) {
//					 displaySubSp = subSpRepository.findOne(opportunitySubSpLinkT.getSubSp()).getDisplaySubSp();
//					 opportunitySubSps.add(displaySubSp);
//				 }
//				 String primaryDisplaySubSp = opportunitySubSpLinkTRepository.findPrimaryDisplaySubSpByOpportunityId(entityId);
//				 if(StringUtils.isNotEmpty(primaryDisplaySubSp)) {
//					 subSpsStr = primaryDisplaySubSp;
//				 }
//				 //getting sales support owners
//				 for (OpportunitySalesSupportLinkT opportunitySalesSupportLinkT : opportunity
//						 .getOpportunitySalesSupportLinkTs()) {
//
//					 if(!opportunitySalesSupportLinkT.getSalesSupportOwner().contains("pmo")) {
//						 opportunitySalesSupportOwners.add(userRepository
//								 .findUserNameByUserId(opportunitySalesSupportLinkT
//										 .getSalesSupportOwner()));
//					 }
//
//				 }
//				 if (CollectionUtils.isNotEmpty(opportunitySalesSupportOwners)) {
//					 withSupportFrom = Constants.WITH_SUPPORT_FROM;
//					 salesSupportOwners = splitStringByComma(opportunitySalesSupportOwners);
//					 logger.info("sales support owners : "+salesSupportOwners);
//				 }
//				 
//					// Getting Opportunity Competitors
//					if (CollectionUtils.isNotEmpty(opportunity
//							.getOpportunityCompetitorLinkTs())) {
//						for (OpportunityCompetitorLinkT opportunityCompetitorLinkT : opportunity
//								.getOpportunityCompetitorLinkTs()) {
//							if (opportunityCompetitorLinkT.getIncumbentFlag().equals(
//									Constants.Y)) {
//								opportunityIncumbentCompetitors
//										.add(opportunityCompetitorLinkT
//												.getCompetitorName());
//							} else {
//								opportunityCompetitors.add(opportunityCompetitorLinkT
//										.getCompetitorName());
//							}
//							
//						}
//					}
//					
//					if (CollectionUtils.isNotEmpty(opportunityCompetitors)) {
//						competitorNames = splitStringByComma(opportunityCompetitors);
//					}
//
//					if (CollectionUtils.isNotEmpty(opportunityIncumbentCompetitors)) {
//						incumbentCompetitorNames = splitStringByComma(opportunityIncumbentCompetitors);
//					}
//
//	             //getting opportunity customer contacts
//						customerContact = contactRepository.findCustomerContactNamesByOpportinityId(entityId);
//						
//					if(CollectionUtils.isNotEmpty(customerContact)) {
//						customerContacts = StringUtils.join(customerContact, ", ");
//					}
//					//crm id
//				 recepientIds.add(opportunityWonLostGroupMailId);
//				 if (opportunity.getSalesStageCode() == 9) {
//						logger.info("opportunity Won");
//						subject.append("DESTiNATION:").append(" ").append(subSpsStr)
//								.append(" ").append("Deal Won for").append(" ")
//								.append(customerName);
//						logger.info("Subject for opportunity won : {}", subject);
//						templateLoc = opportunityWonTemplateLoc;
//						// Getting Win Loss Factors
//						for (OpportunityWinLossFactorsT opportunityWinLossFactorsT : opportunity
//								.getOpportunityWinLossFactorsTs()) {
//							// if the win/Loss factor is Win-Other,adding the win loss
//							// others description to the factors
//							if (opportunityWinLossFactorsT.getWinLossFactor().equals(
//									Constants.WIN_OTHER)) {
//								if (StringUtils.isNotEmpty(opportunityWinLossFactorsT
//										.getWinLossOthersDescription())) {
//									winLossFactors.add(opportunityWinLossFactorsT
//											.getWinLossOthersDescription());
//								}
//							} else {
//								winLossFactors.add(opportunityWinLossFactorsT
//										.getWinLossFactor());
//							}
//						}
//
//					}
//					// If the Opportunity is lost, framing the subject ang getting the
//					// template loc and loss factors
//					if (opportunity.getSalesStageCode() == 10) {
//						logger.info("OpportunityLost");
//						subject.append("DESTiNATION:").append(" ").append(subSpsStr)
//								.append(" ").append("Deal Lost for").append(" ")
//								.append(customerName);
//						logger.info("Subject for opportunity lost :" + subject);
//						templateLoc = opportunityLostTemplateLoc;
//
//						for (OpportunityWinLossFactorsT opportunityWinLossFactorsT : opportunity
//								.getOpportunityWinLossFactorsTs()) {
//							// if the win/Loss factor is Loss-Other,adding the win loss
//							// others description to the factors
//							if (opportunityWinLossFactorsT.getWinLossFactor().equals(
//									Constants.LOSS_OTHER)) {
//								if (StringUtils.isNotEmpty(opportunityWinLossFactorsT
//										.getWinLossOthersDescription())) {
//									winLossFactors.add(opportunityWinLossFactorsT
//											.getWinLossOthersDescription());
//								}
//							} else {
//								winLossFactors.add(opportunityWinLossFactorsT
//										.getWinLossFactor());
//							}
//>>>>>>> master
//						}
//					} else {
//						winLossFactors.add(opportunityWinLossFactorsT
//								.getWinLossFactor());
//					}
//<<<<<<< HEAD
//			
//=======
//
//					if (CollectionUtils.isNotEmpty(winLossFactors)) {
//						factorsForWinLoss = StringUtils.join(winLossFactors, ", ");
//						logger.info("factors for win/loss : " + factorsForWinLoss);
//					}
//				 if(templateLoc!=null) {
//					 DestinationMailMessage message = new DestinationMailMessage();
//					 message.setRecipients(Lists.newArrayList(opportunityWonLostGroupMailId));
//					 logger.info("To email address : "+opportunityWonLostGroupMailId);
//					 message.setSubject(subject.toString());
//					 Map<String, Object> map = new HashMap<String, Object>();
//					 map.put("opportunityName", opportunityName);
//					 map.put("customerName", customerName);
//					 map.put("factorsForWinLoss", factorsForWinLoss);
//					 map.put("opportunityOwner", opportunityOwner);
//					 map.put("salesSupportOwners", salesSupportOwners);
//					 map.put("digitalBidValue", dealValueUSDInNumberScale);
//					 map.put("opportunityDescription", opportunityDescription);
//					 map.put("withSupportFrom", withSupportFrom);
//					 map.put("dealClosureDate", dealClosureDateStr);
//					 map.put("competitorNames", competitorNames);
//					 map.put("incumbentCompetitorNames",
//								incumbentCompetitorNames);
//					 map.put("clientContact", customerContacts);
//					 map.put("crmId", 
//							 StringUtils.isNotEmpty(opportunity.getCrmId()) ? 
//									 opportunity.getCrmId() : Constants.NOT_AVAILABLE);	
//
//					 String text = mergeTmplWithData(map, templateLoc);
//					 logger.info("framed text for mail :" + text);
//					 message.setMessage(text);
//					 logger.info("before sending mail");
//					 destMailSender.send(message);
//					 logger.info("Mail Sent for opportunity win/loss, Opportunity Id : "+entityId);
//				 }
//
//			 } else {
//				 throw new DestinationException("Opportunity not found : "+entityId);
//			 }
//
//
//		 }

	
	/**
	 * This method is used to send the email notification to group of users for
	 * opportunity updates
	 * 
	 * @param entityId
	 * @param dealValue
	 */
	public void sendOpportunityEmailNotification(String entityId,
			Double dealValue) throws Exception {

		logger.info("Inside sendOpportunityEmailNotification method");

		String templateLoc = null;
		StringBuffer subject = new StringBuffer(mailSubjectAppendEnvName);
		Map<String, Object> data = new HashMap<String, Object>();
		List<String> opportunitySalesSupportOwners = new ArrayList<String>();
		List<String> winLossFactors = new ArrayList<String>();
		String factorsForWinLoss = "";
		String salesSupportOwners = "";
		String withSupportFrom = "";
		String subSpsStr = "";
		List<String> opportunityCompetitors = new ArrayList<String>();
		List<String> opportunityIncumbentCompetitors = new ArrayList<String>();
		List<String> customerContact = new ArrayList<String>();
		String customerContacts = Constants.NOT_AVAILABLE;
		String competitorNames = Constants.NOT_AVAILABLE;
		String incumbentCompetitorNames = Constants.NOT_AVAILABLE;
		String dealClosureDateStr = "";
		BigDecimal dealValueUSD = new BigDecimal(dealValue);
		OpportunityT opportunity = opportunityRepository.findOne(entityId);
		if (opportunity != null) {
			String customerName = opportunity.getCustomerMasterT()
					.getCustomerName();
			String iou = opportunity.getCustomerMasterT().getIou();
			String opportunityName = opportunity.getOpportunityName();
			String dealValueInNumberScale = NumericUtil
					.toUSDinNumberScale(dealValueUSD);
			logger.info("OpportunityId: {}, Opportunity Name: {}", entityId,
					opportunityName);

			String opportunityDescription = opportunity
					.getOpportunityDescription();

			String primaryOwner = userRepository
					.findUserNameByUserId(opportunity.getOpportunityOwner());

			// getting sales support owners
			if (CollectionUtils.isNotEmpty(opportunity
					.getOpportunitySalesSupportLinkTs())) {
				for (OpportunitySalesSupportLinkT opportunitySalesSupportLinkT : opportunity
						.getOpportunitySalesSupportLinkTs()) {
					UserT salesSupportOwner = userRepository
							.findOne(opportunitySalesSupportLinkT
									.getSalesSupportOwner());
					if (!StringUtils.equals(salesSupportOwner.getUserGroup(),
							UserGroup.PMO.getValue())) {
						opportunitySalesSupportOwners.add(salesSupportOwner
								.getUserName());
					}

				}
				if (CollectionUtils.isNotEmpty(opportunitySalesSupportOwners)) {
					withSupportFrom = Constants.WITH_SUPPORT_FROM;
					salesSupportOwners = StringUtils.join(
							opportunitySalesSupportOwners, ", ");
					logger.info("sales support owners : " + salesSupportOwners);
				}
			}

			// getting competitors

			// Getting Opportunity Competitors
			if (CollectionUtils.isNotEmpty(opportunity
					.getOpportunityCompetitorLinkTs())) {
				for (OpportunityCompetitorLinkT opportunityCompetitorLinkT : opportunity
						.getOpportunityCompetitorLinkTs()) {
					if (opportunityCompetitorLinkT.getIncumbentFlag().equals(
							Constants.Y)) {
						opportunityIncumbentCompetitors
								.add(opportunityCompetitorLinkT
										.getCompetitorName());
					} else {
						opportunityCompetitors.add(opportunityCompetitorLinkT
								.getCompetitorName());
					}

				}
			}

			if (CollectionUtils.isNotEmpty(opportunityCompetitors)) {
				competitorNames = StringUtils
						.join(opportunityCompetitors, ", ");
			}

			if (CollectionUtils.isNotEmpty(opportunityIncumbentCompetitors)) {
				incumbentCompetitorNames = StringUtils.join(
						opportunityIncumbentCompetitors, ", ");
			}

			// getting primary display subsp
			String primaryDisplaySubSp = opportunitySubSpLinkTRepository
					.findPrimaryDisplaySubSpByOpportunityId(entityId);
			if (StringUtils.isNotEmpty(primaryDisplaySubSp)) {
				subSpsStr = primaryDisplaySubSp;
			}

			// getting opportunity customer contacts
			customerContact = contactRepository
					.findCustomerContactNamesByOpportinityId(entityId);
			if (CollectionUtils.isNotEmpty(customerContact)) {
				customerContacts = StringUtils.join(customerContact, ", ");
			}

			// Getting bid details
			BidDetailsT bidDetailsT = bidDetailsTRepository
					.findFirstByOpportunityIdOrderByModifiedDatetimeDesc(opportunity
							.getOpportunityId());
			if (bidDetailsT != null) {
				data.put("actualSubmissionDate", ACTUAL_FORMAT
						.format(bidDetailsT.getActualBidSubmissionDate()));
				data.put("winProbbility", bidDetailsT.getWinProbability());
				data.put("expectedOutcomeDate", ACTUAL_FORMAT
						.format(bidDetailsT.getExpectedDateOfOutcome()));
			}
			if(opportunity.getDealClosureDate()!=null) {
				dealClosureDateStr = ACTUAL_FORMAT.format(opportunity.getDealClosureDate());
			}
			String[] searchList = { "<digitalBidValue>", "<masterCustomerName>" };
			String[] replacementList = { dealValueInNumberScale, customerName };
			switch (SalesStageCode.valueOf(opportunity.getSalesStageCode())) {

			case RFP_SUBMITTED: // RFP Submitted
				logger.info("Sales Stage : RFP Submitted");
				subject.append(StringUtils.replaceEach(rfpHighValueMailSub,
						searchList, replacementList));
				templateLoc = opportunityRFPSubmittedTemplateLoc;
				break;
			case SHORTLISTED:
				logger.info("Sales Stage : Shortlisted");
				subject.append(StringUtils.replaceEach(
						shortlistedHighValueMailSub, searchList,
						replacementList));
				templateLoc = opportunityShortlistedTemplateLoc;
				break;
			case SELECTED:
				logger.info("Sales Stage : Selected");
				subject.append(StringUtils.replaceEach(
						selectedHighValueMailSub, searchList, replacementList));
				templateLoc = opportunitySelectedTemplateLoc;
				break;
			case CONTRACT_NEGOTIATION:
				logger.info("Sales Stage : Contract Negotiation");
				subject.append(StringUtils.replaceEach(
						contractNegotiationHighValueMailSub, searchList,
						replacementList));
				templateLoc = opportunityContractNegotiationTemplateLoc;
				break;
			case WIN:
				logger.info("Sales Stage : Win");
				subject.append("DESTiNATION:").append(" ").append(subSpsStr)
						.append(" ").append("Deal Won for").append(" ")
						.append(customerName);
				templateLoc = opportunityWonTemplateLoc;
				// Getting Win Loss Factors
				for (OpportunityWinLossFactorsT opportunityWinLossFactorsT : opportunity
						.getOpportunityWinLossFactorsTs()) {
					// if the win/Loss factor is Win-Other,adding the win loss
					// others description to the factors
					if (opportunityWinLossFactorsT.getWinLossFactor().equals(
							Constants.WIN_OTHER)) {
						if (StringUtils.isNotEmpty(opportunityWinLossFactorsT
								.getWinLossOthersDescription())) {
							winLossFactors.add(opportunityWinLossFactorsT
									.getWinLossOthersDescription());
						}
					} else {
						winLossFactors.add(opportunityWinLossFactorsT
								.getWinLossFactor());
					}
				}
				break;
			case LOST:
				logger.info("Sales Stage : Lost");
				subject.append("DESTiNATION:").append(" ").append(subSpsStr)
						.append(" ").append("Deal Lost for").append(" ")
						.append(customerName);
				templateLoc = opportunityLostTemplateLoc;
				for (OpportunityWinLossFactorsT opportunityWinLossFactorsT : opportunity
						.getOpportunityWinLossFactorsTs()) {
					// if the win/Loss factor is Loss-Other,adding the win loss
					// others description to the factors
					if (opportunityWinLossFactorsT.getWinLossFactor().equals(
							Constants.LOSS_OTHER)) {
						if (StringUtils.isNotEmpty(opportunityWinLossFactorsT
								.getWinLossOthersDescription())) {
							winLossFactors.add(opportunityWinLossFactorsT
									.getWinLossOthersDescription());
						}
					} else {
						winLossFactors.add(opportunityWinLossFactorsT
								.getWinLossFactor());
					}
				}
				break;
			default:
				break;

			}

			if (CollectionUtils.isNotEmpty(winLossFactors)) {
				factorsForWinLoss = StringUtils.join(winLossFactors, ", ");
				logger.info("factors for win/loss : " + factorsForWinLoss);
			}

			data.put("customerName", customerName);
			data.put("opportunityName", opportunity.getOpportunityName());
			data.put("salesStage", opportunity.getSalesStageCode());
			data.put("subSp", subSpsStr);
			data.put("iou", iou);
			data.put("opportunityDescription", opportunityDescription);
			data.put("primaryOwner", primaryOwner);
			data.put("salesSupportOwners", salesSupportOwners);
			data.put("withSupportFrom", withSupportFrom);

			data.put("digitalBidValue", dealValueInNumberScale);
			data.put("competitorNames", competitorNames);
			data.put("incumbentCompetitorNames", incumbentCompetitorNames);
			data.put("factorsForWinLoss", factorsForWinLoss);
			data.put(
					"crmId",
					StringUtils.isNotEmpty(opportunity.getCrmId()) ? opportunity
							.getCrmId() : Constants.NOT_AVAILABLE);
			data.put("clientContact", customerContacts);
			data.put("dealClosureDate", dealClosureDateStr);
			DestinationMailMessage message = new DestinationMailMessage();
			message.setRecipients(Lists
					.newArrayList(opportunityWonLostGroupMailId));
			logger.info("To email address : " + opportunityWonLostGroupMailId);
			message.setSubject(subject.toString());
			logger.info("Subject : " + subject.toString());
			String text = mergeTmplWithData(data, templateLoc);
			logger.info("framed text : " + text);
			message.setMessage(text);
			destMailSender.send(message);
			logger.info("Opportunity Mail Sent, Opportunity Id : " + entityId);
		} else {
			throw new DestinationException("Opportunity not found : "
					+ entityId);
		}
	}
	
	 /**
	 * This method is used to send the email notification to group of users on 
	 * opportunity won or lost 
	 * @param entityId
	 */
	 public void sendOpportunityWonLostNotification(String entityId) throws Exception {
		 logger.info("Inside sendOpportunityWonLostNotification method");
		 OpportunityT opportunity = opportunityRepository.findOne(entityId);
		 List<String> recepientIds = new ArrayList<String>();
		 String templateLoc = null;
		 StringBuffer subject = new StringBuffer(mailSubjectAppendEnvName);
		 if (opportunity != null) {
			 String opportunityName = opportunity.getOpportunityName();
			 logger.info("OpportunityId :" + entityId + ", Opportunity Name : "
					 + opportunityName);
			 String customerName = opportunity.getCustomerMasterT()
					 .getCustomerName();
			 String opportunityOwner = userRepository
					 .findUserNameByUserId(opportunity.getOpportunityOwner());
			 String dealValueUSDInNumberScale = "";
			 Integer digitalBidValue = opportunity.getDigitalDealValue();
			 logger.info("digital Bid Value : "+digitalBidValue);
			 if(digitalBidValue!=null) {
				 String currencyType = opportunity.getDealCurrency();
				 //Converting deal value to USD
				 BigDecimal digitalBidValueUSD = opportunityDownloadService.convertCurrencyToUSD(currencyType, digitalBidValue);
				 logger.info("digitalBidValueUSD : "+digitalBidValueUSD);
				 //Converting the USD value in number scale
				 dealValueUSDInNumberScale = NumericUtil.toUSDinNumberScale(digitalBidValueUSD);
				 logger.info("dealValueUSDInNumberScale : "+dealValueUSDInNumberScale);
			 }
			 
			 DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
			 Date dealClosureDate = opportunity.getDealClosureDate();
			 String dealClosureDateStr = df.format(dealClosureDate);
			 String opportunityDescription = StringUtils.isNotEmpty(opportunity
					 .getOpportunityDescription()) ? opportunity
							 .getOpportunityDescription() : Constants.NOT_AVAILABLE;
			 List<String> winLossFactors = new ArrayList<String>();
			 List<String> opportunitySalesSupportOwners = new ArrayList<String>();
			 List<String> opportunitySubSps = new ArrayList<String>();
			 List<String> opportunityCompetitors = new ArrayList<String>();
			 List<String> opportunityIncumbentCompetitors = new ArrayList<String>();
			 List<String> customerContact = new ArrayList<String>();
			 String competitorNames = Constants.NOT_AVAILABLE;
			 String incumbentCompetitorNames = Constants.NOT_AVAILABLE;
             String customerContacts = Constants.NOT_AVAILABLE;
			 String salesSupportOwners = "";
			 String factorsForWinLoss = "";
			 String subSpsStr = "";
			 String withSupportFrom = "";
			 String displaySubSp = null;
			 //getting subsps
			 for(OpportunitySubSpLinkT opportunitySubSpLinkT : opportunity.getOpportunitySubSpLinkTs()) {
				 displaySubSp = subSpRepository.findOne(opportunitySubSpLinkT.getSubSp()).getDisplaySubSp();
				 opportunitySubSps.add(displaySubSp);
			 }
			 String primaryDisplaySubSp = opportunitySubSpLinkTRepository.findPrimaryDisplaySubSpByOpportunityId(entityId);
			 if(StringUtils.isNotEmpty(primaryDisplaySubSp)) {
				 subSpsStr = primaryDisplaySubSp;
			 }
			 //getting sales support owners
			 for (OpportunitySalesSupportLinkT opportunitySalesSupportLinkT : opportunity
					 .getOpportunitySalesSupportLinkTs()) {

				 if(!opportunitySalesSupportLinkT.getSalesSupportOwner().contains("pmo")) {
					 opportunitySalesSupportOwners.add(userRepository
							 .findUserNameByUserId(opportunitySalesSupportLinkT
									 .getSalesSupportOwner()));
				 }

			 }
			 if (CollectionUtils.isNotEmpty(opportunitySalesSupportOwners)) {
				 withSupportFrom = Constants.WITH_SUPPORT_FROM;
				 salesSupportOwners = StringUtils.join(opportunitySalesSupportOwners, ", ");
				 logger.info("sales support owners : "+salesSupportOwners);
			 }
			 
				// Getting Opportunity Competitors
				if (CollectionUtils.isNotEmpty(opportunity
						.getOpportunityCompetitorLinkTs())) {
					for (OpportunityCompetitorLinkT opportunityCompetitorLinkT : opportunity
							.getOpportunityCompetitorLinkTs()) {
						if (opportunityCompetitorLinkT.getIncumbentFlag().equals(
								Constants.Y)) {
							opportunityIncumbentCompetitors
									.add(opportunityCompetitorLinkT
											.getCompetitorName());
						} else {
							opportunityCompetitors.add(opportunityCompetitorLinkT
									.getCompetitorName());
						}
						
					}
				}
				
				if (CollectionUtils.isNotEmpty(opportunityCompetitors)) {
					competitorNames = StringUtils.join(opportunityCompetitors, ", ");
				}

				if (CollectionUtils.isNotEmpty(opportunityIncumbentCompetitors)) {
					incumbentCompetitorNames = StringUtils.join(opportunityIncumbentCompetitors,", ");
				}

             //getting opportunity customer contacts
					customerContact = contactRepository.findCustomerContactNamesByOpportinityId(entityId);
					
				if(CollectionUtils.isNotEmpty(customerContact)) {
					customerContacts = StringUtils.join(customerContact, ", ");
				}
				//crm id
			 recepientIds.add(opportunityWonLostGroupMailId);
			 if (opportunity.getSalesStageCode() == 9) {
					logger.info("opportunity Won");
					subject.append("DESTiNATION:").append(" ").append(subSpsStr)
							.append(" ").append("Deal Won for").append(" ")
							.append(customerName);
					logger.info("Subject for opportunity won : {}", subject);
					templateLoc = opportunityWonTemplateLoc;
					// Getting Win Loss Factors
					for (OpportunityWinLossFactorsT opportunityWinLossFactorsT : opportunity
							.getOpportunityWinLossFactorsTs()) {
						// if the win/Loss factor is Win-Other,adding the win loss
						// others description to the factors
						if (opportunityWinLossFactorsT.getWinLossFactor().equals(
								Constants.WIN_OTHER)) {
							if (StringUtils.isNotEmpty(opportunityWinLossFactorsT
									.getWinLossOthersDescription())) {
								winLossFactors.add(opportunityWinLossFactorsT
										.getWinLossOthersDescription());
							}
						} else {
							winLossFactors.add(opportunityWinLossFactorsT
									.getWinLossFactor());
						}
					}

				}
				// If the Opportunity is lost, framing the subject ang getting the
				// template loc and loss factors
				if (opportunity.getSalesStageCode() == 10) {
					logger.info("OpportunityLost");
					subject.append("DESTiNATION:").append(" ").append(subSpsStr)
							.append(" ").append("Deal Lost for").append(" ")
							.append(customerName);
					logger.info("Subject for opportunity lost :" + subject);
					templateLoc = opportunityLostTemplateLoc;

					for (OpportunityWinLossFactorsT opportunityWinLossFactorsT : opportunity
							.getOpportunityWinLossFactorsTs()) {
						// if the win/Loss factor is Loss-Other,adding the win loss
						// others description to the factors
						if (opportunityWinLossFactorsT.getWinLossFactor().equals(
								Constants.LOSS_OTHER)) {
							if (StringUtils.isNotEmpty(opportunityWinLossFactorsT
									.getWinLossOthersDescription())) {
								winLossFactors.add(opportunityWinLossFactorsT
										.getWinLossOthersDescription());
							}
						} else {
							winLossFactors.add(opportunityWinLossFactorsT
									.getWinLossFactor());
						}
					}
				}

				if (CollectionUtils.isNotEmpty(winLossFactors)) {
					factorsForWinLoss = StringUtils.join(winLossFactors, ", ");
					logger.info("factors for win/loss : " + factorsForWinLoss);
				}
			 if(templateLoc!=null) {
				 DestinationMailMessage message = new DestinationMailMessage();
				 message.setRecipients(Lists.newArrayList(opportunityWonLostGroupMailId));
				 logger.info("To email address : "+opportunityWonLostGroupMailId);
				 message.setSubject(subject.toString());
				 Map<String, Object> map = new HashMap<String, Object>();
				 map.put("opportunityName", opportunityName);
				 map.put("customerName", customerName);
				 map.put("factorsForWinLoss", factorsForWinLoss);
				 map.put("opportunityOwner", opportunityOwner);
				 map.put("salesSupportOwners", salesSupportOwners);
				 map.put("digitalBidValue", dealValueUSDInNumberScale);
				 map.put("opportunityDescription", opportunityDescription);
				 map.put("withSupportFrom", withSupportFrom);
				 map.put("dealClosureDate", dealClosureDateStr);
				 map.put("competitorNames", competitorNames);
				 map.put("incumbentCompetitorNames",
							incumbentCompetitorNames);
				 map.put("clientContact", customerContacts);
				 map.put("crmId", 
						 StringUtils.isNotEmpty(opportunity.getCrmId()) ? 
								 opportunity.getCrmId() : Constants.NOT_AVAILABLE);	

				 String text = mergeTmplWithData(map, templateLoc);
				 logger.info("framed text for mail :" + text);
				 message.setMessage(text);
				 logger.info("before sending mail");
				 destMailSender.send(message);
				 logger.info("Mail Sent for opportunity win/loss, Opportunity Id : "+entityId);
			 }

		 } else {
			 throw new DestinationException("Opportunity not found : "+entityId);
		 }


	 }
	 
	 /**
		 * This method is used to share an entity through email to group of users  
		 * @param entityId
		 * @param entityType
		 * @param recipientIds
		 * @param sender
		 * @param url
		 */
	 public void sendShareEmail(String entityId, String entityType, String recipientIds,String sender,String url) throws Exception{
		 recipientIds = recipientIds.substring(1);
		 String[] recipients = recipientIds.split(",");
		 for(String recipient : recipients){
			 UserT recipientUser = userRepository.findOne(recipient);	   
      	     String recipientName = recipientUser.getUserName();
      	     String recipientEmailId = recipientUser.getUserEmailId();
      	     if(!StringUtils.isEmpty(recipientEmailId)){
      	       UserT senderUser = userRepository.findOne(sender);	   
          	   String senderName = senderUser.getUserName();
          	   
          	   Map<String, Object> map = new HashMap<String, Object>();
          	   map.put("sender", senderName);
          	   map.put("recipient", recipientName);
          	   map.put("url", url);
          	   
          	   DestinationMailMessage message = new DestinationMailMessage();
          	   
          	   List<String> recipientMailIds = Lists.newArrayList();
 	           recipientMailIds.add(recipientEmailId);
     	       message.setRecipients(recipientMailIds);
     	       
          	   switch (EntityType.valueOf(entityType)) {
          	 	 case CONNECT : 
          	 		 ConnectT connect = connectRepository.findOne(entityId);
          	 		 String connectName = connect.getConnectName();
          	 		 map.put("connectName", connectName);
          	 		 String textConnect = mergeTmplWithData(map, shareConnectTemplate);
          	 		 logger.info("framed text for mail :" + textConnect);
          	 		 message.setMessage(textConnect);
			         message.setSubject(shareConnectSubject);
			         break;
			         
          	 	 case OPPORTUNITY :   
          	 		 OpportunityT opportunity = opportunityRepository.findOne(entityId);
				     String opportunityName = opportunity.getOpportunityName();
				 	 map.put("opportunityName", opportunityName);
				 	 String textOpp = mergeTmplWithData(map, shareOpportunityTemplate);
				   	 logger.info("framed text for mail :" + textOpp);
				 	 message.setMessage(textOpp);
				   	 message.setSubject(shareOpportunitySubject);
				   	 break;
				  
				 default :
				   		 break;
          	   }
          	 try{
          		 destMailSender.send(message);
          		 logger.info("Mail Sent for {} Id : {} to {}",entityType,entityId,recipientName);
          	   } catch(Exception e){
          		 logger.error("Error sending mail : {}", e.getMessage());
          	   }
      	     }
		 }
		 
		 
	 }


}
