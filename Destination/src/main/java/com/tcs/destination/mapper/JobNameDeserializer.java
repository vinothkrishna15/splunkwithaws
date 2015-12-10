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
import com.fasterxml.jackson.databind.JsonNode;
import com.tcs.destination.enums.JobName;

/**
 * This JobNameDeserializer class hold the functionality to deserialize the enum
 * 
 */
public class JobNameDeserializer extends JsonDeserializer<JobName>{
	
	public JobName deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException
	{
		
		 JsonNode node = parser.getCodec().readTree(parser);
         String job = node.get("job").asText();
         
         for (JobName jobName: JobName.values()) {
             if (jobName.getJob().equals(job)) {
                 return jobName;
             }
         }
         throw new IllegalArgumentException("Invalid job name: " + job); 
	 
	}

	

}
