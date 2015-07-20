package com.tcs.destination.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
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
	
	//private String recipient = "badrinaraayanan.m@tcs.com";
	@Autowired
	private UserService userService;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private JavaMailSenderImpl sender;

	@Autowired
	private SimpleMailMessage templateMessage;
	
	@Autowired
	private VelocityEngine velocityEngine;
	
	@Value("${forgotPassword}")
	private String forgotPasswordSubject;
	
	@Value("${userAccess}")
	private String userAccessSubject;
	
	@Value("${senderEmailId}")
	private String senderEmailId;
	
	@Value("${reopenOpportunity}")
	private String reopenOpportunitySubject;
	
	@Autowired
	UserAccessRequestRepository userAccessRepo;
	
	@Autowired
	OpportunityReopenRequestRepository oppReopenRepo;
	
	@Autowired
	private OpportunityService oppService;
	
	private static final Logger logger = LoggerFactory
			.getLogger(DestinationMailUtils.class);
	
	public void sendPasswordAutomatedEmail(String userId) throws Exception {
		DestinationMailMessage message = new DestinationMailMessage();
		message.setMessageType("MIME");
		
		List<String> recipientIds = new ArrayList<String>();
        recipientIds.add(userId);
        message.setRecipients(recipientIds);
        
		List<String> ccIds = new ArrayList<String>();
		message.setCcList(ccIds);
		
		List<String> bccIds = new ArrayList<String>();
        message.setBccList(bccIds);
        
        UserT user = userService.findByUserId(userId);
		sendPasswordMail(message,user);
	}
	
	public void sendUserAccessAutomatedEmail(String reqId) throws Exception {
		DestinationMailMessage message = new DestinationMailMessage();
		message.setMessageType("MIME");
		
		List<String> recipientIds = userService.findByUserRole("System Admin");
		message.setRecipients(recipientIds);
		
		List<String> ccIds = new ArrayList<String>();
		message.setCcList(ccIds);
		
		List<String> bccIds = new ArrayList<String>();
        message.setBccList(bccIds);
        
		sendUserAccessMail(message,reqId);
	}
	
	public void sendOpportunityReopenAutomatedEmail(String reqId) throws Exception {
		DestinationMailMessage message = new DestinationMailMessage();
		message.setMessageType("MIME");
		
		List<String> recipientIds = userService.findByUserRole("System Admin");
		message.setRecipients(recipientIds);
		
		List<String> ccIds = new ArrayList<String>();
		message.setCcList(ccIds);
		
		List<String> bccIds = new ArrayList<String>();
        message.setBccList(bccIds);
        
		sendOpportunityReopenMail(message,reqId);
	}
	
	
	public void sendPasswordMail(DestinationMailMessage message, UserT user) throws Exception{
		
		List<String> recipientIdList = message.getRecipients();
	    String[] recipientMailIdsArray = getMailIdsFromUserIds(recipientIdList);
		
	    List<String> ccIdList = message.getCcList();
	    String[] ccMailIdsArray=new String[0];
	    if(ccIdList!=null)
	    ccMailIdsArray = getMailIdsFromUserIds(ccIdList);
		
	    List<String> bccIdList = message.getBccList();
	    String[] bccMailIdsArray=new String[0];
	    if(bccIdList!=null)
	    bccMailIdsArray = getMailIdsFromUserIds(bccIdList);
		
	    if(message.getMessageType().equals("MIME")){
			MimeMessage automatedMIMEMessage = ((JavaMailSenderImpl) mailSender).createMimeMessage();
			try{
				MimeMessageHelper helper = new MimeMessageHelper(automatedMIMEMessage, true);
				helper.setTo(recipientMailIdsArray);
				helper.setCc(ccMailIdsArray);
				helper.setBcc(bccMailIdsArray);
				helper.setSubject(forgotPasswordSubject);
				helper.setFrom(senderEmailId);
				Map model = new HashMap();
				model.put("user", user);
				String text = VelocityEngineUtils.mergeTemplateIntoString(
						velocityEngine,
						"./templates/Template_Forgot_Password.vm", "UTF-8",
						model);
				logMailDetails(recipientMailIdsArray, ccMailIdsArray, bccMailIdsArray, forgotPasswordSubject, text);
				//mailSender.send(automatedMIMEMessage);
			} catch(Exception e){
			 System.out.println(e.getMessage());
			}
		}
		
		
		
		
//		    String result="";
//		    
//			if(message.getMessageType().equals("TEXT")){
//				SimpleMailMessage msg = new SimpleMailMessage(templateMessage);
//				msg.setTo(recipientMailIdsArray);
//				msg.setSubject(message.getSubject());
//				msg.setText(message.getMessage());
//				msg.setFrom(message.getSenderEmail());
//				result = sendTextMail(msg);
//			} else {
//				List<UserT> users = getUsers(recipientIdList);
//				for(UserT user : users){
//					sendConfirmationEmail(user);
//				}
//			}
//			return result;
			}

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
	
