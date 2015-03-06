package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.ConnectCustPartAjax;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.data.repository.ConnectRepository;

@Component
public class ConnectService {

	@Autowired
	ConnectRepository connectRepository;

	public void viewAll() {
		System.out.println(connectRepository.findAll());
	}
	
	public List<ConnectT> searchForConnects(String chars) {
		//connectRepository = appContext.getBean(ConnectRepository.class);
		List<ConnectT> connects = connectRepository.findByConnectNameIgnoreCaseLike("%" + chars + "%");
//		for (ConnectT connect : connects) {
//			ConnectCustPartAjax conn = new ConnectCustPartAjax();
//			conn.setEntityType(connect.getConnectCategory());
//			conn.setId(connect.getConnectId());
//			conn.setName(connect.getConnectName());
//			results.add(conn);
//		}
		return connects;
	}

//	private List<ConnectCustPartAjax> ajaxSearchForConnects(String chars) {
//		List<ConnectCustPartAjax> results = new ArrayList<ConnectCustPartAjax>();
//		connectRepository = appContext.getBean(ConnectRepository.class);
//		List<ConnectT> connects = connectRepository
//				.findByConnectNameIgnoreCaseLike("%" + chars + "%");
//		for (ConnectT connect : connects) {
//			ConnectCustPartAjax conn = new ConnectCustPartAjax();
//			conn.setEntityType(connect.getConnectCategory());
//			conn.setId(connect.getConnectId());
//			conn.setName(connect.getConnectName());
//			results.add(conn);
//		}
//		return results;
	}
