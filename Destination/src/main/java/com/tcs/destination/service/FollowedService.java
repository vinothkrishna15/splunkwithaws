package com.tcs.destination.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.UserTaggedFollowedT;
import com.tcs.destination.data.repository.FollowedRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.DateUtils;

@Component
public class FollowedService {
	
	private static final Logger logger = LoggerFactory.getLogger(FollowedService.class);

	@Autowired
	FollowedRepository followedRepository;

	public List<UserTaggedFollowedT> findFollowedFor(String userId, String entityType)
			throws Exception {
		
		logger.debug("Inside findFollowedFor Service");
		
		if (EntityType.contains(entityType)) {
					
					switch(EntityType.valueOf(entityType)){
					
					case CONNECT:
					case OPPORTUNITY:
					case TASK:
						logger.debug("EntityType is present");
						List<UserTaggedFollowedT> userFollowed = followedRepository.
								findByUserIdAndEntityType(userId, entityType);

						if (userFollowed.isEmpty()) {
							logger.error("NOT_FOUND: No Relevent Data Found in the database");
							throw new DestinationException(HttpStatus.NOT_FOUND,
									"No Relevent Data Found in the database");
						}else 
								return userFollowed;
						
					default : 
						logger.error("NOT_FOUND: You cannot follow "+entityType);
						throw new DestinationException(HttpStatus.NOT_FOUND,
							"You cannot follow "+entityType);
					}
				}
				else {
					logger.error("NOT_FOUND: No such Entity type exists. Please ensure your entity type.");
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"No such Entity type exists. Please ensure your entity type");
				}
			}

	public boolean addFollow(UserTaggedFollowedT followed) throws Exception {
		 
			logger.debug("Inside addFollowed Service");
			if (EntityType.contains(followed.getEntityType())) {
				switch (EntityType.valueOf(followed.getEntityType())) {
				case CONNECT:
					logger.debug("Adding Followed Connect");
					if (followed.getConnectId() == null) {
						logger.error("BAD_REQUEST: Connect ID can not be empty");
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"Connect ID can not be empty");
					} else {
						followed.setOpportunityId(null);
						followed.setTaskId(null);
												
					}
					break;
				case OPPORTUNITY:
					logger.debug("Adding Followed Opportunity");
					if (followed.getOpportunityId() == null) {
						logger.error("BAD_REQUEST: Opportunity ID can not be empty");
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"Opportunity ID can not be empty");
					} else {
						followed.setConnectId(null);
						followed.setTaskId(null);
						
					}
					break;
									
				case TASK:
					logger.debug("Adding Followed Opportunity");
					if (followed.getTaskId() == null) {
						logger.error("BAD_REQUEST: Task ID can not be empty");
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"TASK ID can not be empty");
					} else {
						followed.setConnectId(null);
						followed.setOpportunityId(null);
						
					}
					break;
					
				default : 	logger.error("NOT_FOUND: You cannot follow "+followed.getEntityType());
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"You cannot follow "+followed.getEntityType());
				}
				followed.setCreatedModifiedDatetime(DateUtils.getCurrentTimeStamp());
				try {
					logger.debug("Saving the UserTaggedFollowed");
					return followedRepository.save(followed) != null;
				} catch (Exception e) {
					logger.error("BAD_REQUEST" + e.getMessage());
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							e.getMessage());
				}
	}
			logger.error("BAD_REQUEST: Invalid Entity Type");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Entity Type");
		}
	}