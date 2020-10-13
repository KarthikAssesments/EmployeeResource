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
@SpringBootTest(classes = EmployeeservApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
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
		Employee emp = buildEmployee("TEST-FN2","TEST-LN2","TESTCIty2","842 N Capitol Ave2","IND2","464542","USA");

		ResponseEntity<Employee> response = this.testRestTemplate.exchange(EMP_URL, HttpMethod.POST,
				new HttpEntity<>(emp, getHeaders()), Employee.class);
		assertThat(response).isNotNull();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	
	@Test
	public void shouldGetBadRequestWithEmptyInputRequestBodyWhenCreateUserTest() {
		Employee emp = null;

		ResponseEntity<ErrorMessage> response = this.testRestTemplate.exchange(EMP_URL, HttpMethod.POST,
				new HttpEntity<>(emp, getHeaders()), ErrorMessage.class);
		assertThat(response).isNotNull();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}
	
	
	@Test
	public void shouldGetBadRequestWithErrorsForInvalidInputRequestWhenCreateUserTest() {
		Employee emp = buildEmployee(null,null,null,null,null,null,null);

		ResponseEntity<ErrorMessage> response = this.testRestTemplate.exchange(EMP_URL, HttpMethod.POST,
				new HttpEntity<>(emp, getHeaders()), ErrorMessage.class);
		assertThat(response).isNotNull();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		assertEquals(response.getBody().getErrors().size(),7);
	}
	
	
	@Test
	public void shouldGetConflictStatusCodeWhenCreatingSameUserTwice() {
		
		Employee employeeOne = buildEmployee("TEST-FN1","TEST-LN","TESTCIty","842 N Capitol Ave","IND","46454","USA");

		ResponseEntity<Employee> response = this.testRestTemplate.exchange(EMP_URL, HttpMethod.POST,
				new HttpEntity<>(employeeOne, getHeaders()), Employee.class);
		assertThat(response).isNotNull();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	
		
		Employee emp = buildEmployee("TEST-FN1","TEST-LN","TESTCIty","842 N Capitol Ave","IND","46454","USA");
		ResponseEntity<ErrorMessage> secondResponse = this.testRestTemplate.exchange(EMP_URL, HttpMethod.POST,
				new HttpEntity<>(emp, getHeaders()), ErrorMessage.class);
		assertThat(secondResponse).isNotNull();
		assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
		assertEquals(secondResponse.getBody().getErrors().get(0).getUserMessage(),"Employee with the details already exists");
	}
	
	
	@Test
	public void getEmployeeTest() {
		Employee employee = buildEmployee("TEST-FN3","TEST-LN3","TESTCIty3","842 N Capitol Ave3","IND3","464543","USAThree");

		ResponseEntity<Employee> createdResponse = this.testRestTemplate.exchange(EMP_URL, HttpMethod.POST,
				new HttpEntity<>(employee, getHeaders()), Employee.class);
		assertThat(createdResponse).isNotNull();
		assertThat(createdResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		ResponseEntity<Employee> response = this.testRestTemplate.exchange(EMP_URL + "/" + createdResponse.getBody().getId(), HttpMethod.GET,
				new HttpEntity<>(getHeaders()), Employee.class);
		assertThat(response).isNotNull();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		
		assertEquals(response.getBody().getFirstName(),"TEST-FN3");
		assertEquals(response.getBody().getLastName(),"TEST-LN3");
		assertThat(response.getBody().getDateOfBirth()).isNotNull();
		
		assertThat(response.getBody().getAddress()).isNotNull();
		
		Address addressResponse = response.getBody().getAddress();
		
		assertEquals(addressResponse.getCity(),"TESTCIty3");
		assertEquals(addressResponse.getLine1(),"842 N Capitol Ave3");
		assertEquals(addressResponse.getState(),"IND3");
		assertEquals(addressResponse.getZipCode(),"464543");
		assertEquals(addressResponse.getCountry(),"USAThree");	
	}
	
	@Test
	public void employeeNotFoundTest() {
		
		Employee employee = buildEmployee("TEST-FN4","TEST-LN4","TESTCIty4","842 N Capitol Ave4","IND4","464544","USAFour");

		ResponseEntity<Employee> createdResponse = this.testRestTemplate.exchange(EMP_URL, HttpMethod.POST,
				new HttpEntity<>(employee, getHeaders()), Employee.class);
		assertThat(createdResponse).isNotNull();
		assertThat(createdResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		
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
	
	private Employee buildEmployee(String firstName, String lastName, String city, String line1,String state, String zipCode, String country) {
		Employee emp = new Employee();

		emp.setFirstName(firstName);
		emp.setLastName(lastName);
		emp.setDateOfBirth(new Date());

		Address address = new Address();
		address.setCity(city);
		address.setLine1(line1);
		address.setState(state);
		address.setZipCode(zipCode);
		address.setCountry(country);

		emp.setAddress(address);
		return emp;
	}

}
