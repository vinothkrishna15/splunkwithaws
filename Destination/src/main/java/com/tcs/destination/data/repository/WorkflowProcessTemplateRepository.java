package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.WorkflowProcessTemplate;

@Repository
public interface WorkflowProcessTemplateRepository extends
		CrudRepository<WorkflowProcessTemplate, Integer> {

	@Query(value = "select * from workflow_process_template where entity_type_id = (:entityTypeId) and (user_group is null or user_group = (:userGroup)) and (user_role is null or user_role = (:userRole))", nativeQuery = true)
	WorkflowProcessTemplate findStepByEntityTypeIdAndUserRoleOrUserGroup(
			@Param("entityTypeId") Integer entityTypeId,
			@Param("userRole") String userRole,
			@Param("userGroup") String userGroup);

	WorkflowProcessTemplate findByEntityTypeIdAndUserRoleAndUserGroup(
			Integer entityTypeId, String userRole, String userGroup);

	List<WorkflowProcessTemplate> findByEntityTypeId(Integer entityTypeId);

	void findByStep(int i);

	WorkflowProcessTemplate findByEntityTypeIdAndStep(Integer type, Integer i);

	List<WorkflowProcessTemplate> findByEntityTypeIdAndStepGreaterThan(
			Integer type, Integer step);

}
