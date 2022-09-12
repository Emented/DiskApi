package com.emented.disk_api.validation;

public enum ValidationErrorsEnum {
    PARENT_NOT_FOLDER("Parent should be a folder"),
    PARENT_NOT_FOUND("Parent not found"),
    TYPE_CHANGE_ATTEMPT("Changing the type is not allowed"),
    DUPLICATED_IDS("Duplicated ids in request"),
    FOLDER_SIZE_NOT_NULL("Folder size must be null"),
    FILE_SIZE_NULL("File size must not be null"),
    FILE_SIZE_LESS_THEN_ZERO("File size must be greater then 0"),
    FOLDER_URL_NOT_NULL("Folder URL must be null"),
    FILE_URL_NULL("File URL must not be null"),
    FILE_URL_TOO_LONG("File URL must be no longer, then 255"),
    DEFAULT_MESSAGE("Validation failed"),
    ITEM_NOT_FOUND("Item with this ID not found");

    private final String message;

    ValidationErrorsEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
