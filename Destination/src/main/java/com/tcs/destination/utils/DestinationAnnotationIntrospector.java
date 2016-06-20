package com.tcs.destination.utils;

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;

public class DestinationAnnotationIntrospector extends JacksonAnnotationIntrospector {

	private static final long serialVersionUID = 1L;

	@Override
	public ObjectIdInfo findObjectIdInfo(Annotated ann) {
		//Return null to ignore jsonIdentityInfo annotation
		return null;
	}
	
	@Override
	public ObjectIdInfo findObjectReferenceInfo(Annotated ann,
			ObjectIdInfo objectIdInfo) {
		//Return null to ignore jsonIdentityInfo annotation
		return null;
	}
	
}
