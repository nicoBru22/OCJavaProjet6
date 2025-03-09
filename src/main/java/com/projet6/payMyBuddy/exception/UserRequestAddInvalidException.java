package com.projet6.payMyBuddy.exception;

public class UserRequestAddInvalidException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public UserRequestAddInvalidException(String message) {
		super(message);
	}

}
