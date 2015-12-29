package com.tcs.destination.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.CollaborationCommentT;
import com.tcs.destination.bean.EntityBean;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.TimelineResponse;
import com.tcs.destination.data.repository.CollaborationCommentsRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.TaskRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.DestinationUtils;

/**
 * This service finds the trending opportunities,  
 * to retrieve a comment, to retrieve  a distinct comment
 */
@Service
public class TrendingService {

	private static final Logger logger = LoggerFactory
			.getLogger(TrendingService.class);

	@Autowired
	OpportunityRepository oppRepository;

	@Autowired
	ConnectRepository connectRepository;

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	CollaborationCommentsRepository commentsRepository;

/**
 * this method finds the trending opportunities
 * @param count
 * @return
 */
	public List<OpportunityT> findtrendingOpportunities(int count) {
		logger.info("Begin:findtrendingOpportunities() in TrendingService"); 
		List<OpportunityT> tempList;
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		tempList = oppRepository.findTrendingOpportunities(userId);
		List<OpportunityT> oppList;
		if (tempList.size() >= count) {
			oppList = tempList.subList(0, count);
		} else {
			oppList = tempList;
		}
		logger.info("End:findtrendingOpportunities() in TrendingService"); 
		return oppList;
	}

	/**
	 * this method retrieves distinct comments
	 * @param timestamp
	 * @param count
	 * @param entityType
	 * @return
	 * @throws Exception
	 */
	public TimelineResponse getDistinctComment(Timestamp timestamp, int count,
			String entityType) throws Exception {
		logger.info("Begin: getDistinctComment() in TrendingService"); 
		TimelineResponse response = new TimelineResponse();

		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		List<EntityBean> filteredList = new ArrayList<EntityBean>();
		List<Object[]> entityIdList = commentsRepository.getDistinctComments(
				userId, timestamp, count);

		if (entityIdList != null && entityIdList.size() != 0) {
			for (Object[] item : entityIdList) {
				String commentId = (String) item[0];
				String entityTypeRetrieved = (String) item[1];
				String entityId = (String) item[2];
				Timestamp dateTime = (Timestamp) item[4];

				populateAndAddtoResponseBeanList(filteredList,
						entityTypeRetrieved, entityId, commentId, dateTime);
			}
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Relevant Data found");
		}

		response.setBean(filteredList);
		if(filteredList.isEmpty()){
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Relevant Data found");
		}
		int responseListSize = filteredList.size() - 1;
		EntityBean lastItem = filteredList.get(responseListSize);
		response.setToken(lastItem.getDateTime());
		//logger.info("Page Token sent : " + response.getToken());
		logger.info("End: getDistinctComment() in TrendingService"); 
		return response;
	}
/**
 * 
 * @param filteredList
 * @param entityTypeRetrieved
 * @param entityId
 * @param commentId
 * @param dateTime
 */
	private void populateAndAddtoResponseBeanList(
			List<EntityBean> filteredList, String entityTypeRetrieved,
			String entityId, String commentId, Timestamp dateTime) {
		logger.info("Begin: populateAndAddtoResponseBeanList() in TrendingService"); 
		EntityBean e = new EntityBean();
		e.setCommentId(commentId);
		if (EntityType.contains(entityTypeRetrieved)) {
			switch (EntityType.valueOf(entityTypeRetrieved)) {
			case CONNECT:
				e.setConnect(connectRepository.findByConnectId(entityId));
				break;
			case OPPORTUNITY:
				e.setOpportunity(oppRepository.findByOpportunityId(entityId));
				break;
			case TASK:
				e.setTask(taskRepository.findOne(entityId));
				break;
			}
			e.setDateTime(dateTime);
			filteredList.add(e);
		}
		logger.info("End: populateAndAddtoResponseBeanList() in TrendingService"); 
	}
/**
 * this method returns the end index
 * @param page
 * @param count
 * @param listSize
 * @return
 */
	private int getEndIndex(int page, int count, int listSize) {
		int endIndex = listSize;
		if (page == 0) {
			endIndex = count - 1;
		} else {
			endIndex = (page + 1) * count - 1;
		}
		if (listSize <= endIndex) {
			endIndex = listSize - 1;
		}
		return endIndex;
	}

