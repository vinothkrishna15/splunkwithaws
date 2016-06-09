package com.tcs.destination.data.repository;

import static com.tcs.destination.utils.QueryConstants.CONNECT_REMINDER;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.ConnectT;

/**
 * 
 * Repository for working with {@link ConnectT} domain objects
 */
@Repository
public interface ConnectRepository extends CrudRepository<ConnectT, String> {

	/**
	 * Finds the connection details for the given connection name.
	 * 
	 * @param name
	 *            is the connection name.
	 * @return connection details.
	 */
	Page<ConnectT> findByConnectNameIgnoreCaseLikeOrderByModifiedDatetimeDesc(String name,Pageable page);

	/**
	 * Finds the connection details for the given connection id.
	 * 
	 * @param connectid
	 *            is the connection id.
	 * @return connection details.
	 */
	ConnectT findByConnectId(String connectid);
	
	Page<ConnectT> findByConnectNameIgnoreCaseLikeAndCustomerIdOrderByModifiedDatetimeDesc(String name,
			String customerId,Pageable page);

	@Query(value = "select c from ConnectT c where (primaryOwner=(:primaryOwner) OR (:primaryOwner)='')and startDatetimeOfConnect between (:fromDate) and (:toDate) and (customer_id=(:customerId) OR (:customerId)='') and (partner_id=(:partnerId) OR (:partnerId)='') order by startDatetimeOfConnect asc")
	List<ConnectT> findByPrimaryOwnerIgnoreCaseAndStartDatetimeOfConnectBetweenForCustomerOrPartner(
			@Param("primaryOwner") String primaryOwner,
			@Param("fromDate") Timestamp fromDate,
			@Param("toDate") Timestamp toDate,
			@Param("customerId") String customerId,
			@Param("partnerId") String partnerId);

	/**
	 * This Method is used to get customer connect ids for the given input parameters
	 * @param startDate
	 * @param endDate
	 * @param iou
	 * @param geography
	 * @param country
	 * @param serviceLines
	 * @return
	 */
	@Query(value = "select distinct (CON.connect_id) from connect_t CON "
			+ " JOIN customer_master_t CMT ON  CMT.customer_id=CON.customer_id"
			+ " JOIN iou_customer_mapping_t ICMT ON  CMT.iou=ICMT.iou  "
			+ " JOIN geography_mapping_t GMT ON CMT.geography=GMT.geography "
			+ " left outer join connect_sub_sp_link_t CSL on CON.connect_id=CSL.connect_id"
			+ " left outer JOIN sub_sp_mapping_t SSM on CSL.sub_sp=SSM.sub_sp where CON.connect_category='CUSTOMER' and "
			+ " CON.start_datetime_of_connect between (:startDate) AND (:endDate) AND "
			+ " (ICMT.display_iou IN (:iou) OR ('') IN (:iou)) AND"
			+ " (GMT.display_geography = (:displayGeography) OR ('') = (:displayGeography)) AND"
			+ " (CON.country IN (:country) OR ('') IN (:country)) AND"
			+ " (SSM.display_sub_sp IN (:serviceLines) OR ('') IN (:serviceLines))", nativeQuery = true)
	List<String> findCustomerConnectIds(@Param("startDate") Timestamp startDate,
			@Param("endDate") Timestamp endDate,
			@Param("iou") List<String> iou,
			@Param("displayGeography") String displayGeography,
			@Param("country") List<String> country,
			@Param("serviceLines") List<String> serviceLines);
	
	/**
	 * This Method is used to get partner connect ids for the given input parameters
	 * 
	 * @param startDate
	 * @param endDate
	 * @param geography
	 * @param country
	 * @param serviceLines
	 * @return
	 */
	@Query(value = " select distinct(CON.connect_id) from connect_t CON  "
			+ "	JOIN partner_master_t PAT ON  PAT.partner_id=CON.partner_id "
			+ "	JOIN geography_mapping_t GMT ON PAT.geography=GMT.geography  "
			+ "	left outer Join connect_sub_sp_link_t CSL ON CON.connect_id=CSL.connect_id "
			+ "	left outer JOIN sub_sp_mapping_t SSM ON CSL.sub_sp=SSM.sub_sp where  CON.connect_category='PARTNER' and "
			+ " CON.start_datetime_of_connect between (:startDate) AND (:endDate) AND "
			+ " (GMT.display_geography IN (:displayGeography) OR ('') IN (:displayGeography)) AND"
			+ " (CON.country IN (:country) OR ('') IN (:country)) AND"
			+ " (SSM.display_sub_sp IN (:serviceLines) OR ('') IN (:serviceLines))", nativeQuery = true)
	List<String> findPartnerConnectIds(@Param("startDate") Timestamp startDate,
			@Param("endDate") Timestamp endDate,
			@Param("displayGeography") String displayGeography,
			@Param("country") List<String> country,
			@Param("serviceLines") List<String> serviceLines);



