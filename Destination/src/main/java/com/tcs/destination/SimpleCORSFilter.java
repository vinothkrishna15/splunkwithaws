/**
 * 
 * SimpleCORSFilter.java 
 *
 * @author TCS
 * @Version 1.0 - 2015
 * 
 * @Copyright 2015 Tata Consultancy 
 */
package com.tcs.destination;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * This SimpleCORSFilter class filters the request for the acceptable parameters for the services
 * 
 */
@Component
public class SimpleCORSFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(SimpleCORSFilter.class);
	
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods",
				"POST, GET, OPTIONS, DELETE, PUT");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "accept,authorization,x-requested-with,Content-Type,APP_VERSION,x-auth-token");
		response.setHeader("Access-Control-Expose-Headers","x-auth-token,Date");
		DestinationHttpRequestWrapper myRequestWrapper = logRequest(req);
		if(myRequestWrapper!=null)
			chain.doFilter(myRequestWrapper, res);
		else
			chain.doFilter(req, res);
	}

	private DestinationHttpRequestWrapper logRequest(ServletRequest req) throws ServletException {
		final HttpServletRequest httpServletRequest = (HttpServletRequest) req;
		
		MDC.remove("userId");
		MDC.remove("sessionId"); 

		logger.info("======REQUEST DETAILS======");
		logger.info("Timestamp : " + new Date());
		logger.info("URL : " + getUrlWithParams(httpServletRequest));
		logger.info("Method : " + httpServletRequest.getMethod());

		// Get the current HttpSession, do not create one
		HttpSession session = httpServletRequest.getSession(false);
		
		// Check if the Login Service has Session populated
		if (httpServletRequest.getRequestURL().indexOf("/user/login") > 0) {
			if (session != null) {
				logger.error("Session should not be passed for User Login service : " + 
						session.getId());
				throw new ServletException ("Session should not be passed for User Login service");
			}
		} else {
			// Check if other services has valid session populated
/*			if (session == null) {
				logger.error("Valid session is required");
				throw new ServletException("Valid session is required");
			} else {
				if (session.isNew()) {
					logger.error("Invalid session requested : " + session.getId());
					throw new ServletException("Invalid session requested");
				}
*/		    if (session != null) {
				logger.info("SessionId : " + session.getId());
				MDC.put("sessionId", session.getId());
			}
		}
		
		DestinationHttpRequestWrapper myRequestWrapper = new DestinationHttpRequestWrapper(httpServletRequest);
		try {
			ServletInputStream inputStream = myRequestWrapper.getInputStream();
			String postStr = convertStreamToString(inputStream);
			if (postStr != null && !postStr.trim().isEmpty()) {
				logger.info("POST Data");
				logger.info(postStr);
			}
			
	    } catch(Exception e){
	    	logger.error("Cannot read the request details");
	    }
		return myRequestWrapper;
	}

	private StringBuffer getUrlWithParams(final HttpServletRequest httpRequest) {
		StringBuffer baseUrl = httpRequest.getRequestURL();
		//logger.info("base url = " + baseUrl);
		Map<String, String[]> parameterMap = httpRequest.getParameterMap();
		if(parameterMap.size() != 0){
			baseUrl.append("?");
			for (Map.Entry<String, String[]> entry : parameterMap.entrySet())
			{
				StringBuffer val = new StringBuffer("");
				for( String str : entry.getValue()){
					val.append(str);
				}
				baseUrl.append(entry.getKey() + "=" + val + "&");
			}
			baseUrl.deleteCharAt(baseUrl.lastIndexOf("&"));
		}
		return baseUrl;
	}

	public void init(FilterConfig filterConfig) {
	}

	public void destroy() {
	}
	
	public String convertStreamToString(InputStream is)
            throws IOException {
        //
        // To convert the InputStream to String we use the
        // Reader.read(char[] buffer) method. We iterate until the
        // Reader return -1 which means there's no more data to
        // read. We use the StringWriter class to produce the string.
        //
        if (is != null) {
            Writer writer = new StringWriter();
 
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {        
            return "";
        }
    }

}