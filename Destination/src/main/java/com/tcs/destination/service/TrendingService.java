package com.tcs.destination.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

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

@Component
public class TrendingService {
	
	private static final Logger logger = LoggerFactory.getLogger(TrendingService.class);

	@Autowired
	OpportunityRepository oppRepository;

	@Autowired
	ConnectRepository connectRepository;

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	CollaborationCommentsRepository commentsRepository;

	public List<OpportunityT> findtrendingOpportunities(int count) {
		List<OpportunityT> tempList;
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		tempList = oppRepository.findTrendingOpportunities(userId);
		List<OpportunityT> oppList;
		if (tempList.size() >= count) {
			oppList = tempList.subList(0, count);
		} else {
			oppList = tempList;
		}
		return oppList;
	}

	public TimelineResponse getDistinctComment(Timestamp timestamp, int count,
			String entityType) throws Exception {
		
		logger.info("Page Token(timestamp) : " + timestamp + " , records to fetch : " + count + " , entityType");
		TimelineResponse response = new TimelineResponse();

		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		List<EntityBean> filteredList = new ArrayList<EntityBean>();
		List<Object[]> entityIdList = commentsRepository
				.getDistinctComments(userId, timestamp,count);
		
		if (entityIdList != null && entityIdList.size() != 0) {
			int listSize = entityIdList.size();
			logger.info("Distinct Comments Retrieved Size : " + listSize + " for token: " + timestamp.toString());
			for (Object[] item : entityIdList) {
				String commentId = (String) item[0];
				String entityTypeRetrieved = (String) item[1];
				String entityId = (String) item[2];
				Timestamp dateTime = (Timestamp) item[4];
				populateAndAddtoResponseBeanList(filteredList,
						entityTypeRetrieved, entityId, commentId,dateTime);
			}
		} else {
			logger.info("Datalist empty");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Relevant Data found");
		}

		response.setBean(filteredList);
		int responseListSize = filteredList.size() - 1;
        EntityBean lastItem = filteredList.get(responseListSize);
		response.setToken(lastItem.getDateTime());
		logger.info("Page Token sent : " + response.getToken());
		return response;
	}

	private void populateAndAddtoResponseBeanList(
			List<EntityBean> filteredList, String entityTypeRetrieved,
			String entityId, String commentId,Timestamp dateTime) {
		EntityBean e = new EntityBean();
		e.setCommentId(commentId);
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

	private int getStartIndex(int page, int count, int listSize) {
		int startIndex = 0;
		if (page == 0) {
			startIndex = 0;
		} else {
			startIndex = page * count;
		}
		return startIndex;
	}

	private boolean isValidPagination(int page, int count, int listSize) {
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
		return isValid;
	}

	private boolean isValidEntity(String entityType) {
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

		return isValid;
	}

	public List<EntityBean> getComment(int count) throws Exception {
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
		return filteredList.subList(0, size);
	}

	private EntityBean getEntityBean(CollaborationCommentT comment) {
		EntityBean e = new EntityBean();
		e.setDateTime(comment.getUpdatedDatetime());
		// e.setCommentId(comment.getCommentId());
		e.setConnect(comment.getConnectT());
		e.setTask(comment.getTaskT());
		e.setOpportunity(comment.getOpportunityT());
		return e;
	}

}