	/**
	 * This query select the connect_id from connect_T and
	 * connect_secondary_owner_link_T and which in turn is used to retrieve the
	 * details of the connects under a supervisor
	 * 
	 * @param users
	 * @param startTimestamp
	 * @param endTimestamp
	 * @return
	 */
	@Query(value = "SELECT * FROM connect_t c2 WHERE ((c2.connect_id "
			+ "IN ((SELECT c1.connect_id FROM Connect_T c1 WHERE c1.primary_owner IN (:users)) "
			+ "UNION (SELECT c.connect_id FROM Connect_T c, connect_secondary_owner_link_T cs "
			+ "WHERE (c.connect_id=cs.connect_id) AND (cs.secondary_owner IN (:users))))) "
			+ "AND (c2.start_datetime_of_connect between (:startTimestamp) and (:endTimestamp)))"
			+ "ORDER BY c2.start_datetime_of_connect", nativeQuery = true)
	List<ConnectT> getTeamConnects(@Param("users") List<String> users,
			@Param("startTimestamp") Timestamp startTimestamp,
			@Param("endTimestamp") Timestamp endTimestamp);

	/**
	 * This query retrieves all the connects under a supervisor if they are
	 * primary owners and found between the mentioned dates
	 * 
	 * @param users
	 * @param startTimestamp
	 * @param endTimestamp
	 * @return
	 */
	List<ConnectT> findByPrimaryOwnerInAndStartDatetimeOfConnectBetweenOrderByStartDatetimeOfConnectAsc(
			List<String> users, Timestamp startTimestamp, Timestamp endTimestamp);
	
	Page<ConnectT> findByPrimaryOwnerInAndStartDatetimeOfConnectBetweenOrderByStartDatetimeOfConnectAsc(
			List<String> users, Timestamp startTimestamp, Timestamp endTimestamp, Pageable pageable);


	/**
	 * This query retrieves all the connects under a supervisor if they are
	 * secondary owners and found between the mentioned dates
	 * 
	 * @param users
	 * @param startTimestamp
	 * @param endTimestamp
	 * @return
	 */
	@Query(value = "SELECT * FROM connect_t c2 WHERE ((c2.connect_id IN "
			+ "((SELECT c.connect_id FROM Connect_T c, connect_secondary_owner_link_T cs "
			+ "WHERE (c.connect_id=cs.connect_id) AND (cs.secondary_owner IN (:users)))))) "
			+ "AND (c2.start_datetime_of_connect between (:startTimestamp) and (:endTimestamp)) "
			+ "ORDER BY c2.start_datetime_of_connect", nativeQuery = true)
	List<ConnectT> findTeamConnectsBySecondaryowner(
			@Param("users") List<String> users,
			@Param("startTimestamp") Timestamp startTimestamp,
			@Param("endTimestamp") Timestamp endTimestamp);

	/**
	 * This query returns the Connects based on the list of Connect Ids provided
	 * 
	 * @param connects
	 * @return list of ConnectT
	 */
	List<ConnectT> findByConnectIdInOrderByLocationAsc(List<String> connects);

	
	/**
	 * This Method is used to find the connects related to customer
	 * @param startDate
	 * @param endDate
	 * @param userId
	 * @param iouList
	 * @param geoList
	 * @param countryList
	 * @param serviceLines
	 * @return
	 */
	@Query(value = "select distinct (CON.connect_id) from connect_t CON "
			+ " left outer join connect_secondary_owner_link_t CSOL ON CON.connect_id=CSOL.connect_id "
			+ " JOIN customer_master_t CMT ON CMT.customer_id=CON.customer_id "
			+ " JOIN iou_customer_mapping_t ICMT ON (CMT.iou=ICMT.iou AND (ICMT.display_iou IN (:iouList) OR ('') IN (:iouList))) "
			+ " JOIN geography_mapping_t GMT ON (CMT.geography=GMT.geography AND (GMT.display_geography = (:displayGeography) OR ('') = (:displayGeography))) "
			+ " left outer join connect_sub_sp_link_t CSL on CON.connect_id=CSL.connect_id "
			+ " left outer JOIN sub_sp_mapping_t SSM on (CSL.sub_sp=SSM.sub_sp AND (SSM.display_sub_sp IN (:serviceLines) OR ('') IN (:serviceLines))) "
			+ " where CON.start_datetime_of_connect between (:startDate) AND (:endDate) "
			+ " AND CON.connect_category=('CUSTOMER') AND (CON.country IN (:countryList) OR ('') IN (:countryList))"
			+ " AND ((CON.primary_owner in (:userIds) OR CSOL.secondary_owner in (:userIds)) OR ('') in (:userIds)) ", nativeQuery = true)
	List<String> findByCustomerConnects(@Param("startDate") Timestamp startDate,
			@Param("endDate") Timestamp endDate,
			@Param("userIds") List<String> userIds,
			@Param("iouList") List<String> iouList,
			@Param("displayGeography") String displayGeography,
			@Param("countryList") List<String> countryList,
			@Param("serviceLines") List<String> serviceLines);
	
	
	/**
	 * This Method is used to find the connects related to partner
	 * @param startDate
	 * @param endDate
	 * @param userId
	 * @param iouList
	 * @param geoList
	 * @param countryList
	 * @param serviceLines
	 * @return
	 */
	@Query(value = "select distinct(CON.connect_id) from connect_t CON "
			+ " left outer join connect_secondary_owner_link_t CSOL ON CON.connect_id=CSOL.connect_id "
			+ " JOIN partner_master_t PAT ON PAT.partner_id=CON.partner_id "
			+ " JOIN geography_mapping_t GMT ON (PAT.geography=GMT.geography AND (GMT.display_geography = (:displayGeography) OR ('') = (:displayGeography))) "
			+ " left outer Join connect_sub_sp_link_t CSL ON CON.connect_id=CSL.connect_id  "
			+ " left outer JOIN sub_sp_mapping_t SSM ON (CSL.sub_sp=SSM.sub_sp AND (SSM.display_sub_sp IN (:serviceLines) OR ('') IN (:serviceLines)))"
			+ " where (CON.start_datetime_of_connect between (:startDate) AND (:endDate))"
			+ " AND (CON.country IN (:countryList) OR ('') IN (:countryList)) "
			+ " AND ((CON.primary_owner in (:userIds) OR CSOL.secondary_owner in (:userIds)) OR ('') in (:userIds)) ", nativeQuery = true)
	List<String> findByPartnerConnects(@Param("startDate") Timestamp startDate,
			@Param("endDate") Timestamp endDate,
			@Param("userIds") List<String> userId,
			@Param("displayGeography") String displayGeography,
			@Param("countryList") List<String> countryList,
			@Param("serviceLines") List<String> serviceLines);
	
