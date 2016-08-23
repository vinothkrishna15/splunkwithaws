package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.PartnerContactLinkT;


@Repository
public interface PartnerContactLinkTRepository extends
		CrudRepository<PartnerContactLinkT, String> {}
