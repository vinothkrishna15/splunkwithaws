package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;
import java.math.BigDecimal;


/**
 * The persistent class for the data_processing_request_t database table.
 * 
 */
@Entity
@Table(name="data_processing_request_t")
@NamedQuery(name="DataProcessingRequestT.findAll", query="SELECT d FROM DataProcessingRequestT d")
public class DataProcessingRequestT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="process_request_id")
	private Long processRequestId;

	@Column(name="file_name")
	private String fileName;

	@Column(name="file_path")
	private String filePath;
	
	@Column(name="error_file_name")
	private String errorFileName;

	@Column(name="error_file_path")
	private String errorFilePath;

	@Column(name="modified_datetime")
	private Timestamp modifiedDatetime;

	@Column(name="request_type")
	private int requestType;

	private int status;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="user_id")
	private UserT userT;

	public DataProcessingRequestT() {
	}

	public Long getProcessRequestId() {
		return this.processRequestId;
	}

	public void setProcessRequestId(Long processRequestId) {
		this.processRequestId = processRequestId;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return this.filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Timestamp getModifiedDatetime() {
		return this.modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}

	public int getRequestType() {
		return requestType;
	}

	public void setRequestType(int requestType) {
		this.requestType = requestType;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public UserT getUserT() {
		return this.userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

	public String getErrorFileName() {
		return errorFileName;
	}

	public void setErrorFileName(String errorFileName) {
		this.errorFileName = errorFileName;
	}

	public String getErrorFilePath() {
		return errorFilePath;
	}

	public void setErrorFilePath(String errorFilePath) {
		this.errorFilePath = errorFilePath;
	}
	

}