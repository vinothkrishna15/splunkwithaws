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

}
