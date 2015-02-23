package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.ConnectT;

/**
 * @author bnpp
 *
 */
public interface ConnectRepository extends CrudRepository<ConnectT, String> {
	
}
