package com.tcs.destination.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.bean.IouCustomerMappingT;
import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.bean.UserAccessPrivilegesT;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.data.repository.IouRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.data.repository.UserAccessPrivilegesRepository;
import com.tcs.destination.enums.PrivilegeType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.StringUtils;

/**
 * This service handles requests regarding
 * user access privileges
 */
@Service
public class UserAccessPrivilegeService {

	private static final Logger logger = LoggerFactory
			.getLogger(UserAccessPrivilegeService.class);

	@Autowired
	UserAccessPrivilegesRepository userAccessPrivilegesRepository;

	@Autowired
	GeographyRepository geographyRepository;

	@Autowired
	SubSpRepository subspRepository;

	@Autowired
	IouRepository iouRepository;

	@Autowired
	CustomerRepository custRepository;

	/**
	 * this method validates the request and saves the 
	 * privileges into the database
	 * @param privilege
	 * @return
	 * @throws Exception
	 */
	public boolean insertAccessPrivilege(UserAccessPrivilegesT privilege) throws Exception{
		logger.debug("start: inside insertAccessPrivilege() of UserAccessPrivilegeService");
		validateRequest(privilege);
		if(userAccessPrivilegesRepository.save(privilege) != null){
			return true;
		}
		logger.debug("End: inside insertAccessPrivilege() of UserAccessPrivilegeService");
		return false;
	}
	
	/**
	 * This method validates the request
	 * @param privilege
	 * @throws DestinationException
	 */
	private void validateRequest(UserAccessPrivilegesT privilege) throws DestinationException{
		logger.debug("start: inside validateRequest() of UserAccessPrivilegeService");
		String privilegeType = privilege.getPrivilegeType();
		if(StringUtils.isEmpty(privilegeType)){
			throw new DestinationException(HttpStatus.BAD_REQUEST,"Privilege Type cannot be empty");
		} else {
			boolean validPrivilegeType = false;
			for(PrivilegeType pType : PrivilegeType.values()){
				if(privilegeType.equalsIgnoreCase(pType.getValue())){
					validPrivilegeType = true;
				}
			}
			if(!validPrivilegeType){
				throw new DestinationException(HttpStatus.BAD_REQUEST,"Privilege Type is invalid");
			}
		}

		String privilegeValue = privilege.getPrivilegeValue();
		if(StringUtils.isEmpty(privilegeValue)){
			throw new DestinationException(HttpStatus.BAD_REQUEST,"Privilege Value cannot be empty");
		} else {
			boolean validPrivilegeValue = false;
			switch (PrivilegeType.valueOf(privilegeType)){
			case GEOGRAPHY : 
				GeographyMappingT geography = geographyRepository.findOne(privilegeValue);
				if( geography==null ) {
					logger.error("Privilege Value :Geography is invalid");
					throw new DestinationException(HttpStatus.BAD_REQUEST,"Privilege Value : Geography is invalid");
				}
				break;

			case SUBSP :
				List<SubSpMappingT> subSp = subspRepository.findByDisplaySubSp(privilegeValue);
				if(subSp == null || subSp.isEmpty()) {
					logger.error("Privilege Value :displaysubsp is invalid");
					throw new DestinationException(HttpStatus.BAD_REQUEST,"Privilege Value : DisplaySubsp is invalid");
				}
				break;

			case IOU :     
				List<IouCustomerMappingT> iou = iouRepository.findByDisplayIou(privilegeValue);
				if(iou == null || iou.isEmpty()) {
					logger.error("Privilege Value :displayiou is invalid");
					throw new DestinationException(HttpStatus.BAD_REQUEST,"Privilege Value : DisplayIOU is invalid");
				}
				break;

			case CUSTOMER : 
				if(!privilegeValue.equals(Constants.GLOBAL)){
					logger.error("Wrong Privilege Value {}",privilegeValue);
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Wrong Privilege Value: " + privilegeValue 
							+ ", replace with " + Constants.GLOBAL);
				} else {
					CustomerMasterT customer = custRepository.findByCustomerName(privilegeValue);
					if(customer == null){
						logger.error("Customers not found for the customer name: {}",privilegeValue);
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"Customers not found for the customer name: " + privilegeValue);
					}
				}

				break;

			case GROUP_CUSTOMER :
				List<CustomerMasterT> customerList = custRepository
				.findByGroupCustomerNameIgnoreCaseContainingAndGroupCustomerNameIgnoreCaseNotLikeOrderByGroupCustomerNameAsc(privilegeValue, Constants.UNKNOWN_CUSTOMER);

				if (customerList == null && customerList.isEmpty()) {
					logger.error(
							"Customers not found for the group customer name: {}",
							privilegeValue);
					throw new DestinationException(
							HttpStatus.BAD_REQUEST,
							"Customers not found for the group customer name: "
									+ privilegeValue);

				}
				break;
			}
		}		
		logger.debug("End: inside validateRequest() of UserAccessPrivilegeService");
	}
}
