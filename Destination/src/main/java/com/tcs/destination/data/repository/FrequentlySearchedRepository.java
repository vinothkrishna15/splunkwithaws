package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.FrequentlySearchedCustomerPartnerT;

@Repository
public interface FrequentlySearchedRepository extends
		CrudRepository<FrequentlySearchedCustomerPartnerT, String> {

	@Query(value = "select  entity_type, entity_id from (select count(*), entity_id, entity_type from frequently_searched_customer_partner_t group by entity_id, entity_type order by count(*) desc limit 4)t", nativeQuery = true)
	List<Object[]> findFrequentEntities();

}
