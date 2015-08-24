package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.Map;

public class ContactTMapDTO implements Serializable {

    private static final long serialVersionUID = 6057173048606705149L;

    Map<String, String> mapOfCustomerContactT;
    
    Map<String, String> mapOfTcsContactT;

    public Map<String, String> getMapOfCustomerContactT() {
        return mapOfCustomerContactT;
    }

    public void setMapOfCustomerContactT(Map<String, String> mapOfCustomerContactT) {
        this.mapOfCustomerContactT = mapOfCustomerContactT;
    }

    public Map<String, String> getMapOfTcsContactT() {
        return mapOfTcsContactT;
    }

    public void setMapOfTcsContactT(Map<String, String> mapOfTcsContactT) {
        this.mapOfTcsContactT = mapOfTcsContactT;
    }

    
}
