package com.tcs.destination.data.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.QueryBufferDTO;
import com.tcs.destination.service.CustomerService;
import com.tcs.destination.utils.DestinationUtils;

@Repository
public class CustomerDao 
{
	private static final Logger logger = LoggerFactory
			.getLogger(CustomerDao.class);
	
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	CustomerService customerService;

	public  final String CUSTOMER_NAME_QUERY_PREFIX = "select CMT.customer_name from customer_master_t CMT "
			+ "JOIN iou_customer_mapping_t ICMT on CMT.iou=ICMT.iou";

	public  final String TOP_REVENUE_PROJECTED_PREFIX = "select CMT.* from customer_master_t CMT, (";
	public  final String TOP_REVENUE_PROJECTED_SUFFIX = ") as TRC where CMT.customer_name = TRC.customer_name and CMT.active =TRUE order by TRC.revenue desc";

	public  final String CUSTOMER_IOU_COND_SUFFIX = "ICMT.display_iou in (";
	public  final String CUSTOMER_GEO_COND_SUFFIX = "CMT.geography in (";
	public  final String CUSTOMER_NAME_CUSTOMER_COND_SUFFIX = "CMT.customer_name in (";
	public  final String GROUP_CUSTOMER_NAME_QUERY_PREFIX = "select distinct CMT.group_customer_name from customer_master_t CMT "
			+ "JOIN iou_customer_mapping_t ICMT on CMT.iou=ICMT.iou";

	public  final String GROUP_CUSTOMER_NAME_CUSTOMER_COND_SUFFIX = "CMT.group_customer_name like ";

	public  final String ORDERBY_SUFFIX = " order by CMT.group_customer_name";
 
	/**
	 * This method is used to fetch the customer name based upon priviledge
	 * @param userId
	 * @param customerNameList
	 * @param considerGeoIou
	 * @return
	 * @throws Exception
	 */
	public ArrayList<String> getPreviledgedCustomerName(String userId,
			ArrayList<String> customerNameList, boolean considerGeoIou)
					throws Exception {
		logger.debug("Inside getPreviledgedCustomerName() method");
		HashMap<Integer, String> parameterMap = new HashMap<Integer,String>();
	    QueryBufferDTO queryBufferDTO=new QueryBufferDTO(); //DTO object used to pass query string and parameters for applying access priviledge
        queryBufferDTO = customerService.getCustomerPrevilegeQueryString(userId,
				customerNameList, considerGeoIou);
		logger.info("Query string: {}", queryBufferDTO.getQuery());
		Query customerQuery = entityManager.createNativeQuery(queryBufferDTO.getQuery());
		parameterMap=queryBufferDTO.getParameterMap();
		if(parameterMap!=null)
		{
			for(int i=1;i<=parameterMap.size();i++)
			{
				customerQuery.setParameter(i, parameterMap.get(i));
				
			}
		}
		return (ArrayList<String>) customerQuery.getResultList();
	}
	
	/**
	 * This method is used to find based upon group customer name based on privilege 
	 * @param nameWith
	 *            - string to be searched
	 * @param userId
	 *            - userId for which the privilege restrictions are to be
	 *            applied
	 * @return - List of distinct group customer names based on privileges
	 * @throws Exception
	 */
	public List<String> findByGroupCustomerNameBasedOnPrivilege(String nameWith)
			throws Exception {

	    QueryBufferDTO queryBufferDTO=new QueryBufferDTO(); //DTO object used to pass query string and parameters for applying access priviledge
	    HashMap<Integer, String> parameterMap = new HashMap<Integer,String>();
        queryBufferDTO = customerService.getGroupCustomerPrivilegeQueryString(DestinationUtils
				.getCurrentUserDetails().getUserId(), "'%" + nameWith + "%'");
	    logger.info("Query string: {}", queryBufferDTO.getQuery());
		// Execute the native revenue query string
		Query groupCustomerPrivilegeQuery = entityManager
				.createNativeQuery(queryBufferDTO.getQuery());
		parameterMap=queryBufferDTO.getParameterMap();
		if(parameterMap!=null)
		{
			for(int i=1;i<=parameterMap.size();i++)
			{
				groupCustomerPrivilegeQuery.setParameter(i, parameterMap.get(i));
				
			}
		}
		List<String> resultList = (List<String>) groupCustomerPrivilegeQuery.getResultList();

		return resultList;
	}
 
 
}