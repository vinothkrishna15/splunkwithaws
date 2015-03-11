package com.tcs.destination.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.sun.jna.platform.win32.Sspi.TimeStamp;
import com.tcs.destination.bean.ConnectCustPartAjax;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.exception.NoDataFoundException;

@SuppressWarnings("unused")
@Component
public class ConnectService {

	@Autowired
	ConnectRepository connectRepository;

	public List<ConnectT> searchforConnectsById(String typed) {
		List<ConnectT> connectlist = connectRepository
				.findByConnectIdIgnoreCaseLike("%" + typed + "%");
		if (connectlist.isEmpty())
			throw new NoDataFoundException();
		return connectlist;

	}

	public List<ConnectT> searchforConnectsByName(String typed) {
		List<ConnectT> ct = connectRepository
				.findByConnectNameIgnoreCaseLike("%" + typed + "%");
		return ct;
	}

	public List<ConnectT> searchforConnectsBetween(Date fromDate, Date toDate) {
		return connectRepository.findByDateOfConnectBetween(new Timestamp(
				fromDate.getTime()), new Timestamp(toDate.getTime()));
	}
}