	/**
	 * This Method is used to find count of customer connects for the given input parameters in subSp wise
	 * 
	 * @param startDate
	 * @param endDate
	 * @param userIds
	 * @param iouList
	 * @param displayGeography
	 * @param countryList
	 * @param serviceLines
	 * @return
	 */
	@Query(value = "select count(distinct(CON.connect_id)), COALESCE(display_sub_sp, 'SubSp Not Defined') from connect_t CON "
			+ " left outer join connect_secondary_owner_link_t CSOL ON CON.connect_id=CSOL.connect_id JOIN customer_master_t CMT ON CMT.customer_id=CON.customer_id "
			+ " JOIN iou_customer_mapping_t ICMT ON (CMT.iou=ICMT.iou AND (ICMT.display_iou IN (:iouList) OR ('') IN (:iouList))) "
			+ " JOIN geography_mapping_t GMT ON (CMT.geography=GMT.geography AND (GMT.display_geography = (:displayGeography) OR ('') = (:displayGeography))) "
			+ " left outer join connect_sub_sp_link_t CSL on CON.connect_id=CSL.connect_id "
			+ " left outer JOIN sub_sp_mapping_t SSM on (CSL.sub_sp=SSM.sub_sp AND (SSM.display_sub_sp IN (:serviceLines) OR ('') IN (:serviceLines))) "
			+ " where CON.start_datetime_of_connect between (:startDate) AND (:endDate) "
			+ " AND (CON.country IN (:countryList) OR ('') IN (:countryList)) AND ((CON.primary_owner in (:userIds) "
			+ "  OR CSOL.secondary_owner in (:userIds)) OR ('') in (:userIds)) group by SSM.display_sub_sp ", nativeQuery = true)
	List<Object[]> findBySubSpCustomerConnectSummaryDetails(
			@Param("startDate") Timestamp startDate,
			@Param("endDate") Timestamp endDate,
			@Param("userIds") List<String> userIds,
			@Param("iouList") List<String> iouList,
			@Param("displayGeography") String displayGeography,
			@Param("countryList") List<String> countryList,
			@Param("serviceLines") List<String> serviceLines);
	
	
	/**
	 * This Method is used to find count of customer connects for the given input parameters in subSp wise
	 * 
	 * @param startDate
	 * @param endDate
	 * @param userIds
	 * @param iouList
	 * @param displayGeography
	 * @param countryList
	 * @param serviceLines
	 * @return
	 */
	@Query(value = "select count(distinct(CON.connect_id)), COALESCE(display_sub_sp, 'SubSp Not Defined') from connect_t CON "
			+ " JOIN customer_master_t CMT ON CMT.customer_id=CON.customer_id "
			+ " JOIN iou_customer_mapping_t ICMT ON (CMT.iou=ICMT.iou AND (ICMT.display_iou IN (:iouList) OR ('') IN (:iouList))) "
			+ " JOIN geography_mapping_t GMT ON (CMT.geography=GMT.geography AND (GMT.display_geography = (:displayGeography) OR ('') = (:displayGeography))) "
			+ " left outer join connect_sub_sp_link_t CSL on CON.connect_id=CSL.connect_id "
			+ " left outer JOIN sub_sp_mapping_t SSM on (CSL.sub_sp=SSM.sub_sp AND (SSM.display_sub_sp IN (:serviceLines) OR ('') IN (:serviceLines))) "
			+ " where CON.start_datetime_of_connect between (:startDate) AND (:endDate) "
			+ " AND (CON.country IN (:countryList) OR ('') IN (:countryList)) group by SSM.display_sub_sp ", nativeQuery = true)
	List<Object[]> findSubSpCustomerConnectsSummaryDetails(
			@Param("startDate") Timestamp startDate,
			@Param("endDate") Timestamp endDate,
			@Param("iouList") List<String> iouList,
			@Param("displayGeography") String displayGeography,
			@Param("countryList") List<String> countryList,
			@Param("serviceLines") List<String> serviceLines);
	
	
	/**
	 * This Method is used to find count of Partner connects for the given input parameters in subSp wise
	 * 
	 * @param startDate
	 * @param endDate
	 * @param userIds
	 * @param displayGeography
	 * @param countryList
	 * @param serviceLines
	 * @return
	 */
	@Query(value = "select count(distinct(CON.connect_id)), COALESCE(SSM.display_sub_sp, 'SubSp Not Defined') from connect_t CON "
			+ " left outer join connect_secondary_owner_link_t CSOL ON CON.connect_id=CSOL.connect_id "
			+ " JOIN partner_master_t PAT ON PAT.partner_id=CON.partner_id "
			+ " JOIN geography_mapping_t GMT ON (PAT.geography=GMT.geography AND (GMT.display_geography = (:displayGeography) OR ('') = (:displayGeography))) "
			+ " left outer Join connect_sub_sp_link_t CSL ON CON.connect_id=CSL.connect_id "
			+ " left outer JOIN sub_sp_mapping_t SSM ON (CSL.sub_sp=SSM.sub_sp AND (SSM.display_sub_sp IN (:serviceLines) OR ('') IN (:serviceLines))) "
			+ " where CON.start_datetime_of_connect between (:startDate) AND (:endDate) "
			+ " AND (CON.country IN (:countryList) OR ('') IN (:countryList)) "
			+ " AND ((CON.primary_owner IN (:userIds) OR CSOL.secondary_owner IN (:userIds)) OR ('') IN (:userIds)) "
			+ " group by SSM.display_sub_sp  ", nativeQuery = true)
	List<Object[]> findBySubSpPartnerConnectSummaryDetails(
			@Param("startDate") Timestamp startDate,
			@Param("endDate") Timestamp endDate,
			@Param("userIds") List<String> userIds,
			@Param("displayGeography") String displayGeography,
			@Param("countryList") List<String> countryList,
			@Param("serviceLines") List<String> serviceLines);
	
	
	/**
	 * This Method is used to find count of Partner connects for the given input parameters in subSp wise
	 * 
	 * @param startDate
	 * @param endDate
	 * @param displayGeography
	 * @param countryList
	 * @param serviceLines
	 * @return
	 */
	@Query(value = "select count(distinct(CON.connect_id)), COALESCE(SSM.display_sub_sp, 'SubSp Not Defined') from connect_t CON "
			+ " JOIN partner_master_t PAT ON PAT.partner_id=CON.partner_id "
			+ " JOIN geography_mapping_t GMT ON (PAT.geography=GMT.geography AND (GMT.display_geography = (:displayGeography) OR ('') = (:displayGeography))) "
			+ " left outer Join connect_sub_sp_link_t CSL ON CON.connect_id=CSL.connect_id "
			+ " left outer JOIN sub_sp_mapping_t SSM ON (CSL.sub_sp=SSM.sub_sp AND (SSM.display_sub_sp IN (:serviceLines) OR ('') IN (:serviceLines))) "
			+ " where CON.start_datetime_of_connect between (:startDate) AND (:endDate) "
			+ " AND (CON.country IN (:countryList) OR ('') IN (:countryList)) group by SSM.display_sub_sp  ", nativeQuery = true)
	List<Object[]> findSubSpPartnerConnectsSummaryDetails(
			@Param("startDate") Timestamp startDate,
			@Param("endDate") Timestamp endDate,
			@Param("displayGeography") String displayGeography,
			@Param("countryList") List<String> countryList,
			@Param("serviceLines") List<String> serviceLines);
	

