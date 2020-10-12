package com.paypal.bfs.test.employeeserv.exception;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ErrorMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 651796743341424352L;
	
	public static final String DOMAIN_NAME_FOR_ERROR_STRING = "";
	private List<ErrorDetail> errors = new ArrayList<>();

	public void addError(int code, String userMessage) {
		addError(code, userMessage, userMessage);
	}

	public void addError(int code, String userMessage, String systemMessage) {
		this.errors.add(new ErrorDetail(DOMAIN_NAME_FOR_ERROR_STRING + code, userMessage, systemMessage));
	}

	public List<ErrorDetail> getErrors() {
		return errors;
	}

	public void setErrors(List<ErrorDetail> errors) {
		this.errors = errors;
	}

	public static class ErrorDetail {
		private String code;
		private String userMessage;
		private String systemMessage;

		public ErrorDetail(String code, String userMessage, String systemMessage) {
			this.code = code;
			this.userMessage = userMessage;
			this.systemMessage = systemMessage;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getUserMessage() {
			return userMessage;
		}

		public void setUserMessage(String userMessage) {
			this.userMessage = userMessage;
		}

		public String getSystemMessage() {
			return systemMessage;
		}

		public void setSystemMessage(String systemMessage) {
			this.systemMessage = systemMessage;
		}

	}
}
