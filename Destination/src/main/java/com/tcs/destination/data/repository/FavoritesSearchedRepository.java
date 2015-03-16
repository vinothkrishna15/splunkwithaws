package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.UserFavoritesT;
import com.tcs.destination.bean.UserT;

@Repository
public interface FavoritesSearchedRepository extends
		CrudRepository<UserFavoritesT, String> {
	List<UserFavoritesT> findByUserTAndEntityType(UserT usert, String entityType);
}
