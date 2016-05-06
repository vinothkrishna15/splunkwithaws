package com.tcs.destination.utils;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

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

import com.google.common.collect.Lists;
import com.tcs.destination.bean.DestinationMailMessage;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.UserRepository;
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
	UserRepository userRepository;
	
	@Autowired
	private JavaMailSender mailSender;

	/**
	 * send a mail with the given message
	 * @param message
	 * @throws Exception
	 */
	public void send(final DestinationMailMessage message) throws Exception {

		//filter the in-active user mail ids 
		filterInActiveUsers(message);
		
		if(isValidMessage(message)) {
			try {
				MimeMessage mimeMessage = ((JavaMailSenderImpl) mailSender).createMimeMessage();
				MimeMessageHelper msgHelper = new MimeMessageHelper(mimeMessage, true);
				
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
				
				//log the mail details
				logMailDetails(recipients, ccList, bccList, subject, mailBody);
				
				mailSender.send(mimeMessage);
				logger.info("mail sent, subject : {}", subject);
				
			} catch (MessagingException | MailException e) {
				logger.error("Error sending mail {}", e.getMessage());
				throw e;
			}
		} else {
			throw new DestinationException("Invalid mail : Check recipients and subject");
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
	 * filter the recipients(to), cc and bcc mail list with only active users mails
	 * @param message
	 */
	private void filterInActiveUsers(DestinationMailMessage message) {
		if(message != null) {
				//filter inactive user from recipients
				message.setRecipients(findActiveUserMailIds(message.getRecipients()));
				//filter inactive user from cc
				message.setCcList(findActiveUserMailIds(message.getCcList()));
				//filter inactive user from bcc
				message.setBccList(findActiveUserMailIds(message.getBccList()));
		}
	}
	
	private List<String> findActiveUserMailIds(final List<String> mails) {
		List<String> filteredList = Lists.newArrayList();
		if(CollectionUtils.isNotEmpty(mails)) {
			for (String mail : mails) {
				UserT user = userRepository.findByUserEmailId(mail);
				if(user != null) {
					if(user.isActive()) {
						filteredList.add(mail);
					}
				} else {
					filteredList.add(mail);
				}
			}
		}
		return filteredList;
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
