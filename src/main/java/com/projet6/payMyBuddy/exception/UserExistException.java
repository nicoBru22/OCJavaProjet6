package com.projet6.payMyBuddy.exception;

public class UserExistException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public UserExistException(String message) {
		super(message);
	}

}
