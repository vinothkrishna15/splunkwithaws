package com.tcs.destination.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.exception.ConnectionNotFoundException;


@Component
public class ConnectService {

	@Autowired
	ConnectRepository connectRepository;

	public ConnectT searchforConnectsById(String connectId) {
		ConnectT connect= connectRepository.findByConnectId(connectId);
		
		if (connect == null)
			throw new ConnectionNotFoundException();
	
		return connect;
	 
	}

	public List<ConnectT> searchforConnectsByNameContaining(String name) {
		List<ConnectT> connectList = connectRepository
				.findByConnectNameIgnoreCaseLike("%" + name + "%");
		
		if (connectList.isEmpty())
			throw new ConnectionNotFoundException();
		return connectList;
	}

	public List<ConnectT> searchforConnectsBetween(Date fromDate, Date toDate) {
		return connectRepository.findByStartDatetimeOfConnectBetween(new Timestamp(
				fromDate.getTime()), new Timestamp(toDate.getTime()));
	}
}