	/**
	 * This method is used to get customer connect summary details by geography wise for the given input parameters
	 * 
	 * @param startDate
	 * @param endDate
	 * @param userIds
	 * @param iouList
	 * @param geoList
	 * @param countryList
	 * @param serviceLines
	 * @return
	 */
	@Query(value = "select count(distinct(CON.connect_id)), gmt.display_geography from connect_t CON "
			+ " left outer join connect_secondary_owner_link_t CSOL ON CON.connect_id=CSOL.connect_id "
			+ " JOIN customer_master_t CMT ON CMT.customer_id=CON.customer_id "
			+ " JOIN iou_customer_mapping_t ICMT ON (CMT.iou=ICMT.iou AND (ICMT.display_iou IN (:iouList) OR ('') IN (:iouList))) "
			+ " JOIN geography_mapping_t GMT ON (CMT.geography=GMT.geography AND (GMT.display_geography = (:displayGeography) OR ('') = (:displayGeography))) "
			+ " left outer join connect_sub_sp_link_t CSL on CON.connect_id=CSL.connect_id "
			+ " left outer JOIN sub_sp_mapping_t SSM on (CSL.sub_sp=SSM.sub_sp AND (SSM.display_sub_sp IN (:serviceLines) OR ('') IN (:serviceLines))) "
			+ " where CON.start_datetime_of_connect between (:startDate) AND (:endDate) "
			+ " AND (CON.country IN (:countryList) OR ('') IN (:countryList)) AND ((CON.primary_owner IN (:userIds)"
			+ " OR CSOL.secondary_owner IN (:userIds)) OR ('') IN (:userIds)) group by gmt.display_geography ", nativeQuery = true)
	List<Object[]> findByGeographyCustomerConnectSummaryDetails(
			@Param("startDate") Timestamp startDate,
			@Param("endDate") Timestamp endDate,
			@Param("userIds") List<String> userIds,
			@Param("iouList") List<String> iouList,
			@Param("displayGeography") String displayGeography,
			@Param("countryList") List<String> countryList,
			@Param("serviceLines") List<String> serviceLines);
	
	
	/**
	 * This method is used to get customer connect summary details by geography wise for the given input parameters
	 * 
	 * @param startDate
	 * @param endDate
	 * @param userIds
	 * @param iouList
	 * @param geoList
	 * @param countryList
	 * @param serviceLines
	 * @return
	 */
	@Query(value = "select count(distinct(CON.connect_id)), gmt.display_geography from connect_t CON "
			+ " JOIN customer_master_t CMT ON CMT.customer_id=CON.customer_id "
			+ " JOIN iou_customer_mapping_t ICMT ON (CMT.iou=ICMT.iou AND (ICMT.display_iou IN (:iouList) OR ('') IN (:iouList))) "
			+ " JOIN geography_mapping_t GMT ON (CMT.geography=GMT.geography AND (GMT.display_geography = (:displayGeography) OR ('') = (:displayGeography))) "
			+ " left outer join connect_sub_sp_link_t CSL on CON.connect_id=CSL.connect_id "
			+ " left outer JOIN sub_sp_mapping_t SSM on (CSL.sub_sp=SSM.sub_sp AND (SSM.display_sub_sp IN (:serviceLines) OR ('') IN (:serviceLines))) "
			+ " where CON.start_datetime_of_connect between (:startDate) AND (:endDate) "
			+ " AND (CON.country IN (:countryList) OR ('') IN (:countryList)) group by gmt.display_geography ", nativeQuery = true)
	List<Object[]> findGeographyCustomerConnectsSummaryDetails(
			@Param("startDate") Timestamp startDate,
			@Param("endDate") Timestamp endDate,
			@Param("iouList") List<String> iouList,
			@Param("displayGeography") String displayGeography,
			@Param("countryList") List<String> countryList,
			@Param("serviceLines") List<String> serviceLines);
	
	
	/**
	 * This method is used to get partner connect summary details by geography wise for the given input parameters
	 * 
	 * @param startDate
	 * @param endDate
	 * @param userIds
	 * @param displayGeography
	 * @param countryList
	 * @param serviceLines
	 * @return
	 */
	@Query(value = "select count(distinct(CON.connect_id)), gmt.display_geography from connect_t CON "
			+ " left outer join connect_secondary_owner_link_t CSOL ON CON.connect_id=CSOL.connect_id "
			+ " JOIN partner_master_t PAT ON PAT.partner_id=CON.partner_id "
			+ " JOIN geography_mapping_t GMT ON (PAT.geography=GMT.geography AND (GMT.display_geography =(:displayGeography) OR ('') = (:displayGeography))) "
			+ " left outer Join connect_sub_sp_link_t CSL ON CON.connect_id=CSL.connect_id "
			+ " left outer JOIN sub_sp_mapping_t SSM ON (CSL.sub_sp=SSM.sub_sp AND (SSM.display_sub_sp IN (:serviceLines) OR ('') IN (:serviceLines))) "
			+ " where CON.connect_category='PARTNER' AND CON.start_datetime_of_connect between (:startDate) AND (:endDate)"
			+ "  AND (CON.country IN (:countryList) OR ('') IN (:countryList))"
			+ "  AND ((CON.primary_owner IN (:userIds) OR CSOL.secondary_owner IN (:userIds)) OR ('') IN (:userIds)) "
			+ " group by gmt.display_geography", nativeQuery = true)
	List<Object[]> findByGeographyPartnerConnectSummaryDetails(
			@Param("startDate") Timestamp startDate,
			@Param("endDate") Timestamp endDate,
			@Param("userIds") List<String> userIds,
			@Param("displayGeography") String displayGeography,
			@Param("countryList") List<String> countryList,
			@Param("serviceLines") List<String> serviceLines);
	
	
	/**
	 * This method is used to get partner connect summary details by geography wise for the given input parameters
	 * 
	 * @param startDate
	 * @param endDate
	 * @param displayGeography
	 * @param countryList
	 * @param serviceLines
	 * @return
	 */
	@Query(value = "select count(distinct(CON.connect_id)), gmt.display_geography from connect_t CON "
			+ " JOIN partner_master_t PAT ON PAT.partner_id=CON.partner_id "
			+ " JOIN geography_mapping_t GMT ON (PAT.geography=GMT.geography AND (GMT.display_geography =(:displayGeography) OR ('') = (:displayGeography))) "
			+ " left outer Join connect_sub_sp_link_t CSL ON CON.connect_id=CSL.connect_id "
			+ " left outer JOIN sub_sp_mapping_t SSM ON (CSL.sub_sp=SSM.sub_sp AND (SSM.display_sub_sp IN (:serviceLines) OR ('') IN (:serviceLines))) "
			+ " where CON.connect_category='PARTNER' AND CON.start_datetime_of_connect between (:startDate) AND (:endDate)"
			+ "  AND (CON.country IN (:countryList) OR ('') IN (:countryList))"
			+ " group by gmt.display_geography", nativeQuery = true)
	List<Object[]> findGeographyPartnerConnectsSummaryDetails(
			@Param("startDate") Timestamp startDate,
			@Param("endDate") Timestamp endDate,
			@Param("displayGeography") String displayGeography,
			@Param("countryList") List<String> countryList,
			@Param("serviceLines") List<String> serviceLines);
	
