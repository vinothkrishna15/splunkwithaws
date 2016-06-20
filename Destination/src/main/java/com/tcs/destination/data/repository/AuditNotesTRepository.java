package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditNotesT;

@Repository
public interface AuditNotesTRepository extends CrudRepository<AuditNotesT, Long> {

}
