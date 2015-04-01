package com.tcs.destination;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcs.destination.controller.UserRepositoryUserDetailsService;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Helper methods for dealing with JSON in unit tests.
 * 
  */
public class TestUtil {

	public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	/**
	 * Serializes the given object.
	 * @param object
	 * @return
	 * @throws IOException
	 */
	public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return mapper.writeValueAsBytes(object);
	}
	
	public static void setAuthToken(UserRepositoryUserDetailsService userDetailsService) {
		UserDetails userDetails = userDetailsService.loadUserByUsername("aaa");
		Authentication authToken = new UsernamePasswordAuthenticationToken(
				userDetails, userDetails.getPassword(),
				userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authToken);
	}

	public static String getJsonString(String loc) throws Exception{
		File resourcesDirectory = new File(loc);
		String fullPath = resourcesDirectory.getAbsolutePath();
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(new FileReader(fullPath));
		JSONObject jsonObject = (JSONObject) obj;
		return jsonObject.toString();
	}
	
}