	/**
	 * This method is used to get customer connect summary details by Iou wise for the given input parameters
	 * 
	 * @param startDate
	 * @param endDate
	 * @param userIds
	 * @param iouList
	 * @param displayGeography
	 * @param countryList
	 * @param serviceLines
	 * @return
	 */
	@Query(value = "select count(distinct(CON.connect_id)),display_iou from connect_t CON "
			+ "	 LEFT outer join connect_secondary_owner_link_t CSOL ON CON.connect_id=CSOL.connect_id"
			+ "  JOIN customer_master_t CMT ON  CMT.customer_id=CON.customer_id"
			+ "  JOIN iou_customer_mapping_t ICMT ON (CMT.iou=ICMT.iou AND (ICMT.display_iou IN (:iouList) OR ('') IN (:iouList))) "
			+ "  JOIN geography_mapping_t GMT ON (CMT.geography=GMT.geography AND (GMT.display_geography = (:displayGeography) OR ('') = (:displayGeography))) "
			+ "  LEFT outer JOIN connect_sub_sp_link_t CSL on CON.connect_id=CSL.connect_id "
			+ "  LEFT outer JOIN sub_sp_mapping_t SSM on (CSL.sub_sp=SSM.sub_sp AND (SSM.display_sub_sp IN (:serviceLines) OR ('') IN (:serviceLines))) "
			+ "  where CON.connect_category='CUSTOMER' AND CON.start_datetime_of_connect between (:startDate) AND (:endDate) AND "
			+ "  (CON.country IN (:countryList) OR ('') IN (:countryList)) AND"
			+ " ((CON.primary_owner IN (:userIds) OR CSOL.secondary_owner IN (:userIds)) OR ('') IN (:userIds))"
			+ " group by display_iou", nativeQuery = true)
	List<Object[]> findByIouConnectSummaryReport(
			@Param("startDate") Timestamp startDate,
			@Param("endDate") Timestamp endDate,
			@Param("userIds") List<String> userIds,
			@Param("iouList") List<String> iouList,
			@Param("displayGeography") String displayGeography,
			@Param("countryList") List<String> countryList,
			@Param("serviceLines") List<String> serviceLines);
	
	
	/**
	 * This method is used to get customer connect summary details by Iou wise for the given input parameters
	 * 
	 * @param startDate
	 * @param endDate
	 * @param userIds
	 * @param iouList
	 * @param displayGeography
	 * @param countryList
	 * @param serviceLines
	 * @return
	 */
	@Query(value = "select count(distinct(CON.connect_id)),display_iou from connect_t CON "
			+ "  JOIN customer_master_t CMT ON  CMT.customer_id=CON.customer_id"
			+ "  JOIN iou_customer_mapping_t ICMT ON (CMT.iou=ICMT.iou AND (ICMT.display_iou IN (:iouList) OR ('') IN (:iouList))) "
			+ "  JOIN geography_mapping_t GMT ON (CMT.geography=GMT.geography AND (GMT.display_geography = (:displayGeography) OR ('') = (:displayGeography))) "
			+ "  LEFT outer JOIN connect_sub_sp_link_t CSL on CON.connect_id=CSL.connect_id "
			+ "  LEFT outer JOIN sub_sp_mapping_t SSM on (CSL.sub_sp=SSM.sub_sp AND (SSM.display_sub_sp IN (:serviceLines) OR ('') IN (:serviceLines))) "
			+ "  where CON.connect_category='CUSTOMER' AND CON.start_datetime_of_connect between (:startDate) AND (:endDate) AND "
			+ "  (CON.country IN (:countryList) OR ('') IN (:countryList)) group by display_iou", nativeQuery = true)
	List<Object[]> findIouConnectsSummaryReport(
			@Param("startDate") Timestamp startDate,
			@Param("endDate") Timestamp endDate,
			@Param("iouList") List<String> iouList,
			@Param("displayGeography") String displayGeography,
			@Param("countryList") List<String> countryList,
			@Param("serviceLines") List<String> serviceLines);
	

