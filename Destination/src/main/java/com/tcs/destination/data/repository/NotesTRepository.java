package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.NotesT;

/**
 * @author bnpp
 *
 */
@Repository
public interface NotesTRepository extends
		CrudRepository<NotesT, String> {



}
