package com.emented.disk_api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SystemItemNotFoundException extends RuntimeException {

    public SystemItemNotFoundException(String message) {
        super(message);
    }
}