	@Query(value = "select * from connect_t con where connect_id in (:connectIdList) order by con.connect_id ", nativeQuery = true)
	List<ConnectT> getConnectsByIds(
			@Param("connectIdList") List<String> connectIdList);

	@Query(value = "select distinct secondary_owner from connect_secondary_owner_link_t "
			+ "where connect_id=(:connectId)", nativeQuery = true)
	List<String> getSecondaryOwnerByConnectId(
			@Param("connectId") String connectId);

	/**
	 * Retrieve all connects within the timestamp provided
	 * 
	 * @param startTimestamp
	 * @param endTimestamp
	 * @return List<String>
	 */
	@Query(value = "select connect_id from connect_t where (start_datetime_of_connect between (:startDate) and (:endDate))", nativeQuery = true)
	List<String> getAllConnectsForDashbaord(
			@Param("startDate") Timestamp startTimestamp,
			@Param("endDate") Timestamp endTimestamp);

	/**
	 * Retrieve connectIds which are not present in notes_t (status is open)
	 * 
	 * @param connectIds
	 * @param startTimestamp
	 * @param endTimestamp
	 * @return List<String>
	 */
	@Query(value = "SELECT DISTINCT c.connect_id FROM connect_t c WHERE (c.start_datetime_of_connect BETWEEN (:startDate) AND (:endDate)) EXCEPT SELECT DISTINCT n.connect_id FROM notes_t n WHERE n.connect_id in (:connectIds)", nativeQuery = true)
	List<String> getAllConnectsForDashbaordStatusOpen(
			@Param("connectIds") List<String> connectIds,
			@Param("startDate") Timestamp startTimestamp,
			@Param("endDate") Timestamp endTimestamp);

