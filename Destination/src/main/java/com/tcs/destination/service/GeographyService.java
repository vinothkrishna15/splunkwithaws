package com.tcs.destination.service;

import java.util.List;

import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.tcs.destination.bean.ContentDTO;
import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.bean.dto.GeographyMappingDTO;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.utils.Constants;

/*
 *This service retrieves all the data from geography_mapping_t
 */
@Service
public class GeographyService {
	
	private static final Logger logger = LoggerFactory.getLogger(GeographyService.class);

	@Autowired
	GeographyRepository geographyRepository;

	@Autowired
	DozerBeanMapper beanMapper;

	public List<GeographyMappingT> findAllActive() {
		logger.debug("Inside findAllActive() GeographyService");
		return geographyRepository.findByActiveTrue();
	}

	public ContentDTO<GeographyMappingDTO> findAll() {
		logger.debug("Inside findAll() GeographyService");
		
		List<GeographyMappingDTO> geoDtos = Lists.newArrayList();
		List<GeographyMappingT> geos = (List<GeographyMappingT>) geographyRepository.findAll();
		
		for (GeographyMappingT geographyMappingT : geos) {
			GeographyMappingDTO dto = beanMapper.map(geographyMappingT, GeographyMappingDTO.class, Constants.GEOGRAPHY_MAPPING);
			geoDtos.add(dto);
		}
		
		return new ContentDTO<>(geoDtos);
	}

	public ContentDTO<String> findAllDisplayGeo() {
		logger.debug("Inside findAllActive() GeographyService");
		List<String> displayGeo = Lists.newArrayList(geographyRepository.findDisplayGeo());
		return new ContentDTO<String>(displayGeo);
	}

}
