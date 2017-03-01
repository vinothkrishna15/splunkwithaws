package com.tcs.destination.utils;

import static com.tcs.destination.utils.DateUtils.ACTUAL_FORMAT;
import static com.tcs.destination.utils.DateUtils.DATE_FORMAT_MONTH_NAME;

import java.io.ByteArrayOutputStream;
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
import java.util.Map.Entry;
import java.util.Set;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tcs.destination.bean.AuditDeliveryIntimatedCentreLinkT;
import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.DeliveryCentreT;
import com.tcs.destination.bean.DeliveryClusterT;
import com.tcs.destination.bean.DeliveryIntimatedCentreLinkT;
import com.tcs.destination.bean.DeliveryIntimatedT;
import com.tcs.destination.bean.DeliveryMasterManagerLinkT;
import com.tcs.destination.bean.DeliveryMasterT;
import com.tcs.destination.bean.DestinationMailMessage;
import com.tcs.destination.bean.DocumentsT;
import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.bean.OpportunityCompetitorLinkT;
import com.tcs.destination.bean.OpportunityDeliveryCentreMappingT;
import com.tcs.destination.bean.OpportunityReopenRequestT;
import com.tcs.destination.bean.OpportunitySalesSupportLinkT;
import com.tcs.destination.bean.OpportunitySubSpLinkT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.OpportunityWinLossFactorsT;
import com.tcs.destination.bean.UserAccessRequestT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.bean.WorkflowBfmT;
import com.tcs.destination.bean.WorkflowCompetitorT;
import com.tcs.destination.bean.WorkflowCustomerT;
import com.tcs.destination.bean.WorkflowPartnerT;
import com.tcs.destination.bean.WorkflowRequestT;
import com.tcs.destination.bean.WorkflowStepT;
import com.tcs.destination.data.repository.AuditDeliveryIntimatedCentreLinkRepository;
import com.tcs.destination.data.repository.BidDetailsTRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.DeliveryCentreRepository;
import com.tcs.destination.data.repository.DeliveryClusterRepository;
import com.tcs.destination.data.repository.DeliveryIntimatedRepository;
import com.tcs.destination.data.repository.DeliveryMasterManagerLinkRepository;
import com.tcs.destination.data.repository.DeliveryMasterRepository;
import com.tcs.destination.data.repository.DocumentsTRepository;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.data.repository.OpportunityDeliveryCentreMappingTRepository;
import com.tcs.destination.data.repository.OpportunityReopenRequestRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.OpportunitySalesSupportLinkTRepository;
import com.tcs.destination.data.repository.OpportunitySubSpLinkTRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.data.repository.UserAccessPrivilegesRepository;
import com.tcs.destination.data.repository.UserAccessRequestRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.data.repository.WorkflowBfmTRepository;
import com.tcs.destination.data.repository.WorkflowCompetitorTRepository;
import com.tcs.destination.data.repository.WorkflowCustomerTRepository;
import com.tcs.destination.data.repository.WorkflowPartnerRepository;
import com.tcs.destination.data.repository.WorkflowProcessTemplateRepository;
import com.tcs.destination.data.repository.WorkflowRequestTRepository;
import com.tcs.destination.data.repository.WorkflowStepTRepository;
import com.tcs.destination.enums.DeliveryStage;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.enums.EntityTypeId;
import com.tcs.destination.enums.Geography;
import com.tcs.destination.enums.RequestType;
import com.tcs.destination.enums.SalesStageCode;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.enums.UserRole;
import com.tcs.destination.enums.WorkflowStatus;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.helper.WeeklyReportHelper;
import com.tcs.destination.service.DeliveryMasterService;
import com.tcs.destination.service.NumericUtil;
import com.tcs.destination.service.OpportunityDownloadService;
import com.tcs.destination.service.OpportunityService;
import com.tcs.destination.service.UserService;

@Component
public class DestinationMailUtils {
	
	private static final Logger logger = LoggerFactory
			.getLogger(DestinationMailUtils.class);
	
	private static final String[] deliverySubjectSearchList = { "<environmentName>", "<customerName>",
		"<opportunityId>", "<deliveryCenter>"};
	
	private static final String[] deliveryIntimatedSubjectSearchList = { "<environmentName>", "<customerName>",
		"<opportunityId>"};
	
	private static final Integer OPERATION_INSERT = Integer.valueOf(1);
	private static final Integer OPERATION_UPDATE = Integer.valueOf(2);
	private static final Integer OPERATION_DELETE = Integer.valueOf(0);

	private static final int PATH_B_LAST_STEP_NUMBER = 4;
	private static final int PATH_A_LAST_STEP_NUMBER = 5;

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
	
	@Value("${workflowPendingBFMStep1TemplateLoc}")
	private String workflowPendingBFMStep1TemplateLoc;
	
	@Value("${workflowApprovedRejectedBFMStep1TemplateLoc}")
	private String workflowApprovedRejectedBFMStep1TemplateLoc;
	
	@Value("${workflowPendingBFMEscalateTemplateLoc}")
	private String workflowPendingBFMEscalateTemplateLoc;
	
	@Value("${workflowPendingBFMEscalateBApproveRejectTemplateLoc}")
	private String workflowPendingBFMEscalateBApproveRejectTemplateLoc;
	
	@Value("${workflowBFMEscalatePathAGEOHeadApproveTemplateLoc}")
	private String workflowBFMEscalatePathAGEOHeadApproveTemplateLoc;
	
	@Value("${workflowBFMEscalatePathADESSHeadApproveTemplateLoc}")
	private String workflowBFMEscalatePathADESSHeadApproveTemplateLoc;

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
	
	@Value("${sampleEmailTemplate}")
	private String sampleEmailTemplateLoc;

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
	
	// Weekly report
	@Value("${weeklyReportEmailSubject}")
	private String weeklyReportEmailSubject;

	@Value("${weeklyReportEmailTemplateLoc}")
	private String weeklyReportEmailTemplateLoc;

	@Value("${weeklyReportEmailId}")
	private String weeklyReportEmailId;
	
	@Value("${weeklyReportCCEmailIds}")
	private String weeklyReportCCEmailIds;
	
	//Delivery Emails Subject
	
	@Value("${deliveryIntimatedPriorWinSubject}")
	private String deliveryIntimatedPriorWinSubject;
	
	@Value("${deliveryIntimatedSubject}")
	private String deliveryIntimatedSubject;
	
	@Value("${deliveryAcceptedSubject}")
	private String deliveryAcceptedSubject;
	
	@Value("${deliveryRejectedSubject}")
	private String deliveryRejectedSubject;
	
	@Value("${deliveryAssignedSubject}")
	private String deliveryAssignedSubject;
	
	@Value("${deliveryLiveSubject}")
	private String deliveryLiveSubject;
	
	@Value("${deliveryIntimatedPriorWinTemplateLoc}")
	private String deliveryIntimatedPriorWinTemplateLoc;
	
	@Value("${deliveryIntimatedTemplateLoc}")
	private String deliveryIntimatedTemplateLoc;
	
	@Value("${deliveryAcceptedTemplateLoc}")
	private String deliveryAcceptedTemplateLoc;
	
	@Value("${deliveryRejectedTemplateLoc}")
	private String deliveryRejectedTemplateLoc;
	
	@Value("${deliveryAssignedTemplateLoc}")
	private String deliveryAssignedTemplateLoc;
	
	@Value("${deliveryLiveTemplateLoc}")
	private String deliveryLiveTemplateLoc;
	
	
	
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
	GeographyRepository geographyRepository;
	
	@Autowired
	DocumentsTRepository documentsRepository;
	
	@Autowired
	OpportunityDeliveryCentreMappingTRepository opportunityDeliveryCentreMappingTRepository;
	
	@Autowired
	DeliveryClusterRepository deliveryClusterRepository;
	
	@Autowired
	DeliveryMasterService deliveryMasterService;
	
	@Autowired
	WeeklyReportHelper weeklyReportHelper;

	@Autowired
	private OpportunityService oppService;

	@Autowired
	SubSpRepository subSpRepository;
	
	@Autowired
	ConnectRepository connectRepository;
	
	//added for workflow bfm changes
	@Autowired
	WorkflowBfmTRepository workflowBfmTRepository;
	
	@Autowired
	DeliveryMasterRepository deliveryMasterRepository;
	
	@Autowired
	DeliveryCentreRepository deliveryCentreRepository;

	@Autowired
	private DestinationMailSender destMailSender;
	
	@Autowired
	DeliveryMasterManagerLinkRepository deliveryMasterManagerLinkRepository;
	
	@Autowired
	DeliveryIntimatedRepository deliveryIntimatedRepository;
	
	@Autowired
	AuditDeliveryIntimatedCentreLinkRepository auditDeliveryIntimatedCentreLinkRepository;

	@Value("${userDetailsApprovalSubject}")
	private String userDetailsApprovalSubject;

	@Value("${userDetailsApprovalTemplate}")
	private String userDetailsApprovalTemplate;

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

