package com.emented.disk_api.service;

import com.emented.disk_api.communication.SystemItemImport;
import com.emented.disk_api.dao.SystemItemRepository;
import com.emented.disk_api.communication.SystemItemImportRequest;
import com.emented.disk_api.entity.SystemItem;
import com.emented.disk_api.exception.SystemItemNotFoundException;
import com.emented.disk_api.validation.SystemItemRequestValidator;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    }

    @Override
    public void deleteItemById(String id) {

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

//    private SystemItem convertSystemItemImportToSystemItem(SystemItemImport systemItemImport) {
//
//    }
}
