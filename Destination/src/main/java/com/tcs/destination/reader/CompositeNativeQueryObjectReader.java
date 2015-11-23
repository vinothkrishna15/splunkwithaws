package com.tcs.destination.reader;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class CompositeNativeQueryObjectReader implements ItemReader<List<Object>> {
	
	private static final Log logger = LogFactory.getLog(CompositeNativeQueryObjectReader.class);
	
	List<NativeQueryObjectReader> readers;


	@Override
	public List<Object> read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {
		
		logger.debug("Inside read method:");
		
		List<Object> list = new ArrayList(readers.size());
		boolean flag = false;
		for (NativeQueryObjectReader reader: readers) {
			Object temp = reader.read();
			if (temp != null) {
				flag = true;
			}
			list.add(temp);
		}
		
		return flag ? list: null;
	}


	public List<NativeQueryObjectReader> getReaders() {
		return readers;
	}


	public void setReaders(List<NativeQueryObjectReader> readers) {
		this.readers = readers;
	}
	

}
