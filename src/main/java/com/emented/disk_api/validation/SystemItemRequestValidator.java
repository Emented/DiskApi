package com.emented.disk_api.validation;

import com.emented.disk_api.communication.SystemItemImport;
import com.emented.disk_api.communication.SystemItemImportRequest;
import com.emented.disk_api.entity.SystemItem;
import com.emented.disk_api.entity.SystemItemType;
import com.emented.disk_api.exception.SystemItemValidationException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SystemItemRequestValidator {

    public void validateSystemItemImportRequest(SystemItemImportRequest importRequest,
                                                List<SystemItem> systemItemsFromDBList) {
        Map<String, SystemItemImport> itemsToImport = importRequest
                .getItems()
                .stream()
                .collect(Collectors.toMap(SystemItemImport::getId, Function.identity()));
        Map<String, SystemItem> systemItemsFromDB = systemItemsFromDBList
                .stream()
                .collect(Collectors.toMap(SystemItem::getId, Function.identity()));
        Map<String, SystemItem> systemItemsToUpdate = systemItemsFromDB
                .entrySet()
                .stream()
                .filter(element -> itemsToImport.containsKey(element.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        checkForRepeatID(itemsToImport);
        for (SystemItemImport systemItemImport : itemsToImport.values()) {
            validateSizeCorrectness(systemItemImport);
            validateUrlCorrectness(systemItemImport);
            validateTypeSubstitution(systemItemImport, systemItemsToUpdate);
            if (systemItemImport.getParentId() != null) {
                validateParentCorrectnessAndExistence(systemItemImport, systemItemsFromDB, itemsToImport);
            }
        }

    }

    private void validateParentCorrectnessAndExistence(SystemItemImport systemItemImport,
                                                       Map<String, SystemItem> systemItemsFormDB,
                                                       Map<String, SystemItemImport> itemsToImport) {
        if (systemItemsFormDB.containsKey(systemItemImport.getParentId())) {
            if (systemItemsFormDB.get(systemItemImport.getParentId()).getType() != SystemItemType.FOLDER) {
                throw new SystemItemValidationException("Parent should be a folder");
            }
            return;
        }
        if (itemsToImport.containsKey(systemItemImport.getParentId())) {
            if (itemsToImport.get(systemItemImport.getParentId()).getType() != SystemItemType.FOLDER) {
                throw new SystemItemValidationException("Parent should be a folder");
            }
            return;
        }
        throw new SystemItemValidationException("Parent not found");
    }

    private void validateTypeSubstitution(SystemItemImport systemItemImport,
                                          Map<String, SystemItem> systemItemsToUpdate) {
        if (!systemItemsToUpdate.containsKey(systemItemImport.getId())) {
            return;
        }
        if (systemItemImport.getType() != systemItemsToUpdate.get(systemItemImport.getId()).getType()) {
            throw new SystemItemValidationException("Changing the type is not allowed");
        }
    }

    private void checkForRepeatID(Map<String, SystemItemImport> itemsToImport) {
        Set<String> itemsIds = new HashSet<>(itemsToImport.keySet());
        if (itemsIds.size() != itemsToImport.size()) {
            throw new SystemItemValidationException("Duplicated ids in request");
        }
    }

    private void validateSizeCorrectness(SystemItemImport systemItemImport) {
        if (systemItemImport.getType() == SystemItemType.FOLDER) {
            if (systemItemImport.getSize() != null) {
                throw new SystemItemValidationException("Folder size must be null");
            }
        } else {
            if (systemItemImport.getSize() == null) {
                throw new SystemItemValidationException("File size must not be null");
            }
            if (systemItemImport.getSize() <= 0) {
                throw new SystemItemValidationException("File size must be greater then 0");
            }
        }
    }

    private void validateUrlCorrectness(SystemItemImport systemItemImport) {
        if (systemItemImport.getType() == SystemItemType.FOLDER) {
            if (systemItemImport.getUrl() != null) {
                throw new SystemItemValidationException("Folder URL must be null");
            }
        } else {
            if (systemItemImport.getUrl() == null) {
                throw new SystemItemValidationException("File URL must not be null");
            }
            if (systemItemImport.getUrl().length() > 255) {
                throw new SystemItemValidationException("File URL must be no longer, then 255");
            }
        }
    }
}
