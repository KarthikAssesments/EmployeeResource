package com.paypal.bfs.test.employeeserv.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paypal.bfs.test.employeeserv.entity.EmployeeDO;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeDO, Integer> {

	public EmployeeDO findAllByFirstNameAndLastName(String firstName, String lastName);
}
