package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.FrequentlySearchedCustomerPartnerT;

@Repository
public interface FrequentlySearchedRepository extends
		CrudRepository<FrequentlySearchedCustomerPartnerT, String> {

	@Query(value = "select count(*), entity_id from frequently_searched_customer_partner_t where entity_type=?1 group by entity_id, entity_type order by count(*) desc limit ?2", nativeQuery = true)
	List<Object[]> findFrequentEntities(String entity,int count);

}
