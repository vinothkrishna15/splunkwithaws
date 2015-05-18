package com.tcs.destination;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class SimpleCORSFilter implements Filter {

	static Logger logger = Logger.getLogger(SimpleCORSFilter.class);
	
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods",
				"POST, GET, OPTIONS, DELETE,PUT");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "accept,authorization,x-requested-with,Content-Type");
		DestinationHttpRequestWrapper myRequestWrapper = logRequest(req);
		if(myRequestWrapper!=null)
			chain.doFilter(myRequestWrapper, res);
		else
			chain.doFilter(req, res);
	}

	private DestinationHttpRequestWrapper logRequest(ServletRequest req)  {
		final HttpServletRequest httpRequest = (HttpServletRequest) req;
		logger.info("======REQUEST DETAILS======");
		logger.info("Timestamp : " + new Date());
		logger.info("URL : " + getUrlWithParams(httpRequest));
		logger.info("Method : " + httpRequest.getMethod());
		DestinationHttpRequestWrapper myRequestWrapper = new DestinationHttpRequestWrapper((HttpServletRequest) httpRequest);
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