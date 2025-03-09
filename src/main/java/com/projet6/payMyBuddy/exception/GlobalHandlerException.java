package com.projet6.payMyBuddy.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalHandlerException {
	
	private Logger logger = LogManager.getLogger(GlobalHandlerException.class);
	
//Gestion des erreurs User
	
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
    	logger.error("L'utilisateur n'existe pas : {}", e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(UserExistException.class)
    public ResponseEntity<String> handlerUserExistsException(UserExistException e) {
    	return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<String> handlerInvalidRequestException(InvalidRequestException e) {
    	return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(UserRequestAddInvalidException.class)
    public ResponseEntity<String> handleUserRequestAddInvalidException(UserRequestAddInvalidException e) {
    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    
    @ExceptionHandler(SoldeInvalidException.class)
    public ResponseEntity<String> handleSoldeInvalidException(SoldeInvalidException e) {
    	logger.error("Le solde ne peut pas être inférieur à 0.");
    	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
    
    
    
    
    
    
    
    
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception e) {
    	logger.error("Une erreur interne est survenue : {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }


}
