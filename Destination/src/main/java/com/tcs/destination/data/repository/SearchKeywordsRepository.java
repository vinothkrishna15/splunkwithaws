package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.SearchKeywordsT;

@Repository
public interface SearchKeywordsRepository extends
		CrudRepository<SearchKeywordsT, String> {

	List<SearchKeywordsT> findByEntityTypeAndEntityId(String entityType, String entityId);
	
	@Query(value = "select distinct(search_keywords) from search_keywords_t where UPPER(search_keywords) like UPPER(?1) order by search_keywords asc", nativeQuery=true)
	List<String> findKeywordsWithNameContaining(String keyword);
}