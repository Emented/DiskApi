package com.emented.disk_api.service;

import com.emented.disk_api.communication.SystemItemImportRequest;
import com.emented.disk_api.entity.SystemItem;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public interface SystemItemService {

    void importItem(SystemItemImportRequest systemItemImportRequest);
    void deleteItemById(String id, Instant date);
    SystemItem getItemById(String id);
}
