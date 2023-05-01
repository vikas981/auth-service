package com.viksingh.authservice.exception;


import com.viksingh.authservice.exception.payload.ExceptionMsg;
import com.viksingh.authservice.exception.wrapper.APIException;
import com.viksingh.authservice.exception.wrapper.RoleNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {


	@ExceptionHandler(value = {APIException.class})
	public ResponseEntity<ExceptionMsg> handleAPIException(APIException apiException) {
		log.info("**GlobalExceptionHandler, handleAPIException exception*\n");
		return new ResponseEntity<>(
				ExceptionMsg.builder()
						.message(apiException.getMessage())
						.timestamp(ZonedDateTime.now(ZoneId.systemDefault()))
						.build(),apiException.getHttpStatus());
	}
	
	@ExceptionHandler(value = {RoleNotFoundException.class})
	public ResponseEntity<ExceptionMsg> handleAPIException(RoleNotFoundException exception) {
		log.info("**GlobalExceptionHandler, handleAPIException exception*\n");
		return new ResponseEntity<>(
				ExceptionMsg.builder()
					.message(exception.getMessage())
					.status(exception.getStatusCode().getReasonPhrase())
					.timestamp(ZonedDateTime.now(ZoneId.systemDefault()))
					.build(), exception.getStatusCode());
	}


}










