package com.tcs.destination.helper;

import static com.tcs.destination.utils.Constants.DATE;
import static com.tcs.destination.utils.Constants.DATE_TYPE;
import static com.tcs.destination.utils.Constants.NO;
import static com.tcs.destination.utils.Constants.PATTERN;
import static com.tcs.destination.utils.Constants.SYSTEM_USER;
import static com.tcs.destination.utils.Constants.TOKEN_ENTITY_NAME;
import static com.tcs.destination.utils.Constants.TOKEN_ENTITY_TYPE;
import static com.tcs.destination.utils.Constants.TOKEN_FROM;
import static com.tcs.destination.utils.Constants.TOKEN_TO;
import static com.tcs.destination.utils.Constants.TOKEN_USER;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.NotificationEventGroupMappingT;
import com.tcs.destination.bean.UserNotificationsT;
import com.tcs.destination.data.repository.NotificationEventGroupMappingTRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.exception.DestinationException;

@Component
public class NotificationProcessHelper {

	private static final Logger logger = LoggerFactory
			.getLogger(NotificationProcessHelper.class);

	@Autowired
	private NotificationEventGroupMappingTRepository notificationEvtGrpMTRepository;

	public UserNotificationsT processNotification(String userId,
			final EntityType entity, String entityId, String entityName, int eventId,
			String dateType, String date, String recipient) throws DestinationException {

		logger.debug("Inside processNotification() method");

		UserNotificationsT notification = null;
		List<NotificationEventGroupMappingT> notificationTemplateList = notificationEvtGrpMTRepository
				.findByEventId(eventId);

		if (!notificationTemplateList.isEmpty()) {

			try {
				String msgTemplate = replaceTokens(
						notificationTemplateList.get(0).getMessageTemplate(),
						populateTokens(userId, entityName, null, null,
								WordUtils.capitalize(entity.toString().toLowerCase()), dateType, date));

				if (msgTemplate != null) {

					notification = new UserNotificationsT();
					notification.setEntityType(entity.toString());
					notification.setRead(NO);
					notification.setComments(msgTemplate);
					notification.setUserId(SYSTEM_USER);
					notification.setRecipient(recipient);

					switch (entity) {

					case OPPORTUNITY:
						notification.setOpportunityId(entityId);
						break;
						
					case TASK:
						notification.setTaskId(entityId);
						break;
						
					case CONNECT:
						notification.setConnectId(entityId);
						break;
						
					default:
						break;
					}

					notification.setEventId(eventId);
				}

			} catch (Exception e) {
				logger.error("Error occurred while processing notifications "
						+ e.getMessage());
				throw new DestinationException(
						HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}

		return notification;

	}

	private String replaceTokens(String message, Map<String, String> tokens)
			throws Exception {
		logger.debug("Inside replaceTokens() method");
		Pattern pattern = Pattern.compile(PATTERN);
		Matcher matcher = pattern.matcher(message);
		StringBuilder builder = new StringBuilder();
		int i = 0;
		while (matcher.find()) {
			String replacement = tokens.get(matcher.group(1));
			builder.append(message.substring(i, matcher.start()));
			if (replacement == null)
				builder.append(matcher.group(0));
			else
				builder.append(replacement);
			i = matcher.end();
		}
		builder.append(message.substring(i, message.length()));
		return builder.toString();
	}

	private HashMap<String, String> populateTokens(String user,
			String entityName, String from, String to, String entityType,
			String dateType, String date) throws Exception {
		
		logger.debug("Inside populateTokens() method");
		HashMap<String, String> tokensMap = new HashMap<String, String>();
		if (user != null) {
			tokensMap.put(TOKEN_USER, user);
		}
		if (entityName != null) {
			tokensMap.put(TOKEN_ENTITY_NAME, entityName);
		}
		if (from != null) {
			tokensMap.put(TOKEN_FROM, from);
		}
		if (to != null) {
			tokensMap.put(TOKEN_TO, to);
		}
		if (entityType != null) {
			tokensMap.put(TOKEN_ENTITY_TYPE, entityType);
		}
		if (entityType != null) {
			tokensMap.put(DATE_TYPE, dateType);
		}
		if (entityType != null) {
			tokensMap.put(DATE, date);
		}

		return tokensMap;
	}

}
