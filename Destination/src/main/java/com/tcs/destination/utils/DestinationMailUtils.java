package com.tcs.destination.utils;

import static com.tcs.destination.utils.Constants.ACTUAL_REVENUE_DOWNLOAD_SUBJECT;
import static com.tcs.destination.utils.Constants.ACTUAL_REVENUE_UPLOAD_NOTIFY_SUBJECT;
import static com.tcs.destination.utils.Constants.ACTUAL_REVENUE_UPLOAD_SUBJECT;
import static com.tcs.destination.utils.Constants.BEACON_DOWNLOAD_SUBJECT;
import static com.tcs.destination.utils.Constants.BEACON_UPLOAD_NOTIFY_SUBJECT;
import static com.tcs.destination.utils.Constants.BEACON_UPLOAD_SUBJECT;
import static com.tcs.destination.utils.Constants.CONNECT_DOWNLOAD_SUBJECT;
import static com.tcs.destination.utils.Constants.CONNECT_UPLOAD_NOTIFY_SUBJECT;
import static com.tcs.destination.utils.Constants.CONNECT_UPLOAD_SUBJECT;
import static com.tcs.destination.utils.Constants.CUSTOMER_CONTACT_DOWNLOAD_SUBJECT;
import static com.tcs.destination.utils.Constants.CUSTOMER_CONTACT_UPLOAD_NOTIFY_SUBJECT;
import static com.tcs.destination.utils.Constants.CUSTOMER_CONTACT_UPLOAD_SUBJECT;
import static com.tcs.destination.utils.Constants.CUSTOMER_DOWNLOAD_SUBJECT;
import static com.tcs.destination.utils.Constants.CUSTOMER_UPLOAD_NOTIFY_SUBJECT;
import static com.tcs.destination.utils.Constants.CUSTOMER_UPLOAD_SUBJECT;
import static com.tcs.destination.utils.Constants.OPPORTUNITY_DAILY_DOWNLOAD_SUBJECT;
import static com.tcs.destination.utils.Constants.OPPORTUNITY_DOWNLOAD_SUBJECT;
import static com.tcs.destination.utils.Constants.OPPORTUNITY_UPLOAD_NOTIFY_SUBJECT;
import static com.tcs.destination.utils.Constants.OPPORTUNITY_UPLOAD_SUBJECT;
import static com.tcs.destination.utils.Constants.PARTNER_CONTACT_DOWNLOAD_SUBJECT;
import static com.tcs.destination.utils.Constants.PARTNER_CONTACT_UPLOAD_NOTIFY_SUBJECT;
import static com.tcs.destination.utils.Constants.PARTNER_CONTACT_UPLOAD_SUBJECT;
import static com.tcs.destination.utils.Constants.PARTNER_DOWNLOAD_SUBJECT;
import static com.tcs.destination.utils.Constants.PARTNER_UPLOAD_NOTIFY_SUBJECT;
import static com.tcs.destination.utils.Constants.PARTNER_UPLOAD_SUBJECT;
import static com.tcs.destination.utils.Constants.USER_DOWNLOAD_SUBJECT;
import static com.tcs.destination.utils.Constants.USER_UPLOAD_NOTIFY_SUBJECT;
import static com.tcs.destination.utils.Constants.USER_UPLOAD_SUBJECT;

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
import javax.mail.internet.MimeMessage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.DestinationMailMessage;
import com.tcs.destination.bean.OpportunityPartnerLinkT;
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
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.enums.EntityTypeId;
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

	@Value("${workflowApproveOrRejectTemplateLoc}")
	private String workflowApproveOrRejectTemplateLoc;

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
		message.setMessageType(Constants.MIME);

		List<String> recipientIds = new ArrayList<String>();
		String userId = user.getUserId();
		recipientIds.add(userId);
		message.setRecipients(recipientIds);

		List<String> ccIds = new ArrayList<String>();
		message.setCcList(ccIds);

		List<String> bccIds = new ArrayList<String>();
		message.setBccList(bccIds);

		DateFormat df = new SimpleDateFormat(dateFormatStr);
		String dateStr = df.format(requestedDateTime);
		String sub = new StringBuffer(environmentName).append(" ")
				.append(subject).toString();
		message.setSubject(sub);
		logger.info("Subject : " + sub);
		sendPasswordMail(message, user, dateStr);
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

		boolean status = false;
		List<String> recipientIdList = new ArrayList<String>();
		String[] recipientMailIdsArray = null;
		String dateStr = null;
		UserT user = request.getUserT();
		DateFormat df = new SimpleDateFormat(dateFormatStr);

		if (user != null) {
			recipientIdList.add(user.getUserId());
			dateStr = df.format(request.getSubmittedDatetime());
			recipientMailIdsArray = getMailIdsFromUserIds(recipientIdList);
		}
		if (CollectionUtils.isNotEmpty(roles)) {
			recipientMailIdsArray = getMailIdsFromRoles(roles);
			dateStr = df.format(DateUtils.getCurrentTimeStamp());
		}

		MimeMessage automatedMIMEMessage = ((JavaMailSenderImpl) mailSender)
				.createMimeMessage();

		try {
			MimeMessageHelper helper = new MimeMessageHelper(
					automatedMIMEMessage, true);
			helper.setTo(recipientMailIdsArray);

			helper.setFrom(senderEmailId);

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

			switch (requestType) {

			case 1: {
				// User upload
				subject.append(USER_UPLOAD_SUBJECT);
				userName = user.getUserName();
				entity = WordUtils.capitalize(EntityType.USER.name()
						.toLowerCase());
			}
				break;

			case 2: {
				// Customer upload
				subject.append(CUSTOMER_UPLOAD_SUBJECT);
				userName = user.getUserName();
				entity = WordUtils.capitalize(EntityType.CUSTOMER.name()
						.toLowerCase());
			}
				break;

			case 3: {
				// Connect upload
				subject.append(CONNECT_UPLOAD_SUBJECT);
				userName = user.getUserName();
				entity = WordUtils.capitalize(EntityType.CONNECT.name()
						.toLowerCase());
			}
				break;

			case 4: {
				// Opportunity upload
				subject.append(OPPORTUNITY_UPLOAD_SUBJECT);
				userName = user.getUserName();
				entity = WordUtils.capitalize(EntityType.OPPORTUNITY.name()
						.toLowerCase());
			}
				break;

			case 5: {
				// Actual revenue upload
				subject.append(ACTUAL_REVENUE_UPLOAD_SUBJECT);
				userName = user.getUserName();
				entity = WordUtils.capitalize(EntityType.ACTUAL_REVENUE.name()
						.toLowerCase());
			}
				break;

			case 6: {
				// Customer contact upload
				subject.append(CUSTOMER_CONTACT_UPLOAD_SUBJECT);
				userName = user.getUserName();
				entity = WordUtils.capitalize(EntityType.CUSTOMER_CONTACT
						.name().toLowerCase());
			}
				break;

			case 7: {
				// Partner upload
				subject.append(PARTNER_UPLOAD_SUBJECT);
				userName = user.getUserName();
				entity = WordUtils.capitalize(EntityType.PARTNER.name()
						.toLowerCase());
			}
				break;

			case 8: {
				// Partner contact upload
				subject.append(PARTNER_CONTACT_UPLOAD_SUBJECT);
				userName = user.getUserName();
				entity = WordUtils.capitalize(EntityType.PARTNER_CONTACT.name()
						.toLowerCase());
			}
				break;
			case 9: {
				// Beacon upload
				subject.append(BEACON_UPLOAD_SUBJECT);
				userName = user.getUserName();
				entity = WordUtils.capitalize(EntityType.BEACON.name()
						.toLowerCase());
			}
				break;

			case 10: {
				// User download
				subject.append(USER_DOWNLOAD_SUBJECT);
				userName = user.getUserName();
				entity = WordUtils.capitalize(EntityType.USER.name()
						.toLowerCase());
			}
				break;

			case 11: {
				// Customer download
				subject.append(CUSTOMER_DOWNLOAD_SUBJECT);
				userName = user.getUserName();
				entity = WordUtils.capitalize(EntityType.CUSTOMER.name()
						.toLowerCase());
			}
				break;

			case 12: {
				// Connect download
				subject.append(CONNECT_DOWNLOAD_SUBJECT);
				userName = user.getUserName();
				entity = WordUtils.capitalize(EntityType.CONNECT.name()
						.toLowerCase());
			}
				break;

			case 13: {
				// Opportunity download
				subject.append(OPPORTUNITY_DOWNLOAD_SUBJECT);
				userName = user.getUserName();
				entity = WordUtils.capitalize(EntityType.OPPORTUNITY.name()
						.toLowerCase());
			}
				break;

			case 14: {
				// Actual revenue download
				subject.append(ACTUAL_REVENUE_DOWNLOAD_SUBJECT);
				userName = user.getUserName();
				entity = WordUtils.capitalize(EntityType.ACTUAL_REVENUE.name()
						.toLowerCase());
			}
				break;

			case 15: {
				// Customer contact download
				subject.append(CUSTOMER_CONTACT_DOWNLOAD_SUBJECT);
				userName = user.getUserName();
				entity = WordUtils.capitalize(EntityType.CUSTOMER_CONTACT
						.name().toLowerCase());
			}
				break;

			case 16: {
				// Partner download
				subject.append(PARTNER_DOWNLOAD_SUBJECT);
				userName = user.getUserName();
				entity = WordUtils.capitalize(EntityType.PARTNER.name()
						.toLowerCase());
			}
				break;

			case 17: {
				// Partner contact download
				subject.append(PARTNER_CONTACT_DOWNLOAD_SUBJECT);
				userName = user.getUserName();
				entity = WordUtils.capitalize(EntityType.PARTNER_CONTACT.name()
						.toLowerCase());
			}
				break;

			case 18: {
				// Beacon download
				subject.append(BEACON_DOWNLOAD_SUBJECT);
				userName = user.getUserName();
				entity = WordUtils.capitalize(EntityType.BEACON.name()
						.toLowerCase());
			}
				break;

			case 19: {
				// Opportunity download
				subject.append(OPPORTUNITY_DAILY_DOWNLOAD_SUBJECT);
				userName = "System Admin/Strategic Group Admin";
				entity = WordUtils.capitalize(EntityType.OPPORTUNITY.name()
						.toLowerCase());
			}
				break;

			}

			if (requestType > 0 && requestType < 10) {
				template = uploadTemplateLoc;
				requestId = request.getProcessRequestId().toString();
				uploadedFileName = request.getFileName();
				attachmentFilePath = request.getErrorFilePath()
						+ request.getErrorFileName();
				attachmentFileName = request.getErrorFileName();
			} else if (requestType > 9 && requestType < 19) {
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

			String text = VelocityEngineUtils.mergeTemplateIntoString(
					velocityEngine, template, Constants.UTF8, userRequestMap);

			helper.setSubject(subject.toString());
			helper.setText(text, true);
			helper.addAttachment(attachmentFileName, new FileSystemResource(
					attachmentFilePath));
			logMailDetails(recipientMailIdsArray, null, null,
					subject.toString(), text);
			mailSender.send(automatedMIMEMessage);
			status = true;
		} catch (MailSendException e) {
			logger.error("Error sending mail message", e.getMessage());
			status = false;
			throw e;
		} catch (MailParseException e) {
			logger.error("Error parsing mail message", e.getMessage());
			status = false;
			throw e;
		} catch (MailAuthenticationException e) {
			logger.error("Error authnticatingh e-mail message", e.getMessage());
			status = false;
			throw e;
		} catch (MailPreparationException e) {
			logger.error("Error preparing mail message", e.getMessage());
			status = false;
			throw e;
		} catch (Exception e) {
			logger.error("Error sending mail message", e.getMessage());
			status = false;
			throw e;
		}

		return status;
	}

	/**
	 * @param roles
	 * @return emails Id's
	 */
	private String[] getMailIdsFromRoles(List<UserRole> roles) {

		logger.debug("Inside method: getMailIdsFromRoles");

		List<String> recipientMailIds = new ArrayList<String>();
		List<String> values = new ArrayList<String>(roles.size());
		for (UserRole role : roles) {
			values.add(role.getValue());

		}

		List<UserT> users = userService.getByUserRoles(values);

		for (UserT user : users) {
			String mailId = user.getUserEmailId();
			recipientMailIds.add(mailId);
		}

		String[] recipientMailIdsArray = recipientMailIds
				.toArray(new String[recipientMailIds.size()]);

		return recipientMailIdsArray;
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

		boolean status = false;
		UserT user = request.getUserT();
		DateFormat df = new SimpleDateFormat(dateFormatStr);
		String dateStr = df.format(request.getSubmittedDatetime());
		String[] recipientMailIdsArray = getGroupdMailIdsFromUserIds(user
				.getUserRole());

		MimeMessage automatedMIMEMessage = ((JavaMailSenderImpl) mailSender)
				.createMimeMessage();

		try {
			MimeMessageHelper helper = new MimeMessageHelper(
					automatedMIMEMessage, true);
			helper.setTo(recipientMailIdsArray);
			helper.setFrom(senderEmailId);

			String template = uploadNotifyTemplateLoc;
			StringBuffer subject = new StringBuffer(environmentName)
					.append(" Admin: ");

			String userName = user.getUserName();
			;
			String entity = null;
			String fileName = null;

			switch (request.getRequestType()) {

			case 1: {
				// User upload
				subject.append(USER_UPLOAD_NOTIFY_SUBJECT);
				entity = WordUtils.capitalize(EntityType.USER.name()
						.toLowerCase());
				fileName = request.getFileName();
			}
				break;

			case 2: {
				// Customer upload
				subject.append(CUSTOMER_UPLOAD_NOTIFY_SUBJECT);
				entity = WordUtils.capitalize(EntityType.CUSTOMER.name()
						.toLowerCase());
				fileName = request.getFileName();
			}
				break;

			case 3: {
				// Connect upload
				subject.append(CONNECT_UPLOAD_NOTIFY_SUBJECT);
				entity = WordUtils.capitalize(EntityType.CONNECT.name()
						.toLowerCase());
				fileName = request.getFileName();
			}
				break;

			case 4: {
				// Opportunity upload
				subject.append(OPPORTUNITY_UPLOAD_NOTIFY_SUBJECT);
				entity = WordUtils.capitalize(EntityType.OPPORTUNITY.name()
						.toLowerCase());
				fileName = request.getFileName();
			}
				break;

			case 5: {
				// Actual revenue upload
				subject.append(ACTUAL_REVENUE_UPLOAD_NOTIFY_SUBJECT);
				entity = WordUtils.capitalize(EntityType.ACTUAL_REVENUE.name()
						.toLowerCase());
				fileName = request.getFileName();
			}
				break;

			case 6: {
				// Customer contact upload
				subject.append(CUSTOMER_CONTACT_UPLOAD_NOTIFY_SUBJECT);
				entity = WordUtils.capitalize(EntityType.CUSTOMER_CONTACT
						.name().toLowerCase());
				fileName = request.getFileName();
			}
				break;

			case 7: {
				// Partner upload
				subject.append(PARTNER_UPLOAD_NOTIFY_SUBJECT);
				entity = WordUtils.capitalize(EntityType.PARTNER.name()
						.toLowerCase());
				fileName = request.getFileName();
			}
				break;

			case 8: {
				// Partner contact upload
				subject.append(PARTNER_CONTACT_UPLOAD_NOTIFY_SUBJECT);
				entity = WordUtils.capitalize(EntityType.PARTNER_CONTACT.name()
						.toLowerCase());
				fileName = request.getFileName();
			}
				break;
			case 9: {
				// Beacon upload
				subject.append(BEACON_UPLOAD_NOTIFY_SUBJECT);
				entity = WordUtils.capitalize(EntityType.BEACON.name()
						.toLowerCase());
				fileName = request.getFileName();
			}
				break;

			}

			Map<String, Object> userRequestMap = new HashMap<String, Object>();
			userRequestMap.put("userName", userName);
			userRequestMap.put("entity", entity);
			userRequestMap.put("fileName", fileName);
			userRequestMap.put("submittedDate", dateStr);
			userRequestMap.put("requestId", request.getProcessRequestId()
					.toString());

			String text = VelocityEngineUtils.mergeTemplateIntoString(
					velocityEngine, template, Constants.UTF8, userRequestMap);

			helper.setSubject(subject.toString());
			helper.setText(text, true);
			logMailDetails(recipientMailIdsArray, null, null,
					subject.toString(), text);
			mailSender.send(automatedMIMEMessage);
			status = true;
		} catch (Exception e) {
			logger.error("Error sending mail message", e.getMessage());
			status = false;
			throw e;
		}

		return status;
	}

	/**
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
		message.setMessageType(Constants.MIME);

		UserAccessRequestT userAccessRequest = userAccessRepo.findOne(reqId);

		List<String> recipientIds = userService
				.findByUserRole(Constants.SYSTEM_ADMIN);
		message.setRecipients(recipientIds);

		List<String> ccIds = new ArrayList<String>();
		String supervisorId = userAccessRequest.getSupervisorId();
		ccIds.add(supervisorId);
		message.setCcList(ccIds);

		List<String> bccIds = new ArrayList<String>();
		message.setBccList(bccIds);

		String sub = new StringBuffer(environmentName).append(" ")
				.append(subject).toString();
		message.setSubject(sub);
		logger.info("Subject : " + sub);

		DateFormat df = new SimpleDateFormat(dateFormatStr);
		String requestedDateStr = df.format(requestedDateTime);

		sendUserAccessMail(message, userAccessRequest, requestedDateStr);
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
		message.setMessageType(Constants.MIME);

		OpportunityReopenRequestT oppReopenRequest = oppReopenRepo
				.findOne(reqId);
		UserT user = userService
				.findByUserId(oppReopenRequest.getRequestedBy());
		UserT supervisor = userService.findByUserId(user.getSupervisorUserId());
		OpportunityT opp = oppService.findOpportunityById(oppReopenRequest
				.getOpportunityId());

		List<String> recipientIds = userService
				.findByUserRole(Constants.SYSTEM_ADMIN);
		message.setRecipients(recipientIds);

		List<String> ccIds = new ArrayList<String>();
		ccIds.add(user.getUserId());
		ccIds.add(supervisor.getUserId());
		message.setCcList(ccIds);

		List<String> bccIds = new ArrayList<String>();
		message.setBccList(bccIds);

		DateFormat df = new SimpleDateFormat(dateFormatStr);
		String dateStr = df.format(requestedDateTime);
		String sub = new StringBuffer(environmentName).append(" ")
				.append(subject).toString();
		message.setSubject(sub);
		logger.info("Subject : " + sub);
		sendOpportunityReopenMail(message, oppReopenRequest, user, opp, dateStr);
	}

	/**
	 * @param message
	 * @param user
	 * @param dateStr
	 * @throws Exception
	 */
	private void sendPasswordMail(DestinationMailMessage message, UserT user,
			String dateStr) throws Exception {
		logger.debug("Inside sendPasswordMail method");
		List<String> recipientIdList = message.getRecipients();
		String[] recipientMailIdsArray = getMailIdsFromUserIds(recipientIdList);
		String[] ccMailIdsArray = getMailAddressArr(message.getCcList());
		String[] bccMailIdsArray = getMailAddressArr(message.getBccList());

		if (message.getMessageType().equals(Constants.MIME)) {
			MimeMessage automatedMIMEMessage = ((JavaMailSenderImpl) mailSender)
					.createMimeMessage();
			try {
				MimeMessageHelper helper = new MimeMessageHelper(
						automatedMIMEMessage, true);
				helper.setTo(recipientMailIdsArray);
				helper.setCc(ccMailIdsArray);
				helper.setBcc(bccMailIdsArray);
				String subject = message.getSubject();
				helper.setSubject(subject);
				logger.info("Forgot Password - Sender : " + senderEmailId);
				logger.info("Forgot Password - date : " + dateStr);
				helper.setFrom(senderEmailId);
				Map forgotPasswordTemplateDataModel = new HashMap();
				forgotPasswordTemplateDataModel.put("user", user);
				forgotPasswordTemplateDataModel.put("date", dateStr);
				String text = VelocityEngineUtils.mergeTemplateIntoString(
						velocityEngine, forgotPasswordTemplateLoc,
						Constants.UTF8, forgotPasswordTemplateDataModel);
				helper.setText(text, true);
				logMailDetails(recipientMailIdsArray, ccMailIdsArray,
						bccMailIdsArray, subject, text);
				mailSender.send(automatedMIMEMessage);
				logger.info("Forgot Password : Mail sent");
			} catch (Exception e) {
				logger.error("Error sending mail message", e.getMessage());
				throw e;
			}
		}
	}

	/**
	 * @param message
	 * @param userAccessRequest
	 * @param dateStr
	 * @throws Exception
	 */
	private void sendUserAccessMail(DestinationMailMessage message,
			UserAccessRequestT userAccessRequest, String dateStr)
			throws Exception {
		logger.debug("Inside sendUserAccessMail method");
		List<String> recipientIdList = message.getRecipients();
		String[] recipientMailIdsArray = getMailIdsFromUserIds(recipientIdList);
		String[] ccMailIdsArray = getMailAddressArr(message.getCcList());
		int size = ccMailIdsArray.length;
		ccMailIdsArray = Arrays.copyOf(ccMailIdsArray, size + 1);
		ccMailIdsArray[1] = ccMailIdsArray[0];
		ccMailIdsArray[0] = userAccessRequest.getUserEmailId();
		String[] bccMailIdsArray = getMailAddressArr(message.getBccList());

		if (message.getMessageType().equals(Constants.MIME)) {
			MimeMessage automatedMIMEMessage = ((JavaMailSenderImpl) mailSender)
					.createMimeMessage();
			try {
				MimeMessageHelper helper = new MimeMessageHelper(
						automatedMIMEMessage, true);
				helper.setTo(recipientMailIdsArray);
				helper.setCc(ccMailIdsArray);
				helper.setBcc(bccMailIdsArray);
				String subject = message.getSubject();
				helper.setSubject(subject);
				helper.setFrom(senderEmailId);
				logger.info("User Access - Sender : " + senderEmailId);
				logger.info("User Access - date : " + dateStr);
				Map userAccessTemplateDataModel = new HashMap();
				userAccessTemplateDataModel.put("request", userAccessRequest);
				userAccessTemplateDataModel.put("date", dateStr);
				String text = VelocityEngineUtils.mergeTemplateIntoString(
						velocityEngine, userAccessTemplateLoc, Constants.UTF8,
						userAccessTemplateDataModel);
				helper.setText(text, true);
				logMailDetails(recipientMailIdsArray, ccMailIdsArray,
						bccMailIdsArray, subject, text);
				mailSender.send(automatedMIMEMessage);
				logger.info("User Access : Mail sent");
			} catch (Exception e) {
				logger.error("Error sending mail message", e.getMessage());
				throw e;
			}
		}

	}

	/**
	 * This method initializes the actual mime message that will be sent
	 * 
	 * @param message
	 *            - object containing recipients, message type,
	 * @param oppReopenRequest
	 * @param user
	 * @param opp
	 * @param dateStr
	 * @throws Exception
	 */
	private void sendOpportunityReopenMail(DestinationMailMessage message,
			OpportunityReopenRequestT oppReopenRequest, UserT user,
			OpportunityT opp, String dateStr) throws Exception {
		logger.debug("Inside sendOpportunityReopenMail method");
		List<String> recipientIdList = message.getRecipients();
		String[] recipientMailIdsArray = getMailIdsFromUserIds(recipientIdList);
		String[] ccMailIdsArray = getMailAddressArr(message.getCcList());
		String[] bccMailIdsArray = getMailAddressArr(message.getBccList());

		if (message.getMessageType().equals(Constants.MIME)) {
			MimeMessage automatedMIMEMessage = ((JavaMailSenderImpl) mailSender)
					.createMimeMessage();
			try {
				MimeMessageHelper helper = new MimeMessageHelper(
						automatedMIMEMessage, true);
				helper.setTo(recipientMailIdsArray);
				helper.setCc(ccMailIdsArray);
				helper.setBcc(bccMailIdsArray);
				String subject = message.getSubject();
				helper.setSubject(subject);
				helper.setFrom(senderEmailId);
				logger.info("Opportuity Reopen - Sender : " + senderEmailId);
				logger.info("Opportuity Reopen - date : " + dateStr);
				Map reopenOppTemplateDataModel = new HashMap();
				reopenOppTemplateDataModel.put("request", oppReopenRequest);
				reopenOppTemplateDataModel.put("user", user);
				reopenOppTemplateDataModel.put("opportunity", opp);
				reopenOppTemplateDataModel.put("date", dateStr);
				String text = VelocityEngineUtils.mergeTemplateIntoString(
						velocityEngine, reopenOpportunityTemplateLoc,
						Constants.UTF8, reopenOppTemplateDataModel);
				helper.setText(text, true);
				logMailDetails(recipientMailIdsArray, ccMailIdsArray,
						bccMailIdsArray, subject, text);
				mailSender.send(automatedMIMEMessage);
				logger.info("Opportunity Reopen : Mail sent");
			} catch (Exception e) {
				logger.error("Error sending mail message", e.getMessage());
				throw e;

			}
		}

	}

	/**
	 * @param idList
	 * @return
	 * @throws Exception
	 */
	private String[] getMailAddressArr(List<String> idList) throws Exception {
		String[] mailIdsArray = new String[0];
		if (idList != null)
			mailIdsArray = getMailIdsFromUserIds(idList);
		return mailIdsArray;
	}
	
	private String[] getSetMailAddressArr(Set<String> idList) throws Exception {
		String[] mailIdsArray = new String[0];
		if (idList != null)
			mailIdsArray = getSetMailIdsFromUserIds(idList);
		return mailIdsArray;
	}

	/**
	 * @param recipientIdList
	 * @return
	 * @throws Exception
	 */
	private String[] getMailIdsFromUserIds(List<String> recipientIdList)
			throws Exception {
		List<String> recipientMailIds = new ArrayList<String>();

		for (String recipientId : recipientIdList) {
//			UserT recipient = userService.findByUserId(recipientId);
			UserT recipient = userRepository.findOne(recipientId);
			String mailId = recipient.getUserEmailId();
			recipientMailIds.add(mailId);
		}
		String[] recipientMailIdsArray = recipientMailIds
				.toArray(new String[recipientMailIds.size()]);
		return recipientMailIdsArray;
	}
	
	private String[] getSetMailIdsFromUserIds(Set<String> recipientIdList)
			throws Exception {
		List<String> recipientMailIds = new ArrayList<String>();

		for (String recipientId : recipientIdList) {
			UserT recipient = userService.findByUserId(recipientId);
			String mailId = recipient.getUserEmailId();
			recipientMailIds.add(mailId);
		}
		String[] recipientMailIdsArray = recipientMailIds
				.toArray(new String[recipientMailIds.size()]);
		return recipientMailIdsArray;
	}

	/**
	 * @param userRole
	 * @return
	 * @throws Exception
	 */
	private String[] getGroupdMailIdsFromUserIds(String userRole)
			throws Exception {
		List<String> recipientMailIds = new ArrayList<String>();

		List<UserT> userList = userService.getUsersByRole(userRole);
		for (UserT user : userList) {
			recipientMailIds.add(user.getUserEmailId());
		}
		String[] recipientMailIdsArray = recipientMailIds
				.toArray(new String[recipientMailIds.size()]);
		return recipientMailIdsArray;
	}

	/**
	 * @param recipientMailIdsArray
	 * @param ccMailIdsArray
	 * @param bccMailIdsArray
	 * @param subject
	 * @param content
	 */
	private void logMailDetails(String[] recipientMailIdsArray,
			String[] ccMailIdsArray, String[] bccMailIdsArray, String subject,
			String content) {
		logger.info("Sender : " + senderEmailId);
		logMailIds("To ", recipientMailIdsArray);
		logMailIds("CC ", ccMailIdsArray);
		logMailIds("BCC ", bccMailIdsArray);
		logger.info("Subject " + subject);
	}

	/**
	 * @param recipientType
	 * @param mailIdsArray
	 */
	private void logMailIds(String recipientType, String[] mailIdsArray) {
		logger.info(recipientType + "Mail Ids : ");
		if (mailIdsArray != null) {
			for (String id : mailIdsArray) {
				logger.info(id);
			}
		}
	}

	public void sendOpportunityReopenProcessedAutomatedEmail(
			String reopenOpportunityProcessedSubject, String requestId,
			Date date) throws Exception {
		logger.debug("inside sendOpportunityReopenProcessedAutomatedEmail method");
		DestinationMailMessage message = new DestinationMailMessage();
		message.setMessageType(Constants.MIME);

		OpportunityReopenRequestT oppReopenRequest = oppReopenRepo
				.findOne(requestId);
		UserT user;
		try {
			user = userService.findByUserId(oppReopenRequest.getRequestedBy());
		} catch (Exception e) {
			logger.error(
					"Error occured while retrieving reopen request:{}"
							+ e.getMessage(), oppReopenRequest.getRequestedBy());
			throw e;
		}
		OpportunityT opp = oppService.findOpportunityById(oppReopenRequest
				.getOpportunityId());
		CustomerMasterT customer = opp.getCustomerMasterT();

		String recepientId = user.getUserId();
		List<String> recepientIds = new ArrayList<String>();
		recepientIds.add(recepientId);
		message.setRecipients(recepientIds);

		List<String> ccIds = new ArrayList<String>();
		List<String> salesSupportOwners = new ArrayList<String>();
		String primaryOwner = opp.getOpportunityOwner();
		List<OpportunitySalesSupportLinkT> opportunitySalesSupportOwners = new ArrayList<OpportunitySalesSupportLinkT>();
		opportunitySalesSupportOwners = opportunitySalesSupportLinkTRepository
				.findByOpportunityId(opp.getOpportunityId());
		if (opportunitySalesSupportOwners != null
				&& !opportunitySalesSupportOwners.isEmpty()) {
			for (OpportunitySalesSupportLinkT osslt : opportunitySalesSupportOwners) {
				salesSupportOwners.add(osslt.getSalesSupportOwner());
			}

			ccIds.addAll(salesSupportOwners);
		}
		ccIds.add(primaryOwner);
		for (String ccId : ccIds) {
			if (ccId.equalsIgnoreCase(recepientId)) {
				ccIds.remove(ccId);
				break;
			}
		}
		message.setCcList(ccIds);
		List<String> bccIds = new ArrayList<String>();
		message.setBccList(bccIds);
		DateFormat df = new SimpleDateFormat(dateFormatStr);
		String dateStr = df.format(date);
		String sub = new StringBuffer(environmentName).append(" ")
				.append(reopenOpportunityProcessedSubject).toString();
		message.setSubject(sub);
		logger.info("Subject : " + sub);
		sendOpportunityReopenProcessedMail(message, oppReopenRequest, user,
				opp, dateStr, customer);
	}

	private void sendOpportunityReopenProcessedMail(
			DestinationMailMessage message,
			OpportunityReopenRequestT oppReopenRequest, UserT user,
			OpportunityT opp, String dateStr, CustomerMasterT customer)
			throws Exception {
		logger.debug("Inside sendOpportunityReopenProcessedMail method");
		List<String> recipientIdList = message.getRecipients();
		String[] recipientMailIdsArray = getMailIdsFromUserIds(recipientIdList);
		String[] ccMailIdsArray = getMailAddressArr(message.getCcList());
		String[] bccMailIdsArray = getMailAddressArr(message.getBccList());
		String userName = user.getUserName();
		String opportunityName = opp.getOpportunityName();
		String customerName = customer.getCustomerName();

		if (message.getMessageType().equals(Constants.MIME)) {
			MimeMessage automatedMIMEMessage = ((JavaMailSenderImpl) mailSender)
					.createMimeMessage();
			try {
				MimeMessageHelper helper = new MimeMessageHelper(
						automatedMIMEMessage, true);
				helper.setTo(recipientMailIdsArray);
				helper.setCc(ccMailIdsArray);
				helper.setBcc(bccMailIdsArray);
				String subject = message.getSubject();
				helper.setSubject(subject);
				helper.setFrom(senderEmailId);
				logger.info("Opportuity Reopen - Sender : " + senderEmailId);
				logger.info("Opportuity Reopen - date : " + dateStr);
				Map<String, Object> oppReopenRequestProcessedMap = new HashMap<String, Object>();
				oppReopenRequestProcessedMap.put("userName", userName);
				oppReopenRequestProcessedMap.put("opportunityName",
						opportunityName);
				oppReopenRequestProcessedMap.put("customerName", customerName);
				oppReopenRequestProcessedMap.put("submittedDate", dateStr);
				String text = VelocityEngineUtils.mergeTemplateIntoString(
						velocityEngine, reopenOpportunityProcessedTemplateLoc,
						Constants.UTF8, oppReopenRequestProcessedMap);
				logger.info("Mail text framed :");
				helper.setText(text, true);
				logMailDetails(recipientMailIdsArray, ccMailIdsArray,
						bccMailIdsArray, subject, text);
				mailSender.send(automatedMIMEMessage);
				logger.info("Opportunity Reopen Processed: Mail sent");
			} catch (MessagingException e) {
				logger.error("Error while creating mail message:{}",
						e.getMessage());
				throw e;
			} catch (MailException e) {
				logger.error("Error sending mail message:{}", e.getMessage());
				throw e;

			}
		}

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
		List<String> recepientIds = new ArrayList<String>();
		String userGroupOrUserRoleOrUserId = null;
		String workflowEntity = null;
		String workflowEntityName = null;
		String geography = null;
		String userName = null;
		String operation = null;
		String reason = "";
		String[] recipientMailIdsArray = null;
		String[] ccMailIdsArray = null;
		String pmoValue = "%" + Constants.PMO_KEYWORD + "%";
		DateFormat df = new SimpleDateFormat(dateFormatStr);
		String dateStr = df.format(date);
		StringBuffer subject = new StringBuffer(environmentName);
		WorkflowRequestT workflowRequestT = workflowRequestRepository
				.findOne(requestId);
		String entityId = workflowRequestT.getEntityId();

		List<String> ccIds = new ArrayList<String>();

		switch (EntityTypeId.valueOf(EntityTypeId.getName(entityTypeId))) {
		case CUSTOMER:
			workflowEntity = Constants.WORKFLOW_CUSTOMER;
			WorkflowCustomerT workflowCustomerT = workflowCustomerRepository
					.findOne(entityId);
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
//		case COMPETITOR:
//			workflowEntity = Constants.WORKFLOW_COMPETITOR;
//			WorkflowCompetitorT workflowCompetitor = workflowCompetitorRepository
//					.findOne(entityId);
//			workflowEntityName = workflowCompetitor.getWorkflowCompetitorName();
//			userName = userRepository.findUserNameByUserId(workflowCompetitor
//					.getCreatedBy());
//			subject.append(Constants.WORKFLOW_COMPETITOR_PENDING_SUBJECT)
//					.append(" ").append(Constants.FROM).append(" ")
//					.append(userName);
//			operation = Constants.WORKFLOW_OPERATION_CREATION_TEMPLATE;
//			break;
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
			break;
		default:
			break;
		}
		MimeMessage automatedMIMEMessage = ((JavaMailSenderImpl) mailSender)
				.createMimeMessage();
		MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(automatedMIMEMessage, true);
			WorkflowStepT workflowStepPending = workflowStepRepository
					.findByRequestIdAndStepStatus(requestId,
							WorkflowStatus.PENDING.getStatus());
			if (workflowStepPending.getUserGroup() != null
					|| workflowStepPending.getUserRole() != null
					|| workflowStepPending.getUserId() != null) {
				if (workflowStepPending.getUserGroup() != null) {
					switch (workflowStepPending.getUserGroup()) {
					case Constants.WORKFLOW_GEO_HEADS:
						recepientIds.addAll(userAccessPrivilegesRepository
								.findUserIdsForWorkflowUserGroup(geography,
										Constants.Y,
										UserGroup.GEO_HEADS.getValue()));
						userGroupOrUserRoleOrUserId = Constants.WORKFLOW_GEO_HEADS;
						ccIds.addAll(userAccessPrivilegesRepository
								.findUserIdsForWorkflowPMO(geography,
										Constants.Y, pmoValue));
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
					userGroupOrUserRoleOrUserId = constructUserNamesSplitByComma(userNames);
					recepientIds.addAll(workflowUserIdList);
				}
			}
			recipientMailIdsArray = getMailIdsFromUserIds(recepientIds);
			if (CollectionUtils.isNotEmpty(ccIds)) {
				ccMailIdsArray = getMailIdsFromUserIds(ccIds);
			}

			Map<String, Object> workflowMap = new HashMap<String, Object>();
			workflowMap.put("userGroupOrUserRole", userGroupOrUserRoleOrUserId);
			workflowMap.put("workflowEntity", workflowEntity);
			workflowMap.put("workflowEntityName", workflowEntityName);
			workflowMap.put("submittedDate", dateStr);
			workflowMap.put("userName", userName);
			workflowMap.put("operation", operation);
			workflowMap.put("reason", reason);
			helper.setTo(recipientMailIdsArray);
			if(ccMailIdsArray!=null) {
			helper.setCc(ccMailIdsArray);
			}
			helper.setFrom(senderEmailId);

			String text = VelocityEngineUtils.mergeTemplateIntoString(
					velocityEngine, workflowPendingTemplateLoc, Constants.UTF8,
					workflowMap);
			logger.info("framed text for mail :" + text);

			helper.setSubject(subject.toString());
			helper.setText(text, true);
			logMailDetails(recipientMailIdsArray, ccMailIdsArray, null,
					subject.toString(), text);
			mailSender.send(automatedMIMEMessage);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MailSendException e) {
			logger.error("Error sending mail message", e.getMessage());
			throw e;
		} catch (MailParseException e) {
			logger.error("Error parsing mail message", e.getMessage());
			throw e;
		} catch (MailAuthenticationException e) {
			logger.error("Error authnticatingh e-mail message", e.getMessage());
			throw e;
		} catch (MailPreparationException e) {
			logger.error("Error preparing mail message", e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("Error sending mail message", e.getMessage());
			throw e;
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
		Set<String> ccIds = new HashSet<String>();
		List<String> recepientIds = new ArrayList<String>();
		String[] recipientMailIdsArray = null;
		DateFormat df = new SimpleDateFormat(dateFormatStr);
		String dateStr = df.format(date);
		String approvedOrRejectedUserName = null;
		String entity = null;
		String operation = null;
		String entityName = null;
		String userName = null;
		String geography = null;
		String pmoValue = "%"
				+ Constants.PMO_KEYWORD + "%";
		String subject = new StringBuffer(environmentName).append(" ")
				.append(workflowCustomerApprovedOrRejectSubject).toString();
		WorkflowRequestT workflowRequestT = workflowRequestRepository
				.findOne(requestId);
		String entityId = workflowRequestT.getEntityId();
		MimeMessage automatedMIMEMessage = ((JavaMailSenderImpl) mailSender)
				.createMimeMessage();
		MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(automatedMIMEMessage, true);
			WorkflowStepT workflowStepSubmitted = workflowStepRepository
					.findByRequestIdAndStepStatus(requestId,
							WorkflowStatus.SUBMITTED.getStatus());
			if (workflowStepSubmitted != null) {
				switch (EntityTypeId
						.valueOf(EntityTypeId.getName(entityTypeId))) {
				case CUSTOMER:
					entity = Constants.WORKFLOW_CUSTOMER;
					WorkflowCustomerT workflowCustomerT = workflowCustomerRepository
							.findOne(entityId);
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
					entityName = opportunity.getOpportunityName();
					userName = userRepository.findUserNameByUserId(workflowRequestT.getCreatedBy());
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
						if (workflowStep.getUserGroup() != null
								|| workflowStep.getUserRole() != null
								|| workflowStep.getUserId() != null) {
							if (workflowStep.getUserGroup() != null) {
								switch (workflowStep.getUserGroup()) {
								case Constants.WORKFLOW_GEO_HEADS:

									ccIds.addAll(userAccessPrivilegesRepository
											.findUserIdsForWorkflowUserGroup(
													geography, Constants.Y,
													UserGroup.GEO_HEADS
															.getValue()));
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
				}
				String comment = "";
				recipientMailIdsArray = getMailIdsFromUserIds(recepientIds);
				String[] ccMailIdsArray = null;
				if (CollectionUtils.isNotEmpty(ccIds)) {
					ccMailIdsArray = getSetMailAddressArr(ccIds);
					helper.setCc(ccMailIdsArray);
				}

				helper.setTo(recipientMailIdsArray);
				helper.setFrom(senderEmailId);
				helper.setSubject(subject);
				Map<String, Object> workflowMap = new HashMap<String, Object>();
				workflowMap.put("userName", userName);
				workflowMap.put("entity", entity);
				workflowMap.put("entityName", entityName);
				workflowMap.put("operation", operation);
				workflowMap.put("submittedDate", dateStr);

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
				}
				String text = VelocityEngineUtils.mergeTemplateIntoString(
						velocityEngine, workflowApproveOrRejectTemplateLoc,
						Constants.UTF8, workflowMap);
				logger.info("framed text for mail :" + text);
				helper.setText(text, true);
				logMailDetails(recipientMailIdsArray, ccMailIdsArray, null,
						subject, text);
				mailSender.send(automatedMIMEMessage);
			}

		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MailSendException e) {
			logger.error("Error sending mail message", e.getMessage());
			throw e;
		} catch (MailParseException e) {
			logger.error("Error parsing mail message", e.getMessage());
			throw e;
		} catch (MailAuthenticationException e) {
			logger.error("Error authnticatingh e-mail message", e.getMessage());
			throw e;
		} catch (MailPreparationException e) {
			logger.error("Error preparing mail message", e.getMessage());
			throw e;
		} catch (Exception e) {
			logger.error("Error sending mail message", e.getMessage());
			throw e;
		}
	}
	
	 private String constructUserNamesSplitByComma(List<String> userNames) {
			
			StringBuilder buffer = new StringBuilder();

			for(String userName : userNames){
			    buffer.append(userName+",");
			}
			
			if(buffer.length()>0){
			    buffer.deleteCharAt(buffer.length()-1);
			}
			logger.debug("Inside constructUserNamesSplitByComma Service");
			return buffer.toString();
		    }

}