	/**
	 * Get all connects present in the list of connectIds sorted by
	 * start_datetime_of_connect
	 * 
	 * @param connectIds
	 * @return List<ConnectT>
	 */
	List<ConnectT> findByConnectIdInOrderByStartDatetimeOfConnectAsc(
			List<String> connectIds);
	
	Page<ConnectT> findByConnectIdInOrderByStartDatetimeOfConnectAsc(
			List<String> connectIds, Pageable pageable);

	/**
	 * This query performs search of connect_t and search_keyword_t based on
	 * name and keyword. Note that results from connect_t will have is_name as
	 * 't' and results from search_keywords_t will have is_name as 'f'
	 * 
	 * @param name
	 * @param keyword
	 * @return List<Object[]>
	 */
	@Query(value = "select result, connect_id as connect, is_name , created_datetime from (select connect_name as result , connect_id , 't' as is_name, "
			+ "created_datetime from connect_t  where UPPER(connect_name) like ?1 union select SKT.search_keywords as result, "
			+ "SKT.entity_id as connect_id ,'f' as is_name , OPP.created_datetime as created_datetime from search_keywords_t SKT "
			+ "JOIN connect_t OPP on OPP.connect_id=SKT.entity_id where SKT.entity_type ='CONNECT' and UPPER(search_keywords) like ?2) "
			+ "as search order by created_datetime desc", nativeQuery = true)
	List<Object[]> findConnectNameOrKeywords(String name, String keyword);

	@Query(value = "select distinct(user_id) from (select primary_owner as user_id from connect_t  where connect_id  = (:userId) "
			+ "union select secondary_owner as user_id from connect_secondary_owner_link_t  where connect_id =(:userId)) as users", nativeQuery = true)
	List<String> findOwnersOfConnect(@Param("userId") String connectId);


	
	@Query(value = "select USER_ID, SUM(primaryConnectsCount) as PCOUNT, SUM(secondaryConnectsCount) as SCOUNT from ( "
			+ " select primary_owner as USER_ID, count(c.connect_id) as primaryConnectsCount, (0) as secondaryConnectsCount from connect_t c "
			+ " where primary_owner = (:userId) and start_datetime_of_connect between (:fromDate) and (:toDate) group by primary_owner "
			+ " union select distinct secondary_owner as USER_ID, (0) as primaryConnectsCount, count(CSOL.connect_id) as secondaryConnectsCount from connect_t c " 
			+ " JOIN connect_secondary_owner_link_t CSOL ON c.connect_id=CSOL.connect_id "
			+ " where secondary_owner = (:userId) and start_datetime_of_connect between (:fromDate) and (:toDate) group by secondary_owner "
			+ " ) AS ConnectsCount GROUP BY USER_ID",nativeQuery = true)
	Object[][] findConnectsByPrimaryOwnerOrSecondaryOwner(
			@Param("userId") String userId, 
			@Param("fromDate") Timestamp fromDate, 
			@Param("toDate") Timestamp toDate);


	@Query(value = "select CNN.* from (select CON.* from connect_t  CON where CON.start_datetime_of_connect between (:fromDate) and (:toDate) "
			+ "and (CON.primary_owner=(:owner) or (:owner)='' ) and"
			+ "	(CON.customer_id = (:customerId) or (:customerId)='') and (CON.partner_id = (:partnerId) or (:partnerId)='') "
			+ "union "
			+ "select CON.* from connect_t  CON "
			+ "JOIN connect_secondary_owner_link_t CSOLT on CON.connect_id = CSOLT.connect_id "
			+ "where CON.start_datetime_of_connect between (:fromDate) and (:toDate) and "
			+ "(CSOLT.secondary_owner=(:owner) or (:owner)='') and "
			+ "(CON.customer_id = (:customerId) or (:customerId)='') and (CON.partner_id = (:partnerId) or (:partnerId)='')) as CNN order by CNN.start_datetime_of_connect asc",nativeQuery=true)
	List<ConnectT> findForAllOwnersStartDatetimeOfConnectBetweenForCustomerOrPartner(
			@Param("owner") String owner,
			@Param("fromDate") Timestamp fromDate,
			@Param("toDate") Timestamp toDate,
			@Param("customerId") String customerId,
			@Param("partnerId") String partnerId);
	
