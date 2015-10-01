package com.tcs.destination.reader;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.adapter.AbstractMethodInvokingDelegator.InvocationTargetThrowableWrapper;
import org.springframework.batch.item.adapter.DynamicMethodInvocationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.util.Assert;
import org.springframework.util.MethodInvoker;

public class NativeQueryReader implements ItemReader<Object[]>, InitializingBean {
	
	protected static final Log logger = LogFactory.getLog(NativeQueryReader.class);
	
	private int rowNo = 0;
	
	private List<Object[]> resultSet = null;
	
	private CrudRepository<T, ?> repository;

	private String methodName;
	
	private List<?> arguments;
	
	@Override
	public Object[] read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {
		
		logger.debug("Inside read method - reading record:" + (rowNo + 1));
		
		Object[] returnValue = null;
		if (rowNo == 0) {
			MethodInvoker invoker = createMethodInvoker(repository, methodName);
			resultSet = doInvoke(invoker);
		}
		if (!resultSet.isEmpty() && rowNo < resultSet.size()) {
			returnValue = resultSet.get(rowNo);
			rowNo++;
		}
		
		
		return returnValue;
	}
	
	private List<Object[]> doInvoke(MethodInvoker invoker) throws Exception{
		try {
			invoker.prepare();
		}
		catch (ClassNotFoundException e) {
			throw new DynamicMethodInvocationException(e);
		}
		catch (NoSuchMethodException e) {
			throw new DynamicMethodInvocationException(e);
		}

		try {
			List<Object> parameters = new ArrayList<Object>();

			if(arguments != null && arguments.size() > 0) {
				parameters.addAll(arguments);
			}

			invoker.setArguments(parameters.toArray());
			
			return ((List<Object[]>) invoker.invoke());
		}
		catch (InvocationTargetException e) {
			if (e.getCause() instanceof Exception) {
				throw (Exception) e.getCause();
			}
			else {
				throw new InvocationTargetThrowableWrapper(e.getCause());
			}
		}
		catch (IllegalAccessException e) {
			throw new DynamicMethodInvocationException(e);
		}
	}

	private MethodInvoker createMethodInvoker(Object targetObject, String targetMethod) {
		MethodInvoker invoker = new MethodInvoker();
		invoker.setTargetObject(targetObject);
		invoker.setTargetMethod(targetMethod);
		return invoker;
	}

	public CrudRepository<T, ?> getRepository() {
		return repository;
	}

	public void setRepository(CrudRepository<T, ?> repository) {
		this.repository = repository;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public List<?> getArguments() {
		return arguments;
	}

	public void setArguments(List<?> arguments) {
		this.arguments = arguments;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.state(repository != null, "A CrudRepository implementation is required");
		
	}
	
	

}