		if ((requestType > 0 && requestType < 10) || requestType == RequestType.PARTNER_MASTER_UPLOAD.getType()
				|| requestType == RequestType.PRODUCT_UPLOAD.getType() ||
				requestType == RequestType.PRODUCT_CONTACT_UPLOAD.getType() ||
				requestType == RequestType.RGS_UPLOAD.getType() ||
				requestType == RequestType.CUSTOMER_ASSOCIATE_UPLOAD.getType() ||
				requestType == RequestType.UTILISATION_UPLOAD.getType() ||
				requestType == RequestType.UNALLOCATION_UPLOAD.getType()) { // upload
			template = uploadTemplateLoc;
			requestId = request.getProcessRequestId().toString();
			uploadedFileName = request.getFileName();
			attachmentFilePath = request.getErrorFilePath()
					+ request.getErrorFileName();
			attachmentFileName = request.getErrorFileName();
		} else if ((requestType > 9 && requestType < 19) || requestType == RequestType.PARTNER_MASTER_DOWNLOAD.getType()
				|| requestType == RequestType.PRODUCT_DOWNLOAD.getType() ||
				requestType == RequestType.PRODUCT_CONTACT_DOWNLOAD.getType() ||
				requestType == RequestType.RGS_DOWNLOAD.getType()) { // download
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

		StringBuffer subject = new StringBuffer(mailSubjectAppendEnvName)
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
		StringBuffer subject = new StringBuffer(mailSubjectAppendEnvName);
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
											UserGroup.SALES_HEAD.getValue()));
					logger.debug("recepient Ids for Sales Head :" + recepientIds);
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
	 * This method is used to add Default user in cc list
	 * 
	 * @param ccIds
	 */
	private void addDefaultUserInCC(List<String> ccIds){
		ccIds.add(Constants.defaultUser_CC_BFM);
	}
	
	
	/**
	 * This method is used to send mail notification to whom the request is
	 * pending
	 * 
	 * 
	 * @param requestId
	 * @param entityTypeId
	 * @throws Exception
	 */
	public void sendWorkflowPendingBFMStep1Mail(Integer requestId,
			Integer entityTypeId) throws Exception {
		logger.info("Inside sendWorkflowPendingBFMStep1Mail method");

		DestinationMailMessage message = new DestinationMailMessage();

		List<String> recepientIds = new ArrayList<String>();
		List<String> ccIds = new ArrayList<String>();
		String userGroupOrUserRoleOrUserId = null;
		String customerName = null;
		String oppName = null;
		String opportunityId = null;
		String crmId = null;
		String dealValueUSDInNumberScale = null;

		StringBuffer subject = new StringBuffer("");
		String userName = null;

		logger.info("sendWorkflowPendingBFMStep1Mail :: RequestId" + requestId);
		WorkflowRequestT workflowRequestT = workflowRequestRepository
				.findOne(requestId);
		
		if (workflowRequestT != null) {
			String createdById = workflowRequestT.getCreatedBy();
			UserT createdByUser = userRepository.findOne(createdById);
			userName = createdByUser.getUserName();
			String entityId = workflowRequestT.getEntityId();
			logger.debug("Request fetched:");
			logger.debug("EntityId:" + entityId);
			switch (EntityTypeId.valueOf(EntityTypeId.getName(entityTypeId))) {
			case BFM:
				WorkflowBfmT workflowBfmT = workflowBfmTRepository
						.findOne(entityId);
				OpportunityT opportunityT = workflowBfmT.getOpportunityT();
				opportunityId = workflowBfmT.getOpportunityId();
				crmId = opportunityT.getCrmId();
				CustomerMasterT customerMasterT = opportunityT
						.getCustomerMasterT();
				customerName = customerMasterT.getCustomerName();
				oppName = opportunityT.getOpportunityName();
				Integer dealValue = opportunityT.getDigitalDealValue();
				String dealCurrency = opportunityT.getDealCurrency();
				BigDecimal newDealValueInUSD = opportunityDownloadService
						.convertCurrencyToUSD(dealCurrency, dealValue);
				dealValueUSDInNumberScale = NumericUtil
						.toUSDinNumberScale(newDealValueInUSD);

				userName = userRepository.findUserNameByUserId(workflowBfmT
						.getCreatedBy());
				
				subject.append(Constants.WORKFLOW_BFM_STEP1_PENDING_SUBJECT);
				subject.append(getCustomerOpportunitySubString(opportunityId,customerName));
				WorkflowStepT workflowStepPending = workflowStepRepository
						.findByRequestIdAndStepStatus(requestId,
								WorkflowStatus.PENDING.getStatus());

				WorkflowStepT requestRaisedStep = workflowStepRepository
						.findByRequestIdAndStep(requestId, 1);
				if (requestRaisedStep.getUserRole() != null) {
					String userRolesStr = requestRaisedStep.getUserRole();
					String[] userRoles = userRolesStr.split(",");
					ccIds.add(createdById);
					for(String userRole : userRoles){
					  if(!userRole.equalsIgnoreCase("User"))	
						  ccIds.addAll(userRepository.findUserIdByUserRole(userRole));
					}
				}
				if (workflowStepPending.getUserRole() != null) {
					String[] workflowUserRoles = workflowStepPending.getUserRole()
							.split(",");
					List<String> workflowUserRolesList = Arrays.asList(workflowUserRoles);
					List<UserT> userList = userRepository.findByUserRoles(workflowUserRolesList);
					List<String> userIdList = new ArrayList<String>();
					
					for(UserT user : userList){
						userIdList.add(user.getUserId());
					}
					recepientIds.addAll(userIdList);
					userGroupOrUserRoleOrUserId = workflowStepPending.getUserRole();

				}
				if (workflowStepPending.getUserId() != null) {
					String[] workflowUserIds = workflowStepPending.getUserId()
							.split(",");
					List<String> workflowUserIdList = Arrays
							.asList(workflowUserIds);
					
					recepientIds.addAll(workflowUserIdList);
				}
				break;

			default:
				break;
			}
			
			addSysAdminStrategicAdminCC(ccIds);
			addDefaultUserInCC(ccIds);
			message.setRecipients(listMailIdsFromUserIds(recepientIds));
			message.setCcList(listMailIdsFromUserIds(ccIds));

			Map<String, Object> workflowMap = new HashMap<String, Object>();

			workflowMap.put("userGroupOrUserRole", userGroupOrUserRoleOrUserId);

			workflowMap.put("userName", userName);
			workflowMap.put("opportunityId", opportunityId);
			workflowMap.put("opportunityName", oppName);
			workflowMap.put("crmId", crmId);
			workflowMap.put("dealValue", dealValueUSDInNumberScale);
			workflowMap.put("customerName", customerName);

			String tmpl = workflowPendingBFMStep1TemplateLoc;

			String text = mergeTmplWithData(workflowMap, tmpl);

			logger.info("framed text for mail :" + text);
			message.setSubject(formatSubject(subject.toString()));
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
											UserGroup.SALES_HEAD.getValue()));
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
		String sub = new StringBuffer(mailSubjectAppendEnvName).append(" ")
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
			         message.setSubject(new StringBuffer(mailSubjectAppendEnvName).append(shareConnectSubject).toString());
			         break;
			         
          	 	 case OPPORTUNITY :   
          	 		 OpportunityT opportunity = opportunityRepository.findOne(entityId);
				     String opportunityName = opportunity.getOpportunityName();
				 	 map.put("opportunityName", opportunityName);
				 	 String textOpp = mergeTmplWithData(map, shareOpportunityTemplate);
				   	 logger.info("framed text for mail :" + textOpp);
				 	 message.setMessage(textOpp);
				   	 message.setSubject(new StringBuffer(mailSubjectAppendEnvName).append(shareOpportunitySubject).toString());
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
	 
	/**
	 * Method used to send the weekly report to Dr.Satya
	 * 
	 * @throws Exception
	 */
	public void sendWeeklyReport() throws Exception {Date currentDate = DateUtils.getCurrentMidnightDate();
	String currentDateString = ACTUAL_FORMAT.format(currentDate);
	String reportDateString = DATE_FORMAT_MONTH_NAME.format(currentDate);
	Date previousWeekDate = DateUtils.getPreviousWeekDate();
	Date previousDate = DateUtils.getPreviousDate();
	String previousDateString = ACTUAL_FORMAT.format(previousDate);
	String previousWeekDateString = ACTUAL_FORMAT.format(previousWeekDate);
	String financialYear = DateUtils.getFinancialYr();
	String financialEndYear = DateUtils.getFinancialEndYr();
	int weekNumber = DateUtils.weekOfFinancialYr(new Date());
	JasperPrint reportAPAC;
	JasperPrint reportAmerica;
	JasperPrint reportUK;
	List<String> apacGeos = Lists.newArrayList();
	List<String> americaGeos = Lists.newArrayList();
	List<String> euGeos = Lists.newArrayList();
	List<GeographyMappingT> geographyMappingTs = (List<GeographyMappingT>) geographyRepository
			.findAll();
	for (GeographyMappingT geographyMappingT : geographyMappingTs) {
		if (geographyMappingT.getDisplayGeography().equals(
				Geography.APAC_IND_MEA.getDisplayGeography())) {
			apacGeos.add(geographyMappingT.getGeography());
		} else if (geographyMappingT.getDisplayGeography().equals(
				Geography.AMERICAS.getDisplayGeography())) {
			americaGeos.add(geographyMappingT.getGeography());
		} else if (geographyMappingT.getDisplayGeography().equals(
				Geography.EU_UK.getDisplayGeography())) {
			euGeos.add(geographyMappingT.getGeography());
		}
	}
	// Report for APAC India ME
	reportAPAC = weeklyReportHelper.constructWeeklyReport(apacGeos,
			currentDate, previousWeekDate,
			Geography.APAC_IND_MEA.getDisplayGeography(), weekNumber, financialEndYear);
	// Report for americas
	reportAmerica = weeklyReportHelper.constructWeeklyReport(americaGeos,
			currentDate, previousWeekDate,
			Geography.AMERICAS.getDisplayGeography(), weekNumber, financialEndYear);
	// Report for Europe
	reportUK = weeklyReportHelper.constructWeeklyReport(euGeos,
			currentDate, previousWeekDate,
			Geography.EU_UK.getDisplayGeography(), weekNumber, financialEndYear);

	ByteArrayOutputStream byteArrayOutputStreamAmer = new ByteArrayOutputStream();
	ByteArrayOutputStream byteArrayOutputStreamAPAC = new ByteArrayOutputStream();
	ByteArrayOutputStream byteArrayOutputStreamUK = new ByteArrayOutputStream();

	//Saving the weekly report to Documents
	
//	JasperExportManager.exportReportToPdfFile(reportAPAC, "/Users/bnpp/Desktop/Mani_PDF/WeeklyReportAPAC.pdf");
//	
//	JasperExportManager.exportReportToPdfFile(reportAmerica, "/Users/bnpp/Desktop/Mani_PDF/WeeklyReportAmerica.pdf");
//	
//	JasperExportManager.exportReportToPdfFile(reportUK, "/Users/bnpp/Desktop/Mani_PDF/WeeklyReportUK.pdf");
	
	JasperExportManager.exportReportToPdfStream(reportAmerica, byteArrayOutputStreamAmer);
	
	JasperExportManager.exportReportToPdfStream(reportAPAC, byteArrayOutputStreamAPAC);

	JasperExportManager.exportReportToPdfStream(reportUK, byteArrayOutputStreamUK);

	
	byte[] bytesAPAC = byteArrayOutputStreamAPAC.toByteArray();
	byte[] bytesAmer = byteArrayOutputStreamAmer.toByteArray();
	byte[] bytesUK = byteArrayOutputStreamUK.toByteArray();
	Map<String, byte[]> byteMap = Maps.newHashMap();
	byteMap.put("Weekly Report APAC Ind ME "
			+ reportDateString + ".pdf", bytesAPAC);
	byteMap.put("Weekly Report Americas " + reportDateString
			+ ".pdf", bytesAmer);
	byteMap.put("Weekly Report EU & UK " + reportDateString
			+ ".pdf", bytesUK);
	saveDocuments(byteMap);
	
	String templateLoc = weeklyReportEmailTemplateLoc;
	String subject = new StringBuffer(mailSubjectAppendEnvName)
			.append(weeklyReportEmailSubject).append(" ")
			.append(currentDateString).toString();
	Map<String, Object> data = new HashMap<String, Object>();
	logger.info("Report for EU/UK produced");
	data.put("weekNumber", weekNumber);
	data.put("weekStartDate", previousWeekDateString);
	data.put("weekEndDate", previousDateString);
	data.put("financialYear", financialYear);
	DestinationMailMessage message = new DestinationMailMessage();
	List<String> recipientList = Lists.newArrayList();
	recipientList = Arrays.asList(StringUtils.split(weeklyReportEmailId, ","));
	message.setRecipients(recipientList);
	if(StringUtils.isNotEmpty(weeklyReportCCEmailIds)) {
		List<String> ccList = Lists.newArrayList();
		ccList = Arrays.asList(StringUtils.split(weeklyReportCCEmailIds, ","));
		message.setCcList(ccList);
	}
	logger.info("To email address : " + weeklyReportEmailId);
	message.setSubject(subject.toString());
	logger.info("Subject : " + subject.toString());
	String text = mergeTmplWithData(data, templateLoc);
	logger.info("framed text : " + text);
	message.setMessage(text);
	message.setAttachments(byteMap);
	destMailSender.send(message);
	logger.info("Weekly report mail sent");}

	/**
	 * Method used to save the weekly report to Documents
	 * @param byteMap
	 */
	private void saveDocuments(Map<String, byte[]> byteMap) {
		logger.debug("Inside saveDocuments Method");
		for (Entry<String, byte[]> map : byteMap.entrySet()) {
			DocumentsT document = new DocumentsT();
			document.setDocContent(map.getValue());
			document.setDocName(map.getKey());
			document.setEntityId(Constants.NONE);
			document.setDocType(Constants.NONE);
			document.setVersion(1);
			document.setCreatedBy(Constants.SYSTEM_USER);
			document.setModifiedBy(Constants.SYSTEM_USER);
			document.setEntityType(EntityType.WEEKLY_REPORT.getName());
			documentsRepository.save(document);
			logger.debug("Documents Saved");
		}
	}

	/**
	 * This method is used to send approved/rejected BFM mail notification 
	 * 
	 * @param requestId
	 * @param entityTypeId
	 * @param status
	 * @throws Exception
	 */
	public void sendEmailNotificationforBFMStep1ApproveOrReject(
			Integer requestId, Integer entityTypeId, String status) throws Exception{
		
			logger.info("Inside sendEmailNotificationforBFMStep1ApproveOrReject method");

			DestinationMailMessage message = new DestinationMailMessage();

			List<String> recepientIds = new ArrayList<String>();
			List<String> ccIds = new ArrayList<String>();
			
			String userGroupOrUserRoleOrUserId = null;
			String customerName = null;
			String oppName = null;
			String opportunityId = null;
			String comments = null;
			String salesStageCode = null;
			String reqStatus = null;

			StringBuffer subject = new StringBuffer("");
			String userName = null;

			logger.info("sendEmailNotificationforBFMStep1ApproveOrReject :: RequestId" + requestId);
			
			WorkflowRequestT workflowRequestT = workflowRequestRepository
					.findOne(requestId);
			
			if (workflowRequestT != null) {
				String createdById = workflowRequestT.getCreatedBy();
				UserT createdByUser = userRepository.findOne(createdById);
				userName = createdByUser.getUserName();
				String entityId = workflowRequestT.getEntityId();
				logger.debug("Request fetched:");
				logger.debug("EntityId:" + entityId);
				switch (EntityTypeId.valueOf(EntityTypeId.getName(entityTypeId))) {
				case BFM:
					WorkflowBfmT workflowBfmT = workflowBfmTRepository.findOne(entityId);
					OpportunityT opportunityT = workflowBfmT.getOpportunityT();
					opportunityId = workflowBfmT.getOpportunityId();
					
					CustomerMasterT customerMasterT = opportunityT
							.getCustomerMasterT();
					customerName = customerMasterT.getCustomerName();
					oppName = opportunityT.getOpportunityName();
                    
					salesStageCode = SalesStageCode.valueOf(opportunityT.getSalesStageCode()).getDescription(); 
					
					userName = userRepository.findUserNameByUserId(workflowBfmT
							.getCreatedBy());
					
					WorkflowStepT approvedRejectedStep = null;
					if(status.equalsIgnoreCase("Approved")){
						approvedRejectedStep = workflowStepRepository
								.findByRequestIdAndStepStatus(requestId,
										WorkflowStatus.APPROVED.getStatus());
						subject.append(Constants.WORKFLOW_BFM_STEP1_APPROVED_SUBJECT);
						// Adding respective Sales Head to cc Ids	
						 List<String> geoHeads = userAccessPrivilegesRepository.findUserIdsForCustomerUserGroup(customerMasterT.getGeography(), Constants.Y, UserGroup.SALES_HEAD.getValue());
						if(CollectionUtils.isNotEmpty(geoHeads)) {
							ccIds.addAll(geoHeads);
						}
						reqStatus = "Approved";
						salesStageCode = SalesStageCode.valueOf(opportunityT.getSalesStageCode()).getDescription(); 
					} else {
						approvedRejectedStep = workflowStepRepository
								.findByRequestIdAndStepStatus(requestId,
										WorkflowStatus.REJECTED.getStatus());
						
						
						subject.append(Constants.WORKFLOW_BFM_STEP1_REJECTED_SUBJECT);
						// Adding respective Sales Head to cc Ids	
						 List<String> geoHeads = userAccessPrivilegesRepository.findUserIdsForCustomerUserGroup(customerMasterT.getGeography(), Constants.Y, UserGroup.SALES_HEAD.getValue());
						if(CollectionUtils.isNotEmpty(geoHeads)) {
							ccIds.addAll(geoHeads);
						}
						reqStatus = "Rejected";
						salesStageCode = SalesStageCode.valueOf(SalesStageCode.RFP_IN_PROGRESS.getCode()).getDescription(); 
					}
					subject.append(getCustomerOpportunitySubString(opportunityId,customerName));
					comments = approvedRejectedStep.getComments();
					if(StringUtils.isEmpty(comments)){
						comments = "No Comments";
					}
					WorkflowStepT requestRaisedStep = workflowStepRepository
							.findByRequestIdAndStep(requestId, 1);
					if (requestRaisedStep.getUserRole() != null) {
						String userRolesStr = requestRaisedStep.getUserRole();
						String[] userRoles = userRolesStr.split(",");
						recepientIds.add(createdById);
						for(String userRole : userRoles){
						  if(!userRole.equalsIgnoreCase("User"))	
							  ccIds.addAll(userRepository.findUserIdByUserRole(userRole));
						}
					}
					
					if (approvedRejectedStep.getUserRole() != null) {
						String[] workflowUserRoles = approvedRejectedStep.getUserRole()
								.split(",");
						List<String> workflowUserRolesList = Arrays.asList(workflowUserRoles);
						List<UserT> userList = userRepository.findByUserRoles(workflowUserRolesList);
						List<String> userIdList = new ArrayList<String>();
						
						for(UserT user : userList){
							userIdList.add(user.getUserId());
						}
						ccIds.addAll(userIdList);
						userGroupOrUserRoleOrUserId = approvedRejectedStep.getUserRole();

					}
					if (approvedRejectedStep.getUserId() != null) {
						String[] workflowUserIds = approvedRejectedStep.getUserId()
								.split(",");
						List<String> workflowUserIdList = Arrays
								.asList(workflowUserIds);
						
						ccIds.addAll(workflowUserIdList);
					}
					break;

				default:
					break;
				}

				
				//adding system admin and strategic group admin in cc
				addSysAdminStrategicAdminCC(ccIds);
				addDefaultUserInCC(ccIds);
				message.setRecipients(listMailIdsFromUserIds(recepientIds));
				message.setCcList(listMailIdsFromUserIds(ccIds));

				Map<String, Object> workflowMap = new HashMap<String, Object>();

				workflowMap.put("userGroupOrUserRole", userGroupOrUserRoleOrUserId);

				workflowMap.put("userName", userName);
				workflowMap.put("opportunityId", opportunityId);
				workflowMap.put("opportunityName", oppName);
				workflowMap.put("salesStageCode", salesStageCode);
				workflowMap.put("customerName", customerName);
				workflowMap.put("comments", comments);
				
				workflowMap.put("status",reqStatus);

				String tmpl = workflowApprovedRejectedBFMStep1TemplateLoc;

				String text = mergeTmplWithData(workflowMap, tmpl);

				logger.info("framed text for mail :" + text);
				message.setSubject(formatSubject(subject.toString()));
				message.setMessage(text);

				destMailSender.send(message);
				logger.info("Mail Sent for request"
						+ workflowRequestT.getRequestId());

			} else {
				logger.error("request not fetched");
			}
		}

	private void addSysAdminStrategicAdminCC(List<String> ccIds) {
		String[] workflowUserRoles = Constants.notifyUserRolesForBFM.split(",");
		List<String> workflowUserRolesList = Arrays.asList(workflowUserRoles);
		List<UserT> userList = userRepository.findByUserRoles(workflowUserRolesList);
		
		for(UserT user : userList){
			ccIds.add(user.getUserId());
		}
	}

	
	/**
	 * this method returns opportunity id and customer in the desired format
	 * @param opportunityId
	 * @param customerName
	 * @return
	 */
	private String getCustomerOpportunitySubString(String opportunityId,
			String customerName) {
		StringBuffer subString=new StringBuffer("");
		subString.append(opportunityId);
		subString.append(" - "+customerName);
		return subString.toString();
		
	}

	/**
	 * this method is used to send mail during the escalation to GeoHead
	 * @param requestId
	 * @param entityTypeId
	 * @throws Exception
	 */
	public void sendWorkflowPendingBFMEscalateMail(Integer requestId,
			Integer entityTypeId) throws Exception{
		logger.info("Inside sendWorkflowPendingBFMEscalateMail method");

		DestinationMailMessage message = new DestinationMailMessage();

		List<String> recepientIds = new ArrayList<String>();
		List<String> ccIds = new ArrayList<String>();
		String userGroupOrUserRoleOrUserId = null;
		String customerName = null;
		String oppName = null;
		String opportunityId = null;
		String crmId = null;
		String dealValueUSDInNumberScale = null;
		String comments = null;
		String geography = null;
		String exceptions = null;

		StringBuffer subject = new StringBuffer("");
		String userName = null;

		logger.info("sendWorkflowPendingBFMEscalateMail :: RequestId" + requestId);
		WorkflowRequestT workflowRequestT = workflowRequestRepository
				.findOne(requestId);
		
		if (workflowRequestT != null) {
			String createdById = workflowRequestT.getCreatedBy();
			UserT createdByUser = userRepository.findOne(createdById);
			userName = createdByUser.getUserName();
			ccIds.add(createdById);
			String entityId = workflowRequestT.getEntityId();
			logger.debug("Request fetched:");
			logger.debug("EntityId:" + entityId);
			switch (EntityTypeId.valueOf(EntityTypeId.getName(entityTypeId))) {
			case ESCALATION_A:
			case ESCALATION_B:
			case CONSULTED_ESCALATION_A:
			case CONSULTED_ESCALATION_B:	
				WorkflowBfmT workflowBfmT = workflowBfmTRepository
						.findOne(entityId);
				exceptions = workflowBfmT.getExceptions();
				OpportunityT opportunityT = workflowBfmT.getOpportunityT();
				opportunityId = workflowBfmT.getOpportunityId();
				crmId = opportunityT.getCrmId();
				CustomerMasterT customerMasterT = opportunityT
						.getCustomerMasterT();
				customerName = customerMasterT.getCustomerName();
				geography = customerMasterT.getGeography();
				oppName = opportunityT.getOpportunityName();
				
				Integer dealValue = opportunityT.getDigitalDealValue();
				String dealCurrency = opportunityT.getDealCurrency();
				BigDecimal newDealValueInUSD = opportunityDownloadService
						.convertCurrencyToUSD(dealCurrency, dealValue);
				dealValueUSDInNumberScale = NumericUtil
						.toUSDinNumberScale(newDealValueInUSD);

				userName = userRepository.findUserNameByUserId(workflowBfmT
						.getCreatedBy());
				subject.append(Constants.WORKFLOW_BFM_ESCALATE_PENDING_SUBJECT);
				subject.append(customerName);
				
				
				WorkflowStepT workflowStepPending = workflowStepRepository
						.findByRequestIdAndStepStatus(requestId,
								WorkflowStatus.PENDING.getStatus());
				
				
				WorkflowStepT escalatedStep = workflowStepRepository
						.findByRequestIdAndStep(requestId, workflowStepPending.getStep()-1);
				comments = escalatedStep.getComments();
				
				if (escalatedStep.getUserRole() != null) {
					 List<String> userIdsByRole = getUserIdsByRole(escalatedStep);
					 ccIds.addAll(userIdsByRole);
					}
					if (escalatedStep.getUserId() != null) {
					 List<String> userIdsByIds = getUserIdsById(escalatedStep);
					 ccIds.addAll(userIdsByIds);
					}
					
					if (escalatedStep.getUserGroup() != null) {
						switch (escalatedStep.getUserGroup()) {
						case Constants.WORKFLOW_GEO_HEADS:
							ccIds
									.addAll(userAccessPrivilegesRepository
											.findUserIdsForWorkflowUserGroup(geography,
													Constants.Y,
													UserGroup.SALES_HEAD.getValue()));
							logger.debug("recepient Ids for Sales Head :" + recepientIds);
							userGroupOrUserRoleOrUserId = Constants.WORKFLOW_GEO_HEADS;
							ccIds.addAll(userAccessPrivilegesRepository
									.findUserIdsForWorkflowUserGroup(geography,
											Constants.Y, UserGroup.PMO.getValue()));
							logger.debug("CCIds for PMO :" + ccIds);
							break;
						case Constants.WORKFLOW_PMO:
							ccIds.addAll(userAccessPrivilegesRepository
									.findUserIdsForWorkflowUserGroup(geography,
											Constants.Y, UserGroup.PMO.getValue()));
							userGroupOrUserRoleOrUserId = Constants.WORKFLOW_PMO;
						default:
							break;
						}
					}
				
				if (workflowStepPending.getUserRole() != null) {
				 List<String> userIdsByRole = getUserIdsByRole(workflowStepPending);
				 recepientIds.addAll(userIdsByRole);
				}
				if (workflowStepPending.getUserId() != null) {
				 List<String> userIdsByIds = getUserIdsById(workflowStepPending);
				 recepientIds.addAll(userIdsByIds);
				}
				
				if (workflowStepPending.getUserGroup() != null) {
					switch (workflowStepPending.getUserGroup()) {
					case Constants.WORKFLOW_GEO_HEADS:
						recepientIds
								.addAll(userAccessPrivilegesRepository
										.findUserIdsForCustomerUserGroup(geography,
												Constants.Y,
												UserGroup.SALES_HEAD.getValue()));
						logger.debug("recepient Ids for Sales Head :" + recepientIds);
						userGroupOrUserRoleOrUserId = Constants.WORKFLOW_GEO_HEADS;
						ccIds.addAll(userAccessPrivilegesRepository
								.findUserIdsForCustomerUserGroup(geography,
										Constants.Y, UserGroup.PMO.getValue()));
						logger.debug("CCIds for PMO :" + ccIds);
						break;
					case Constants.WORKFLOW_PMO:
						recepientIds.addAll(userAccessPrivilegesRepository
								.findUserIdsForCustomerUserGroup(geography,
										Constants.Y, UserGroup.PMO.getValue()));
						userGroupOrUserRoleOrUserId = Constants.WORKFLOW_PMO;
					default:
						break;
					}
				}
				
				break;

			default:
				break;
			}
			addDefaultUserInCC(ccIds);
			message.setRecipients(listMailIdsFromUserIds(recepientIds));
			message.setCcList(listMailIdsFromUserIds(ccIds));

			Map<String, Object> workflowMap = new HashMap<String, Object>();

			workflowMap.put("userGroupOrUserRole", userGroupOrUserRoleOrUserId);

			workflowMap.put("userName", userName);
			workflowMap.put("opportunityId", opportunityId);
			workflowMap.put("opportunityName", oppName);
			workflowMap.put("crmId", crmId);
			workflowMap.put("dealValue", dealValueUSDInNumberScale);
			workflowMap.put("customerName", customerName);
			workflowMap.put("exceptions", exceptions);
			workflowMap.put("comments",comments);

			String tmpl = workflowPendingBFMEscalateTemplateLoc;

			String text = mergeTmplWithData(workflowMap, tmpl);

			logger.info("framed text for mail :" + text);
			message.setSubject(formatSubject(subject.toString()));
			message.setMessage(text);

			destMailSender.send(message);
			logger.info("Mail Sent for request"
					+ workflowRequestT.getRequestId());

		} else {
			logger.error("request not fetched");
		}
		
	}

	
	/**
	 * this method returns userIds by userId from step
	 * @param workflowStep
	 * @return
	 */
	private List<String> getUserIdsById(WorkflowStepT workflowStep) {
		String[] workflowUserIds = workflowStep.getUserId()
				.split(",");
		List<String> workflowUserIdList = Arrays
				.asList(workflowUserIds);
		return workflowUserIdList;
	}

	/**
	 * this method returns user Ids by user role from step
	 * @param workflowStep
	 * @return
	 */
	private List<String> getUserIdsByRole(WorkflowStepT workflowStep) {
		String[] workflowUserRoles = workflowStep.getUserRole()
				.split(",");
		
		List<String> workflowUserRolesList = Arrays.asList(workflowUserRoles);
		
		workflowUserRolesList.remove("User");
		
		List<UserT> userList = userRepository.findByUserRoles(workflowUserRolesList);
		List<String> userIdList = new ArrayList<String>();
		
		for(UserT user : userList){
			userIdList.add(user.getUserId());
		}

		return userIdList;
	}
	
	/**
	 * this method is used to send mail when BFM workflow is approved/rejected in pathB 
	 * @param requestId
	 * @param entityTypeId
	 * @param status
	 * @throws Exception
	 */
	public void sendEmailNotificationforBFM_PathB_ApproveOrReject(
			Integer requestId, Integer entityTypeId, String status) throws Exception {
		logger.info("destinationmailutils - inside sendEmailNotificationforBFM_PathB_ApproveOrReject method");
		String entityId = null;
		String exceptions = null;
		String opportunityId = null;
		String crmId = null;
		String opportunityName = null;
		String customerName = null;
		String dealValueUSDInNumberScale = null;
		String geography = null;
		
		WorkflowRequestT workflowRequestT = workflowRequestRepository
				.findOne(requestId);
		
		if(workflowRequestT!=null){
		entityId = workflowRequestT.getEntityId();
		
		//populate content map
		Map<String, Object> workflowMap = new HashMap<String, Object>();
		
				
		WorkflowBfmT workflowBfmT = workflowBfmTRepository.findOne(entityId);
		exceptions = workflowBfmT.getExceptions();
		
		OpportunityT opportunityT = workflowBfmT.getOpportunityT();
		opportunityId = opportunityT.getOpportunityId();
		crmId = opportunityT.getCrmId();
		opportunityName = opportunityT.getOpportunityName();
		Integer dealValue = opportunityT.getDigitalDealValue();
		String dealCurrency = opportunityT.getDealCurrency();
		BigDecimal newDealValueInUSD = opportunityDownloadService
				.convertCurrencyToUSD(dealCurrency, dealValue);
		dealValueUSDInNumberScale = NumericUtil
				.toUSDinNumberScale(newDealValueInUSD);
		
		CustomerMasterT customer = opportunityT.getCustomerMasterT();
		customerName = customer.getCustomerName();
		geography = customer.getGeography();
		
		workflowMap.put("opportunityId",opportunityId);
		workflowMap.put("crmId",crmId);
		workflowMap.put("opportunityName",opportunityName);
		workflowMap.put("customerName",customerName);
		workflowMap.put("dealValue",dealValueUSDInNumberScale);
		workflowMap.put("exceptions", exceptions);
		workflowMap.put("geography", geography);
		
		
		
		WorkflowStepT pendingStep = workflowStepRepository
				.findByRequestIdAndStepStatus(requestId,
						WorkflowStatus.PENDING.getStatus());
		
		if(pendingStep == null){
			//the request flow is completed
			//check if rejected
			WorkflowStepT rejectedStep = workflowStepRepository
					.findByRequestIdAndStepStatus(requestId,
							WorkflowStatus.REJECTED.getStatus());
			String salesStageCode = null;
			if(rejectedStep != null) {
				switch(rejectedStep.getStep()){
				case 3 :
					salesStageCode=SalesStageCode.valueOf(SalesStageCode.RFP_IN_PROGRESS.getCode()).getDescription();
					workflowMap.put("salesStageCode",salesStageCode);
					    sendPathAGEOHeadRejectedMail(workflowMap,workflowRequestT,workflowBfmT,rejectedStep);
					    break;
				case PATH_B_LAST_STEP_NUMBER:
					salesStageCode=SalesStageCode.valueOf(SalesStageCode.RFP_IN_PROGRESS.getCode()).getDescription();
					workflowMap.put("salesStageCode",salesStageCode);
					 sendPathAFinalStepRejectedEmail(workflowMap,workflowRequestT,workflowBfmT,rejectedStep);
					 break;
				}
			} else {
				// approved final step by Shrilakshmi
				salesStageCode=SalesStageCode.valueOf(opportunityT.getSalesStageCode()).getDescription();
				workflowMap.put("salesStageCode",salesStageCode);
				WorkflowStepT approvedStep = workflowStepRepository
						.findByRequestIdAndStep(requestId,
								PATH_B_LAST_STEP_NUMBER);
				sendPathAFinalStepApprovedEmail(workflowMap,workflowRequestT,workflowBfmT,approvedStep);
			}
		} else {
			//approved by geohead and pending with shrilakshmi
			WorkflowStepT approvedStep = workflowStepRepository
					.findByRequestIdAndStepStatus(requestId,
							WorkflowStatus.APPROVED.getStatus());
			
			sendPathBGEOHeadApprovedMail(workflowMap,workflowRequestT,workflowBfmT,approvedStep);
			
		}
		} else {
			logger.error("Request not found");
		}
	}

	/**
	 * this method is used to send mail when BFM workflow is approved/rejected in pathA
	 * @param requestId
	 * @param entityTypeId
	 * @param status
	 * @throws Exception
	 */
	public void sendEmailNotificationforBFM_PathA_ApproveOrReject(
			Integer requestId, Integer entityTypeId, String status) throws Exception {
		logger.info("destinationmailutils - inside sendEmailNotificationforBFM_PathA_ApproveOrReject method");
		String entityId = null;
		String exceptions = null;
		String opportunityId = null;
		String crmId = null;
		String opportunityName = null;
		String customerName = null;
		String dealValueUSDInNumberScale = null;
		String geography = null;
		
		WorkflowRequestT workflowRequestT = workflowRequestRepository
				.findOne(requestId);
		if(workflowRequestT!=null){
		entityId = workflowRequestT.getEntityId();
		
		//populate content map
		Map<String, Object> workflowMap = new HashMap<String, Object>();
		
				
		WorkflowBfmT workflowBfmT = workflowBfmTRepository.findOne(entityId);
		exceptions = workflowBfmT.getExceptions();
		
		OpportunityT opportunityT = workflowBfmT.getOpportunityT();
		opportunityId = opportunityT.getOpportunityId();
		crmId = opportunityT.getCrmId();
		opportunityName = opportunityT.getOpportunityName();
		Integer dealValue = opportunityT.getDigitalDealValue();
		String dealCurrency = opportunityT.getDealCurrency();
		BigDecimal newDealValueInUSD = opportunityDownloadService
				.convertCurrencyToUSD(dealCurrency, dealValue);
		dealValueUSDInNumberScale = NumericUtil
				.toUSDinNumberScale(newDealValueInUSD);
		
		CustomerMasterT customer = opportunityT.getCustomerMasterT();
		customerName = customer.getCustomerName();
		geography = customer.getGeography();
		
		workflowMap.put("opportunityId",opportunityId);
		workflowMap.put("crmId",crmId);
		workflowMap.put("opportunityName",opportunityName);
		workflowMap.put("customerName",customerName);
		workflowMap.put("dealValue",dealValueUSDInNumberScale);
		workflowMap.put("exceptions", exceptions);
		workflowMap.put("geography", geography);
		
		String salesStageCode=null;
		
		WorkflowStepT pendingStep = workflowStepRepository
				.findByRequestIdAndStepStatus(requestId,
						WorkflowStatus.PENDING.getStatus());
		
		if(pendingStep == null){
			//the request flow is completed
			//check if rejected
			WorkflowStepT rejectedStep = workflowStepRepository
					.findByRequestIdAndStepStatus(requestId,
							WorkflowStatus.REJECTED.getStatus());
			if(rejectedStep != null) {
				//check which step it was rejected
				int rejectedStepNumber = rejectedStep.getStep();
				if(PATH_A_LAST_STEP_NUMBER == rejectedStepNumber){
					//rejected at the last step by shrilakshmi
					salesStageCode=SalesStageCode.valueOf(SalesStageCode.RFP_IN_PROGRESS.getCode()).getDescription();
					workflowMap.put("salesStageCode",salesStageCode);
					sendPathAFinalStepRejectedEmail(workflowMap,workflowRequestT,workflowBfmT,rejectedStep);
				} else {
					switch (rejectedStepNumber) {
					case 3:
						// rejected by GEO Head
						salesStageCode=SalesStageCode.valueOf(SalesStageCode.RFP_IN_PROGRESS.getCode()).getDescription();
						workflowMap.put("salesStageCode",salesStageCode);
						sendPathAGEOHeadRejectedMail(workflowMap,workflowRequestT,workflowBfmT,rejectedStep);
						break;
					case 4:
						// rejected by DESS Head
						salesStageCode=SalesStageCode.valueOf(SalesStageCode.RFP_IN_PROGRESS.getCode()).getDescription();
						workflowMap.put("salesStageCode",salesStageCode);
						sendPathADESSHeadRejectedMail(workflowMap,workflowRequestT,workflowBfmT,rejectedStep);
						break;
					default:

					}
				}
			} else {
				// approved final step by Shrilakshmi
				salesStageCode=SalesStageCode.valueOf(opportunityT.getSalesStageCode()).getDescription();
				workflowMap.put("salesStageCode",salesStageCode);
				WorkflowStepT approvedStep = workflowStepRepository
						.findByRequestIdAndStep(requestId,PATH_A_LAST_STEP_NUMBER
								);
				sendPathAFinalStepApprovedEmail(workflowMap,workflowRequestT,workflowBfmT,approvedStep);
			}
		} else {
			// workflow process is still in progress
			WorkflowStepT approvedStep = null;
			int pendingStepNumber = pendingStep.getStep();
			
			if(pendingStepNumber==(PATH_A_LAST_STEP_NUMBER)){
				//last pending - satya approved, last-1 pending - geohead approved
				approvedStep = workflowStepRepository
						.findByRequestIdAndStep(requestId,pendingStepNumber-1
								);
			} else {
				//escalated step is already intimated via another thread, so it must be approved step
				approvedStep = workflowStepRepository
						.findByRequestIdAndStepStatus(requestId,
								WorkflowStatus.APPROVED.getStatus());
			}
			
			int approvedStepNumber = approvedStep.getStep();
			
			switch(approvedStepNumber){
			  case 3 : //approved by GEO Head
				   salesStageCode=SalesStageCode.valueOf(opportunityT.getSalesStageCode()).getDescription();
					workflowMap.put("salesStageCode",salesStageCode);
				       sendPathAGEOHeadApprovedMail(workflowMap,workflowRequestT,workflowBfmT,approvedStep);
				       break;
			  case 4 : //approved by DESS Head
				  salesStageCode=SalesStageCode.valueOf(opportunityT.getSalesStageCode()).getDescription();
					workflowMap.put("salesStageCode",salesStageCode);
				       sendPathADESSHeadApprovedMail(workflowMap,workflowRequestT,workflowBfmT,approvedStep);
				       break;
			  default :
				  
			}
			
		}
		} else {
			logger.error("Request not found");
		}
	}

	
	/**
	 * this method is used to send mail when BFM workflow is rejected by DESS Head 
	 * @param workflowMap
	 * @param workflowRequestT
	 * @param workflowBfmT
	 * @param rejectedStep
	 * @throws Exception
	 */
	private void sendPathADESSHeadRejectedMail(Map<String, Object> workflowMap,
			WorkflowRequestT workflowRequestT, WorkflowBfmT workflowBfmT,
			WorkflowStepT rejectedStep) throws Exception {
		logger.info("destinationmailutils - inside sendPathADESSHeadRejectedMail method");
		DestinationMailMessage message = new DestinationMailMessage();
		List<String> recepientIds = new ArrayList<String>();
		List<String> ccIds = new ArrayList<String>();
		
		String templateLoc = workflowBFMEscalatePathADESSHeadApproveTemplateLoc;
		
		OpportunityT opportunity = workflowBfmT.getOpportunityT();
		CustomerMasterT customer = opportunity.getCustomerMasterT();
		String customerName = customer.getCustomerName();
		String geography = customer.getGeography();
		
		List<String> geoHeads = userAccessPrivilegesRepository.findUserIdsForCustomerUserGroup(geography, Constants.Y, UserGroup.SALES_HEAD.getValue());
		if(CollectionUtils.isNotEmpty(geoHeads)) {
			ccIds.addAll(geoHeads);
		}
		
		// populate subject
		StringBuffer subject = new StringBuffer("");
		subject.append(Constants.WORKFLOW_BFM_ESCALATE_PATH_A_REJECTED_SUBJECT);
		subject.append(customerName);
		String mailSubject = formatSubject(subject.toString());
		message.setSubject(mailSubject);
		
		if(StringUtils.isEmpty(rejectedStep.getComments())){
			workflowMap.put("comments","Not Provided");	
		} else {
		    workflowMap.put("comments",rejectedStep.getComments());
		}
		workflowMap.put("status","rejected");
		
		WorkflowStepT pendingStepFinal =workflowStepRepository
				.findByRequestIdAndStep(workflowRequestT.getRequestId(),rejectedStep.getStep()+1);
		
		WorkflowStepT previousForRejectedStep =workflowStepRepository
				.findByRequestIdAndStep(workflowRequestT.getRequestId(),rejectedStep.getStep()-1);
		
		// populate recepients
		if (pendingStepFinal.getUserRole() != null) {
					List<String> userIdsByRole = getUserIdsByRole(pendingStepFinal);
					recepientIds.addAll(userIdsByRole);
		}
		if (pendingStepFinal.getUserId() != null) {
					List<String> userIdsByIds = getUserIdsById(pendingStepFinal);
					recepientIds.addAll(userIdsByIds);
		}
							
		if (pendingStepFinal.getUserGroup() != null) {
						switch (pendingStepFinal.getUserGroup()) {
							    case Constants.WORKFLOW_GEO_HEADS:
							    	recepientIds
											.addAll(userAccessPrivilegesRepository
													.findUserIdsForCustomerUserGroup(geography,
															Constants.Y,
															UserGroup.SALES_HEAD.getValue()));
									logger.debug("recepient Ids for Sales Head :" + recepientIds);
							ccIds.addAll(userAccessPrivilegesRepository
											.findUserIdsForCustomerUserGroup(geography,
													Constants.Y, UserGroup.PMO.getValue()));
									logger.debug("CCIds for PMO :" + ccIds);
									break;
								case Constants.WORKFLOW_PMO:
									ccIds.addAll(userAccessPrivilegesRepository
											.findUserIdsForCustomerUserGroup(geography,
													Constants.Y, UserGroup.PMO.getValue()));
						default:
									break;
								}
		}
		
		String createdById = workflowRequestT.getCreatedBy();
		ccIds.add(createdById);
		
		// populate recepients
		if (previousForRejectedStep.getUserRole() != null) {
							List<String> userIdsByRole = getUserIdsByRole(previousForRejectedStep);
							ccIds.addAll(userIdsByRole);
		}
		if (previousForRejectedStep.getUserId() != null) {
							List<String> userIdsByIds = getUserIdsById(previousForRejectedStep);
							ccIds.addAll(userIdsByIds);
		}
									
		if (previousForRejectedStep.getUserGroup() != null) {
								switch (previousForRejectedStep.getUserGroup()) {
									    case Constants.WORKFLOW_GEO_HEADS:
									    	ccIds.addAll(userAccessPrivilegesRepository
															.findUserIdsForCustomerUserGroup(geography,
																	Constants.Y,
																	UserGroup.SALES_HEAD.getValue()));
											logger.debug("recepient Ids for Sales Head :" + recepientIds);
									ccIds.addAll(userAccessPrivilegesRepository
													.findUserIdsForCustomerUserGroup(geography,
															Constants.Y, UserGroup.PMO.getValue()));
											logger.debug("CCIds for PMO :" + ccIds);
											break;
										case Constants.WORKFLOW_PMO:
											ccIds.addAll(userAccessPrivilegesRepository
													.findUserIdsForCustomerUserGroup(geography,
															Constants.Y, UserGroup.PMO.getValue()));
								default:
											break;
										}
		}
		
		if (rejectedStep.getUserRole() != null) {
			List<String> userIdsByRole = getUserIdsByRole(rejectedStep);
			ccIds.addAll(userIdsByRole);
		}
		if (rejectedStep.getUserId() != null) {
			List<String> userIdsByIds = getUserIdsById(rejectedStep);
			ccIds.addAll(userIdsByIds);
		}
					
		if (rejectedStep.getUserGroup() != null) {
				switch (rejectedStep.getUserGroup()) {
					    case Constants.WORKFLOW_GEO_HEADS:
					    	ccIds
									.addAll(userAccessPrivilegesRepository
											.findUserIdsForCustomerUserGroup(geography,
													Constants.Y,
													UserGroup.SALES_HEAD.getValue()));
							logger.debug("recepient Ids for Sales Head :" + recepientIds);
					ccIds.addAll(userAccessPrivilegesRepository
									.findUserIdsForCustomerUserGroup(geography,
											Constants.Y, UserGroup.PMO.getValue()));
							logger.debug("CCIds for PMO :" + ccIds);
							break;
						case Constants.WORKFLOW_PMO:
							ccIds.addAll(userAccessPrivilegesRepository
									.findUserIdsForCustomerUserGroup(geography,
											Constants.Y, UserGroup.PMO.getValue()));
				default:
							break;
						}
		}
		addDefaultUserInCC(ccIds);
		message.setRecipients(listMailIdsFromUserIds(recepientIds));
		message.setCcList(listMailIdsFromUserIds(ccIds));
		
		// populate template
		String text = mergeTmplWithData(workflowMap, templateLoc);
		logger.info("framed text for mail :" + text);
		message.setMessage(text);

		// send mail
		destMailSender.send(message);
		logger.info("Mail Sent for request"
							+ workflowRequestT.getRequestId());
	}
	
	/**
	 * this method is used to send mail when BFM workflow is approved by GEO Head in PathB
	 * @param workflowMap
	 * @param workflowRequestT
	 * @param workflowBfmT
	 * @param approvedStep
	 * @throws Exception
	 */
	private void sendPathBGEOHeadApprovedMail(Map<String, Object> workflowMap,
			WorkflowRequestT workflowRequestT, WorkflowBfmT workflowBfmT,
			WorkflowStepT approvedStep) throws Exception {
		logger.info("destinationmailutils - inside sendPathBGEOHeadApprovedMail method");
		DestinationMailMessage message = new DestinationMailMessage();
		List<String> recepientIds = new ArrayList<String>();
		List<String> ccIds = new ArrayList<String>();
		
		String templateLoc = workflowPendingBFMEscalateBApproveRejectTemplateLoc;
		
		OpportunityT opportunity = workflowBfmT.getOpportunityT();
		CustomerMasterT customer = opportunity.getCustomerMasterT();
		String customerName = customer.getCustomerName();
		String geography = customer.getGeography();
		// populate subject
		StringBuffer subject = new StringBuffer("");
		subject.append(Constants.WORKFLOW_BFM_ESCALATE_PATH_A_APPROVED_SUBJECT);
		subject.append(customerName);
		String mailSubject = formatSubject(subject.toString());
		message.setSubject(mailSubject);
		
		if(StringUtils.isEmpty(approvedStep.getComments())){
			workflowMap.put("comments","Not Provided");	
		} else {
		    workflowMap.put("comments",approvedStep.getComments());
		}
		workflowMap.put("status","approved");
		
		//pending last step
		WorkflowStepT pendingStep =workflowStepRepository
				.findByRequestIdAndStep(workflowRequestT.getRequestId(),approvedStep.getStep()+1);
		
				
		if (approvedStep.getUserRole() != null) {
					List<String> userIdsByRole = getUserIdsByRole(approvedStep);
					ccIds.addAll(userIdsByRole);
		}
	    if (approvedStep.getUserId() != null) {
					List<String> userIdsByIds = getUserIdsById(approvedStep);
					ccIds.addAll(userIdsByIds);
		}
							
		if (approvedStep.getUserGroup() != null) {
						switch (approvedStep.getUserGroup()) {
							    case Constants.WORKFLOW_GEO_HEADS:
							    	ccIds
											.addAll(userAccessPrivilegesRepository
													.findUserIdsForCustomerUserGroup(geography,
															Constants.Y,
															UserGroup.SALES_HEAD.getValue()));
									logger.debug("recepient Ids for Sales Head :" + recepientIds);
							ccIds.addAll(userAccessPrivilegesRepository
											.findUserIdsForCustomerUserGroup(geography,
													Constants.Y, UserGroup.PMO.getValue()));
									logger.debug("CCIds for PMO :" + ccIds);
									break;
								case Constants.WORKFLOW_PMO:
									ccIds.addAll(userAccessPrivilegesRepository
											.findUserIdsForCustomerUserGroup(geography,
													Constants.Y, UserGroup.PMO.getValue()));
						default:
									break;
								}
		}
		
		if (pendingStep.getUserRole() != null) {
			List<String> userIdsByRole = getUserIdsByRole(pendingStep);
			recepientIds.addAll(userIdsByRole);
		}
		if (pendingStep.getUserId() != null) {
			List<String> userIdsByIds = getUserIdsById(pendingStep);
			recepientIds.addAll(userIdsByIds);
		}
					
		if (pendingStep.getUserGroup() != null) {
				switch (pendingStep.getUserGroup()) {
					    case Constants.WORKFLOW_GEO_HEADS:
					    	recepientIds
									.addAll(userAccessPrivilegesRepository
											.findUserIdsForCustomerUserGroup(geography,
													Constants.Y,
													UserGroup.SALES_HEAD.getValue()));
							logger.debug("recepient Ids for Sales Head :" + recepientIds);
					ccIds.addAll(userAccessPrivilegesRepository
									.findUserIdsForCustomerUserGroup(geography,
											Constants.Y, UserGroup.PMO.getValue()));
							logger.debug("CCIds for PMO :" + ccIds);
							break;
						case Constants.WORKFLOW_PMO:
							recepientIds.addAll(userAccessPrivilegesRepository
									.findUserIdsForCustomerUserGroup(geography,
											Constants.Y, UserGroup.PMO.getValue()));
				default:
							break;
						}
		}
		
		String createdById = workflowRequestT.getCreatedBy();
		ccIds.add(createdById);
		addDefaultUserInCC(ccIds);
		message.setRecipients(listMailIdsFromUserIds(recepientIds));
		message.setCcList(listMailIdsFromUserIds(ccIds));
		
		// populate template
		String text = mergeTmplWithData(workflowMap, templateLoc);
		logger.info("framed text for mail :" + text);
		message.setMessage(text);

		// send mail
		destMailSender.send(message);
		logger.info("Mail Sent for request"
							+ workflowRequestT.getRequestId());
		
	}
	
	/**
	 * this method is used to send mail when BFM workflow is rejected by GEO Head in path A
	 * @param workflowMap
	 * @param workflowRequestT
	 * @param workflowBfmT
	 * @param rejectedStep
	 * @throws Exception
	 */
	private void sendPathAGEOHeadRejectedMail(Map<String, Object> workflowMap,
			WorkflowRequestT workflowRequestT, WorkflowBfmT workflowBfmT,
			WorkflowStepT rejectedStep) throws Exception {
		logger.info("destinationmailutils - inside sendPathAGEOHeadRejectedMail method");
		DestinationMailMessage message = new DestinationMailMessage();
		List<String> recepientIds = new ArrayList<String>();
		List<String> ccIds = new ArrayList<String>();
		
		
		
		String templateLoc = workflowPendingBFMEscalateBApproveRejectTemplateLoc;
		
		OpportunityT opportunity = workflowBfmT.getOpportunityT();
		CustomerMasterT customer = opportunity.getCustomerMasterT();
		String customerName = customer.getCustomerName();
		String geography = customer.getGeography();
		
		List<String> geoHeads = userAccessPrivilegesRepository.findUserIdsForCustomerUserGroup(geography, Constants.Y, UserGroup.SALES_HEAD.getValue());
		if(CollectionUtils.isNotEmpty(geoHeads)) {
			ccIds.addAll(geoHeads);
		}
		
		// populate subject
		StringBuffer subject = new StringBuffer("");
		subject.append(Constants.WORKFLOW_BFM_ESCALATE_PATH_A_REJECTED_SUBJECT);
		subject.append(customerName);
		String mailSubject = formatSubject(subject.toString());
		message.setSubject(mailSubject);
		
		if(StringUtils.isEmpty(rejectedStep.getComments())){
			workflowMap.put("comments","Not Provided");	
		} else {
		    workflowMap.put("comments",rejectedStep.getComments());
		}
		workflowMap.put("status","rejected");
		
		WorkflowStepT previousForRejectedStep =workflowStepRepository
				.findByRequestIdAndStep(workflowRequestT.getRequestId(),rejectedStep.getStep()-1);
		
		// populate recepients
		if (previousForRejectedStep.getUserRole() != null) {
									List<String> userIdsByRole = getUserIdsByRole(previousForRejectedStep);
									recepientIds.addAll(userIdsByRole);
		}
		if (previousForRejectedStep.getUserId() != null) {
									List<String> userIdsByIds = getUserIdsById(previousForRejectedStep);
									recepientIds.addAll(userIdsByIds);
		}
											
		if (previousForRejectedStep.getUserGroup() != null) {
										switch (previousForRejectedStep.getUserGroup()) {
											    case Constants.WORKFLOW_GEO_HEADS:
											    	recepientIds
															.addAll(userAccessPrivilegesRepository
																	.findUserIdsForCustomerUserGroup(geography,
																			Constants.Y,
																			UserGroup.SALES_HEAD.getValue()));
													logger.debug("recepient Ids for Sales Head :" + recepientIds);
											ccIds.addAll(userAccessPrivilegesRepository
															.findUserIdsForCustomerUserGroup(geography,
																	Constants.Y, UserGroup.PMO.getValue()));
													logger.debug("CCIds for PMO :" + ccIds);
													break;
												case Constants.WORKFLOW_PMO:
													ccIds.addAll(userAccessPrivilegesRepository
															.findUserIdsForCustomerUserGroup(geography,
																	Constants.Y, UserGroup.PMO.getValue()));
										default:
													break;
												}
		}
				
		if (rejectedStep.getUserRole() != null) {
					List<String> userIdsByRole = getUserIdsByRole(rejectedStep);
					ccIds.addAll(userIdsByRole);
		}
	    if (rejectedStep.getUserId() != null) {
					List<String> userIdsByIds = getUserIdsById(rejectedStep);
					ccIds.addAll(userIdsByIds);
		}
							
		if (rejectedStep.getUserGroup() != null) {
						switch (rejectedStep.getUserGroup()) {
							    case Constants.WORKFLOW_GEO_HEADS:
							    	ccIds
											.addAll(userAccessPrivilegesRepository
													.findUserIdsForCustomerUserGroup(geography,
															Constants.Y,
															UserGroup.SALES_HEAD.getValue()));
									logger.debug("recepient Ids for Sales Head :" + recepientIds);
							ccIds.addAll(userAccessPrivilegesRepository
											.findUserIdsForCustomerUserGroup(geography,
													Constants.Y, UserGroup.PMO.getValue()));
									logger.debug("CCIds for PMO :" + ccIds);
									break;
								case Constants.WORKFLOW_PMO:
									ccIds.addAll(userAccessPrivilegesRepository
											.findUserIdsForCustomerUserGroup(geography,
													Constants.Y, UserGroup.PMO.getValue()));
						default:
									break;
								}
		}
		
		String createdById = workflowRequestT.getCreatedBy();
		ccIds.add(createdById);
		addDefaultUserInCC(ccIds);
		message.setRecipients(listMailIdsFromUserIds(recepientIds));
		message.setCcList(listMailIdsFromUserIds(ccIds));
		
		// populate template
		String text = mergeTmplWithData(workflowMap, templateLoc);
		logger.info("framed text for mail :" + text);
		message.setMessage(text);

		// send mail
		destMailSender.send(message);
		logger.info("Mail Sent for request"
							+ workflowRequestT.getRequestId());
		
	}
	
	/**
	 * this method is used to send mail when BFM workflow is approved by DESS Head
	 * @param workflowMap
	 * @param workflowRequestT
	 * @param workflowBfmT
	 * @param approvedStep
	 * @throws Exception
	 */
	private void sendPathADESSHeadApprovedMail(Map<String, Object> workflowMap,
			WorkflowRequestT workflowRequestT, WorkflowBfmT workflowBfmT,
			WorkflowStepT approvedStep) throws Exception {
		logger.info("destinationmailutils - inside sendPathADESSHeadApprovedMail method");
		DestinationMailMessage message = new DestinationMailMessage();
		List<String> recepientIds = new ArrayList<String>();
		List<String> ccIds = new ArrayList<String>();
		
		String templateLoc = workflowBFMEscalatePathADESSHeadApproveTemplateLoc;
		
		OpportunityT opportunity = workflowBfmT.getOpportunityT();
		CustomerMasterT customer = opportunity.getCustomerMasterT();
		String customerName = customer.getCustomerName();
		String geography = customer.getGeography();
		
		// populate subject
		StringBuffer subject = new StringBuffer("");
		subject.append(Constants.WORKFLOW_BFM_ESCALATE_PATH_A_APPROVED_SUBJECT);
		subject.append(customerName);
		String mailSubject = formatSubject(subject.toString());
		message.setSubject(mailSubject);
		
		if(StringUtils.isEmpty(approvedStep.getComments())){
			workflowMap.put("comments","Not Provided");	
		} else {
		    workflowMap.put("comments",approvedStep.getComments());
		}
		workflowMap.put("status","approved");
		
		WorkflowStepT pendingStepFinal =workflowStepRepository
				.findByRequestIdAndStep(workflowRequestT.getRequestId(),approvedStep.getStep()+1);
		
		WorkflowStepT previousForApprovedStep =workflowStepRepository
				.findByRequestIdAndStep(workflowRequestT.getRequestId(),approvedStep.getStep()-1);
		
		// populate recepients
		if (pendingStepFinal.getUserRole() != null) {
					List<String> userIdsByRole = getUserIdsByRole(pendingStepFinal);
					recepientIds.addAll(userIdsByRole);
		}
		if (pendingStepFinal.getUserId() != null) {
					List<String> userIdsByIds = getUserIdsById(pendingStepFinal);
					recepientIds.addAll(userIdsByIds);
		}
							
		if (pendingStepFinal.getUserGroup() != null) {
						switch (pendingStepFinal.getUserGroup()) {
							    case Constants.WORKFLOW_GEO_HEADS:
							    	recepientIds
											.addAll(userAccessPrivilegesRepository
													.findUserIdsForCustomerUserGroup(geography,
															Constants.Y,
															UserGroup.SALES_HEAD.getValue()));
									logger.debug("recepient Ids for Sales Head :" + recepientIds);
							ccIds.addAll(userAccessPrivilegesRepository
											.findUserIdsForCustomerUserGroup(geography,
													Constants.Y, UserGroup.PMO.getValue()));
									logger.debug("CCIds for PMO :" + ccIds);
									break;
								case Constants.WORKFLOW_PMO:
									ccIds.addAll(userAccessPrivilegesRepository
											.findUserIdsForCustomerUserGroup(geography,
													Constants.Y, UserGroup.PMO.getValue()));
						default:
									break;
								}
		}
		
		String createdById = workflowRequestT.getCreatedBy();
		ccIds.add(createdById);
		
		// populate recepients
		if (previousForApprovedStep.getUserRole() != null) {
							List<String> userIdsByRole = getUserIdsByRole(previousForApprovedStep);
							ccIds.addAll(userIdsByRole);
		}
		if (previousForApprovedStep.getUserId() != null) {
							List<String> userIdsByIds = getUserIdsById(previousForApprovedStep);
							ccIds.addAll(userIdsByIds);
		}
									
		if (previousForApprovedStep.getUserGroup() != null) {
								switch (previousForApprovedStep.getUserGroup()) {
									    case Constants.WORKFLOW_GEO_HEADS:
									    	ccIds
													.addAll(userAccessPrivilegesRepository
															.findUserIdsForCustomerUserGroup(geography,
																	Constants.Y,
																	UserGroup.SALES_HEAD.getValue()));
											logger.debug("recepient Ids for Sales Head :" + recepientIds);
									ccIds.addAll(userAccessPrivilegesRepository
													.findUserIdsForCustomerUserGroup(geography,
															Constants.Y, UserGroup.PMO.getValue()));
											logger.debug("CCIds for PMO :" + ccIds);
											break;
										case Constants.WORKFLOW_PMO:
											ccIds.addAll(userAccessPrivilegesRepository
													.findUserIdsForCustomerUserGroup(geography,
															Constants.Y, UserGroup.PMO.getValue()));
								default:
											break;
										}
		}
		
		if (approvedStep.getUserRole() != null) {
			List<String> userIdsByRole = getUserIdsByRole(approvedStep);
			ccIds.addAll(userIdsByRole);
		}
		if (approvedStep.getUserId() != null) {
			List<String> userIdsByIds = getUserIdsById(approvedStep);
			ccIds.addAll(userIdsByIds);
		}
					
		if (approvedStep.getUserGroup() != null) {
				switch (approvedStep.getUserGroup()) {
					    case Constants.WORKFLOW_GEO_HEADS:
					    	ccIds
									.addAll(userAccessPrivilegesRepository
											.findUserIdsForCustomerUserGroup(geography,
													Constants.Y,
													UserGroup.SALES_HEAD.getValue()));
							logger.debug("recepient Ids for Sales Head :" + recepientIds);
					ccIds.addAll(userAccessPrivilegesRepository
									.findUserIdsForCustomerUserGroup(geography,
											Constants.Y, UserGroup.PMO.getValue()));
							logger.debug("CCIds for PMO :" + ccIds);
							break;
						case Constants.WORKFLOW_PMO:
							ccIds.addAll(userAccessPrivilegesRepository
									.findUserIdsForCustomerUserGroup(geography,
											Constants.Y, UserGroup.PMO.getValue()));
				default:
							break;
						}
		}
		addDefaultUserInCC(ccIds);
		message.setRecipients(listMailIdsFromUserIds(recepientIds));
		message.setCcList(listMailIdsFromUserIds(ccIds));
		
		// populate template
		String text = mergeTmplWithData(workflowMap, templateLoc);
		logger.info("framed text for mail :" + text);
		message.setMessage(text);

		// send mail
		destMailSender.send(message);
		logger.info("Mail Sent for request"
							+ workflowRequestT.getRequestId());
	}

	
	/**
	 * this method is used to send mail when BFM workflow is approved by GEO Head in path A
	 * @param workflowMap
	 * @param workflowRequestT
	 * @param workflowBfmT
	 * @param approvedStep
	 * @throws Exception
	 */
	private void sendPathAGEOHeadApprovedMail(Map<String, Object> workflowMap,
			WorkflowRequestT workflowRequestT, WorkflowBfmT workflowBfmT,
			WorkflowStepT approvedStep) throws Exception {
		logger.info("destinationmailutils - inside sendPathAGEOHeadApprovedMail method");
		DestinationMailMessage message = new DestinationMailMessage();
		List<String> recepientIds = new ArrayList<String>();
		List<String> ccIds = new ArrayList<String>();
		
		String templateLoc = workflowBFMEscalatePathAGEOHeadApproveTemplateLoc;
		
		OpportunityT opportunity = workflowBfmT.getOpportunityT();
		CustomerMasterT customer = opportunity.getCustomerMasterT();
		String customerName = customer.getCustomerName();
		String geography = customer.getGeography();
		
		// populate subject
		StringBuffer subject = new StringBuffer("");
		subject.append(Constants.WORKFLOW_BFM_ESCALATE_PENDING_SUBJECT);
		subject.append(customerName);
		String mailSubject = formatSubject(subject.toString());
		message.setSubject(mailSubject);
		
		WorkflowStepT pendingStepDESSHead =workflowStepRepository
				.findByRequestIdAndStep(workflowRequestT.getRequestId(),approvedStep.getStep()+1);
		
		WorkflowStepT escalatedStep =workflowStepRepository
				.findByRequestIdAndStep(workflowRequestT.getRequestId(),approvedStep.getStep()-1);
		
		if(StringUtils.isEmpty(approvedStep.getComments())){
			workflowMap.put("comments","Not Provided");	
		} else {
		    workflowMap.put("comments",approvedStep.getComments());
		}
		
		// populate recepients
		if (pendingStepDESSHead.getUserRole() != null) {
			List<String> userIdsByRole = getUserIdsByRole(pendingStepDESSHead);
			recepientIds.addAll(userIdsByRole);
		}
		if (pendingStepDESSHead.getUserId() != null) {
			List<String> userIdsByIds = getUserIdsById(pendingStepDESSHead);
			recepientIds.addAll(userIdsByIds);
		}
					
		if (pendingStepDESSHead.getUserGroup() != null) {
				switch (pendingStepDESSHead.getUserGroup()) {
					    case Constants.WORKFLOW_GEO_HEADS:
					    	recepientIds
									.addAll(userAccessPrivilegesRepository
											.findUserIdsForCustomerUserGroup(geography,
													Constants.Y,
													UserGroup.SALES_HEAD.getValue()));
							logger.debug("recepient Ids for Sales Head :" + recepientIds);
					ccIds.addAll(userAccessPrivilegesRepository
									.findUserIdsForCustomerUserGroup(geography,
											Constants.Y, UserGroup.PMO.getValue()));
							logger.debug("CCIds for PMO :" + ccIds);
							break;
						case Constants.WORKFLOW_PMO:
							ccIds.addAll(userAccessPrivilegesRepository
									.findUserIdsForCustomerUserGroup(geography,
											Constants.Y, UserGroup.PMO.getValue()));
				default:
							break;
						}
		}
		
		if (approvedStep.getUserRole() != null) {
			List<String> userIdsByRole = getUserIdsByRole(approvedStep);
			ccIds.addAll(userIdsByRole);
		}
		if (approvedStep.getUserId() != null) {
			List<String> userIdsByIds = getUserIdsById(approvedStep);
			ccIds.addAll(userIdsByIds);
		}
					
		if (approvedStep.getUserGroup() != null) {
				switch (approvedStep.getUserGroup()) {
					    case Constants.WORKFLOW_GEO_HEADS:
					    	ccIds
									.addAll(userAccessPrivilegesRepository
											.findUserIdsForCustomerUserGroup(geography,
													Constants.Y,
													UserGroup.SALES_HEAD.getValue()));
							logger.debug("recepient Ids for Sales Head :" + recepientIds);
					ccIds.addAll(userAccessPrivilegesRepository
									.findUserIdsForCustomerUserGroup(geography,
											Constants.Y, UserGroup.PMO.getValue()));
							logger.debug("CCIds for PMO :" + ccIds);
							break;
						case Constants.WORKFLOW_PMO:
							ccIds.addAll(userAccessPrivilegesRepository
									.findUserIdsForCustomerUserGroup(geography,
											Constants.Y, UserGroup.PMO.getValue()));
				default:
							break;
						}
		}
		
		if (escalatedStep.getUserRole() != null) {
			List<String> userIdsByRole = getUserIdsByRole(escalatedStep);
			ccIds.addAll(userIdsByRole);
		}
		if (escalatedStep.getUserId() != null) {
			List<String> userIdsByIds = getUserIdsById(escalatedStep);
			ccIds.addAll(userIdsByIds);
		}
					
		if (escalatedStep.getUserGroup() != null) {
				switch (escalatedStep.getUserGroup()) {
					    case Constants.WORKFLOW_GEO_HEADS:
					    	ccIds
									.addAll(userAccessPrivilegesRepository
											.findUserIdsForCustomerUserGroup(geography,
													Constants.Y,
													UserGroup.SALES_HEAD.getValue()));
							logger.debug("recepient Ids for Sales Head :" + recepientIds);
					ccIds.addAll(userAccessPrivilegesRepository
									.findUserIdsForCustomerUserGroup(geography,
											Constants.Y, UserGroup.PMO.getValue()));
							logger.debug("CCIds for PMO :" + ccIds);
							break;
						case Constants.WORKFLOW_PMO:
							ccIds.addAll(userAccessPrivilegesRepository
									.findUserIdsForCustomerUserGroup(geography,
											Constants.Y, UserGroup.PMO.getValue()));
				default:
							break;
						}
		}
		
		 UserT approvedUser = userRepository.findOne(approvedStep.getUserId());
		 String geoHead = approvedUser.getUserName();
		 workflowMap.put("geoHead", geoHead);
		
		String createdById = workflowRequestT.getCreatedBy();
		ccIds.add(createdById);
		addDefaultUserInCC(ccIds);
		message.setRecipients(listMailIdsFromUserIds(recepientIds));
		message.setCcList(listMailIdsFromUserIds(ccIds));
		
		// populate template
		String text = mergeTmplWithData(workflowMap, templateLoc);
		logger.info("framed text for mail :" + text);
		message.setMessage(text);

		// send mail
		destMailSender.send(message);
		logger.info("Mail Sent for request"
							+ workflowRequestT.getRequestId());
		
	}

	
	/**
	 * this method is used to send mail when BFM workflow is approved finally (Path A and B)
	 * @param workflowMap
	 * @param workflowRequestT
	 * @param workflowBfmT
	 * @param approvedStep
	 * @throws Exception
	 */
	private void sendPathAFinalStepApprovedEmail(
			Map<String, Object> workflowMap, WorkflowRequestT workflowRequestT,
			WorkflowBfmT workflowBfmT, WorkflowStepT approvedStep) throws Exception {
		logger.info("destinationmailutils - inside sendPathAFinalStepApprovedEmail method");
        DestinationMailMessage message = new DestinationMailMessage();
		
		String status = "approved";
		workflowMap.put("status", status);
		
		if(StringUtils.isEmpty(approvedStep.getComments())){
			workflowMap.put("comments","Not Provided");	
		} else {
		    workflowMap.put("comments",approvedStep.getComments());
		}
		
		OpportunityT opportunity = workflowBfmT.getOpportunityT();
		String opportunityId = opportunity.getOpportunityId();
		CustomerMasterT customer = opportunity.getCustomerMasterT();
		String customerName = customer.getCustomerName();
		String geography = customer.getGeography();
		
		// populate subject
		StringBuffer subject = new StringBuffer("");
		subject.append(Constants.WORKFLOW_BFM_STEP1_APPROVED_SUBJECT);
		subject.append(getCustomerOpportunitySubString(opportunityId,customerName));
		String mailSubject = formatSubject(subject.toString());
		message.setSubject(mailSubject);
		
		List<String> recepientIds = new ArrayList<String>();
		List<String> ccIds = new ArrayList<String>();
		
		// populate recepients
		if (approvedStep.getUserRole() != null) {
			 List<String> userIdsByRole = getUserIdsByRole(approvedStep);
			 ccIds.addAll(userIdsByRole);
			}
			if (approvedStep.getUserId() != null) {
			 List<String> userIdsByIds = getUserIdsById(approvedStep);
			 ccIds.addAll(userIdsByIds);
			}
		// Adding respective Sales Head to cc Ids	
		 List<String> geoHeads = userAccessPrivilegesRepository.findUserIdsForCustomerUserGroup(geography, Constants.Y, UserGroup.SALES_HEAD.getValue());
		if(CollectionUtils.isNotEmpty(geoHeads)) {
			ccIds.addAll(geoHeads);
		}
			if (approvedStep.getUserGroup() != null) {
				switch (approvedStep.getUserGroup()) {
				case Constants.WORKFLOW_GEO_HEADS:
					ccIds
							.addAll(userAccessPrivilegesRepository
									.findUserIdsForCustomerUserGroup(geography,
											Constants.Y,
											UserGroup.SALES_HEAD.getValue()));
					logger.debug("recepient Ids for Sales Head :" + recepientIds);
					ccIds.addAll(userAccessPrivilegesRepository
							.findUserIdsForCustomerUserGroup(geography,
									Constants.Y, UserGroup.PMO.getValue()));
					logger.debug("CCIds for PMO :" + ccIds);
					break;
				case Constants.WORKFLOW_PMO:
					ccIds.addAll(userAccessPrivilegesRepository
							.findUserIdsForCustomerUserGroup(geography,
									Constants.Y, UserGroup.PMO.getValue()));
				default:
					break;
				}
			}
		
			String createdById = workflowRequestT.getCreatedBy();
			recepientIds.add(createdById);
			
			//adding system admin and strategic group admin in cc
			addSysAdminStrategicAdminCC(ccIds);
			addDefaultUserInCC(ccIds);
			message.setRecipients(listMailIdsFromUserIds(recepientIds));
			message.setCcList(listMailIdsFromUserIds(ccIds));
			
			// populate template
			String templateLoc = workflowApprovedRejectedBFMStep1TemplateLoc;	
			String text = mergeTmplWithData(workflowMap, templateLoc);
			logger.info("framed text for mail :" + text);
			message.setMessage(text);

			// send mail
			destMailSender.send(message);
			logger.info("Mail Sent for request"
					+ workflowRequestT.getRequestId());
		
	}

	/**
	 * this method is used to send mail when BFM workflow is rejected finally
	 * @param workflowMap
	 * @param workflowRequestT
	 * @param workflowBfmT
	 * @param rejectedStep
	 * @throws Exception
	 */
	private void sendPathAFinalStepRejectedEmail(
			Map<String, Object> workflowMap, WorkflowRequestT workflowRequestT,
			WorkflowBfmT workflowBfmT, WorkflowStepT rejectedStep) throws Exception {
		logger.info("destinationmailutils - inside sendPathAFinalStepRejectedEmail method");

		DestinationMailMessage message = new DestinationMailMessage();
		
		String status = "rejected";
		workflowMap.put("status", status);
		
		OpportunityT opportunity = workflowBfmT.getOpportunityT();
		String opportunityId = opportunity.getOpportunityId();
		CustomerMasterT customer = opportunity.getCustomerMasterT();
		String customerName = customer.getCustomerName();
		String geography = customer.getGeography();
		
		// populate subject
		StringBuffer subject = new StringBuffer("");
		subject.append(Constants.WORKFLOW_BFM_STEP1_REJECTED_SUBJECT);
		subject.append(getCustomerOpportunitySubString(opportunityId,customerName));
		String mailSubject = formatSubject(subject.toString());
		message.setSubject(mailSubject);
		
		List<String> recepientIds = new ArrayList<String>();
		List<String> ccIds = new ArrayList<String>();
		
		List<String> geoHeads = userAccessPrivilegesRepository.findUserIdsForCustomerUserGroup(geography, Constants.Y, UserGroup.SALES_HEAD.getValue());
		if(CollectionUtils.isNotEmpty(geoHeads)) {
			ccIds.addAll(geoHeads);
		}
		
		if(StringUtils.isEmpty(rejectedStep.getComments())){
			workflowMap.put("comments","Not Provided");	
		} else {
		    workflowMap.put("comments",rejectedStep.getComments());
		}
		
		// populate recepients
		if (rejectedStep.getUserRole() != null) {
			 List<String> userIdsByRole = getUserIdsByRole(rejectedStep);
			 ccIds.addAll(userIdsByRole);
			}
			if (rejectedStep.getUserId() != null) {
			 List<String> userIdsByIds = getUserIdsById(rejectedStep);
			 ccIds.addAll(userIdsByIds);
			}
			
			if (rejectedStep.getUserGroup() != null) {
				switch (rejectedStep.getUserGroup()) {
				case Constants.WORKFLOW_GEO_HEADS:
					ccIds
							.addAll(userAccessPrivilegesRepository
									.findUserIdsForCustomerUserGroup(geography,
											Constants.Y,
											UserGroup.SALES_HEAD.getValue()));
					logger.debug("recepient Ids for Sales Head :" + recepientIds);
					ccIds.addAll(userAccessPrivilegesRepository
							.findUserIdsForCustomerUserGroup(geography,
									Constants.Y, UserGroup.PMO.getValue()));
					logger.debug("CCIds for PMO :" + ccIds);
					break;
				case Constants.WORKFLOW_PMO:
					ccIds.addAll(userAccessPrivilegesRepository
							.findUserIdsForCustomerUserGroup(geography,
									Constants.Y, UserGroup.PMO.getValue()));
				default:
					break;
				}
			}
		
			String createdById = workflowRequestT.getCreatedBy();
			recepientIds.add(createdById);
			
			//adding system admin and strategic group admin in cc
			addSysAdminStrategicAdminCC(ccIds);
			addDefaultUserInCC(ccIds);
			message.setRecipients(listMailIdsFromUserIds(recepientIds));
			message.setCcList(listMailIdsFromUserIds(ccIds));
			
			// populate template
			String templateLoc = workflowApprovedRejectedBFMStep1TemplateLoc;	
			String text = mergeTmplWithData(workflowMap, templateLoc);
			logger.info("framed text for mail :" + text);
			message.setMessage(text);

			// send mail
			destMailSender.send(message);
			logger.info("Mail Sent for request"
					+ workflowRequestT.getRequestId());
			
	}
	
	/**
	 * Sends email for delivery flow
	 * 
	 * @param entityId
	 * @param entityType
	 * @param deliveryCenterId
	 * @throws Exception
	 */
	public void sendDeliveryEmails(String entityId, String entityType,
			Integer deliveryCenterId) throws Exception {
		DestinationMailMessage message = getDeliveryMailDetails(entityId,
				entityType, deliveryCenterId);
		if(message!=null) {
			destMailSender.send(message);
		}
		logger.info("Mail Sent for Engagement "+entityId);

	}

	/**
	 * Gets the details related to delivery emails
	 * @param entityId
	 * @param entityType
	 * @param deliveryCenterId
	 * @return
	 * @throws Exception 
	 */
	private DestinationMailMessage getDeliveryMailDetails(
             String entityId,String entityType, Integer deliveryCenterId) throws Exception {
		logger.info("Inside getDeliveryMailDetails Method");
		DestinationMailMessage message = new DestinationMailMessage();
		Map<String, Object> data = Maps.newHashMap();
		List<String> reciepientIds = Lists.newArrayList();
		List<String> ccIds = Lists.newArrayList();
		String subject = null;
		String templateLoc = null;
		switch (EntityType.getByValue(entityType)) {
		// Entity Type -> Delivery : Opportunity tagged for engagement after win
		case DELIVERY:
			DeliveryMasterT deliveryMaster = deliveryMasterRepository
					.findOne(entityId);
			if (deliveryMaster != null) {
				List<DeliveryMasterManagerLinkT> deliveryMasterManagerLinkTs = deliveryMasterManagerLinkRepository.findByDeliveryMasterId(deliveryMaster.getDeliveryMasterId());
				deliveryMaster.setDeliveryMasterManagerLinkTs(deliveryMasterManagerLinkTs);
				DeliveryCentreT deliveryCentreT = deliveryMaster
						.getDeliveryCentreT();
				DeliveryClusterT deliveryClusterT = deliveryCentreT
						.getDeliveryClusterT();
				OpportunityT opportunity = deliveryMaster.getOpportunityT();
				CustomerMasterT customer = opportunity.getCustomerMasterT();
				// getting data for mail message content
				data.putAll(getDataForDelivery(deliveryMaster));
				data.putAll(getDataForOpportunityDelivery(opportunity));

				String[] replacementList = { mailSubjectAppendEnvName,
						customer.getCustomerName(),
						opportunity.getOpportunityId(),
						deliveryCentreT.getDeliveryCentre() };

				String deliveryClusterHead = deliveryClusterT
						.getDeliveryClusterHead();
				String deliveryCentreHead = deliveryCentreT
						.getDeliveryCentreHead();
				if (StringUtils.isEmpty(deliveryCentreHead)) {
					// assign cluster head if centre head is not available
					deliveryCentreHead = deliveryClusterHead;
				}

				switch (DeliveryStage.byStageCode(deliveryMaster
						.getDeliveryStage())) {
				case ACCEPTED:
					subject = StringUtils.replaceEach(deliveryAcceptedSubject,
							deliverySubjectSearchList, replacementList);

					if (StringUtils.equals(deliveryCentreHead,
							deliveryClusterHead)) {
						reciepientIds.add(deliveryClusterHead);
					} else {
						reciepientIds.add(deliveryCentreHead);
						ccIds.add(deliveryClusterHead);
					}
					templateLoc = deliveryAcceptedTemplateLoc;

					break;
				case ASSIGNED:
					subject = StringUtils.replaceEach(deliveryAssignedSubject,
							deliverySubjectSearchList, replacementList);

					reciepientIds.addAll(getDeliveryManagers(deliveryMaster
							.getDeliveryMasterManagerLinkTs()));
					ccIds.add(deliveryClusterHead);
					ccIds.add(deliveryCentreHead);
					templateLoc = deliveryAssignedTemplateLoc;
					break;
				case LIVE:
					subject = StringUtils.replaceEach(deliveryLiveSubject,
							deliverySubjectSearchList, replacementList);
					reciepientIds.add(deliveryCentreHead);
					List<String> assignedManagers = getDeliveryManagers(deliveryMaster
							.getDeliveryMasterManagerLinkTs());
					data.put("deliveryManager",deliveryMaster.getModifiedByUser().getUserName());
					ccIds.add(deliveryClusterHead);
					ccIds.addAll(assignedManagers);
					List<String> strategicInitiatives = userRepository
							.findUserIdByUserGroup(UserGroup.STRATEGIC_INITIATIVES
									.getValue());
					ccIds.addAll(strategicInitiatives);
					templateLoc = deliveryLiveTemplateLoc;
					break;
				default:
					break;
				}
			} else {
				throw new DestinationException("Engagement Not Found :"
						+ entityId);
			}
			break;
		// Entity Type -> Opportunity : Intimating cluster head of delivery
		// centres tagged to Opportunity prior win
		case OPPORTUNITY:
			OpportunityT opportunity = opportunityRepository.findOne(entityId);
			if (opportunity != null) {
				// getting data for mail message content
				data.putAll(getDataForOpportunityDelivery(opportunity));
				DeliveryCentreT deliveryCentre = deliveryCentreRepository
						.findOne(deliveryCenterId);
				reciepientIds.add(deliveryCentre.getDeliveryClusterT()
						.getDeliveryClusterHead());
				String[] replacementList = { mailSubjectAppendEnvName,
						opportunity.getCustomerMasterT().getCustomerName(),
						opportunity.getOpportunityId(),
						deliveryCentre.getDeliveryCentre() };
				data.put("deliveryCenter", deliveryCentre.getDeliveryCentre());
				subject = StringUtils.replaceEach(
						deliveryIntimatedPriorWinSubject,
						deliverySubjectSearchList, replacementList);
				templateLoc = deliveryIntimatedPriorWinTemplateLoc;
			} else {
				throw new DestinationException("Opportunity Not Found :"
						+ entityId);
			}
			break;
		// Entity Type -> Delivery Intimated : Intimating cluster head of delivery
		// centres tagged to Opportunity prior win
		case DELIVERY_INTIMATED:
			List<Integer> deliveryCentreIds = Lists.newArrayList();
			OpportunityT opportunityT = opportunityRepository.findOne(entityId);
			CustomerMasterT customerMasterT = opportunityT.getCustomerMasterT();
			List<OpportunityDeliveryCentreMappingT> opportunityDeliveryCentreMappingTs = opportunityDeliveryCentreMappingTRepository
					.findByOpportunityId(entityId);
			for (OpportunityDeliveryCentreMappingT opportunityDeliveryCentreMappingT : opportunityDeliveryCentreMappingTs) {
				deliveryCentreIds.add(opportunityDeliveryCentreMappingT
						.getDeliveryCentreId());
			}
			Map<Integer, List<Integer>> deliveryCentreMap = deliveryMasterService
					.getDeliveryCentreForCluster(deliveryCentreIds);
			String[] replacementList = { mailSubjectAppendEnvName,
					customerMasterT.getCustomerName(),
					entityId};
			subject = StringUtils.replaceEach(deliveryIntimatedSubject,
					deliveryIntimatedSubjectSearchList, replacementList);
			templateLoc = deliveryIntimatedTemplateLoc;
			for (Integer clusterId : deliveryCentreMap.keySet()) {
				Map<String, Object> dataIntimated = Maps.newHashMap();
				List<String> recipientIdsForIntimated = Lists.newArrayList();
				List<String> deliveryCentreNames = getDeliveryCentreNamesFromIds(deliveryCentreMap.get(clusterId));
				dataIntimated.put("deliveryCenter", defaultIfEmpty(StringUtils.join(deliveryCentreNames, ",")));
				dataIntimated.putAll(getDataForOpportunityDelivery(opportunityT));
				recipientIdsForIntimated.add(deliveryClusterRepository.findOne(clusterId).getDeliveryClusterHead());
				DestinationMailMessage messageForIntimated = constructMailMessage(recipientIdsForIntimated, ccIds, null, subject,
						templateLoc, dataIntimated);
				destMailSender.send(messageForIntimated);
			}
			return null;
			
			/*List<String> deliveryCentres = Lists.newArrayList();
			DeliveryIntimatedT deliveryIntimatedT = deliveryIntimatedRepository.findOne(entityId);
			CustomerMasterT customerMasterT = deliveryIntimatedT.getOpportunityT().getCustomerMasterT();
			String[] replacementList = { mailSubjectAppendEnvName,
					customerMasterT.getCustomerName(),
					deliveryIntimatedT.getOpportunityId()};
			deliveryCentres = getDeliveryCentreNames(deliveryIntimatedT.getDeliveryIntimatedCentreLinkTs());
			data.putAll(getDataForDeliveryintimated(deliveryIntimatedT,deliveryCentres));
			data.putAll(getDataForOpportunityDelivery(deliveryIntimatedT.getOpportunityT()));
			subject = StringUtils.replaceEach(deliveryIntimatedSubject,
					deliveryIntimatedSubjectSearchList, replacementList);
			if (deliveryCentres.contains(Constants.OPEN)) {
				subject = StringUtils.replaceEach(deliveryRejectedSubject,
						deliveryIntimatedSubjectSearchList, replacementList);
				List<String> rejectedCentreNames = auditDeliveryIntimatedCentreLinkRepository
						.getRejectedDeliveryCentreNames(
								deliveryIntimatedT.getModifiedDatetime(),
								OPERATION_DELETE);
				data.put("rejectedDeliveryCenter",
						defaultIfEmpty(StringUtils.join(rejectedCentreNames, ",")));
				List<String> pmo = userAccessPrivilegesRepository
						.findUserIdsForCustomerUserGroup(
								customerMasterT.getGeography(), Constants.Y,
								UserGroup.PMO.getValue());
				List<String> geoHeads = userAccessPrivilegesRepository
						.findUserIdsForCustomerUserGroup(
								customerMasterT.getGeography(), Constants.Y,
								UserGroup.GEO_HEADS.getValue());
				reciepientIds.add(deliveryIntimatedT.getOpportunityT().getPrimaryOwnerUser()
						.getUserId());
				ccIds.addAll(userRepository
						.findUserIdByUserGroup(UserGroup.STRATEGIC_INITIATIVES
								.getValue()));
				reciepientIds.addAll(pmo);
				reciepientIds.addAll(geoHeads);
				templateLoc = deliveryRejectedTemplateLoc;
			} else {
				subject = StringUtils.replaceEach(deliveryIntimatedSubject,
						deliveryIntimatedSubjectSearchList, replacementList);
				reciepientIds.add(deliveryIntimatedT
						.getDeliveryIntimatedCentreLinkTs().get(0)
						.getDeliveryCentreT().getDeliveryClusterT()
						.getDeliveryClusterHead());
				templateLoc = deliveryIntimatedTemplateLoc;
			}*/
			
		default:
			break;
		}
		message = constructMailMessage(reciepientIds, ccIds, null, subject,
				templateLoc, data);

		return message;
	}

	private List<String> getDeliveryCentreNamesFromIds(List<Integer> deliveryCentreIds) {
		List<String> deliveryCentreNames = Lists.newArrayList();
			if(CollectionUtils.isNotEmpty(deliveryCentreIds)) {
				deliveryCentreNames = deliveryCentreRepository.findDeliveryCentreNamesByIds(deliveryCentreIds);
			}
		return deliveryCentreNames;
	}

	private Map<String,Object> getDataForDeliveryintimated(
			DeliveryIntimatedT deliveryIntimatedT, List<String> deliveryCentres) {
		logger.debug("Inside getDataForDeliveryintimated method");
		Map<String, Object> data = Maps.newHashMap();
		data.put("deliveryCenter", defaultIfEmpty(StringUtils.join(deliveryCentres, ",")));
		data.put("rejectionReasons", defaultIfEmpty(deliveryIntimatedT.getRejectReason()));
		data.put("comments", defaultIfEmpty(deliveryIntimatedT.getRejectComments()));
		data.put("rejectedDeliveryClusterHeadName",defaultIfEmpty(deliveryIntimatedT.getModifiedByUser().getUserName()));
		logger.debug("End of getDataForDeliveryintimated method");
		return data;
	}

	private List<String> getDeliveryCentreNames(
			List<DeliveryIntimatedCentreLinkT> deliveryIntimatedCentreLinkTs) {
		List<String> deliveryCentreNames = Lists.newArrayList();
		List<Integer> deliveryCentreIds = Lists.newArrayList();
		if(CollectionUtils.isNotEmpty(deliveryIntimatedCentreLinkTs)) {
			for (DeliveryIntimatedCentreLinkT deliveryIntimatedCentreLinkT : deliveryIntimatedCentreLinkTs) {
				deliveryCentreIds.add(deliveryIntimatedCentreLinkT.getDeliveryCentreId());
			}
			if(CollectionUtils.isNotEmpty(deliveryCentreIds)) {
				deliveryCentreNames = deliveryCentreRepository.findDeliveryCentreNamesByIds(deliveryCentreIds);
			}
		}
		return deliveryCentreNames;
	}

	/**
	 * gets the data map for a particular engagement
	 * @param deliveryMaster
	 * @return
	 */
	private Map<String, Object> getDataForDelivery(
			DeliveryMasterT deliveryMaster) {
		logger.debug("Inside getDataForDelivery method");
		Map<String, Object> data = Maps.newHashMap();
		data.put("engagementId", defaultIfEmpty(deliveryMaster.getDeliveryMasterId()));
		data.put("actualStartDate", defaultIfEmpty(DateUtils.format(deliveryMaster.getActualStartDate(), DateUtils.ACTUAL_FORMAT)));
		data.put("deliveryCenter", defaultIfEmpty(deliveryMaster.getDeliveryCentreT().getDeliveryCentre()));
		data.put("rejectionReasons", defaultIfEmpty(deliveryMaster.getReason()));
		data.put("comments", defaultIfEmpty(deliveryMaster.getComments()));
		data.put("won", defaultIfEmpty(deliveryMaster.getWonNum()));
		data.put("rejectedDeliveryClusterHeadName",defaultIfEmpty(deliveryMaster.getModifiedByUser().getUserName()));
		logger.debug("End of getDataForDelivery method");
		return data;
	}

	/**
	 * Gets the data map of Opportunity tagged for delivery
	 * @param opportunity
	 * @return
	 */
	private Map<String, Object> getDataForOpportunityDelivery(
			OpportunityT opportunity) {
		logger.debug("Inside getDataForOpportunityDelivery method");
		Map<String, Object> data = Maps.newHashMap();
		data .put("opportunityId", defaultIfEmpty(opportunity.getOpportunityId()));
		data.put("customerName", defaultIfEmpty(opportunity.getCustomerMasterT()
		.getCustomerName()));
		data.put("opportunityName", defaultIfEmpty(opportunity.getOpportunityName()));
		data.put("engagementStartDate",	defaultIfEmpty(DateUtils.format(opportunity.getEngagementStartDate(), DateUtils.ACTUAL_FORMAT)));
		BigDecimal engagementDuration = opportunity.getEngagementDuration();
		if(engagementDuration!=null) {
			data.put("engagementDuration", defaultIfEmpty(engagementDuration.intValue()));
		}
		data.put("deliveryOwnership", defaultIfEmpty(opportunity.getDeliveryOwnershipT().getOwnership()));
		data.put("crmId", defaultIfEmpty(opportunity.getCrmId()));
		// Getting bid details
		BidDetailsT bidDetailsT = bidDetailsTRepository
				.findFirstByOpportunityIdOrderByModifiedDatetimeDesc(opportunity
						.getOpportunityId());
		if(bidDetailsT !=null) {
			data.put("winProbability", defaultIfEmpty(bidDetailsT.getWinProbability()));
		}
		
		data.put("opportunityOwner", defaultIfEmpty(opportunity.getPrimaryOwnerUser().getUserName()));
		data.put("salesStage", defaultIfEmpty(SalesStageCode.valueOf(opportunity.getSalesStageCode()).getDescription()));
		logger.debug("End of getDataForOpportunityDelivery method");
		return data;
	}

	/**
	 * Assigns default value as 'Not Available' if the given object is empty
	 * @param value
	 * @return
	 */
	private String defaultIfEmpty(Object value) {
		 return String.valueOf(ObjectUtils.defaultIfNull(value, Constants.NOT_AVAILABLE));
	}

	/**
	 * Method used to construct Mail details such as recipients,subject,mail content
	 * @param recipientIdList
	 * @param ccList
	 * @param bccList
	 * @param subject
	 * @param mailTemplateLoc
	 * @param data
	 * @return
	 */
	private DestinationMailMessage constructMailMessage(
			List<String> recipientIdList, List<String> ccList,
			List<String> bccList, String subject, String mailTemplateLoc,
			Map<String, Object> data) {
		logger.debug("Inside constructMailMessage method");
		DestinationMailMessage message = new DestinationMailMessage();
		message.setRecipients(listMailIdsFromUserIds(recipientIdList));
		message.setCcList(listMailIdsFromUserIds(ccList));
		message.setBccList(listMailIdsFromUserIds(bccList));
		message.setSubject(subject);
		String msg = mergeTmplWithData(data, mailTemplateLoc);
		message.setMessage(msg);
		logger.info("Subject : " + subject);
		logger.info("Message : " + msg);
		logger.debug("End of constructMailMessage method");
		return message;
	}
	
	/**
	 * Method used to get the list of delivery managers id's
	 * @param deliveryManagers
	 * @return
	 */
	private List<String> getDeliveryManagers(
			List<DeliveryMasterManagerLinkT> deliveryManagers) {
		logger.debug("Inside getDeliveryManagers method");
		List<String> assignedManagers = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(deliveryManagers)) {
			for (DeliveryMasterManagerLinkT deliveryMasterManagerLinkT : deliveryManagers) {
				assignedManagers.add(deliveryMasterManagerLinkT
						.getDeliveryManagerId());
			}
		}
		logger.debug("End of getDeliveryManagers method");
		return assignedManagers;
	}
}


