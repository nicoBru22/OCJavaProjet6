package com.projet6.payMyBuddy.exception;

public class UserInvalidRequestException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public UserInvalidRequestException(String message) {
		super(message);
	}

}
