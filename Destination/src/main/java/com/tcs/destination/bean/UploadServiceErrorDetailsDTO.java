package com.tcs.destination.bean;

import java.io.Serializable;

public class UploadServiceErrorDetailsDTO implements Serializable{

    private static final long serialVersionUID = 8124830922686791666L;
    
    private Integer rowNumber;
    
    private String message;

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
        this.message = message;
    }

}
