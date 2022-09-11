package com.emented.disk_api.service;

import com.emented.disk_api.communication.SystemItemImport;
import com.emented.disk_api.dao.SystemItemRepository;
import com.emented.disk_api.communication.SystemItemImportRequest;
import com.emented.disk_api.entity.SystemItem;
import com.emented.disk_api.exception.SystemItemNotFoundException;
import com.emented.disk_api.validation.SystemItemRequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class SystemItemServiceImpl implements SystemItemService {

    private final SystemItemRepository systemItemRepository;

    private final SystemItemRequestValidator systemItemRequestValidator;

    @Autowired
    public SystemItemServiceImpl(SystemItemRepository systemItemRepository,
                                 SystemItemRequestValidator systemItemRequestValidator) {
        this.systemItemRepository = systemItemRepository;
        this.systemItemRequestValidator = systemItemRequestValidator;
    }

    @Override
    public void importItem(SystemItemImportRequest systemItemImportRequest) {
        systemItemRequestValidator.validateSystemItemImportRequest(systemItemImportRequest, systemItemRepository.findAll());
        List<SystemItem> systemItemsToAdd = new ArrayList<>();
        Set<String> updatedIDs = new HashSet<>();
        for (SystemItemImport systemItemImport : systemItemImportRequest.getItems()) {
            SystemItem systemItem = convertSystemItemImportToSystemItem(systemItemImport);
            systemItem.setDate(systemItemImportRequest.getUpdateDate());
            systemItemsToAdd.add(systemItem);
            if (systemItem.getParentId() != null) {
                String parentId = systemItem.getParentId();
                while (parentId != null) {
                    if (updatedIDs.contains(parentId)) {
                        break;
                    }
                    Optional<SystemItem> parentItemOptional = systemItemRepository.findById(parentId);
                    if (parentItemOptional.isPresent()) {
                        SystemItem parentItem = parentItemOptional.get();
                        parentItem.setDate(systemItemImportRequest.getUpdateDate());
                        systemItemsToAdd.add(parentItem);
                        updatedIDs.add(parentId);
                        parentId = parentItem.getParentId();
                    } else {
                        break;
                    }
                }
            }
        }
        systemItemRepository.saveAll(systemItemsToAdd);
    }



    @Override
    public void deleteItemById(String id) {
        try {
            systemItemRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new SystemItemNotFoundException("Item with this ID not found");
        }
    }

    @Override
    public SystemItem getItemById(String id) {
        Optional<SystemItem> systemItem = systemItemRepository.findById(id);
        if (systemItem.isPresent()) {
            return systemItem.get();
        } else {
            throw new SystemItemNotFoundException("Item with this ID not found");
        }
    }

    private SystemItem convertSystemItemImportToSystemItem(SystemItemImport systemItemImport) {
        SystemItem systemItem = new SystemItem();
        systemItem.setId(systemItemImport.getId());
        systemItem.setUrl(systemItemImport.getUrl());
        systemItem.setParentId(systemItemImport.getParentId());
        systemItem.setType(systemItemImport.getType());
        systemItem.setSize(systemItemImport.getSize());
        return systemItem;
    }
}
