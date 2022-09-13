package com.emented.disk_api.service;

import com.emented.disk_api.communication.SystemItemHistoryResponse;
import com.emented.disk_api.communication.SystemItemHistoryUnit;
import com.emented.disk_api.communication.SystemItemImport;
import com.emented.disk_api.communication.SystemItemImportRequest;
import com.emented.disk_api.dao.SystemItemRepository;
import com.emented.disk_api.entity.SystemItem;
import com.emented.disk_api.entity.SystemItemType;
import com.emented.disk_api.exception.SystemItemNotFoundException;
import com.emented.disk_api.util.SystemItemConverter;
import com.emented.disk_api.validation.SystemItemRequestValidator;
import com.emented.disk_api.validation.ValidationErrorsEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SystemItemServiceImpl implements SystemItemService {

    private final SystemItemRepository systemItemRepository;

    private final SystemItemRequestValidator systemItemRequestValidator;

    private final SystemItemConverter systemItemConverter;

    private final SystemItemConditionService systemItemConditionService;

    @Autowired
    public SystemItemServiceImpl(SystemItemRepository systemItemRepository,
                                 SystemItemRequestValidator systemItemRequestValidator,
                                 SystemItemConverter systemItemConverter,
                                 SystemItemConditionService systemItemConditionService) {
        this.systemItemRepository = systemItemRepository;
        this.systemItemRequestValidator = systemItemRequestValidator;
        this.systemItemConverter = systemItemConverter;
        this.systemItemConditionService = systemItemConditionService;
    }

    @Override
    public void importItem(SystemItemImportRequest systemItemImportRequest) {
        Map<String, SystemItem> elementsToUpdateFromDB = getElementsToUpdateFromDB(systemItemImportRequest);
        Map<String, SystemItem> parentElementsFromDB = getParentElementsFromDB(systemItemImportRequest);
        Map<String, SystemItem> systemItemsFromRequest = getSystemItemsFromImportRequest(systemItemImportRequest);
        systemItemRequestValidator.validateSystemItemImportRequest(systemItemImportRequest,
                elementsToUpdateFromDB,
                parentElementsFromDB);
        systemItemsFromRequest.forEach((id, systemItem) -> {
            if (systemItem.getType() == SystemItemType.FOLDER) {
                systemItem.setSize(0L);
            }
        });
        Map<String, SystemItem> updatedItems = new HashMap<>();
        updatedItems.putAll(insertNewFolders(systemItemsFromRequest, elementsToUpdateFromDB));
        updatedItems.putAll(updateElements(elementsToUpdateFromDB, systemItemsFromRequest,
                systemItemImportRequest.getUpdateDate()));
        updatedItems.putAll(insertNewFiles(systemItemsFromRequest, elementsToUpdateFromDB));
        updatedItems.values().forEach(systemItemConditionService::saveCondition);
    }


    @Override
    public void deleteItemById(String id, Instant date) {
        Optional<SystemItem> optionalItemToDelete = systemItemRepository.findById(id);
        if (optionalItemToDelete.isPresent()) {
            SystemItem itemToDelete = optionalItemToDelete.get();
            Map<String, SystemItem> updatedElements = updateBranch(itemToDelete, -itemToDelete.getSize(), date);
            updatedElements.values().forEach(systemItemConditionService::saveCondition);
            systemItemRepository.delete(itemToDelete);
        } else {
            throw new SystemItemNotFoundException(ValidationErrorsEnum.ITEM_NOT_FOUND.getMessage());
        }
    }

    @Override
    public SystemItem getItemById(String id) {
        Optional<SystemItem> systemItem = systemItemRepository.findById(id);
        if (systemItem.isPresent()) {
            return systemItem.get();
        } else {
            throw new SystemItemNotFoundException(ValidationErrorsEnum.ITEM_NOT_FOUND.getMessage());
        }
    }

    @Override
    public SystemItemHistoryResponse getItemsUpdatedInLast24Hours(Instant date) {
        List<SystemItemHistoryUnit> historyUnits = systemItemRepository
                .findAllByDateIsBetween(date.minus(24, ChronoUnit.HOURS),
                        date).stream().map(systemItemConverter::convertSystemItemToHistoryUnit).toList();
        return new SystemItemHistoryResponse(historyUnits);
    }

    private Map<String, SystemItem> updateBranch(SystemItem item, Long sizeDifference, Instant date) {
        String parentId = item.getParentId();
        Map<String, SystemItem> updatedItems = new HashMap<>();
        while (parentId != null) {
            SystemItem parent = systemItemRepository.getReferenceById(parentId);
            boolean isUpdated = false;
            if (parent.getDate() != date) {
                parent.setDate(date);
                isUpdated = true;
            }
            if (sizeDifference != 0) {
                parent.setSize(parent.getSize() + sizeDifference);
                isUpdated = true;
            }
            if (isUpdated) {
                updatedItems.put(parent.getId(), parent);
                systemItemRepository.save(parent);
            }
            parentId = parent.getParentId();
        }
        return updatedItems;
    }

    private Map<String, SystemItem> updateElements(Map<String, SystemItem> elementsToUpdateFromDB,
                                Map<String, SystemItem> systemItemsFromRequest,
                                Instant date) {
        Map<String, SystemItem> updatedItems = new HashMap<>();
        for (Map.Entry<String, SystemItem> updateEntryFromDB : elementsToUpdateFromDB.entrySet()) {
            SystemItem itemBeforeUpdate = updateEntryFromDB.getValue();
            SystemItem itemAfterUpdate = systemItemsFromRequest.get(itemBeforeUpdate.getId());
            if (!itemBeforeUpdate.equals(itemAfterUpdate)) {
                System.out.println("hui");
                updatedItems.put(itemAfterUpdate.getId(), itemAfterUpdate);
                if (itemAfterUpdate.getType() == SystemItemType.FOLDER) {
                    itemAfterUpdate.setSize(itemBeforeUpdate.getSize());
                }
                if (!Objects.equals(itemAfterUpdate.getParentId(), itemBeforeUpdate.getParentId())) {
                    if (itemBeforeUpdate.getParentId() != null) {
                        updatedItems.putAll(updateBranch(itemBeforeUpdate,
                                -itemBeforeUpdate.getSize(),
                                date));
                    }
                    if (itemAfterUpdate.getParentId() != null) {
                        updatedItems.putAll(updateBranch(itemAfterUpdate,
                                itemAfterUpdate.getSize(),
                                date));
                    }
                } else {
                    updatedItems.putAll(updateBranch(itemAfterUpdate,
                            itemAfterUpdate.getSize() - itemBeforeUpdate.getSize(),
                            date));
                }
                systemItemRepository.save(itemAfterUpdate);
            }
        }
        return updatedItems;
    }

    private Map<String, SystemItem> insertNewFiles(Map<String, SystemItem> systemItemsFromRequest,
                                Map<String, SystemItem> elementsToUpdateFromDB) {
        Map<String, SystemItem> newFiles = systemItemsFromRequest
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().getType() == SystemItemType.FILE &&
                        !elementsToUpdateFromDB.containsKey(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Map<String, SystemItem> updatedItems = new HashMap<>(newFiles);
        for (Map.Entry<String, SystemItem> fileEntry : newFiles.entrySet()) {
            SystemItem file = fileEntry.getValue();
            updatedItems.putAll(updateBranch(file, file.getSize(), file.getDate()));
            systemItemRepository.save(file);
        }
        return updatedItems;
    }

    private Map<String, SystemItem> insertNewFolders(Map<String, SystemItem> systemItemsFromRequest,
                                  Map<String, SystemItem> elementsToUpdateFromDB) {
        Map<String, SystemItem> newFolders = systemItemsFromRequest
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().getType() == SystemItemType.FOLDER &&
                        !elementsToUpdateFromDB.containsKey(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        systemItemRepository.saveAll(newFolders.values());
        Map<String, SystemItem> updatedItems = new HashMap<>(newFolders);
        Set<String> updatedIDs = new HashSet<>();
        for (Map.Entry<String, SystemItem> folderEntry : newFolders.entrySet()) {
            SystemItem folder = folderEntry.getValue();
            String parentId = folderEntry.getKey();
            while (parentId != null) {
                if (updatedIDs.contains(parentId)) {
                    break;
                }
                SystemItem parent = systemItemRepository.getReferenceById(parentId);
                if (parent.getDate() != folder.getDate()) {
                    parent.setDate(folder.getDate());
                    updatedIDs.add(folder.getId());
                    updatedItems.put(parent.getId(), parent);
                    systemItemRepository.save(parent);
                }
                parentId = parent.getParentId();
            }
        }
        return updatedItems;
    }

    private Map<String, SystemItem> getElementsToUpdateFromDB(SystemItemImportRequest systemItemImportRequest) {
        List<SystemItem> systemItemListFromDB = systemItemRepository
                .findAllById(
                        systemItemImportRequest
                                .getItems()
                                .stream()
                                .map(SystemItemImport::getId).toList());
        return systemItemListFromDB.stream().collect(Collectors.toMap(SystemItem::getId, Function.identity()));
    }

    private Map<String, SystemItem> getParentElementsFromDB(SystemItemImportRequest systemItemImportRequest) {
        List<SystemItem> systemItemListFromDB = systemItemRepository
                .findAllById(
                        systemItemImportRequest
                                .getItems()
                                .stream()
                                .map(SystemItemImport::getParentId)
                                .filter(Objects::nonNull).toList());
        return systemItemListFromDB.stream().collect(Collectors.toMap(SystemItem::getId, Function.identity()));
    }

    private Map<String, SystemItem> getSystemItemsFromImportRequest(SystemItemImportRequest systemItemImportRequest) {
        List<SystemItem> systemItemListFromDB = systemItemImportRequest
                .getItems()
                .stream()
                .map(systemItemImport -> systemItemConverter.convertSystemItemImportToSystemItem(systemItemImport,
                        systemItemImportRequest.getUpdateDate()))
                .toList();
        return systemItemListFromDB.stream().collect(Collectors.toMap(SystemItem::getId, Function.identity()));
    }
}
