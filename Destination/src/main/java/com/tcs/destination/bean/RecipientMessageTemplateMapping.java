package com.tcs.destination.bean;

import java.util.List;

/**
 * This class used for mapping the users and the message templates for notification
 * @author TCS
 *
 */
public class RecipientMessageTemplateMapping {

	private List<String> templates;
	
	private List<String> users;

	public List<String> getTemplates() {
		return templates;
	}

	public void setTemplates(List<String> templates) {
		this.templates = templates;
	}

	public List<String> getUsers() {
		return users;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}
	
	
	
}
