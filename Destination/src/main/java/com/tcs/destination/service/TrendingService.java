package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.CollaborationCommentT;
import com.tcs.destination.bean.EntityBean;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.data.repository.CollaborationCommentsRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.DestinationUtils;

@Component
public class TrendingService {

	@Autowired 
	OpportunityRepository oppRepository;
	
	@Autowired 
	CollaborationCommentsRepository commentsRepository;
	
	public List<OpportunityT> findtrendingOpportunities(int count){
		List<OpportunityT> tempList;
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		tempList = oppRepository.findTrendingOpportunities(userId);
		List<OpportunityT> oppList;
		if(tempList.size() >= count) {
		 oppList = tempList.subList(0, count);
		} else {
		 oppList = tempList;
		}
		return oppList;
	}
	
	
	public List<EntityBean> getDistinctComment( int count) throws Exception{
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		//Pageable pageable=new PageRequest(page, count);
		List<CollaborationCommentT> commentList = commentsRepository.getDistinctComments(userId);
		List<EntityBean> filteredList = new ArrayList<EntityBean>();
		for(CollaborationCommentT comment : commentList){
			 filteredList.add(getEntityBean(comment));
		}
		return filteredList;
	}
	
	
	public List<EntityBean> getComment(int count) throws Exception{
		List<CollaborationCommentT> commentList;
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		commentList = commentsRepository.getComments(userId);
		List<EntityBean> filteredList = new ArrayList<EntityBean>();
		List<String> uniqueIdList = new ArrayList<String>();
		for(CollaborationCommentT comment : commentList){
           String entityType = comment.getEntityType();
           EntityBean e = getEntityBean(comment);
           
           if(entityType.equalsIgnoreCase("CONNECT")){
        	   String connectId=comment.getConnectId();
        	   if(!uniqueIdList.contains(connectId)){
        		   uniqueIdList.add(connectId);
        		   filteredList.add(e);
        	   }
           } else if(entityType.equalsIgnoreCase("TASK")){
        	   String taskId=comment.getTaskId();
        	   if(!uniqueIdList.contains(taskId)){
        		   uniqueIdList.add(taskId);
        		   filteredList.add(e);
        	   }
           } else if(entityType.equalsIgnoreCase("OPPORTUNITY")){
        	   String oppId=comment.getOpportunityId();
        	   if(!uniqueIdList.contains(oppId)){
        		   uniqueIdList.add(oppId);
        		   filteredList.add(e);
        	   }
           } else {
        	   throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,"Wrong entity type encountered in DB");
           }
		}
		int size;
		if(filteredList.size()<count){
			size = filteredList.size();
		} else {
			size = count;
		}
		return filteredList.subList(0, size);
	}

	private EntityBean getEntityBean(CollaborationCommentT comment) {
		EntityBean e = new EntityBean();
		e.setDateTime(comment.getUpdatedDatetime());
		e.setCommentId(comment.getCommentId());
		e.setConnect(comment.getConnectT());
		e.setTask(comment.getTaskT());
		e.setOpportunity(comment.getOpportunityT());
		return e;
	}
	
	
}
