package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

public class UploadStatusDTO implements Serializable {

    private static final long serialVersionUID = 5685042932959751483L;

    private boolean statusFlag;
    
    private List<UploadServiceErrorDetailsDTO> listOfErrors;

    public List<UploadServiceErrorDetailsDTO> getListOfErrors() {
        return listOfErrors;
    }

    public void setListOfErrors(List<UploadServiceErrorDetailsDTO> listOfErrors) {
        this.listOfErrors = listOfErrors;
    }

    public boolean isStatusFlag() {
        return statusFlag;
    }

    public void setStatusFlag(boolean statusFlag) {
        this.statusFlag = statusFlag;
    }
    
}
