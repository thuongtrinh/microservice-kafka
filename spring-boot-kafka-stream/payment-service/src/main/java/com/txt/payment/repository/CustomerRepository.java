package com.txt.payment.repository;

import org.springframework.data.repository.CrudRepository;
import com.txt.payment.domain.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
}
