package com.nagarro.ordermanagement.exception;

public class OrderManagementException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8164343005650823571L;
	
	private String msg;

	public OrderManagementException(String msg) {
		super();
		this.msg = msg;
	}

	@Override
	public String getMessage() {
		return msg;
	}

}
