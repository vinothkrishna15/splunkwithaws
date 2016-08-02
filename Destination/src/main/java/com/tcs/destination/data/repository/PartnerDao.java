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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.QueryBufferDTO;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.helper.UserAccessPrivilegeQueryBuilder;
import com.tcs.destination.service.CustomerService;
import com.tcs.destination.service.PartnerService;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DestinationUtils;

@Repository
public class PartnerDao 
{
	private static final Logger logger = LoggerFactory
			.getLogger(PartnerDao.class);

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	PartnerService partnerService;


	@Autowired
	UserAccessPrivilegeQueryBuilder userAccessPrivilegeQueryBuilder;

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
	 * This method is used to fetch the partner name based upon privilege
	 * @param userId
	 * @param customerNameList
	 * @param considerGeoIou
	 * @return
	 * @throws Exception
	 */
	public ArrayList<String> getPreviledgedPartnerName(String userId,
			ArrayList<String> partnerNameList, boolean considerGeoIou)  {
		logger.debug("Inside getPreviledgedCustomerName() method");
		HashMap<Integer, String> parameterMap = new HashMap<Integer,String>();
		QueryBufferDTO queryBufferDTO=new QueryBufferDTO(); //DTO object used to pass query string and parameters for applying access priviledge
		queryBufferDTO = getPartnerPrevilegeQueryString(userId,
				partnerNameList, considerGeoIou);
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

	private QueryBufferDTO getPartnerPrevilegeQueryString(String userId,
			ArrayList<String> partnerNameList, boolean considerGeo)  {
		logger.debug("Inside getRevenueQueryString() method");
		QueryBufferDTO queryBufferDTO=new QueryBufferDTO(); //DTO object used to pass query string and parameters for applying access priviledge
		StringBuffer queryBuffer = new StringBuffer(CUSTOMER_NAME_QUERY_PREFIX);

		HashMap<String, String> queryPrefixMap;
		try{
			if (considerGeo) {
				queryPrefixMap = userAccessPrivilegeQueryBuilder.getQueryPrefixMap(
						CUSTOMER_GEO_COND_SUFFIX, null,null,
						null);
			} else {
				queryPrefixMap = userAccessPrivilegeQueryBuilder.getQueryPrefixMap(
						null, null, null, CUSTOMER_NAME_CUSTOMER_COND_SUFFIX);
			}

			// Get WHERE clause string
			queryBufferDTO = userAccessPrivilegeQueryBuilder
					.getUserAccessPrivilegeWhereCondition(userId,
							queryPrefixMap);


			queryBuffer.append(" where ");


			if (partnerNameList != null && partnerNameList.size() > 0) {
				String customerNameQueryList = "(";
				{
					for (String customerName : partnerNameList)
						customerNameQueryList += "'"
								+ customerName.replace("\'", "\'\'") + "',";
				}
				customerNameQueryList = customerNameQueryList.substring(0,
						customerNameQueryList.length() - 1);
				customerNameQueryList += ")";

				queryBuffer
				.append(" CMT.customer_name in " + customerNameQueryList);
			}

			if(queryBufferDTO!=null)
			{
				if(queryBufferDTO.getQuery() != null && !queryBufferDTO.getQuery().isEmpty()) 
				{
					queryBuffer.append(Constants.AND_CLAUSE);
					queryBuffer.append(queryBufferDTO.getQuery());
				}
				queryBufferDTO.setQuery(queryBuffer.toString());
			}
			else
			{
				queryBufferDTO=new QueryBufferDTO();
				queryBufferDTO.setQuery(queryBuffer.toString());
				queryBufferDTO.setParameterMap(null);
			}
		}
		catch (Exception e){
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		return queryBufferDTO;
	}
}