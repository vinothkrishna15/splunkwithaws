package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.UserFavoritesT;
import com.tcs.destination.bean.UserT;

@Repository
public interface FavoritesSearchedRepository extends
		CrudRepository<UserFavoritesT, String> {
	
	List<UserFavoritesT> findByUserTAndEntityTypeIgnoreCaseOrderByCreatedDatetimeDesc(UserT usert, String entityType, Pageable pageable);
	
	UserFavoritesT findByCustomerIdAndUserId(String customerId,String userId);
	
	UserFavoritesT findByPartnerIdAndUserId(String partnerId,String userId);
	
	UserFavoritesT findByConnectIdAndUserId(String connectId,String userId);
	
	UserFavoritesT findByOpportunityIdAndUserId(String opportunityId,String userId);
	
	UserFavoritesT findByDocumentIdAndUserId(String documentId,String userId);
	
	UserFavoritesT findByContactIdAndUserId(String contactId,String userId);
	
}
 