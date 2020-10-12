package com.paypal.bfs.test.employeeserv.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class EmployeeResourceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3079876596895222299L;

	public EmployeeResourceException(String message) {
		super(message);
	}

	public EmployeeResourceException(String message, Throwable cause) {
		super(message, cause);
	}

}
