package com.paypal.bfs.test.employeeserv.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paypal.bfs.test.employeeserv.api.model.Address;
import com.paypal.bfs.test.employeeserv.api.model.Employee;
import com.paypal.bfs.test.employeeserv.entity.AddressDO;
import com.paypal.bfs.test.employeeserv.entity.EmployeeDO;
import com.paypal.bfs.test.employeeserv.exception.EmployeeResourceException;
import com.paypal.bfs.test.employeeserv.repository.EmployeeRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmployeeService {

	@Autowired
	private EmployeeRepository employeeRepository;

	public Employee getEmployee(Integer id) {
		Optional<EmployeeDO> empById = employeeRepository.findById(id);
		if (empById.isPresent()) {
			return mapToEmployee(empById.get());
		}
		return null;
	}
	
	public Employee createEmployee(Employee emp) {
		final EmployeeDO empDO = mapToEmplyeeDO(emp);
		
		//check the idempotency, considering first name, last name and dob as unique parameters for a user
		
		EmployeeDO existingEmp = employeeRepository.findAllByFirstNameAndLastName(emp.getFirstName(), emp.getLastName());
		
		if(Objects.nonNull(existingEmp)) {
			log.error("Employee with the details already exists: id: {}", emp.getId());
			throw new EmployeeResourceException("Employee with the details already exists");
		}
		
		EmployeeDO newEmpDO = employeeRepository.save(empDO);
		return mapToEmployee(newEmpDO);
	}

	private Employee mapToEmployee(EmployeeDO empDO) {
		
		Employee emp = null;
		if (Objects.nonNull(empDO)) {
			emp = new Employee();
			emp.setId(Integer.valueOf(empDO.getId().toString()));
			emp.setFirstName(empDO.getFirstName());
			emp.setLastName(empDO.getLastName());
			emp.setDateOfBirth(empDO.getDateOfBirth());
			
			AddressDO addressDO = empDO.getAddressDO();
			
			Address address = new Address();
			address.setLine1(addressDO.getLine1());
			address.setLine2(addressDO.getLine2());
			address.setState(addressDO.getState());
			address.setCity(addressDO.getCity());
			address.setCountry(addressDO.getCountry());
			address.setZipCode(addressDO.getZipCode());

			emp.setAddress(address);
		}
		return emp;
	}
	
	private EmployeeDO mapToEmplyeeDO(Employee emp) {
		EmployeeDO empDO = null;
		if (Objects.nonNull(emp)) {
			empDO = new EmployeeDO();
			empDO.setFirstName(emp.getFirstName());
			empDO.setLastName(emp.getLastName());
			empDO.setDateOfBirth(emp.getDateOfBirth());
			
			Address address = emp.getAddress();
			
			AddressDO addressDO = new AddressDO();
			addressDO.setLine1(address.getLine1());
			addressDO.setLine2(address.getLine2());
			addressDO.setState(address.getState());
			addressDO.setCity(address.getCity());
			addressDO.setCountry(address.getCountry());
			addressDO.setZipCode(address.getZipCode());
			
			empDO.setAddressDO(addressDO);
		}
		return empDO;
	}

}
