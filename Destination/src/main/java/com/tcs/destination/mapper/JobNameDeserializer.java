/**
 * 
 * JobNameDeserializer.java 
 *
 * @author TCS
 * @Version 1.0 - 2015
 * 
 * @Copyright 2015 Tata Consultancy 
 */
package com.tcs.destination.mapper;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.tcs.destination.enums.JobName;

/**
 * This JobNameDeserializer class hold the functionality to deserialize the enum
 * 
 */
public class JobNameDeserializer extends JsonDeserializer<JobName>{
	
	public JobName deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException
	{
	    final String jsonValue = parser.getText();
	    JobName returnValue = null;
	    for (final JobName value : JobName.values())
	    {
	        if (value.getValue().equals(jsonValue))
	        {
	        	returnValue =  value;
	        }
	    }
	    return returnValue;
	}

	

}
