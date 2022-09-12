package com.emented.disk_api.exception_handling;

import com.emented.disk_api.communication.Error;
import com.emented.disk_api.exception.SystemItemNotFoundException;
import com.emented.disk_api.exception.SystemItemValidationException;
import com.emented.disk_api.validation.ValidationErrorsEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class DiskAPIExceptionHandler {

    @ExceptionHandler(value = {SystemItemValidationException.class})
    public ResponseEntity<Error> handleValidationException(SystemItemValidationException apiException) {
        Error error = new Error(HttpStatus.BAD_REQUEST.value(), apiException.toString());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Error> handleJsonParseException() {
        Error error = new Error(HttpStatus.BAD_REQUEST.value(), ValidationErrorsEnum.DEFAULT_MESSAGE.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<Error> handleValidationException(MethodArgumentNotValidException argumentNotValidException) {
        BindingResult bindingResult = argumentNotValidException.getBindingResult();
        Map<String, String> errors = bindingResult
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() == null ?
                                ValidationErrorsEnum.DEFAULT_MESSAGE.getMessage() :
                                fieldError.getDefaultMessage()));
        Error error = new Error(HttpStatus.BAD_REQUEST.value(), errors.toString());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {SystemItemNotFoundException.class})
    public ResponseEntity<Error> handleItemNotFoundException(SystemItemNotFoundException systemItemNotFoundException) {
        Error error = new Error(HttpStatus.NOT_FOUND.value(), systemItemNotFoundException.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<Error> handleConstraintValidationException(ConstraintViolationException constraintViolationException) {
        Error error = new Error(HttpStatus.BAD_REQUEST.value(), constraintViolationException.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
