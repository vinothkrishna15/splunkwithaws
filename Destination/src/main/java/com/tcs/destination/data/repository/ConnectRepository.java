package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.tcs.destination.bean.ConnectT;


@Repository
public interface ConnectRepository extends CrudRepository<ConnectT, String> {
	
	List<ConnectT> findByConnectNameIgnoreCaseLike(String name);

}
