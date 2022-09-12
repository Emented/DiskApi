package com.emented.disk_api.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class SystemItemValidationException extends RuntimeException {

    private final Map<String, String> errors;

    public SystemItemValidationException(Map<String, String> errors) {
        super();
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
