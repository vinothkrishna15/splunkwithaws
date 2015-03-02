package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.CustPartResultCard;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.utils.Constants;

@Component
public class RecentlyAddedService {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	PartnerRepository partnerRepository;

	public List<CustPartResultCard> recentlyAdded() {
		ArrayList<CustPartResultCard> frequentList = new ArrayList<CustPartResultCard>();
		List<CustomerMasterT> customerList = customerRepository.findRecent5();

		for (CustomerMasterT customer : customerList) {
			CustPartResultCard recent = Constants.convertToCard(customer);
			frequentList.add(recent);
		}

		List<PartnerMasterT> partnerList = partnerRepository.findRecent5();

		for (PartnerMasterT partner : partnerList) {
			CustPartResultCard recent = Constants.convertToCard(partner);
			frequentList.add(recent);
		}
		Collections.sort(frequentList);
		return frequentList;
	}

}