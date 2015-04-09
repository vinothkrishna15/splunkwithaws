package com.tcs.destination.bean;

import java.sql.Timestamp;

public class EntityBean {

	private String commentId;
	private Timestamp dateTime;
	private OpportunityT opportunity;
	private TaskT task;
	private ConnectT connect;
	
	
	public String getCommentId() {
		return commentId;
	}
	public void setCommentId(String commentId) {
		this.commentId = commentId;
	}
	public OpportunityT getOpportunity() {
		return opportunity;
	}
	public void setOpportunity(OpportunityT opportunity) {
		this.opportunity = opportunity;
	}
	public TaskT getTask() {
		return task;
	}
	public void setTask(TaskT task) {
		this.task = task;
	}
	public ConnectT getConnect() {
		return connect;
	}
	public void setConnect(ConnectT connect) {
		this.connect = connect;
	}
	public Timestamp getDateTime() {
		return dateTime;
	}
	public void setDateTime(Timestamp dateTime) {
		this.dateTime = dateTime;
	}
	
	
	
	
}
