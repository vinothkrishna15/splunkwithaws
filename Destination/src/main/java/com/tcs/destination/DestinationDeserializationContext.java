package com.tcs.destination;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
import com.fasterxml.jackson.databind.deser.DeserializerCache;
import com.fasterxml.jackson.databind.deser.DeserializerFactory;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;

public final class DestinationDeserializationContext extends
		DefaultDeserializationContext {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor for a blueprint object, which will use the standard
	 * {@link DeserializerCache}, given factory.
	 */
	public DestinationDeserializationContext(DeserializerFactory df) {
		super(df, null);
	}

	protected DestinationDeserializationContext(
			DestinationDeserializationContext src,
			DeserializationConfig config, JsonParser jp, InjectableValues values) {
		super(src, config, jp, values);
	}

	protected DestinationDeserializationContext(
			DestinationDeserializationContext src) {
		super(src);
	}

	protected DestinationDeserializationContext(
			DestinationDeserializationContext src, DeserializerFactory factory) {
		super(src, factory);
	}

	@Override
	public DefaultDeserializationContext copy() {
		if (getClass() != DestinationDeserializationContext.class) {
			return super.copy();
		}
		return new DestinationDeserializationContext(this);
	}

	@Override
	public DefaultDeserializationContext createInstance(
			DeserializationConfig config, JsonParser jp, InjectableValues values) {
		System.out.println("DefaultDeserializationContext");
		return new DestinationDeserializationContext(this, config, jp, values);
	}

	@Override
	public DefaultDeserializationContext with(DeserializerFactory factory) {
		return new DestinationDeserializationContext(this, factory);
	}

	@Override
	public ReadableObjectId findObjectId(Object id, ObjectIdGenerator<?> gen,
			ObjectIdResolver resolverType) {
		System.out.println("Object ID is set");
		return new ReadableObjectId(id);
	}
	
}