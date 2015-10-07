package com.tcs.destination.bean;

import java.io.Serializable;

public class RequestStatusDTO implements Serializable {

    private static final long serialVersionUID = 5685042932959751483L;

    private boolean statusFlag;
    
    private String message;

	public boolean isStatusFlag() {
		return statusFlag;
	}

	public void setStatusFlag(boolean statusFlag) {
		this.statusFlag = statusFlag;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

    
}
