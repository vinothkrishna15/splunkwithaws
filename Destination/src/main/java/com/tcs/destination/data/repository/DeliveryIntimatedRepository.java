package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.DeliveryIntimatedT;

@Repository
public interface DeliveryIntimatedRepository extends CrudRepository<DeliveryIntimatedT, String> {

	@Query(value = "SELECT dit FROM DeliveryIntimatedT dit "
			+ "JOIN dit.deliveryIntimatedCentreLinkTs dclt "
			+ "JOIN dit.opportunityT opt "
			+ "WHERE dclt.deliveryCentreId IN (:centreIdList) AND UPPER(opt.opportunityId) LIKE UPPER(:term) ORDER BY dit.modifiedDatetime DESC")
	List<DeliveryIntimatedT> searchByOppIdTermAndCentresIn(@Param("term") String queryTerm,
			@Param("centreIdList") List<?> idList);

	@Query(value = "SELECT dit FROM DeliveryIntimatedT dit "
			+ "JOIN dit.deliveryIntimatedCentreLinkTs dclt "
			+ "JOIN dit.opportunityT opt "
			+ "JOIN opt.customerMasterT cmt "
			+ "WHERE dclt.deliveryCentreId IN (:centreIdList) AND UPPER(cmt.customerName) LIKE UPPER(:term) ORDER BY dit.modifiedDatetime DESC")
	List<DeliveryIntimatedT> searchByCustNameTermAndCentresIn(@Param("term") String queryTerm,
			@Param("centreIdList") List<?> idList);

	@Query(value = "SELECT dit FROM DeliveryIntimatedT dit "
			+ "JOIN dit.deliveryIntimatedCentreLinkTs dclt "
			+ "JOIN dit.opportunityT opt "
			+ "JOIN dclt.deliveryCentreT dct "
			+ "WHERE dclt.deliveryCentreId IN (:centreIdList) AND UPPER(dct.deliveryCentre) LIKE UPPER(:term) ORDER BY dit.modifiedDatetime DESC")
	List<DeliveryIntimatedT> searchByCentreTermAndCentresIn(@Param("term") String queryTerm,
			@Param("centreIdList") List<?> idList);
}
