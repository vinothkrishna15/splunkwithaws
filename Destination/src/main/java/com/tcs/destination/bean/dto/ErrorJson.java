package com.tcs.destination.bean.dto;

import java.util.Map;

public class ErrorJson {

	public Integer status;
    public String error;
    public String message;
    public String timeStamp;
    public String trace;
    public String path;
    

    public ErrorJson(int status, Map<String, Object> errorAttributes) {
        this.status = status;
        this.error = (String) errorAttributes.get("error");
        this.message = (String) errorAttributes.get("message");
        this.timeStamp = errorAttributes.get("timestamp").toString();
        this.trace = (String) errorAttributes.get("trace");//----
        this.path = (String) errorAttributes.get("path");
        //exception
    }
}