//	private List<UserT> getUsers(List<String> recipientIdList) throws Exception{
//		List<UserT> userList = new ArrayList<UserT>();
//		
//		for(String recipientId : recipientIdList){
//		  UserT recipient = userService.findByUserId(recipientId);
//		  userList.add(recipient);
//		}
//		return userList;
//	}

//	private String sendMIMEMail(DestinationMailMessage message) {
//		// TODO Auto-generated method stub
//		
//	}

//	private String sendTextMail(SimpleMailMessage msg) {
//		// TODO Auto-generated method stub
//		try {
//		//	this.mailSender.send(msg);
//			System.out.println("Success !! Text Mail Sent !! ");
//			return "Success !! Text Mail sent !! ";
//		} catch (Exception ex) {
//			System.err.println(ex.getMessage());
//			return "Failure !! Text Mail not sent !!";
//		}
//	}
//	
//	private void sendConfirmationEmail(final UserT user) {
//		
//		MimeMessage message = ((JavaMailSenderImpl) mailSender).createMimeMessage();
//		 
//		   try{
//			MimeMessageHelper helper = new MimeMessageHelper(message, true);
//	 
//			helper.setFrom("badrinaraayanan.m@tcs.com");
//			helper.setTo(user.getUserEmailId());
//			helper.setSubject("MIME message");
//			helper.setText("MIME TEXT");
//
//			Map model = new HashMap();
//			model.put("user", user);
//			String text = VelocityEngineUtils.mergeTemplateIntoString(
//					velocityEngine,
//					"Sample_HTML_Template.vm", "UTF-8",
//					model);
//			FileSystemResource res = new FileSystemResource(new File("/Users/bnpp/Desktop/Naveen Khanna.png"));
//			helper.addInline("identifier1234", res);
//			helper.setText(text, true);			
//	 
//		     }catch (MessagingException e) {
//			throw new MailParseException(e);
//		     }
//		    // mailSender.send(message);
//		
//		
//		
////        MimeMessagePreparator preparator = new MimeMessagePreparator() {
////        	@Override
////            public void prepare(MimeMessage mimeMessage) throws Exception {
////                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
////                message.setTo(user.getUserEmailId());
////                message.setFrom(""); // could be parameterized...
////                Map model = new HashMap();
////                model.put("user", user);
////                String text = VelocityEngineUtils.mergeTemplateIntoString(
////                        velocityEngine, "com/dns/registration-confirmation.vm", model);
////                message.setText(text, true);
////            }
////
////        };
////     mailSender.send(preparator);
//    }

	public void sendUserAccessMail(DestinationMailMessage message, String reqId) throws Exception {
		// TODO Auto-generated method stub
		List<String> recipientIdList = message.getRecipients();
		
		UserAccessRequestT userAccessRequest = userAccessRepo.findOne(reqId);
		String supervisorId = userAccessRequest.getSupervisorId();
		recipientIdList.add(supervisorId);
	    String[] recipientMailIdsArray = getMailIdsFromUserIds(recipientIdList);
	    
	    String[] ccMailIdsArray={userAccessRequest.getUserEmailId()};
		
	    List<String> bccIdList = message.getBccList();
	    String[] bccMailIdsArray=new String[0];
	    if(bccIdList!=null)
	    bccMailIdsArray = getMailIdsFromUserIds(bccIdList);
	    
	    if(message.getMessageType().equals("MIME")){
			MimeMessage automatedMIMEMessage = ((JavaMailSenderImpl) mailSender).createMimeMessage();
			try{
				MimeMessageHelper helper = new MimeMessageHelper(automatedMIMEMessage, true);
				helper.setTo(recipientMailIdsArray);
				helper.setCc(ccMailIdsArray);
				helper.setBcc(bccMailIdsArray);
				helper.setSubject(userAccessSubject);
				helper.setFrom(senderEmailId);
				Map model = new HashMap();
				model.put("request", userAccessRequest);
				String text = VelocityEngineUtils.mergeTemplateIntoString(
						velocityEngine,
						"./templates/Template_User_Access.vm", "UTF-8",
						model);
				logMailDetails(recipientMailIdsArray,ccMailIdsArray,bccMailIdsArray,userAccessSubject,text);
				//mailSender.send(automatedMIMEMessage);
			} catch(Exception e){
			 System.out.println(e.getMessage());
			}
		}
	    
	}

	public void sendOpportunityReopenMail(DestinationMailMessage message,
			String reqId) throws Exception {
		List<String> recipientIdList = message.getRecipients();
		OpportunityReopenRequestT oppReopenRequest = oppReopenRepo.findOne(reqId);
        UserT user = userService.findByUserId(oppReopenRequest.getRequestedBy());
        UserT supervisor = userService.findByUserId(user.getSupervisorUserId());
        recipientIdList.add(supervisor.getUserId());
        
        OpportunityT opp = oppService.findOpportunityById(oppReopenRequest.getOpportunityId());
	    String[] recipientMailIdsArray = getMailIdsFromUserIds(recipientIdList);
	    
	    List<String> ccIdList=message.getCcList();
	    String[] ccMailIdsArray=new String[0];
	    if(ccIdList!=null)
	    ccMailIdsArray = getMailIdsFromUserIds(ccIdList);
		
	    List<String> bccIdList = message.getBccList();
	    String[] bccMailIdsArray=new String[0];
	    if(bccIdList!=null)
	    bccMailIdsArray = getMailIdsFromUserIds(bccIdList);
	    
	    if(message.getMessageType().equals("MIME")){
			MimeMessage automatedMIMEMessage = ((JavaMailSenderImpl) mailSender).createMimeMessage();
			try{
				MimeMessageHelper helper = new MimeMessageHelper(automatedMIMEMessage, true);
				helper.setTo(recipientMailIdsArray);
				helper.setCc(ccMailIdsArray);
				helper.setBcc(bccMailIdsArray);
				helper.setSubject(reopenOpportunitySubject);
				helper.setFrom(senderEmailId);
				Map model = new HashMap();
				
				model.put("request", oppReopenRequest);
				model.put("user",user);
				model.put("opportunity",opp);
				String text = VelocityEngineUtils.mergeTemplateIntoString(
						velocityEngine,
						"./templates/Template_Opportunity_Reopen.vm", "UTF-8",
						model);
				logMailDetails(recipientMailIdsArray, ccMailIdsArray, bccMailIdsArray, reopenOpportunitySubject, text);
				//mailSender.send(automatedMIMEMessage);
			} catch(Exception e){
			 System.out.println(e.getMessage());
			}
		}
		
	}
	
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

	private void logMailIds(String recipientType,String[] mailIdsArray) {
		logger.info(recipientType + "Mail Ids : " );
		for(String id : mailIdsArray){
			logger.info(id);
		}
	}
}
