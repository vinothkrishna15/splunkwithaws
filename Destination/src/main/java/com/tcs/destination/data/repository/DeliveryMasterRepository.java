package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryMasterT;

@Repository
public interface DeliveryMasterRepository extends JpaRepository<DeliveryMasterT, String> {
	
	/*------------------Start of delivery smart search queries-------------------*/
	
	@Query(value="SELECT * FROM delivery_master_t where delivery_centre_id in (SELECT delivery_centre_id FROM delivery_centre_t where delivery_cluster_id in ("
			+ " SELECT delivery_cluster_id FROM delivery_cluster_t WHERE delivery_cluster_head=(:userId))) AND "
			+ " UPPER(opportunity_id) like UPPER(:term) ORDER BY modified_datetime DESC LIMIT CASE WHEN :getAll THEN null ELSE 3 END",nativeQuery=true)
	List<DeliveryMasterT> searchDeliveryClusterDetailsById(@Param("term") String term,
			@Param("getAll") boolean getAll, @Param("userId") String userId);

	@Query(value="SELECT * FROM delivery_master_t where delivery_centre_id in (SELECT delivery_centre_id FROM delivery_centre_t WHERE delivery_centre_head=(:userId)) AND "
			+ " UPPER(opportunity_id) like UPPER(:term) ORDER BY modified_datetime DESC LIMIT CASE WHEN :getAll THEN null ELSE 3 END",nativeQuery=true)
	List<DeliveryMasterT> searchDeliveryCentreDetailsById(@Param("term") String term,
			@Param("getAll") boolean getAll, @Param("userId") String userId);

	@Query(value="SELECT * FROM delivery_master_t where delivery_master_id in (SELECT delivery_master_id FROM delivery_master_manager_link_t WHERE delivery_manager_id=(:userId))"
			+ " UPPER(opportunity_id) like UPPER(:term) ORDER BY modified_datetime DESC LIMIT CASE WHEN :getAll THEN null ELSE 3 END",nativeQuery=true)
	List<DeliveryMasterT> searchDeliveryManagerDetailsById(@Param("term") String term,
			@Param("getAll") boolean getAll, @Param("userId") String userId);

	@Query(value="SELECT * FROM delivery_master_t where delivery_centre_id in (SELECT delivery_centre_id FROM delivery_centre_t where delivery_cluster_id in ("
			+ " SELECT delivery_cluster_id FROM delivery_cluster_t WHERE delivery_cluster_head=(:userId)) AND UPPER(delivery_centre) like UPPER(:term)) "
			+ " ORDER BY modified_datetime DESC LIMIT CASE WHEN :getAll THEN null ELSE 3 END", nativeQuery=true)
	List<DeliveryMasterT> searchDeliveryClusterDetailsByDeliveryCentres(@Param("term") String term,
			@Param("getAll") boolean getAll, @Param("userId") String userId);

	@Query(value="SELECT * FROM delivery_master_t where delivery_centre_id in (SELECT delivery_centre_id FROM delivery_centre_t WHERE delivery_centre_head=(:userId) "
			+ "  AND UPPER(delivery_centre) like UPPER(:term)) ORDER BY modified_datetime DESC LIMIT CASE WHEN :getAll THEN null ELSE 3 END", nativeQuery=true)
	List<DeliveryMasterT> searchDeliveryCentreDetailsByDeliveryCentres(@Param("term") String term,
			@Param("getAll") boolean getAll, @Param("userId") String userId);

	@Query(value="SELECT * FROM delivery_master_t where delivery_master_id in (SELECT delivery_master_id FROM delivery_master_manager_link_t"
			+ " WHERE delivery_manager_id = (:userId)) AND delivery_centre_id in (SELECT delivery_centre_id FROM delivery_centre_t "
			+ " WHERE UPPER(delivery_centre) like UPPER(:term)) ORDER BY modified_datetime DESC LIMIT CASE WHEN :getAll THEN null ELSE 3 END", nativeQuery=true)
	List<DeliveryMasterT> searchDeliveryManagerDetailsByDeliveryCentres(@Param("term") String term,
			@Param("getAll") boolean getAll, @Param("userId") String userId);

	@Query(value="SELECT * FROM delivery_master_t where delivery_centre_id in (SELECT delivery_centre_id FROM delivery_centre_t WHERE delivery_cluster_id in ("
			+ " SELECT delivery_cluster_id FROM delivery_cluster_t WHERE delivery_cluster_head=(:userId))) AND opportunity_id IN (SELECT opportunity_id FROM opportunity_t WHERE customer_id IN ("
			+ " SELECT customer_id from customer_master_t WHERE UPPER(customer_name) like UPPER(:term))) ORDER BY modified_datetime DESC LIMIT CASE WHEN :getAll THEN null ELSE 3 END ", nativeQuery=true) 
	List<DeliveryMasterT> searchDeliveryClusterDetailsByCustomerName(@Param("term") String term,
			@Param("getAll") boolean getAll, @Param("userId") String userId);
	
	@Query(value="SELECT * FROM delivery_master_t where delivery_centre_id in (SELECT delivery_centre_id FROM delivery_centre_t WHERE delivery_centre_head=(:userId)) "
			+ " AND opportunity_id IN (SELECT opportunity_id FROM opportunity_t WHERE customer_id IN (SELECT customer_id from customer_master_t WHERE UPPER(customer_name) like UPPER(:term))) "
			+ " ORDER BY modified_datetime DESC LIMIT CASE WHEN :getAll THEN null ELSE 3 END", nativeQuery=true)
	List<DeliveryMasterT> searchDeliveryCentreDetailsByCustomerName(@Param("term") String term,
			@Param("getAll") boolean getAll, @Param("userId") String userId);
	
	@Query(value="SELECT * FROM delivery_master_t where delivery_master_id in (SELECT delivery_master_id FROM delivery_master_manager_link_t WHERE delivery_manager_id=(:userId))"
			+ " AND opportunity_id IN (SELECT opportunity_id FROM opportunity_t WHERE customer_id IN (SELECT customer_id from customer_master_t WHERE UPPER(customer_name) like UPPER(:term))) "
			+ " ORDER BY modified_datetime DESC LIMIT CASE WHEN :getAll THEN null ELSE 3 END", nativeQuery=true)
	List<DeliveryMasterT> searchDeliveryManagerDetailsByCustomerName(@Param("term") String term,
			@Param("getAll") boolean getAll, @Param("userId") String userId);
	
	/*------------------End of delivery smart search queries-------------------*/
}
