package com.tcs.destination.data.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.QueryBufferDTO;
import com.tcs.destination.helper.UserAccessPrivilegeQueryBuilder;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DestinationUtils;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OpportunityDao 
{
	private static final Logger logger = LoggerFactory
			.getLogger(OpportunityDao.class);
	
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	UserAccessPrivilegeQueryBuilder userAccessPrivilegeQueryBuilder;

 public final String OPPORTUNITY_QUERY_PREFIX = "select distinct(OPP.opportunity_id) from opportunity_t OPP "
		+ "LEFT JOIN geography_country_mapping_t GCMT on OPP.country =GCMT.country "
		+ "LEFT JOIN customer_master_t CMT on OPP.customer_id = CMT.customer_id  "
		+ "LEFT JOIN iou_customer_mapping_t ICMT on CMT.iou=ICMT.iou "
		+ "LEFT JOIN opportunity_sub_sp_link_t OSSLT on OSSLT.opportunity_id=OPP.opportunity_id "
		+ "LEFT JOIN sub_sp_mapping_t SSMT on OSSLT.sub_sp=SSMT.sub_sp where";

 public final String OPPORTUNITY_GEO_INCLUDE_COND_PREFIX = "GCMT.geography in (";
 public final String OPPORTUNITY_SUBSP_INCLUDE_COND_PREFIX = "SSMT.display_sub_sp in (";
 public final String OPPORTUNITY_IOU_INCLUDE_COND_PREFIX = "ICMT.display_iou in (";
 public final String OPPORTUNITY_CUSTOMER_INCLUDE_COND_PREFIX = "CMT.customer_name in (";
 
 
 
 /**
  * This method is used to fetch the opportunity details based upon the priviledge
  * @param opportunityIds
  * @return
  * @throws Exception
  */
     public List<String> getPriviledgedOpportunityId(List<String> opportunityIds)
			throws Exception 
	{       
		    logger.debug("Inside setPreviledgeConstraints(opportunityIds) method");
		    HashMap<Integer, String> parameterMap = new HashMap<Integer,String>();
		    QueryBufferDTO queryBufferDTO=new QueryBufferDTO(); //DTO object used to pass query string and parameters for applying access priviledge
			queryBufferDTO = getOpportunityPriviledgeString(DestinationUtils.getCurrentUserDetails().getUserId(), opportunityIds);
		    logger.info("Query string: {}", queryBufferDTO.getQuery());
			Query opportunityQuery = entityManager.createNativeQuery(queryBufferDTO.getQuery());
			parameterMap=queryBufferDTO.getParameterMap();
			if(parameterMap!=null)
			{
				for(int i=1;i<=parameterMap.size();i++)
				{
					opportunityQuery.setParameter(i, parameterMap.get(i));
					
				}
			}
			return opportunityQuery.getResultList();
}
 
/**
 *  This method is used to fetch the opportunity details based upon the priviledge
 * @param opportunityId
 * @return
 * @throws Exception
 */
	 public List<String> getPriviledgedOpportunityId(String opportunityId) throws Exception {
		    logger.debug("Inside setPreviledgeConstraints(opportunityId) method");
		    HashMap<Integer, String> parameterMap = new HashMap<Integer,String>();
		    QueryBufferDTO queryBufferDTO=new QueryBufferDTO(); //DTO object used to pass query string and parameters for applying access priviledge
            List<String> opportunityIds = new ArrayList<String>();
			opportunityIds.add(opportunityId);
			queryBufferDTO  = getOpportunityPriviledgeString(DestinationUtils
					.getCurrentUserDetails().getUserId(), opportunityIds);
			logger.info("Query string: {}", queryBufferDTO.getQuery());
			Query opportunityQuery = entityManager.createNativeQuery(queryBufferDTO.getQuery(),
					OpportunityT.class);
			parameterMap=queryBufferDTO.getParameterMap();
			if(parameterMap!=null)
			{
			 for(int i=1;i<=parameterMap.size();i++)
			 {
				opportunityQuery.setParameter(i, parameterMap.get(i));
				
			 }
			}

			return opportunityQuery.getResultList();
	}
	 
		/**
		 * This method set the priviledge conditions based upon which opportunity details is to fetched 
		 * @param userId
		 * @param opportunityIds
		 * @return
		 * @throws Exception
		 */

		public QueryBufferDTO getOpportunityPriviledgeString(String userId,
				List<String> opportunityIds) throws Exception {
			logger.debug("Inside getOpportunityPriviledgeString() method");
			QueryBufferDTO queryBufferDTO=new QueryBufferDTO(); //DTO object used to pass query string and parameters for applying access priviledge
	        StringBuffer queryBuffer = new StringBuffer(OPPORTUNITY_QUERY_PREFIX);
			
			// Get user access privilege groups

			HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
					.getQueryPrefixMap(OPPORTUNITY_GEO_INCLUDE_COND_PREFIX,
							OPPORTUNITY_SUBSP_INCLUDE_COND_PREFIX,
							OPPORTUNITY_IOU_INCLUDE_COND_PREFIX,
							OPPORTUNITY_CUSTOMER_INCLUDE_COND_PREFIX);

			// Get WHERE clause string
			queryBufferDTO= userAccessPrivilegeQueryBuilder
					.getUserAccessPrivilegeWhereCondition(userId, queryPrefixMap);

			if (opportunityIds.size() > 0) {
				String oppIdList = "(";
				{
					for (String opportunityId : opportunityIds)
						oppIdList += "'" + opportunityId + "',";
				}
				oppIdList = oppIdList.substring(0, oppIdList.length() - 1);
				oppIdList += ")";


					queryBuffer.append(" OPP.opportunity_id in " + oppIdList);
				}

	               if(queryBufferDTO!=null)
	               {
				    if (queryBufferDTO.getQuery() != null && !queryBufferDTO.getQuery().isEmpty()) 
				    {
					 queryBuffer.append(Constants.AND_CLAUSE + queryBufferDTO.getQuery());
					}
				    queryBufferDTO.setQuery(queryBuffer.toString());
	               }
	               else
				   {
					queryBufferDTO=new QueryBufferDTO();
					queryBufferDTO.setQuery(queryBuffer.toString());
					queryBufferDTO.setParameterMap(null);
				   }
				   return queryBufferDTO;
		}
}