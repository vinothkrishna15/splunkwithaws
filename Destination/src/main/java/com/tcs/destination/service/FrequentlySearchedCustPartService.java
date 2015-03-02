package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.CustPartResultCard;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.FrequentlySearchedCustomerPartnerT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.FrequentlySearchedRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.utils.Constants;

@Component
public class FrequentlySearchedCustPartService {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	PartnerRepository partnerRepository;

	@Autowired
	FrequentlySearchedRepository frequentRepository;

	public List<CustPartResultCard> frequentCustPart() {

		List<Object[]> frequentMapping = frequentRepository
				.findFrequentEntities();

		List<CustPartResultCard> sortedList = new ArrayList<CustPartResultCard>();

		for (Object[] frequent : frequentMapping) {

			if (frequent[0].toString().equals(
					Constants.EntityType.CUSTOMER.toString())) {
				CustomerMasterT customer = customerRepository
						.findOne(frequent[1].toString());
				CustPartResultCard card = Constants.convertToCard(customer);
				sortedList.add(card);
			} else if (frequent[0].toString().equals(
					Constants.EntityType.PARTNER.toString())) {
				PartnerMasterT partner = partnerRepository.findOne(frequent[1]
						.toString());
				CustPartResultCard card = Constants.convertToCard(partner);
				sortedList.add(card);
			}
		}

		return sortedList;
	}

	public boolean insertFrequent(FrequentlySearchedCustomerPartnerT frequent) {
		return frequentRepository.save(frequent) != null ? true : false;
	}

}