package com.tcs.destination.bean;

import java.util.List;

public class DestinationMailMessage {

	private List<String> recipientIdList;
	private List<String> ccList;
	private List<String> bccList;
	private String senderEmail;
	private String messageType;
	private String subject;
	private String message;
	
	private String atchFileName;
	private String atchFilePath;
	
	public List<String> getRecipients() {
		return recipientIdList;
	}
	public void setRecipients(List<String> recipientIdList) {
		this.recipientIdList = recipientIdList;
	}
	public List<String> getCcList() {
		return ccList;
	}
	public void setCcList(List<String> ccList) {
		this.ccList = ccList;
	}
	public List<String> getBccList() {
		return bccList;
	}
	public void setBccList(List<String> bccList) {
		this.bccList = bccList;
	}
	public String getSenderEmail() {
		return senderEmail;
	}
	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<String> getRecipientIdList() {
		return recipientIdList;
	}
	public void setRecipientIdList(List<String> recipientIdList) {
		this.recipientIdList = recipientIdList;
	}
	public String getAtchFileName() {
		return atchFileName;
	}
	public void setAtchFileName(String atchFileName) {
		this.atchFileName = atchFileName;
	}
	public String getAtchFilePath() {
		return atchFilePath;
	}
	public void setAtchFilePath(String atchFilePath) {
		this.atchFilePath = atchFilePath;
	}
	
}
