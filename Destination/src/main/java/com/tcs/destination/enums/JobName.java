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
	
		connectUpload("connectUpload"),
		customerUpload("customerUpload"),
		opportunityUpload("opportunityUpload"),
		revenueUpload("revenueUpload"),
		partnerUpload("partnerUpload"),
		partnerContactUpload("partnerContactUpload"),
		beaconUpload("beaconUpload"),
        userUpload("userUpload"),
        customerContactUpload("customerContactUpload"),
		opportunityWonLostEmailNotification("opportunityWonLostEmailNotification");

		
		private String job;
		
		@JsonProperty("job")
		public String getJob() {
			return job;
		}
		
		private JobName(String job) {
			this.job = job;
		}

}
