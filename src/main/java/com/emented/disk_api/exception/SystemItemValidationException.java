package com.emented.disk_api.exception;


import com.emented.disk_api.validation.ValidationErrorsEnum;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class SystemItemValidationException extends RuntimeException {

    private final Map<Integer, ValidationErrorsEnum> errors;

    public SystemItemValidationException(Map<Integer, ValidationErrorsEnum> errors) {
        super();
        this.errors = errors;
    }

    public Map<Integer, ValidationErrorsEnum> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        for (Map.Entry<Integer, ValidationErrorsEnum> errorEntry : errors.entrySet()) {
            if (errorEntry.getKey() == -1) {
                stringBuilder.append("items=");
            } else {
                stringBuilder.append("items[").append(errorEntry.getKey()).append("]=");
            }
            stringBuilder.append(errorEntry.getValue().getMessage()).append(", ");
        }
        stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
