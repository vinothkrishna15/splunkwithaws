package com.tcs.destination.bean;

import java.io.Serializable;

import com.tcs.destination.utils.StringUtils;

public class UploadServiceErrorDetailsDTO implements Serializable{

    private static final long serialVersionUID = 8124830922686791666L;
    
    private String sheetName;
    
    private Integer rowNumber;
    
    private String message;
    
    private int duplicateFlag;

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
    	if (!StringUtils.isEmpty(this.message)) {
    		this.message = this.message.concat(message);
    	} else {
    		this.message = message;
    	}
        
    }

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public int getDuplicateFlag() {
		return duplicateFlag;
	}

	public void setDuplicateFlag(int duplicateFlag) {
		this.duplicateFlag = duplicateFlag;
	}

}
