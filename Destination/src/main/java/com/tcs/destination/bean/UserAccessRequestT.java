package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;


/**
 * The persistent class for the user_access_request_t database table.
 * 
 */
@Entity
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "reqId")
@Table(name="user_access_request_t")
@NamedQuery(name="UserAccessRequestT.findAll", query="SELECT u FROM UserAccessRequestT u")
public class UserAccessRequestT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="request_id")
	private String requestId;

	@Column(name="approved_rejected_datetime")
	private Timestamp approvedRejectedDateTime;

	@Column(name="approved_rejected_comments")
	private String approvedRejectedComments;

	@Column(name="reason_for_request")
	private String reasonForRequest;

	@Column(name="request_received_datetime")
	private Timestamp requestReceivedDateTime;

	@Column(name="supervisor_email_id")
	private String supervisorEmailId;

	@Column(name="user_email_id")
	private String userEmailId;

	@Column(name="user_id")
	private String userId;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JsonIgnore
	@JoinColumn(name="supervisor_id",insertable=false,updatable=false)
	private UserT userTSupervisorId;
	
	@Column(name="supervisor_id")
    private String supervisorId;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JsonIgnore
	@JoinColumn(name="approved_rejected_by",insertable=false,updatable=false)
	private UserT userTApprovedRejectedBy;
	
	@Column(name="approved_rejected_by")
	private String approvedRejectedBy;

	public UserAccessRequestT() {
	}

	public String getRequestId() {
		return this.requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Timestamp getApprovedRejectedDate() {
		return this.approvedRejectedDateTime;
	}

	public void setApprovedRejectedDate(Timestamp approvedRejectedDateTime) {
		this.approvedRejectedDateTime = approvedRejectedDateTime;
	}

	public String getApprovedRejectedComments() {
		return this.approvedRejectedComments;
	}

	public void setApprovedRejectedComments(String approvedRejectedComments) {
		this.approvedRejectedComments = approvedRejectedComments;
	}

	public String getReasonForRequest() {
		return this.reasonForRequest;
	}

	public void setReasonForRequest(String reasonForRequest) {
		this.reasonForRequest = reasonForRequest;
	}

	public Timestamp getRequestReceivedDateTime() {
		return this.requestReceivedDateTime;
	}

	public void setRequestReceivedDateTime(Timestamp requestReceivedDateTime) {
		this.requestReceivedDateTime = requestReceivedDateTime;
	}

	public String getSupervisorEmailId() {
		return this.supervisorEmailId;
	}

	public void setSupervisorEmailId(String supervisorEmailId) {
		this.supervisorEmailId = supervisorEmailId;
	}

	public String getUserEmailId() {
		return this.userEmailId;
	}

	public void setUserEmailId(String userEmailId) {
		this.userEmailId = userEmailId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public UserT getUserTApprovedRejectedBy() {
		return this.userTApprovedRejectedBy;
	}

	public void setUserTApprovedBy(UserT userTApprovedRejectedBy) {
		this.userTApprovedRejectedBy = userTApprovedRejectedBy;
	}

	public UserT getUserTSupervisorId() {
		return this.userTSupervisorId;
	}

	public void setUserTSupervisorId(UserT userTSupervisorId) {
		this.userTSupervisorId = userTSupervisorId;
	}

	public String getSupervisorId() {
		return supervisorId;
	}

	public void setSupervisorId(String supervisorId) {
		this.supervisorId = supervisorId;
	}

	public String getApprovedRejectedBy() {
		return approvedRejectedBy;
	}

	public void setApprovedRejectedBy(String approvedRejectedBy) {
		this.approvedRejectedBy = approvedRejectedBy;
	}

}