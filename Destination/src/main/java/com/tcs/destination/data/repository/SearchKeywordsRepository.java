package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.SearchKeywordsT;

@Repository
public interface SearchKeywordsRepository extends
		CrudRepository<SearchKeywordsT, String> {

	List<SearchKeywordsT> findByEntityTypeAndEntityId(String entityType, String entityId);
}