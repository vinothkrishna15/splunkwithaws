package com.tcs.destination.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.CompetitorMappingT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.GroupCustomerT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.bean.dto.ImageUploadDTO;
import com.tcs.destination.data.repository.CompetitorRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.GroupCustomerRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.ErrorConstants;
import com.tcs.destination.utils.PropertyUtil;
import com.tcs.destination.utils.StringUtils;

/**
 * This service downloads rgs data from database into an excel
 */
/**
 * @author bnpp
 *
 */
@Service
public class DownloadUploadService 
{

	@Autowired
	CustomerRepository customerRepo;
	
	@Autowired
	GroupCustomerRepository grpCustRepo;
	
	@Autowired
	CompetitorRepository competitorRepo;
	
	@Autowired
	UserRepository userRepo;

	@Autowired
	PartnerRepository partnerRepo;
	
	private static final Logger logger = LoggerFactory.getLogger(DownloadUploadService .class);

	/**
	 * this method downloads the logo
	 * @return
	 * @throws Exception
	 */
	public byte[] getLogo(String type, String id) throws Exception 
	{
		logger.debug("Begin:Inside getLogo() method of DownloadService"); 
			if(StringUtils.isEmpty(type) || StringUtils.isEmpty(id)) {
				throw new DestinationException(HttpStatus.BAD_REQUEST, PropertyUtil.getProperty(ErrorConstants.ERR_LOGO_BAD_REQUEST));
			}
			
			byte[] bytes = null;
			if(type.equals("CUSTOMER")) {
				bytes = customerRepo.getLogo(id);
			} else if(type.equals("GRP_CUSTOMER")) {
				bytes = grpCustRepo.getLogo(id);
			} else if(type.equals("PARTNER")) {
				bytes = partnerRepo.getLogo(id);
			} else if(type.equals("COMPETITOR")) {
				bytes = competitorRepo.getLogo(id);
			} else if(type.equals("USER")) {
				bytes = userRepo.getLogo(id);
			}
			
			if(bytes == null) {
				throw new DestinationException(HttpStatus.NOT_FOUND, PropertyUtil.getProperty(ErrorConstants.ERR_LOGO_NOT_FOUND));
			}
		logger.debug("End:Inside getLogo() method of DownloadService"); 
		return bytes;
	}

	public void putLogo(ImageUploadDTO uploadDTO) {
		logger.debug("Begin:Inside getLogo() method of DownloadService"); 
		String type = uploadDTO.getType();
		String id = uploadDTO.getId();
		if(StringUtils.isEmpty(type) || StringUtils.isEmpty(id)) {
			throw new DestinationException(HttpStatus.BAD_REQUEST, PropertyUtil.getProperty(ErrorConstants.ERR_LOGO_BAD_REQUEST));
		}
		
		byte[] logo = DestinationUtils.decodeBase64(uploadDTO.getImgData());
		if(type.equals("CUSTOMER")) {
			CustomerMasterT customer = customerRepo.findOne(id);
			customer.setLogo(logo);
			customerRepo.save(customer);
		} else if(type.equals("GRP_CUSTOMER")) {
			GroupCustomerT grpCustomer = grpCustRepo.findOne(id);
			grpCustomer.setLogo(logo);
			grpCustRepo.save(grpCustomer);
		} else if(type.equals("COMPETITOR")) {
			CompetitorMappingT competitor = competitorRepo.findOne(id);
			competitor.setLogo(logo);
			competitorRepo.save(competitor);
		} else if(type.equals("USER")) {
			UserT user = userRepo.findOne(id);
			user.setUserPhoto(logo);
			userRepo.save(user);
		}
		
	logger.debug("End:Inside getLogo() method of DownloadService"); 
		
	}
}
