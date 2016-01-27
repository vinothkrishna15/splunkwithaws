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
import java.util.List;
import java.util.Map;

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
import com.tcs.destination.bean.OpportunityReopenRequestT;
import com.tcs.destination.bean.OpportunitySalesSupportLinkT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.UserAccessRequestT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.OpportunityReopenRequestRepository;
import com.tcs.destination.data.repository.OpportunitySalesSupportLinkTRepository;
import com.tcs.destination.data.repository.UserAccessRequestRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.enums.UserRole;
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

	/**
	 * @param recipientIdList
	 * @return
	 * @throws Exception
	 */
	private String[] getMailIdsFromUserIds(List<String> recipientIdList)
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

}
