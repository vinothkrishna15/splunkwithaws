package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.NotesT;

/**
 * @author bnpp
 *
 */
@Repository
public interface NotesTRepository extends
		CrudRepository<NotesT, String> {

	/**
	 * Retrieve connectIds which are present in notes_t (status is closed) 
	 * 
	 * @param connectIds
	 * @return List<String>
	 */
	@Query(value = "select distinct n.connect_id from notes_t n where n.connect_id IN (:connectIds)",nativeQuery = true)
	List<String> getAllConnectsForDashbaordStatusClosed(@Param("connectIds") List<String> connectIds);
	
	List<NotesT> findByEntityTypeAndConnectIdIsNotNull(String entityType);

	List<NotesT> findByOpportunityId(String opportunityId);
	
	/**
	 * This Method is used to get notes updated for the given opportunityId
	 * @param opportunityId
	 * @return
	 */
	@Query(value = "select distinct notes_updated from notes_t where opportunity_id=?1",nativeQuery = true)
	List<String> findDealRemarksNotesByOpportunityId(String opportunityId);

	/**
	 * This Method is used to get notes updated for the given connectId
	 * @param connectId
	 * @return
	 */
	@Query(value = "select distinct notes_updated from notes_t where connect_id=?1",nativeQuery = true)
	List<String> findConnectNotesByConnectId(String connectId);

	/**
	 * This Method is used to get notes updated for the given taskId
	 * @param taskId
	 * @return
	 */
	@Query(value = "select distinct notes_updated from notes_t where task_id=?1",nativeQuery = true)
	List<String> findNotesUpdatedByNotesId(String taskId);

}
