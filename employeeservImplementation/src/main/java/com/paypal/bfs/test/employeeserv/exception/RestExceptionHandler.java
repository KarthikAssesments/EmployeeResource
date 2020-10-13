package com.paypal.bfs.test.employeeserv.exception;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorMessage> handleException(Exception ex, HttpServletRequest request) {
		HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		if (ex instanceof ConstraintViolationException) {
			httpStatus = HttpStatus.BAD_REQUEST;
		} else {
			ResponseStatus responseStatus = AnnotatedElementUtils.findMergedAnnotation(ex.getClass(),
					ResponseStatus.class);
			if (responseStatus != null) {
				httpStatus = responseStatus.code();
			}
		}

		ErrorMessage errorDetail = createErrorDetail(ex, httpStatus);

		log.error("Error occured processing the request: {}, status: {}", errorDetail, httpStatus, ex);
		return new ResponseEntity<>(errorDetail, httpStatus);
	}

	/*
	 * Overriding this to make sure message body is always populated
	 */
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		if (body == null) {
			body = createErrorDetail(ex, status);
		}

		return super.handleExceptionInternal(ex, body, headers, status, request);
	}

	/**
	 * This method creates error message in the format we need. Added custom error
	 * message for input validation errors
	 */
	private ErrorMessage createErrorDetail(Exception ex, HttpStatus httpStatus) {
		// Create custom error message
		ErrorMessage errorMessage = new ErrorMessage();
		if (ex instanceof MethodArgumentNotValidException) {
			List<FieldError> fieldErrors = ((MethodArgumentNotValidException) ex).getBindingResult().getFieldErrors();
			fieldErrors.stream().forEach(fe -> errorMessage.addError(httpStatus.value(), getErrorMessage(fe)));
		} else if (ex instanceof ConstraintViolationException) {
			Set<ConstraintViolation<?>> violations = ((ConstraintViolationException) ex).getConstraintViolations();
			violations.stream().forEach(violation -> errorMessage.addError(httpStatus.value(),
					violation.getPropertyPath() + " " + violation.getMessage()));
		} else {
			errorMessage.addError(httpStatus.value(), ex.getMessage(), httpStatus.getReasonPhrase());
		}

		// Log the exception
		logException(ex, httpStatus);

		return errorMessage;
	}

	/**
	 * Don't log the stack trace for expected or business errors
	 */
	private void logException(Exception ex, HttpStatus httpStatus) {
		if (httpStatus.value() < 500 || ex instanceof ConstraintViolationException) {
			logger.warn(ex.getMessage());
		} else {
			logger.error(ex.getMessage(), ex);
		}
	}

	private String getErrorMessage(FieldError fe) {
		String errorMessage = "";
		if (Objects.nonNull(fe)) {
			errorMessage = fe.getField() + " " + fe.getDefaultMessage();
		}
		return errorMessage;
	}

}
