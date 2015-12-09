/**
 * 
 * JobName.java 
 *
 * @author TCS
 * @Version 1.0 - 2015
 * 
 * @Copyright 2015 Tata Consultancy 
 */
package com.tcs.destination.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.tcs.destination.mapper.JobNameDeserializer;

/**
 * This JobName enum contains the jobs names in the application
 * 
 */
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
@JsonDeserialize(using=JobNameDeserializer.class)
public enum JobName {
	
		userRemindersBidAndTaskNrDue("userRemindersBidAndTaskNrDue"),
		userRemindersBidAndTaskPostDue("userRemindersBidAndTaskPostDue"),
		userRemindersBidAndTskPstDueSupervisor("userRemindersBidAndTskPstDueSupervisor"),
		userRemindersConnectUpdate("userRemindersConnectUpdate"),
		partnerUpload("partnerUpload"),
		connectUpload("connectUpload"),
		customerUpload("customerUpload"),
		customerDownload("customerDownload"),
		revenueUpload("revenueUpload"),
		emailJob("emailJob"),
		maintenanceJob("maintenanceJob"),
		userDownloadJob("userDownloadJob"),
		actualRevenueDownload("actualRevenueDownload"),
		opportunityDownload("opportunityDownload"),
		opportunityDailyDownload("opportunityDailyDownload");
		
		private String value;
		
		private JobName(String value) {
			this.value = value;
		}
		
		@JsonProperty("value")
		public String getValue() {
			return value;
		}
		
		public void setValue(String value) {
			this.value = value;
		}

		
}
