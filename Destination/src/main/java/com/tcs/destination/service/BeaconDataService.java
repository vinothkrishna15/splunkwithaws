package com.tcs.destination.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.destination.bean.BeaconDataT;
import com.tcs.destination.data.repository.BeaconDataTRepository;
import com.tcs.destination.data.repository.BeaconRepository;

@Service
public class BeaconDataService {
	private static final Logger logger = LoggerFactory.getLogger(BeaconDataService.class);

	@Autowired
	BeaconRepository beaconRepository;
	
	@Autowired
	BeaconDataTRepository beaconDataTRepository;
	
	
		/**
		 * This method inserts Beacon customers to the database
		 * @param beaconCustomerToInsert
		 * @return BeaconCustomerMappingT
		 * @throws Exception
		 */
		@Transactional
		public BeaconDataT addBeaconData(BeaconDataT beaconDataToInsert) throws Exception{
			BeaconDataT BeaconDataT = null;
			if(beaconDataToInsert!=null){
				
				BeaconDataT = new BeaconDataT();
				
				BeaconDataT.setBeaconGroupClient(beaconDataToInsert.getBeaconGroupClient());
				BeaconDataT.setBeaconIou(beaconDataToInsert.getBeaconIou());
				BeaconDataT.setQuarter(beaconDataToInsert.getQuarter());
				BeaconDataT.setFinancialYear(beaconDataToInsert.getFinancialYear());
				BeaconDataT.setTarget(beaconDataToInsert.getTarget());
				BeaconDataT.setBeaconGeography(beaconDataToInsert.getBeaconGeography());
				BeaconDataT.setBeaconCustomerName(beaconDataToInsert.getBeaconCustomerName());
					
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


