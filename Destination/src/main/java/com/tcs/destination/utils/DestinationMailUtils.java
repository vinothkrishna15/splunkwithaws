package com.tcs.destination.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.tcs.destination.bean.DestinationMailMessage;
import com.tcs.destination.bean.OpportunityReopenRequestT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.UserAccessRequestT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.OpportunityReopenRequestRepository;
import com.tcs.destination.data.repository.UserAccessRequestRepository;
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
	private OpportunityService oppService;
	
	private static final Logger logger = LoggerFactory
			.getLogger(DestinationMailUtils.class);
	
	
	/**
	 * @param user
	 * @param requestedDateTime
	 * @throws Exception
	 */
	public void sendPasswordAutomatedEmail(String subject,UserT user, Date requestedDateTime) throws Exception {
		logger.info("inside sendPasswordAutomatedEmail method");
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
        message.setSubject(subject);
        logger.info("Subject : " + subject);
        //df.setTimeZone(TimeZone.getTimeZone("GMT+5.30"));  
		sendPasswordMail(message,user,dateStr);
	}
	
	/**
	 * @param subject - subject of the mail to be sent
	 * @param reqId - request id for new user access
	 * @param requestedDateTime - requested timestamp
	 * @throws Exception
	 */
	public void sendUserAccessAutomatedEmail(String subject, String reqId, Date requestedDateTime) throws Exception {
		logger.info("inside sendUserAccessAutomatedEmail method");
		DestinationMailMessage message = new DestinationMailMessage();
		message.setMessageType(Constants.MIME);
		
		UserAccessRequestT userAccessRequest = userAccessRepo.findOne(reqId);
		
		List<String> recipientIds = userService.findByUserRole(Constants.SYSTEM_ADMIN);
        message.setRecipients(recipientIds);
		
		List<String> ccIds = new ArrayList<String>();
        String supervisorId = userAccessRequest.getSupervisorId();
        ccIds.add(supervisorId);
		message.setCcList(ccIds);
		
		List<String> bccIds = new ArrayList<String>();
        message.setBccList(bccIds);
        
        message.setSubject(subject);
        
        DateFormat df = new SimpleDateFormat(dateFormatStr);
        String requestedDateStr = df.format(requestedDateTime);
        
		sendUserAccessMail(message,userAccessRequest,requestedDateStr);
	}
	
	/**
	 * @param subject
	 * @param reqId 
	 * @param requestedDateTime
	 * @throws Exception
	 */
	public void sendOpportunityReopenAutomatedEmail(String subject, String reqId, Date requestedDateTime) throws Exception {
		logger.info("inside sendUserAccessAutomatedEmail method");
		DestinationMailMessage message = new DestinationMailMessage();
		message.setMessageType(Constants.MIME);
		
		OpportunityReopenRequestT oppReopenRequest = oppReopenRepo.findOne(reqId);
        UserT user = userService.findByUserId(oppReopenRequest.getRequestedBy());
        UserT supervisor = userService.findByUserId(user.getSupervisorUserId());
        OpportunityT opp = oppService.findOpportunityById(oppReopenRequest.getOpportunityId());
        
		List<String> recipientIds = userService.findByUserRole(Constants.SYSTEM_ADMIN);
		message.setRecipients(recipientIds);
		
		List<String> ccIds = new ArrayList<String>();
		ccIds.add(user.getUserId());
		ccIds.add(supervisor.getUserId());
		message.setCcList(ccIds);
		
		List<String> bccIds = new ArrayList<String>();
        message.setBccList(bccIds);
        
        DateFormat df = new SimpleDateFormat(dateFormatStr);
        String dateStr = df.format(requestedDateTime);
        message.setSubject(subject);
		sendOpportunityReopenMail(message,oppReopenRequest,user,opp,dateStr);
	}
	
	/**
	 * @param message
	 * @param user
	 * @param dateStr
	 * @throws Exception
	 */
	private void sendPasswordMail(DestinationMailMessage message, UserT user, String dateStr) throws Exception{
		logger.info("Inside sendPasswordMail method");
		List<String> recipientIdList = message.getRecipients();
	    String[] recipientMailIdsArray = getMailIdsFromUserIds(recipientIdList);
	    String[] ccMailIdsArray=getMailAddressArr(message.getCcList());
	    String[] bccMailIdsArray =getMailAddressArr(message.getBccList());
		
	    if(message.getMessageType().equals(Constants.MIME)){
			MimeMessage automatedMIMEMessage = ((JavaMailSenderImpl) mailSender).createMimeMessage();
			try{
				MimeMessageHelper helper = new MimeMessageHelper(automatedMIMEMessage, true);
				helper.setTo(recipientMailIdsArray);
				helper.setCc(ccMailIdsArray);
				helper.setBcc(bccMailIdsArray);
				String subject = message.getSubject();
				helper.setSubject(subject);
				logger.info("Sender : " + senderEmailId);
				logger.info("date : " + dateStr);
				helper.setFrom(senderEmailId);
				Map forgotPasswordTemplateDataModel = new HashMap();
				forgotPasswordTemplateDataModel.put("user", user);
				forgotPasswordTemplateDataModel.put("date", dateStr);
				String text = VelocityEngineUtils.mergeTemplateIntoString(
						velocityEngine,forgotPasswordTemplateLoc, Constants.UTF8,forgotPasswordTemplateDataModel);
				logMailDetails(recipientMailIdsArray, ccMailIdsArray, bccMailIdsArray, subject, text);
				mailSender.send(automatedMIMEMessage);
			} catch(Exception e){
			 System.out.println(e.getMessage());
			}
		}
			}
	
	/**
	 * @param message
	 * @param userAccessRequest
	 * @param dateStr
	 * @throws Exception
	 */
	private void sendUserAccessMail(DestinationMailMessage message, UserAccessRequestT userAccessRequest, String dateStr) throws Exception {
		logger.info("Inside sendUserAccessMail method");
		List<String> recipientIdList = message.getRecipients();
	    String[] recipientMailIdsArray = getMailIdsFromUserIds(recipientIdList);
	    String[] ccMailIdsArray=getMailAddressArr(message.getCcList());
	    //shuffling the order of Ids
	    int size = ccMailIdsArray.length;
        ccMailIdsArray = Arrays.copyOf(ccMailIdsArray, size + 1);
        ccMailIdsArray[1] = ccMailIdsArray[0];
        ccMailIdsArray[0] = userAccessRequest.getUserEmailId(); 
	    String[] bccMailIdsArray=getMailAddressArr(message.getBccList());
	    
	    if(message.getMessageType().equals(Constants.MIME)){
			MimeMessage automatedMIMEMessage = ((JavaMailSenderImpl) mailSender).createMimeMessage();
			try{
				MimeMessageHelper helper = new MimeMessageHelper(automatedMIMEMessage, true);
				helper.setTo(recipientMailIdsArray);
				helper.setCc(ccMailIdsArray);
				helper.setBcc(bccMailIdsArray);
				String subject = message.getSubject();
				helper.setSubject(subject);
				helper.setFrom(senderEmailId);
				Map userAccessTemplateDataModel = new HashMap();
				userAccessTemplateDataModel.put("request", userAccessRequest);
				userAccessTemplateDataModel.put("date", dateStr);
				String text = VelocityEngineUtils.mergeTemplateIntoString(
						velocityEngine,userAccessTemplateLoc, Constants.UTF8,userAccessTemplateDataModel);
				logMailDetails(recipientMailIdsArray,ccMailIdsArray,bccMailIdsArray,subject,text);
				mailSender.send(automatedMIMEMessage);
			} catch(Exception e){
			 System.out.println(e.getMessage());
			}
		}
	    
	}

	/**
	 * This method initializes the actual mime message that will be sent
	 * @param message - object containing recipients, message type, 
	 * @param oppReopenRequest
	 * @param user
	 * @param opp
	 * @param dateStr
	 * @throws Exception
	 */
	private void sendOpportunityReopenMail(DestinationMailMessage message,
			OpportunityReopenRequestT oppReopenRequest, UserT user, OpportunityT opp,String dateStr) throws Exception {
		logger.info("Inside sendOpportunityReopenMail method");
		List<String> recipientIdList = message.getRecipients();
	    String[] recipientMailIdsArray = getMailIdsFromUserIds(recipientIdList);
	    String[] ccMailIdsArray=getMailAddressArr(message.getCcList());
	    String[] bccMailIdsArray=getMailAddressArr(message.getBccList());
	    
	    if(message.getMessageType().equals(Constants.MIME)){
			MimeMessage automatedMIMEMessage = ((JavaMailSenderImpl) mailSender).createMimeMessage();
			try{
				MimeMessageHelper helper = new MimeMessageHelper(automatedMIMEMessage, true);
				helper.setTo(recipientMailIdsArray);
				helper.setCc(ccMailIdsArray);
				helper.setBcc(bccMailIdsArray);
				String subject = message.getSubject();
				helper.setSubject(subject);
				helper.setFrom(senderEmailId);
				Map reopenOppTemplateDataModel = new HashMap();
				
				reopenOppTemplateDataModel.put("request", oppReopenRequest);
				reopenOppTemplateDataModel.put("user",user);
				reopenOppTemplateDataModel.put("opportunity",opp);
				reopenOppTemplateDataModel.put("date",dateStr);
				String text = VelocityEngineUtils.mergeTemplateIntoString(
						velocityEngine,reopenOpportunityTemplateLoc, Constants.UTF8,reopenOppTemplateDataModel);
				logMailDetails(recipientMailIdsArray, ccMailIdsArray, bccMailIdsArray, subject, text);
				mailSender.send(automatedMIMEMessage);
			} catch(Exception e){
			 System.out.println(e.getMessage());
			}
		}
		
	}

	/**
	 * @param idList
	 * @return
	 * @throws Exception
	 */
	private String[] getMailAddressArr(List<String> idList) throws Exception{
	    String[] mailIdsArray=new String[0];
	    if(idList!=null)
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
		
		for(String recipientId : recipientIdList){
		  UserT recipient = userService.findByUserId(recipientId);
		  String mailId = recipient.getUserEmailId(); 	
		  recipientMailIds.add(mailId);	
		}
		String[] recipientMailIdsArray = recipientMailIds.toArray(new String[recipientMailIds.size()]);
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
			String[] ccMailIdsArray, String[] bccMailIdsArray,
			String subject, String content) {
		logger.info("Sender : " + senderEmailId);
		logMailIds("To ",recipientMailIdsArray);
		logMailIds("CC ",ccMailIdsArray);
		logMailIds("BCC ",bccMailIdsArray);
		logger.info("Subject " + subject);
		System.out.print("Message ");
		System.out.println(content);
	}

	/**
	 * @param recipientType
	 * @param mailIdsArray
	 */
	private void logMailIds(String recipientType,String[] mailIdsArray) {
		logger.info(recipientType + "Mail Ids : " );
		for(String id : mailIdsArray){
			logger.info(id);
		}
	}

}
