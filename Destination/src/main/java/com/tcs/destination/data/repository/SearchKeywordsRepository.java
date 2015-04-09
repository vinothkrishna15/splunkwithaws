package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.SearchKeywordsT;

@Repository
public interface SearchKeywordsRepository extends
		CrudRepository<SearchKeywordsT, String> {

}