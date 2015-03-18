package com.tcs.destination.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.ConnectSecondaryOwnerRepository;
import com.tcs.destination.exception.NoDataFoundException;
import com.tcs.destination.exception.NoSuchOwnerTypeException;
import com.tcs.destination.utils.Constants.OWNER_TYPE;

@SuppressWarnings("unused")
@Component
public class ConnectService {

	@Autowired
	ConnectRepository connectRepository;

	@Autowired
	ConnectSecondaryOwnerRepository connectSecondaryOwnerRepository;

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

	public List<ConnectT> searchforConnectsBetweenForUser(Date fromDate,
			Date toDate, UserT userT, String owner) {
		if (OWNER_TYPE.contains(owner)) {
			List<ConnectT> connects = new ArrayList<ConnectT>();
			if (owner.equalsIgnoreCase(OWNER_TYPE.PRIMARY.toString())) {
				connects = connectRepository
						.findByPrimaryOwnerIgnoreCaseAndStartDatetimeOfConnectBetween(
								userT.getUserId(),
								new Timestamp(fromDate.getTime()),
								new Timestamp(toDate.getTime()));
				System.out.println("Primary :" + connects.size());
			} else if (owner.equalsIgnoreCase(OWNER_TYPE.SECONDARY.toString())) {
				connects = connectSecondaryOwnerRepository
						.findConnectTWithDateWithRangeForSecondaryOwner(
								userT.getUserId(),
								new Timestamp(fromDate.getTime()),
								new Timestamp(toDate.getTime()));
			} else if (owner.equalsIgnoreCase(OWNER_TYPE.ALL.toString())) {
				connects.addAll(connectRepository
						.findByPrimaryOwnerIgnoreCaseAndStartDatetimeOfConnectBetween(
								userT.getUserId(),
								new Timestamp(fromDate.getTime()),
								new Timestamp(toDate.getTime())));
				connects.addAll(connectSecondaryOwnerRepository
						.findConnectTWithDateWithRangeForSecondaryOwner(
								userT.getUserId(),
								new Timestamp(fromDate.getTime()),
								new Timestamp(toDate.getTime())));
			}
			if (connects.isEmpty())
				throw new NoDataFoundException();
			return connects;
		}
		throw new NoSuchOwnerTypeException();
	}
}
