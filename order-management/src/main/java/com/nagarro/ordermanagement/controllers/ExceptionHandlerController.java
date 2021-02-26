package com.nagarro.ordermanagement.controllers;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.nagarro.ordermanagement.constants.ExceptionMessageConstants;
import com.nagarro.ordermanagement.exception.OrderManagementException;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandlerController.class);

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Object> handleRunTimeException(final Exception ex, final WebRequest request) {
		LOG.error("RuntimeException : ", ex);
		return new ResponseEntity<>(
				getErrorMap(HttpStatus.INTERNAL_SERVER_ERROR, ExceptionMessageConstants.GENERIC_ERROR_MSG),
				new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(OrderManagementException.class)
	public ResponseEntity<Object> handleOrderManagementException(final Exception ex, final WebRequest request) {
		LOG.error("OrderManagementException : ", ex);
		return new ResponseEntity<>(getErrorMap(HttpStatus.BAD_REQUEST, ex.getMessage()), new HttpHeaders(),
				HttpStatus.BAD_REQUEST);
	}

	private Map<String, Object> getErrorMap(final HttpStatus status, final String errorMsg) {
		Map<String, Object> errorMap = new HashMap<>();
		errorMap.put("errorMsg", errorMsg);
		errorMap.put("errorStatus", status.toString());
		return errorMap;
	}

}
