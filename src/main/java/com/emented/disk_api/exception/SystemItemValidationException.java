package com.emented.disk_api.exception;


import com.emented.disk_api.validation.ValidationErrorsEnum;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Map;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class SystemItemValidationException extends RuntimeException {

    private final Map<Integer, List<ValidationErrorsEnum>> errors;

    public SystemItemValidationException(Map<Integer, List<ValidationErrorsEnum>> errors) {
        super();
        this.errors = errors;
    }

    public Map<Integer, List<ValidationErrorsEnum>> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        for (Map.Entry<Integer, List<ValidationErrorsEnum>> errorEntry : errors.entrySet()) {
            for (ValidationErrorsEnum errorsEnum : errorEntry.getValue()) {
                if (errorEntry.getKey() == -1) {
                    stringBuilder.append("items=");
                } else {
                    stringBuilder.append("items[").append(errorEntry.getKey()).append("]=");
                }
                stringBuilder.append(errorsEnum.getMessage()).append(", ");
            }
        }
        stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
