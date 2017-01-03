package com.tcs.destination.data.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.GroupCustomerT;

@Repository
public interface GroupCustomerPagingRepository extends PagingAndSortingRepository<GroupCustomerT, String> {

}