	/**
	 * this method returns the start index
	 * @param page
	 * @param count
	 * @param listSize
	 * @return
	 */
	private int getStartIndex(int page, int count, int listSize) {
		int startIndex = 0;
		if (page == 0) {
			startIndex = 0;
		} else {
			startIndex = page * count;
		}
		return startIndex;
	}

	/**
	 * this method checks whether it is a valid pagination or not
	 * @param page
	 * @param count
	 * @param listSize
	 * @return
	 */
	private boolean isValidPagination(int page, int count, int listSize) {
		logger.info("Begin: inside isValidPagination() of TrendingService");
		boolean isValid = false;
		int numAllowedPages = 0;
		int numPages = listSize / count;
		int numLastPageRecords = listSize % count;
		if (numLastPageRecords == 0) {
			numAllowedPages = numPages;
		} else {
			numAllowedPages = numPages + 1;
		}
		if (page < numAllowedPages) {
			isValid = true;
		}
		logger.info("End: inside isValidPagination() of TrendingService: is It valid?", isValid);
		return isValid;
	}

	/**
	 * this method checks whether it is a valid Entity or not
	 * @param entityType
	 * @return
	 */
	private boolean isValidEntity(String entityType) {
		logger.info("Begin: inside isValidEntity() of TrendingService");
		boolean isValid = false;
		if (EntityType.contains(entityType)) {
			switch (EntityType.valueOf(entityType)) {
			case CONNECT:
				isValid = true;
				break;
			case OPPORTUNITY:
				isValid = true;
				break;
			case TASK:
				isValid = true;
				break;
			default:
				isValid = false;
			}
		} else {
			if (entityType.equalsIgnoreCase("all")) {
				isValid = true;
			}
		}
		logger.info("end: inside isValidEntity() of TrendingService: is valid entity?", isValid);
		return isValid;
	}

	/**
	 * this method retrieves a comment from comments repository 
	 * based on userId
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public List<EntityBean> getComment(int count) throws Exception {
		logger.info("Begin: inside getComment() of TrendingService");
		List<CollaborationCommentT> commentList;
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		commentList = commentsRepository.getComments(userId);
		List<EntityBean> filteredList = new ArrayList<EntityBean>();
		List<String> uniqueIdList = new ArrayList<String>();
		for (CollaborationCommentT comment : commentList) {
			String entityType = comment.getEntityType();
			EntityBean e = getEntityBean(comment);

			if (entityType.equalsIgnoreCase("CONNECT")) {
				String connectId = comment.getConnectId();
				if (!uniqueIdList.contains(connectId)) {
					uniqueIdList.add(connectId);
					filteredList.add(e);
				}
			} else if (entityType.equalsIgnoreCase("TASK")) {
				String taskId = comment.getTaskId();
				if (!uniqueIdList.contains(taskId)) {
					uniqueIdList.add(taskId);
					filteredList.add(e);
				}
			} else if (entityType.equalsIgnoreCase("OPPORTUNITY")) {
				String oppId = comment.getOpportunityId();
				if (!uniqueIdList.contains(oppId)) {
					uniqueIdList.add(oppId);
					filteredList.add(e);
				}
			} else {
				throw new DestinationException(
						HttpStatus.INTERNAL_SERVER_ERROR,
						"Wrong entity type encountered in DB");
			}
		}
		int size;
		if (filteredList.size() < count) {
			size = filteredList.size();
		} else {
			size = count;
		}
		logger.info("End: inside getComment() of TrendingService");
		return filteredList.subList(0, size);
	}
	
/**
 * this method returns the Entity Bean corresponding to the collaboration comment
 * @param comment
 * @return
 */
	private EntityBean getEntityBean(CollaborationCommentT comment) {
		logger.info("Begin: inside getEntityBean() of TrendingService");
		EntityBean e = new EntityBean();
		e.setDateTime(comment.getUpdatedDatetime());
		// e.setCommentId(comment.getCommentId());
		e.setConnect(comment.getConnectT());
		e.setTask(comment.getTaskT());
		e.setOpportunity(comment.getOpportunityT());
		logger.info("End: inside getEntityBean() of TrendingService");
		return e;
	}

}
