/**
 * 
 */
package com.tcs.destination.data.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.CustomerAssociateT;
import com.tcs.destination.bean.dto.CustomerAssociateDTO;

/**
 * @author tcs2
 *
 */
public interface CustomerAssociateRepository extends
		CrudRepository<CustomerAssociateT, Serializable> {

	@Query(value = "select NEW com.tcs.destination.bean.dto.CustomerAssociateDTO(cmt.groupCustomerName, count(cat.customerAssociateId)) from CustomerAssociateT cat"
			+ " JOIN cat.revenueCustomerMappingT rcmt "
			+ " JOIN rcmt.customerMasterT cmt group by cmt.groupCustomerName")
	List<CustomerAssociateDTO> findAssociatesByGroupCustomer();

	@Query(value = "select count(distinct Associate_id), max(created_date) from customer_associate_t where allocation_category='WON'", nativeQuery = true)
	List<Object[]> getTotalAssociatesCount();
}
