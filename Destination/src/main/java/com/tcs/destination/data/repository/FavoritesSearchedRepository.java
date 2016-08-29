package com.tcs.destination.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.UserFavoritesT;

@Repository
public interface FavoritesSearchedRepository extends
	JpaRepository<UserFavoritesT, String> {

	Page<UserFavoritesT> findByUserIdAndEntityTypeIgnoreCaseOrderByCreatedDatetimeDesc(String userId, String entityType, Pageable pageable);

	UserFavoritesT findByCustomerIdAndUserId(String customerId,String userId);

	UserFavoritesT findByPartnerIdAndUserId(String partnerId,String userId);

	UserFavoritesT findByConnectIdAndUserId(String connectId,String userId);

	UserFavoritesT findByOpportunityIdAndUserId(String opportunityId,String userId);

	UserFavoritesT findByDocumentIdAndUserId(String documentId,String userId);

	UserFavoritesT findByContactIdAndUserId(String contactId,String userId);
	
	/**
	 * Find Favorites for user
	 * 
	 * @param userId
	 * @param pageable
	 * @return
	 */
	Page<UserFavoritesT> findByUserIdOrderByCreatedDatetimeDesc(String userId, Pageable pageable);

}
