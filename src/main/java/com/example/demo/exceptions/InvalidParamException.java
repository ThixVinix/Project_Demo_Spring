package com.example.demo.exceptions;

public class InvalidParamException extends RuntimeException {
    
	private static final long serialVersionUID = -534739623504679593L;

	public InvalidParamException(String message) {
        super(message);
    }
}
