package com.tcs.destination.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.data.repository.SubSpRepository;

@Component
public class SubSpService {
	
	@Autowired
	SubSpRepository subSpRepository;

	public ArrayList<SubSpMappingT> findAll() {
		return (ArrayList<SubSpMappingT>) subSpRepository.findAll();
	}

}
