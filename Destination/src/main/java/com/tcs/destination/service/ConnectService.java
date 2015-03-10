package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.ConnectCustPartAjax;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.exception.NoDataFoundException;

@SuppressWarnings("unused")
@Component
public class ConnectService {
	
	@Autowired
	ApplicationContext appContext;

	@Autowired
	ConnectRepository connectRepository;

		
	public List<ConnectT> searchforConnectsById(String typed)
	{
		ConnectRepository connectRepository = appContext
				.getBean(ConnectRepository.class);
	    List<ConnectT> connectlist= connectRepository.findByConnectIdIgnoreCaseLike("%" + typed + "%");
	    if (connectlist.isEmpty())
			throw new NoDataFoundException();
		return connectlist;
		
	}
	
	
	public List<ConnectT> searchforConnectsByName(String typed)
	{
		ConnectRepository connectRepository = appContext
				.getBean(ConnectRepository.class);
	    List<ConnectT> ct= connectRepository.findByConnectNameIgnoreCaseLike("%" + typed + "%");
		return ct;
	}
	}
