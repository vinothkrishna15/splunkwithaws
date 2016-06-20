package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.AuditUserFavoritesT;

@Repository
public interface AuditUserFavoritesTRepository extends CrudRepository<AuditUserFavoritesT, Long>{

}
