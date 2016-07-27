package com.tcs.destination.utils;

import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.sun.istack.ByteArrayDataSource;
import com.tcs.destination.bean.DestinationMailMessage;
import com.tcs.destination.exception.DestinationException;

/**
 * 
 * @author TCS
 *
 */
@Component
public class DestinationMailSender {
	
	private static final Logger logger = LoggerFactory
			.getLogger(DestinationMailSender.class);

	@Value("${senderEmailId}")
	private String senderEmailId;
	
	@Autowired
	private JavaMailSender mailSender;

	/**
	 * send a mail with the given message
	 * @param message
	 * @throws Exception
	 */
	public void send(final DestinationMailMessage message) throws Exception {

		//filter the in-active user mail ids 
		//filterInActiveUsers(message);
		
		if(isValidMessage(message)) {
			try {
				MimeMessage mimeMessage = ((JavaMailSenderImpl) mailSender).createMimeMessage();
				MimeMessageHelper msgHelper = new MimeMessageHelper(mimeMessage, true, Constants.UTF8);
				
				List<String> recipients = message.getRecipients();
				List<String> ccList = message.getCcList();
				List<String> bccList = message.getBccList();
				String subject = message.getSubject();
				String mailBody = message.getMessage();

				msgHelper.setFrom(senderEmailId);
				msgHelper.setTo(convertToArray(recipients));
				if(CollectionUtils.isNotEmpty(ccList)) {
					msgHelper.setCc(convertToArray(ccList));
				}
				if(CollectionUtils.isNotEmpty(bccList)) {
					msgHelper.setBcc(convertToArray(bccList));
				}
				msgHelper.setSubject(subject);
				msgHelper.setText(mailBody, true);
				if(hasAttachment(message)) {
					msgHelper.addAttachment(message.getAtchFileName(), new FileSystemResource(message.getAtchFilePath()));
				}
				
				if(StringUtils.isNotEmpty(message.getContentId())) {
					mimeMessage.setContentID(message.getContentId());
				}
				
				//log the mail details
				logMailDetails(recipients, ccList, bccList, subject, mailBody);
				
				mailSender.send(mimeMessage);
				logger.info("mail sent, subject : {}", subject);
				
			} catch (MessagingException | MailException e) {
				logger.error("Error sending mail ", e);
				throw e;
			}
		} else {
			throw new DestinationException("Invalid mail : Recipients and subject are mandatory");
		}


	}

	/**
	 * send a mail with the given message
	 * @param message
	 * @throws Exception
	 */
	public void sendMultiPart(final DestinationMailMessage message1) throws Exception {
		
			try {
				MimeMessage message = ((JavaMailSenderImpl) mailSender).createMimeMessage();
				
				
				 // Set From: header field of the header.
		         message.setFrom(new InternetAddress(senderEmailId));

		         Address[] address = new Address[2];
		         address[0] = new InternetAddress("manikandan.5@tcs.com");
		         address[1] = new InternetAddress("s.razeen@tcs.com");
		        	 
		         // Set To: header field of the header.
		         message.setRecipients(Message.RecipientType.TO,address);

		         // Set Subject: header field
		         message.setSubject("Testing digest poc Subject");

		         // This mail has 2 part, the BODY and the embedded image
		         MimeMultipart multipart = new MimeMultipart("related");

		         // first part (the html)
		         BodyPart messageBodyPart = new MimeBodyPart();
		         String htmlText = "<H1>Hello</H1><img src=\"cid:image\">";
		         messageBodyPart.setContent(htmlText, "text/html");
		         // add it
		         multipart.addBodyPart(messageBodyPart);

		         // second part (the image)
		         messageBodyPart = new MimeBodyPart();
		         byte[] fileBinary = StreamUtils.copyToByteArray(getClass().getResourceAsStream("/templates/img/MountView.png"));
		 		
		         DataSource fds = new ByteArrayDataSource(fileBinary, "image/png");

		         messageBodyPart.setDataHandler(new DataHandler(fds));
		         messageBodyPart.setHeader("Content-ID", "<image>");

		         // add image to the multipart
		         multipart.addBodyPart(messageBodyPart);

		         // put everything together
		         message.setContent(multipart);
		         // Send message
		         Transport.send(message);

		         System.out.println("Sent message successfully....");
				
				
				
				
			} catch (MessagingException | MailException e) {
				logger.error("Error sending mail ", e);
				throw e;
			}
		
	}

	/**
	 * check the mail has attachments
	 * @param message
	 * @return
	 */
	private boolean hasAttachment(final DestinationMailMessage message) {
		return StringUtils.isNotBlank(message.getAtchFileName()) && StringUtils.isNotBlank(message.getAtchFilePath());
	}

	/**
	 * convert list to array
	 * @param list
	 * @return
	 */
	private String[] convertToArray(final List<String> list) {
		return list.toArray(new String[0]);
	}

	/**
	 * @param message
	 * @return true if from, to and subjects are not blank
	 */
	private boolean isValidMessage(DestinationMailMessage message) {
		return message != null 
				&& CollectionUtils.isNotEmpty(message.getRecipients()) 
				&& StringUtils.isNotBlank(senderEmailId)
				&& StringUtils.isNotBlank(message.getSubject());
	}
	
	/**
	 * logs all mail details
	 * @param recipients
	 * @param ccMailIds
	 * @param bccMailIds
	 * @param subject
	 * @param content
	 */
	private void logMailDetails(final List<String> recipients,
			final List<String> ccMailIds, final List<String> bccMailIds, final String subject,
			final String content) {
		logger.info("Sender : {}", senderEmailId);
		logger.info("To :{}", recipients);
		logger.info("CC :{}", ccMailIds);
		logger.info("BCC :{}", bccMailIds);
		logger.info("Subject :{}" , subject);
	}

}
