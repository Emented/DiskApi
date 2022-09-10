package com.emented.disk_api.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class SystemItemValidationException extends RuntimeException {

    public SystemItemValidationException(String message) {
        super(message);
    }
}
