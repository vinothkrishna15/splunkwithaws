package com.tcs.destination.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class FieldValidator {

	//user fields
	public static final String UserT_userId = "UserT_userId";
	public static final String UserT_userName = "UserT_userName";
	public static final String UserT_tempPassword= "UserT_tempPassword";
	public static final String UserT_baseLocation = "UserT_baseLocation";
	public static final String UserT_supervisorUserId = "UserT_supervisorUserId";
	public static final String UserT_supervisorUserName = "UserT_supervisorUserName";
	public static final String UserT_userTelephone = "UserT_userTelephone";
	public static final String UserT_userEmailId = "UserT_userEmailId";
	public static final String UserT_userRole = "UserT_userRole";
	public static final String UserT_userGroup = "UserT_userGroup";
	
	//user privileges fields
	public static final String User_PrivilegesT_UserId = "User_PrivilegesT_UserId";
	public static final String User_PrivilegesT_ParentType = "User_PrivilegesT_ParentType";
	public static final String User_PrivilegesT_ParentValues = "User_PrivilegesT_ParentValues";
	public static final String User_PrivilegesT_ChildType = "User_PrivilegesT_ChildType";
	public static final String User_PrivilegesT_ChildValues = "User_PrivilegesT_ChildValues";
	
	
	//user general settings fields
	public static final String User_General_SettingsT_timeZoneDesc = "User_General_SettingsT_timeZoneDesc";
	
	
	private static final Map<String,Integer>FIELD_LENGTH_MAP;
	public static final Map<String,Integer>FIELD_INDEX_MAP;
	
	
	
	static{
	  Map<String,Integer> fieldLengthMap = new HashMap<String,Integer>();
	  fieldLengthMap.put(UserT_userId,10);
	  fieldLengthMap.put(UserT_userName, 50);
	  fieldLengthMap.put(UserT_tempPassword, 20);
	  fieldLengthMap.put(UserT_baseLocation, 50);
	  fieldLengthMap.put(UserT_supervisorUserId, 10);
	  fieldLengthMap.put(UserT_supervisorUserName, 50);
	  fieldLengthMap.put(UserT_userTelephone,20);
	  fieldLengthMap.put(UserT_userEmailId,60);
	  fieldLengthMap.put(UserT_userRole, 30);
	  fieldLengthMap.put(UserT_userGroup, 21);
	  fieldLengthMap.put(User_General_SettingsT_timeZoneDesc, 70);
	  fieldLengthMap.put(User_PrivilegesT_UserId, 10);
	  fieldLengthMap.put(User_PrivilegesT_ParentType, 30);
	  fieldLengthMap.put(User_PrivilegesT_ParentValues, 100);
	  fieldLengthMap.put(User_PrivilegesT_ChildType, 30);
	  fieldLengthMap.put(User_PrivilegesT_ChildValues, 100);
	 
	  FIELD_LENGTH_MAP = Collections.unmodifiableMap(fieldLengthMap);
	  Map<String,Integer> fieldIndexMap = new HashMap<String,Integer>();
	  fieldIndexMap.put(UserT_userId,1);
	  fieldIndexMap.put(UserT_userName, 2);
	  fieldIndexMap.put(UserT_tempPassword, 3);
	  fieldIndexMap.put(UserT_baseLocation, 6);
	  fieldIndexMap.put(UserT_supervisorUserId, 10);
	  fieldIndexMap.put(UserT_supervisorUserName, 11);
	  fieldIndexMap.put(User_General_SettingsT_timeZoneDesc, 7);
	  fieldIndexMap.put(UserT_userTelephone,8);
	  fieldIndexMap.put(UserT_userEmailId,9);
	  fieldIndexMap.put(UserT_userRole, 5);
	  fieldIndexMap.put(UserT_userGroup, 4);
	  fieldIndexMap.put(User_PrivilegesT_UserId, 1);
	  fieldIndexMap.put(User_PrivilegesT_ParentType, 4);
	  fieldIndexMap.put(User_PrivilegesT_ParentValues, 5);
	  fieldIndexMap.put(User_PrivilegesT_ChildType, 6);
	  fieldIndexMap.put(User_PrivilegesT_ChildValues, 7);
	  
	  FIELD_INDEX_MAP = Collections.unmodifiableMap(fieldIndexMap);
	}
	
	
	
	public static List<String> validate(String key,String value,boolean isMandatory){
		List<String> error = new ArrayList<String>();
		if(StringUtils.isEmpty(value)){
			if(isMandatory){
			 error.add("Missing value for " + key);
			}
		}else if(isExceedsLength(key,value)){
			error.add(key + " length exceeds " + value);
		}
		return error;
	}
	
	public static boolean isExceedsLength(String key,String value){
		if(value.length() > FIELD_LENGTH_MAP.get(key)){
			return true;
		}
		return false;
	}
	
	
	
}
