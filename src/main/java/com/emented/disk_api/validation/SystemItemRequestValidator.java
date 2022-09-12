package com.emented.disk_api.validation;

import com.emented.disk_api.communication.SystemItemImport;
import com.emented.disk_api.communication.SystemItemImportRequest;
import com.emented.disk_api.entity.SystemItem;
import com.emented.disk_api.entity.SystemItemType;
import com.emented.disk_api.exception.SystemItemValidationException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SystemItemRequestValidator {

    private final Map<String, String> errors = new HashMap<>();

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
                errors.put("items[" + number + "]", "Parent should be a folder");
            }
            return;
        }
        if (itemsToImport.containsKey(systemItemImport.getParentId())) {
            if (itemsToImport.get(systemItemImport.getParentId()).getType() != SystemItemType.FOLDER) {
                errors.put("items[" + number + "]", "Parent should be a folder");
            }
            return;
        }
        errors.put("items[" + number + "]", "Parent not found");
    }

    private void validateTypeSubstitution(SystemItemImport systemItemImport,
                                          Map<String, SystemItem> systemItemsToUpdate,
                                          int number) {
        if (!systemItemsToUpdate.containsKey(systemItemImport.getId())) {
            return;
        }
        if (systemItemImport.getType() != systemItemsToUpdate.get(systemItemImport.getId()).getType()) {
            errors.put("items[" + number + "]", "Changing the type is not allowed");
        }
    }

    private void checkForRepeatID(List<SystemItemImport> itemImports) {
        Set<String> itemsIds = itemImports.stream().map(SystemItemImport::getId).collect(Collectors.toSet());
        if (itemsIds.size() != itemImports.size()) {
            throw new SystemItemValidationException(Map.of("items", "Duplicated ids in request"));
        }
    }

    private void validateSizeCorrectness(SystemItemImport systemItemImport,
                                         int number) {
        if (systemItemImport.getType() == SystemItemType.FOLDER) {
            if (systemItemImport.getSize() != null) {
                errors.put("items[" + number + "]", "Folder size must be null");
            }
        } else {
            if (systemItemImport.getSize() == null) {
                errors.put("items[" + number + "]", "File size must not be null");
                return;
            }
            if (systemItemImport.getSize() <= 0) {
                errors.put("items[" + number + "]", "File size must be greater then 0");
            }
        }
    }

    private void validateUrlCorrectness(SystemItemImport systemItemImport,
                                        int number) {
        if (systemItemImport.getType() == SystemItemType.FOLDER) {
            if (systemItemImport.getUrl() != null) {
                errors.put("items[" + number + "]", "Folder URL must be null");
            }
        } else {
            if (systemItemImport.getUrl() == null) {
                errors.put("items[" + number + "]", "File URL must not be null");
                return;
            }
            if (systemItemImport.getUrl().length() > 255) {
                errors.put("items[" + number + "]", "File URL must be no longer, then 255");
            }
        }
    }
}
