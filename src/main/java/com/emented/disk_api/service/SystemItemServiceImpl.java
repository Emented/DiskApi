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

    /**
     * The method responsible for updating and adding new SystemItems
     *
     * @param systemItemImportRequest Request
     */
    @Override
    public void importItem(SystemItemImportRequest systemItemImportRequest) {
        Map<String, SystemItem> elementsToUpdateFromDB = getElementsToUpdateFromDB(systemItemImportRequest);
        Map<String, SystemItem> parentElementsFromDB = getParentElementsFromDB(systemItemImportRequest);
        Map<String, SystemItem> systemItemsFromRequest = getSystemItemsFromImportRequest(systemItemImportRequest);
        // validating request
        systemItemRequestValidator.validateSystemItemImportRequest(systemItemImportRequest,
                elementsToUpdateFromDB,
                parentElementsFromDB);
        systemItemsFromRequest.forEach((id, systemItem) -> {
            if (systemItem.getType() == SystemItemType.FOLDER) {
                systemItem.setSize(0L);
            }
        });
        Map<String, SystemItem> updatedItems = new HashMap<>();
        // inserting new folders
        updatedItems.putAll(insertNewFolders(systemItemsFromRequest, elementsToUpdateFromDB));
        // updating already existing elements
        updatedItems.putAll(updateElements(elementsToUpdateFromDB, systemItemsFromRequest,
                systemItemImportRequest.getUpdateDate()));
        // inserting new files
        updatedItems.putAll(insertNewFiles(systemItemsFromRequest, elementsToUpdateFromDB));
        // storing item's conditions
        updatedItems.values().forEach(systemItemConditionService::saveCondition);
    }


    /**
     * The method responsible for deleting SystemItems
     *
     * @param id   Id of element
     * @param date Date of deletion
     */
    @Override
    public void deleteItemById(String id, Instant date) {
        Optional<SystemItem> optionalItemToDelete = systemItemRepository.findById(id);
        if (optionalItemToDelete.isPresent()) {
            // getting item from DB
            SystemItem itemToDelete = optionalItemToDelete.get();
            // updating branch
            Map<String, SystemItem> updatedElements = updateBranch(itemToDelete, -itemToDelete.getSize(), date);
            // storing item's conditions
            updatedElements.values().forEach(systemItemConditionService::saveCondition);
            systemItemRepository.delete(itemToDelete);
        } else {
            throw new SystemItemNotFoundException(ValidationErrorsEnum.ITEM_NOT_FOUND.getMessage());
        }
    }

    /**
     * The method responsible for getting SystemItem from DB
     *
     * @param id Id of element
     * @return SystemItem
     */
    @Override
    public SystemItem getItemById(String id) {
        Optional<SystemItem> systemItem = systemItemRepository.findById(id);
        if (systemItem.isPresent()) {
            return systemItem.get();
        } else {
            throw new SystemItemNotFoundException(ValidationErrorsEnum.ITEM_NOT_FOUND.getMessage());
        }
    }

    /**
     * The method responsible for getting updates in last 24 hours
     *
     * @param date Current date
     * @return Response with updates
     */
    @Override
    public SystemItemHistoryResponse getItemsUpdatedInLast24Hours(Instant date) {
        List<SystemItemHistoryUnit> historyUnits = systemItemRepository
                .findAllByDateIsBetween(date.minus(24, ChronoUnit.HOURS),
                        date).stream().map(systemItemConverter::convertSystemItemToHistoryUnit).toList();
        return new SystemItemHistoryResponse(historyUnits);
    }

    /**
     * The method responsible for recursively updating branches
     *
     * @param item           The start of recursion
     * @param sizeDifference Difference in size
     * @param date           Date of update
     * @return Changed items
     */
    private Map<String, SystemItem> updateBranch(SystemItem item, Long sizeDifference, Instant date) {
        String parentId = item.getParentId();
        Map<String, SystemItem> updatedItems = new HashMap<>();
        while (parentId != null) {
            SystemItem parent = systemItemRepository.getReferenceById(parentId);
            boolean isUpdated = false;
            if (!parent.getDate().equals(date)) {
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

    /**
     * The method responsible for updating already existing items
     *
     * @param elementsToUpdateFromDB Elements to update
     * @param systemItemsFromRequest Elements from request
     * @param date                   Date of updating
     * @return Updated items
     */
    private Map<String, SystemItem> updateElements(Map<String, SystemItem> elementsToUpdateFromDB,
                                                   Map<String, SystemItem> systemItemsFromRequest,
                                                   Instant date) {
        Map<String, SystemItem> updatedItems = new HashMap<>();
        for (Map.Entry<String, SystemItem> updateEntryFromDB : elementsToUpdateFromDB.entrySet()) {
            SystemItem itemBeforeUpdate = updateEntryFromDB.getValue();
            SystemItem itemAfterUpdate = systemItemsFromRequest.get(itemBeforeUpdate.getId());
            // checking that the element has been updated
            if (!itemBeforeUpdate.equals(itemAfterUpdate)) {
                updatedItems.put(itemAfterUpdate.getId(), itemAfterUpdate);
                // update the size if the item is a file
                if (itemAfterUpdate.getType().equals(SystemItemType.FOLDER)) {
                    itemAfterUpdate.setSize(itemBeforeUpdate.getSize());
                }
                // checking whether the parent has changed, and if it has changed, then we bypass two branches
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

    /**
     * The method responsible for inserting new files
     *
     * @param systemItemsFromRequest Elements from request
     * @param elementsToUpdateFromDB Elements to update
     * @return Updated items
     */
    private Map<String, SystemItem> insertNewFiles(Map<String, SystemItem> systemItemsFromRequest,
                                                   Map<String, SystemItem> elementsToUpdateFromDB) {
        Map<String, SystemItem> newFiles = systemItemsFromRequest
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().getType().equals(SystemItemType.FILE) &&
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

    /**
     * The method responsible for inserting new folders
     *
     * @param systemItemsFromRequest Elements from request
     * @param elementsToUpdateFromDB Elements to update
     * @return Updated items
     */
    private Map<String, SystemItem> insertNewFolders(Map<String, SystemItem> systemItemsFromRequest,
                                                     Map<String, SystemItem> elementsToUpdateFromDB) {
        Map<String, SystemItem> newFolders = systemItemsFromRequest
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().getType().equals(SystemItemType.FOLDER) &&
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
                if (!parent.getDate().equals(folder.getDate())) {
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
