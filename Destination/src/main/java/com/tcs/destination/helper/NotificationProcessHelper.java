package com.tcs.destination.helper;

import static com.tcs.destination.utils.Constants.DATE;
import static com.tcs.destination.utils.Constants.DATE_TYPE;
import static com.tcs.destination.utils.Constants.NO;
import static com.tcs.destination.utils.Constants.PATTERN;
import static com.tcs.destination.utils.Constants.SYSTEM_USER;
import static com.tcs.destination.utils.Constants.TOKEN_CST_OR_PARTNER;
import static com.tcs.destination.utils.Constants.TOKEN_CST_OR_PARTNER_VALUE;
import static com.tcs.destination.utils.Constants.TOKEN_ENTITY_NAME;
import static com.tcs.destination.utils.Constants.TOKEN_ENTITY_TYPE;
import static com.tcs.destination.utils.Constants.TOKEN_FROM;
import static com.tcs.destination.utils.Constants.TOKEN_PRIMARY_OWNER;
import static com.tcs.destination.utils.Constants.TOKEN_SECONDARY_OWNERS;
import static com.tcs.destination.utils.Constants.TOKEN_SUBORDINATE;
import static com.tcs.destination.utils.Constants.TOKEN_TO;
import static com.tcs.destination.utils.Constants.TOKEN_USER;
import static com.tcs.destination.utils.Constants.TOKEN_OWNERSHIP;
import static com.tcs.destination.utils.Constants.NOTIFICATION_PRIMARY_OWNER;
import static com.tcs.destination.utils.Constants.NOTIFICATION_SECONDARY_OWNER;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
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

	public UserNotificationsT processNotification(EntityType entity,
			String entityId, String entityName, int eventId, String dateType,
			String date, String recipientId, String recipientName,
			String subordinateName, String entityReference,
			String referenceValue, String primaryOwner, String secondaryOwners)
			throws DestinationException {

		logger.debug("Inside processNotification() method");

		UserNotificationsT notification = null;
		List<NotificationEventGroupMappingT> notificationTemplateList = notificationEvtGrpMTRepository
				.findByEventId(eventId);
		String ownership = null;
		String[] secondaryOwner = null;
		if (subordinateName.equalsIgnoreCase(primaryOwner.trim())) {
			ownership = NOTIFICATION_PRIMARY_OWNER;
		} else if (secondaryOwners != null) {
			secondaryOwner = secondaryOwners.split(",");
			for (String secOwner : secondaryOwner) {
				if (secOwner.equalsIgnoreCase(subordinateName.trim())) {
					ownership = NOTIFICATION_SECONDARY_OWNER;
					break;
				}
			}
		} 

		if (!notificationTemplateList.isEmpty()) {

			try {
				String[] values = { recipientName, entityName, null, null,
						WordUtils.capitalize(entity.toString().toLowerCase()),
						dateType, date, subordinateName, entityReference,
						referenceValue, primaryOwner, secondaryOwners,
						ownership };

				String msgTemplate = replaceTokens(
						StringUtils.isEmpty(secondaryOwners) ? notificationTemplateList
								.get(0)
								.getMessageTemplate()
								.replace(" and <secondaryOwners>(Secondary)",
										"")
								: notificationTemplateList.get(0)
										.getMessageTemplate(),
						populateTokens(values));

				if (msgTemplate != null) {

					notification = new UserNotificationsT();
					notification.setEntityType(entity.toString());
					notification.setRead(NO);
					notification.setComments(msgTemplate);
					notification.setUserId(SYSTEM_USER);
					notification.setRecipient(recipientId);

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
			if (replacement == null) {
				builder.append(matcher.group(0));
			} else {
				builder.append(replacement);
			}
			i = matcher.end();
		}
		builder.append(message.substring(i, message.length()));
		return builder.toString();
	}

	private HashMap<String, String> populateTokens(String[] values)
			throws Exception {

		logger.debug("Inside populateTokens() method");

		HashMap<String, String> tokensMap = new HashMap<String, String>();
		if (values[0] != null) {
			tokensMap.put(TOKEN_USER, values[0]);
		}
		if (values[1] != null) {
			tokensMap.put(TOKEN_ENTITY_NAME, values[1]);
		}
		if (values[2] != null) {
			tokensMap.put(TOKEN_FROM, values[2]);
		}
		if (values[3] != null) {
			tokensMap.put(TOKEN_TO, values[3]);
		}
		if (values[4] != null) {
			tokensMap.put(TOKEN_ENTITY_TYPE, values[4]);
		}
		if (values[5] != null) {
			tokensMap.put(DATE_TYPE, values[5]);
		}
		if (values[6] != null) {
			tokensMap.put(DATE, values[6]);
		}
		if (values[7] != null) {
			tokensMap.put(TOKEN_SUBORDINATE, values[7]);
		}
		if (values[8] != null) {
			tokensMap.put(TOKEN_CST_OR_PARTNER, values[8]);
		}
		if (values[9] != null) {
			tokensMap.put(TOKEN_CST_OR_PARTNER_VALUE, values[9]);
		}
		if (values[10] != null) {
			tokensMap.put(TOKEN_PRIMARY_OWNER, values[10]);
		}
		if (values[11] != null) {
			tokensMap.put(TOKEN_SECONDARY_OWNERS, values[11]);
		}
		if (values[12] != null) {
			tokensMap.put(TOKEN_OWNERSHIP, values[12]);
		}

		return tokensMap;
	}

}
