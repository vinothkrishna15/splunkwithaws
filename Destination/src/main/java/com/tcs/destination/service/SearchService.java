package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.SearchCusPartAjax;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.PartnerRepository;

@Component
public class SearchService {

	@Autowired
	ApplicationContext appContext;

	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	PartnerRepository partnerRepository;
	

	@Autowired
	ContactRepository contactRepository;

	@Autowired
	OpportunityRepository opportunityRepository;

	// @Autowired
	// PartnerRepository partnerRepository;

	public void viewAll() {
		System.out.println(customerRepository.findAll());
	}

	public List<CustomerMasterT> searchforCustomer(String chars) {
		List<CustomerMasterT> customers = customerRepository
				.findByCustomerNameIgnoreCaseLike("%" + chars + "%");
		return customers;
	}
	
	public List<PartnerMasterT> searchforPartner(String chars) {
		List<PartnerMasterT> partners = partnerRepository.findByPartnerNameIgnoreCaseLike("%" + chars + "%");
		return partners;
	}

	public List<SearchCusPartAjax> searchForCustPartContaining(String chars) {
		List<SearchCusPartAjax> results = ajaxSearchForCustContaining(chars);
		ajaxSearchForPartnerContaining(chars, results);
		return results;
	}

	private void ajaxSearchForPartnerContaining(String chars,
			List<SearchCusPartAjax> results) {
		PartnerRepository partnerRepository = appContext
				.getBean(PartnerRepository.class);
		List<PartnerMasterT> partners = partnerRepository
				.findByPartnerNameIgnoreCaseLike("%" + chars + "%");
		for (PartnerMasterT partner : partners) {
			SearchCusPartAjax search = new SearchCusPartAjax();
			search.setId(partner.getPartnerId());
			search.setName(partner.getPartnerName());
			results.add(search);
		}
	}

	private List<SearchCusPartAjax> ajaxSearchForCustContaining(String chars) {
		List<SearchCusPartAjax> results = new ArrayList<SearchCusPartAjax>();
		CustomerRepository repository = appContext
				.getBean(CustomerRepository.class);
		List<CustomerMasterT> customers = repository
				.findByCustomerNameIgnoreCaseLike("%" + chars + "%");
		for (CustomerMasterT customer : customers) {
			SearchCusPartAjax search = new SearchCusPartAjax();
			search.setId(customer.getCustomerId());
			search.setName(customer.getCustomerName());
			results.add(search);
		}
		return results;
	}

	// public List<ContactT> searchContacts(String id) {
	// List<ContactT> contacts = new ArrayList<ContactT>();
	// if (id.contains("CUS")) {
	// contacts = contactRepository.findByCustomerId(id);
	// } else if (id.contains("PAT")) {
	// contacts = contactRepository.findByPartnerId(id);
	// }
	// return contacts;
	// }

//	public OpportunityT searchOppurtunity(String id) {
//		OpportunityT opportunity = new OpportunityT();
//		if (id.contains("CUS")) {
//			List<OpportunityT> tempOpps = opportunityRepository
//					.findByCustomerId(id);
//			if (tempOpps.size() > 0) {
//				opportunity = tempOpps.get(0);
//			}
//		} else if (id.contains("PAT")) {
//			List<OpportunityT> tempOpps = opportunityRepository
//					.findByPartnerId(id);
//			if (tempOpps.size() > 0) {
//				opportunity = tempOpps.get(0);
//			}
//		}
//		return opportunity;
//	}

	public OpportunityT save(OpportunityT op) {
		OpportunityT opp1 = opportunityRepository.save(op);
		return opp1;
	}

}