package com.example.demo.exceptions;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.log4j.Log4j2;

@Log4j2
@ControllerAdvice
public class TreatmentExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidParamException.class)
    public ResponseEntity<ExceptionDetails> handlerInvalidParamException(InvalidParamException ex) {
        return new ResponseEntity<>(
                ExceptionDetails.builder()
                        .title("Parâmetro inválido")
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build(),
                HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionDetails> handlerDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error(">>>>>>>>>>>>>> OCORREU VIOLACAO DE INTEGRIDADE NO BANCO DE DADOS <<<<<<<<<<<<<<");
    	return new ResponseEntity<>(
                ExceptionDetails.builder()
                        .title("Dados inválidos")
                        .message("Um ou mais valores fornecidos ferem as regras de integridade da aplicação")
                        .timestamp(LocalDateTime.now(ZoneId.of("UTC")))
                        .build(),
                HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {

        List<FieldError> erros = ex.getFieldErrors();

        return new ResponseEntity<>(
          ExceptionDetails.builder()
                  .title("Parâmetros inválidos")
                  .message("Um ou mais campos com valor inválido")
                  .fields(erros.stream().map(FieldError::getField)
                          .collect(Collectors.joining(";")))
                  .fieldsMessages(erros.stream().map(FieldError::getDefaultMessage)
                          .collect(Collectors.joining(";")))
                  .timestamp(LocalDateTime.now(ZoneId.of("UTC")))
                  .build(),
                status
        );

    }
}
