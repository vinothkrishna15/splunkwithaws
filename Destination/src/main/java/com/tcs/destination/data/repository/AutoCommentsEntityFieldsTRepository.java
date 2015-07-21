package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AutoCommentsEntityFieldsT;

@Repository
public interface AutoCommentsEntityFieldsTRepository extends
	CrudRepository<AutoCommentsEntityFieldsT, Integer> {

	List<AutoCommentsEntityFieldsT> findByEntityIdAndIsactiveOrderByTypeAsc(Integer entityId, String isActive);

	List<AutoCommentsEntityFieldsT> findByEntityIdAndTypeAndIsactive(Integer entityId, String type, String isActive);

	@Query(value="select ACEFT.* from auto_comments_entity_fields_t ACEFT"
			+ " where ACEFT.entity_id = (select entity_id from auto_comments_entity_t where name = ?1 and isactive = 'Y')"
			+ " and ACEFT.type = ?2 and ACEFT.isactive = 'Y' order by field_id asc", nativeQuery=true)
	List<AutoCommentsEntityFieldsT> findByEntityTypeAndFieldType(String entityType, String type);
}
