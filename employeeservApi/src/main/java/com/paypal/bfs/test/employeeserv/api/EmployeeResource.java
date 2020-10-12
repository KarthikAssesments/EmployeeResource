package com.paypal.bfs.test.employeeserv.api;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.paypal.bfs.test.employeeserv.api.model.Employee;

/**
 * Interface for employee resource operations.
 */

public interface EmployeeResource {

    /**
     * Retrieves the {@link Employee} resource by id.
     *
     * @param id employee id.
     * @return {@link Employee} resource.
     */
    @GetMapping("/v1/bfs/employees/{id}")
    public ResponseEntity<Employee> employeeGetById(@PathVariable("id") String id);

       /**
     * Creates new Employee
     *
     * @return {@link Employee} resource.
     */
    @PostMapping("/v1/bfs/employees")
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee);

}
