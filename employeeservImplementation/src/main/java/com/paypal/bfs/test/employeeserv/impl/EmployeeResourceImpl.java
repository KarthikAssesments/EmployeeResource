package com.paypal.bfs.test.employeeserv.impl;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.paypal.bfs.test.employeeserv.api.EmployeeResource;
import com.paypal.bfs.test.employeeserv.api.model.Employee;
import com.paypal.bfs.test.employeeserv.exception.DataNotFoundException;
import com.paypal.bfs.test.employeeserv.service.EmployeeService;

/**
 * Implementation class for employee resource.
 */
@RestController
public class EmployeeResourceImpl implements EmployeeResource {

	@Autowired
	private EmployeeService empService;

	@Override
	public ResponseEntity<Employee> employeeGetById(String id) {
		Integer empId;
		try {
			empId = Integer.valueOf(id);
		} catch (NumberFormatException e) {
			throw new DataNotFoundException("Employee details not found for the given id: " + id);
		}
		
		Employee employee = empService.getEmployee(empId);
		if (Objects.isNull(employee)) {
			throw new DataNotFoundException("Employee details not found for the given id: " + id);
		}
		return new ResponseEntity<>(employee, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Employee> createEmployee(Employee employee) {
		Employee emp = empService.createEmployee(employee);
		return new ResponseEntity<>(emp, HttpStatus.CREATED);
	}
}
