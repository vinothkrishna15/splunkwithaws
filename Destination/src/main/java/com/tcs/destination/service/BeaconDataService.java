package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.destination.bean.BeaconCustomerMappingT;
import com.tcs.destination.bean.BeaconDataT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.data.repository.BeaconDataTRepository;
import com.tcs.destination.data.repository.BeaconRepository;
import com.tcs.destination.exception.DestinationException;

@Service
public class BeaconDataService {
	private static final Logger logger = LoggerFactory.getLogger(BeaconDataService.class);

	@Autowired
	BeaconRepository beaconRepository;
	
	@Autowired
	BeaconDataTRepository beaconDataTRepository;
	
	
        // TODO Auto-generated method stub
		/**
		 * This method inserts Beacon customers to the database
		 * @param beaconCustomerToInsert
		 * @return BeaconCustomerMappingT
		 * @throws Exception
		 */
		@Transactional
		public BeaconDataT addBeaconData(BeaconDataT beaconDataToInsert) throws Exception{
			BeaconDataT BeaconDataT = null;
			 List<BeaconDataT> beaconCustomers = null;
			if(beaconDataToInsert!=null){
				BeaconDataT = new BeaconDataT();
				// to find the uniqueness of the primary key (here composite key)
				beaconCustomers = beaconDataTRepository.checkBeaconDuplicatesForPKey(beaconDataToInsert.getBeaconIou(),beaconDataToInsert.getQuarter(),beaconDataToInsert.getFinancialYear(),beaconDataToInsert.getBeaconGeography(),beaconDataToInsert.getBeaconCustomerName());
				if (beaconCustomers.isEmpty()) 
	            {
					BeaconDataT.setBeaconGroupClient(beaconDataToInsert.getBeaconGroupClient());
					BeaconDataT.setBeaconIou(beaconDataToInsert.getBeaconIou());
					BeaconDataT.setQuarter(beaconDataToInsert.getQuarter());
					BeaconDataT.setFinancialYear(beaconDataToInsert.getFinancialYear());
					BeaconDataT.setTarget(beaconDataToInsert.getTarget());
					BeaconDataT.setBeaconGeography(beaconDataToInsert.getBeaconGeography());
					BeaconDataT.setBeaconCustomerName(beaconDataToInsert.getBeaconCustomerName());
	            }
	            else
	            {
	                logger.error("EXISTS: Beacon Already Exist!");
	                throw new DestinationException(HttpStatus.CONFLICT,"Beacon Already Exist!");
	            }
				BeaconDataT = beaconDataTRepository.save(BeaconDataT);
				logger.info("Beacon Saved .... "+ "beacon primary key" + BeaconDataT.getBeaconDataId());
			}
			return BeaconDataT;
		}
		
		/** 
		 * To insert beacon data into beacon_data_t
		 * @param insertList
		 * @throws Exception
		 */
		public void save(List<BeaconDataT> insertList) throws Exception 
		{
			if( !insertList.isEmpty())
			{
			   beaconDataTRepository.save(insertList);
			}
			else
			{
				logger.debug("No Beacon Data To Insert");
			}
			logger.debug("Beacon Saved...!");
		}
	}


