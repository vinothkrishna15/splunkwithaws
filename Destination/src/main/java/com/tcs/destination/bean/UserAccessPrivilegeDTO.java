package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

public class UserAccessPrivilegeDTO implements Serializable {

    private static final long serialVersionUID = 5685042932959751483L;

    private String userId;
    
    private Integer privilegeId;
    
    private Integer parentPrivilegeId;
    
    private String primaryPrivilegeType;
    
    private List<String> primaryPrivilegeValues;
    
    private String secondaryPrivilegeType;
    
    private List<String> secondaryPrivilegeValues;
    
    private String isactive;
    
    private List<UploadServiceErrorDetailsDTO> listOfErrors;

     public List<UploadServiceErrorDetailsDTO> getListOfErrors() {
        return listOfErrors;
     }

     public void setListOfErrors(List<UploadServiceErrorDetailsDTO> listOfErrors) {
        this.listOfErrors = listOfErrors;
     }
    
     public String getUserId() {
		return userId;
	 }

	 public void setUserId(String userId) {
		this.userId = userId;
	 }

	 public Integer getPrivilegeId() {
		return privilegeId;
	 }

	 public void setPrivilegeId(Integer privilegeId) {
		this.privilegeId = privilegeId;
	 }

	 public Integer getParentPrivilegeId() {
		return parentPrivilegeId;
	 }

	 public void setParentPrivilegeId(Integer parentPrivilegeId) {
		this.parentPrivilegeId = parentPrivilegeId;
	 }

	 public String getIsactive() {
		return isactive;
	 }

	 public void setIsactive(String isactive) {
		this.isactive = isactive;
	 }
    
	 public String getPrimaryPrivilegeType() {
			return primaryPrivilegeType;
		}

	 public void setPrimaryPrivilegeType(String primaryPrivilegeType) {
			this.primaryPrivilegeType = primaryPrivilegeType;
		}

	 public List<String> getPrimaryPrivilegeValues() {
			return primaryPrivilegeValues;
		}

	 public void setPrimaryPrivilegeValues(List<String> primaryPrivilegeValues) {
			this.primaryPrivilegeValues = primaryPrivilegeValues;
		}

	 public String getSecondaryPrivilegeType() {
			return secondaryPrivilegeType;
		}

	 public void setSecondaryPrivilegeType(String secondaryPrivilegeType) {
			this.secondaryPrivilegeType = secondaryPrivilegeType;
		}

	 public List<String> getSecondaryPrivilegeValues() {
			return secondaryPrivilegeValues;
		}

	 public void setSecondaryPrivilegeValues(List<String> secondaryPrivilegeValues) {
			this.secondaryPrivilegeValues = secondaryPrivilegeValues;
		}
 }
