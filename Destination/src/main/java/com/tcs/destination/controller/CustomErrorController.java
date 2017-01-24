package com.tcs.destination.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.tcs.destination.bean.dto.ErrorJson;
import com.tcs.destination.exception.DestinationException;

/**
 * Controller to handle city based search requests
 * 
 */
@RestController
public class CustomErrorController implements ErrorController {
	private static final Logger logger = LoggerFactory
			.getLogger(CustomErrorController.class);

    private static final String PATH = "/error";
    public static String KEY_THROWABLE = "throwable";

    @Autowired
    private ErrorAttributes errorAttributes;

    @RequestMapping(value = PATH)
    ErrorJson error(HttpServletRequest request, HttpServletResponse response) {
        // Appropriate HTTP response code (e.g. 404 or 500) is automatically set by Spring. 
        // Here we just define response body.
    	logger.info("CustomErrorController error method : start");
    	int status = response.getStatus();
        Map<String, Object> errAttr = getErrorAttributes(request, false);
        Throwable error = (Throwable)errAttr.get(KEY_THROWABLE);
        if(error != null && error instanceof DestinationException) {
        	logger.info("CustomErrorController:: error : DestinationException");
        	status = ((DestinationException)error).getHttpStatus().value();
        	response.setStatus(status);
        } 
		return new ErrorJson(status, errAttr);
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
    	RequestAttributes requestAttributes = new ServletRequestAttributes(request);
    	Map<String, Object> errAttr = errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);

    	Throwable error = new DefaultErrorAttributes().getError(requestAttributes);
    	if(error != null) {
    		errAttr.put(KEY_THROWABLE, error);
    	}
    	return errAttr;
    }

}
