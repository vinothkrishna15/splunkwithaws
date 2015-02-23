package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.RecentlyAddedCustPart;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.PartnerRepository;

@Component
public class RecentlyAddedService {

	@Autowired
	ApplicationContext appContext;
//
//	public List<RecentlyAddedCustPart> recentlyAdded() {
//		ArrayList<RecentlyAddedCustPart> recentList = new ArrayList<RecentlyAddedCustPart>();
//		CustomerRepository customerRepository = appContext
//				.getBean(CustomerRepository.class);
//		PartnerRepository partnerRepository = appContext
//				.getBean(PartnerRepository.class);
//		List<CustomerMasterT> customerList = customerRepository
//				.findTop5OrderByCreatedModifiedDatetime();
//
//		for (CustomerMasterT customer : customerList) {
//			RecentlyAddedCustPart recent = new RecentlyAddedCustPart();
//			recent.setGroupCustomerName(customer.getGroupCustomerName());
//			recent.setGeographyMappingT(customer.getGeographyMappingT());
//			recent.setId(customer.getCustomerId());
//			recent.setLogo(customer.getLogo());
//			recent.setName(customer.getCustomerName());
//			customer.getCreatedModifiedDatetime();
//			recentList.add(recent);
//		}
//
//		List<PartnerMasterT> partnerList = partnerRepository
//				.findTop5OrderByCreatedModifiedDatetime();
//
//		for (PartnerMasterT partners : partnerList) {
//			RecentlyAddedCustPart recent = new RecentlyAddedCustPart();
//
//			// TODO:Resolve this dependency
//			// recent.setGroupCustomerName(partners.getCorporateHqAddress());
//			// recent.setGeographyMappingT(partners.getGeographyMappingT());
//			recent.setId(partners.getPartnerId());
//			recent.setLogo(partners.getLogo());
//			recent.setName(partners.getPartnerName());
//			recentList.add(recent);
//		}
//
//		return recentList;
//	}
//
//	private Sort sortByRecentAdded() {
//		return new Sort(Sort.Direction.ASC, "createdModifiedDatetime");
//	}

}