	@Query(value = " select sum(PCOUNT) from (select distinct connect_id, SUM((connectsCount)) as PCOUNT from ( "
			+ " select c.connect_id , count(c.connect_id) as connectsCount "
			+ " from connect_t c where primary_owner in (:userIds) and start_datetime_of_connect between (:fromDate) and (:toDate) "
			+ " group by c.connect_id union select distinct CSOL.connect_id, count(CSOL.connect_id) as connectsCount from connect_t c " 
			+ " JOIN connect_secondary_owner_link_t CSOL ON c.connect_id=CSOL.connect_id where secondary_owner in (:userIds) and "
			+ " start_datetime_of_connect between (:fromDate) and (:toDate)  group by CSOL.connect_id ) " 
			+ " AS ConnectsCount GROUP BY connect_id ) as connectsSupported ", nativeQuery = true)
	BigInteger getTotalConnectsSupported(
			@Param("userIds") List<String> userIds, 
			@Param("fromDate") Timestamp fromDate, 
			@Param("toDate") Timestamp toDate);

	@Query(value = "select connect_name from connect_t CONN "
			+ "join connect_opportunity_link_id_t COPPL on CONN.connect_id=COPPL.connect_id where opportunity_id=?1",nativeQuery=true)
	List<String> findConnectNameByOpportunityId(String opportunityId);
	
	@Query(value = CONNECT_REMINDER, nativeQuery = true)
	List<Object[]> getConnectReminders();

	/**
	 * This Method is used to get the customer connectids or partner connectids for the given date range
	 * 
	 * @param fromDate
	 * @param toDate
	 * @param string
	 * @return
	 */
	@Query(value = "select distinct connect_id from connect_t CONN where "
			+ " start_datetime_of_connect between (:fromDate) and (:toDate) and CONN.connect_category=(:connectCategory)", nativeQuery = true)
	List<String> findByConnectCategory(
			@Param("fromDate") Timestamp fromDate,
			@Param("toDate") Timestamp toDate,
			@Param("connectCategory") String connectCategory);

	
	/* ---------- repository methods for smart search --------- */
	
	@Query(value = "SELECT * FROM connect_t "
			+ "WHERE UPPER(connect_name) LIKE UPPER(:term) "
			+ "ORDER BY modified_datetime DESC "
			+ "LIMIT CASE WHEN :getAll THEN null ELSE 3 END", nativeQuery = true)
	List<ConnectT> searchByConnectName(@Param("term") String term, @Param("getAll") boolean getAll);
	
	@Query(value = "SELECT * FROM connect_t "
			+ "WHERE customer_id IN (SELECT customer_id FROM customer_master_t WHERE UPPER(customer_name) LIKE UPPER(:term)) "
			+ "ORDER BY modified_datetime DESC "
			+ "LIMIT CASE WHEN :getAll THEN null ELSE 3 END", nativeQuery = true)
	List<ConnectT> searchByCustomerName(@Param("term") String term, @Param("getAll") boolean getAll);

	@Query(value = "SELECT * FROM connect_t  "
			+ "WHERE partner_id IN (SELECT partner_id FROM partner_master_t WHERE UPPER(partner_name) LIKE UPPER(:term)) "
			+ "ORDER BY modified_datetime DESC "
			+ "LIMIT CASE WHEN :getAll THEN null ELSE 3 END", nativeQuery = true)
	List<ConnectT> searchByPartnerName(@Param("term") String term, @Param("getAll") boolean getAll);

	@Query(value = "SELECT * FROM connect_t "
			+ "WHERE connect_id IN (SELECT DISTINCT(connect_id) FROM connect_sub_sp_link_t WHERE UPPER(sub_sp) LIKE UPPER(:term)) "
			+ "ORDER BY modified_datetime DESC "
			+ "LIMIT CASE WHEN :getAll THEN null ELSE 3 END", nativeQuery = true)
	List<ConnectT> searchBySubsp(@Param("term") String term, @Param("getAll") boolean getAll);

	/* ---------- ends - repository methods for smart search --------- */
	
	/**
	 * Get Connects for Users
	 * 
	 * @param users
	 * @param fromDate
	 * @param toDate
	 * @param category
	 * @return
	 */
	@Query(value = "SELECT * FROM connect_t c2 WHERE "
			+ "((c2.connect_id IN ((SELECT c1.connect_id FROM Connect_T c1 WHERE c1.primary_owner in (:users)) "
			+ "UNION (SELECT c.connect_id FROM Connect_T c, connect_secondary_owner_link_T cs "
			+ "WHERE (c.connect_id=cs.connect_id) AND (cs.secondary_owner in (:users))))) "
			+ "AND (c2.start_datetime_of_connect between (:fromDate) and (:toDate))) "
			+ "AND (c2.connect_category = (:category) OR '' = (:category)) "
			+ "ORDER BY c2.location", nativeQuery = true)
	List<ConnectT> getConnectsForUsers(@Param("users") List<String> users, @Param("fromDate") Timestamp fromDate, 
			@Param("toDate") Timestamp toDate, @Param("category") String category);
}
