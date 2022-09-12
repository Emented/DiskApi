package com.emented.disk_api.validation;

import com.emented.disk_api.communication.SystemItemImport;
import com.emented.disk_api.communication.SystemItemImportRequest;
import com.emented.disk_api.entity.SystemItem;
import com.emented.disk_api.entity.SystemItemType;
import com.emented.disk_api.exception.SystemItemValidationException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SystemItemRequestValidator {

    private final Map<Integer, List<ValidationErrorsEnum>> errors = new HashMap<>();

    public void validateSystemItemImportRequest(SystemItemImportRequest importRequest,
                                                Map<String, SystemItem> elementsToUpdateFromDB,
                                                Map<String, SystemItem> parentElementsFromDB) {
        errors.clear();
        checkForRepeatID(importRequest.getItems());
        Map<String, SystemItemImport> itemsToImport = importRequest
                .getItems()
                .stream()
                .collect(Collectors.toMap(SystemItemImport::getId, Function.identity()));
        List<SystemItemImport> imports = importRequest.getItems();
        for (int number = 0; number < imports.size(); number++) {
            SystemItemImport systemItemImport = imports.get(number);
            validateSizeCorrectness(systemItemImport, number);
            validateUrlCorrectness(systemItemImport, number);
            validateTypeSubstitution(systemItemImport, elementsToUpdateFromDB, number);
            if (systemItemImport.getParentId() != null) {
                validateParentCorrectnessAndExistence(systemItemImport, parentElementsFromDB, itemsToImport, number);
            }
        }
        if (!errors.isEmpty()) {
            throw new SystemItemValidationException(errors);
        }
    }

    private void validateParentCorrectnessAndExistence(SystemItemImport systemItemImport,
                                                       Map<String, SystemItem> parentsFromDB,
                                                       Map<String, SystemItemImport> itemsToImport,
                                                       int number) {
        if (parentsFromDB.containsKey(systemItemImport.getParentId())) {
            if (parentsFromDB.get(systemItemImport.getParentId()).getType() != SystemItemType.FOLDER) {
                addNewError(number, ValidationErrorsEnum.PARENT_NOT_FOLDER);
            }
            return;
        }
        if (itemsToImport.containsKey(systemItemImport.getParentId())) {
            if (itemsToImport.get(systemItemImport.getParentId()).getType() != SystemItemType.FOLDER) {
                addNewError(number, ValidationErrorsEnum.PARENT_NOT_FOLDER);
            }
            return;
        }
        addNewError(number, ValidationErrorsEnum.PARENT_NOT_FOUND);
    }

    private void validateTypeSubstitution(SystemItemImport systemItemImport,
                                          Map<String, SystemItem> systemItemsToUpdate,
                                          int number) {
        if (!systemItemsToUpdate.containsKey(systemItemImport.getId())) {
            return;
        }
        if (systemItemImport.getType() != systemItemsToUpdate.get(systemItemImport.getId()).getType()) {
            addNewError(number, ValidationErrorsEnum.TYPE_CHANGE_ATTEMPT);
        }
    }

    private void checkForRepeatID(List<SystemItemImport> itemImports) {
        Set<String> itemsIds = itemImports.stream().map(SystemItemImport::getId).collect(Collectors.toSet());
        if (itemsIds.size() != itemImports.size()) {
            throw new SystemItemValidationException(Map.of(-1, List.of(ValidationErrorsEnum.DUPLICATED_IDS)));
        }
    }

    private void validateSizeCorrectness(SystemItemImport systemItemImport,
                                         int number) {
        if (systemItemImport.getType() == SystemItemType.FOLDER) {
            if (systemItemImport.getSize() != null) {
                addNewError(number, ValidationErrorsEnum.FOLDER_SIZE_NOT_NULL);
            }
        } else {
            if (systemItemImport.getSize() == null) {
                addNewError(number, ValidationErrorsEnum.FILE_SIZE_NULL);
                return;
            }
            if (systemItemImport.getSize() <= 0) {
                addNewError(number, ValidationErrorsEnum.FILE_SIZE_LESS_THEN_ZERO);
            }
        }
    }

    private void validateUrlCorrectness(SystemItemImport systemItemImport,
                                        int number) {
        if (systemItemImport.getType() == SystemItemType.FOLDER) {
            if (systemItemImport.getUrl() != null) {
                addNewError(number, ValidationErrorsEnum.FOLDER_URL_NOT_NULL);
            }
        } else {
            if (systemItemImport.getUrl() == null) {
                addNewError(number, ValidationErrorsEnum.FILE_URL_NULL);
                return;
            }
            if (systemItemImport.getUrl().length() > 255) {
                addNewError(number, ValidationErrorsEnum.FILE_URL_TOO_LONG);
            }
        }
    }

    private void addNewError(Integer number, ValidationErrorsEnum validationErrorsEnum) {
        List<ValidationErrorsEnum> errorsEnumList = errors.getOrDefault(number, new ArrayList<>());
        errorsEnumList.add(validationErrorsEnum);
        errors.put(number, errorsEnumList);
    }
}
