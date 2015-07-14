package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AutoCommentsEntityFieldsT;

@Repository
public interface AutoCommentsEntityFieldsTRepository extends
	CrudRepository<AutoCommentsEntityFieldsT, Integer> {

	List<AutoCommentsEntityFieldsT> findByEntityIdAndIsactiveOrderByTypeAsc(Integer entityId, String isActive);
}
