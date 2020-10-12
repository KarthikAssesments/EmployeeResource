package com.paypal.bfs.test.employeeserv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.paypal.bfs.test.employeeserv.api.model.Address;
import com.paypal.bfs.test.employeeserv.api.model.Employee;
import com.paypal.bfs.test.employeeserv.exception.ErrorMessage;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class EmployeeServiceTest {

	private final String CONTROLLER_PATH = "/v1/bfs/employees";

	@LocalServerPort
	private int port;
	protected String EMP_URL = null;

	@Autowired
	protected TestRestTemplate testRestTemplate;

	@PostConstruct
	public void init() {
		EMP_URL = "http://localhost:" + port + CONTROLLER_PATH;
	}

	@Test
	public void createEmployeeTest() {
		Employee emp = buildEmployee();

		ResponseEntity<Employee> response = this.testRestTemplate.exchange(EMP_URL, HttpMethod.POST,
				new HttpEntity<>(emp, getHeaders()), Employee.class);
		assertThat(response).isNotNull();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	public void getEmployeeTest() {
		createEmployeeTest();
		Integer id = 1;
		ResponseEntity<Employee> response = this.testRestTemplate.exchange(EMP_URL + "/" + id, HttpMethod.GET,
				new HttpEntity<>(getHeaders()), Employee.class);
		assertThat(response).isNotNull();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		assertEquals(response.getBody().getFirstName(),"TEST-FN1");
		assertEquals(response.getBody().getFirstName(),"TEST-LN");
		assertEquals(response.getBody().getDateOfBirth(),new Date());
		
		assertThat(response.getBody().getAddress()).isNotNull();
		
		Address addressResponse = response.getBody().getAddress();
		
		assertEquals(addressResponse.getCity(),"TESTCIty");
		assertEquals(addressResponse.getLine1(),"842 N Capitol Ave");
		assertEquals(addressResponse.getState(),"IND");
		assertEquals(addressResponse.getZipCode(),"46454");
		assertEquals(addressResponse.getCountry(),"USA");	
	}
	
	@Test
	public void employeeNotFoundTest() {
		createEmployeeTest();
		Integer id = 100;
		ResponseEntity<ErrorMessage> response = this.testRestTemplate.exchange(EMP_URL + "/" + id, HttpMethod.GET,
				new HttpEntity<>(getHeaders()), ErrorMessage.class);
		assertThat(response).isNotNull();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		assertEquals(response.getBody().getErrors().get(0).getUserMessage(),"Employee details not found for the given id: 100");
	}
	
	

	protected HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		headers.set("Accept", "application/json");
		return headers;
	}
	
	private Employee buildEmployee() {
		Employee emp = new Employee();

		emp.setFirstName("TEST-FN1");
		emp.setLastName("TEST-LN");
		emp.setDateOfBirth(new Date());

		Address address = new Address();
		address.setCity("TESTCIty");
		address.setLine1("842 N Capitol Ave");
		address.setState("IND");
		address.setZipCode("46454");
		address.setCountry("USA");

		emp.setAddress(address);
		return emp;
	}

}
