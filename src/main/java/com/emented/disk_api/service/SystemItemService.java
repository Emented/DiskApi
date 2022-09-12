package com.emented.disk_api.service;

import com.emented.disk_api.communication.SystemItemHistoryResponse;
import com.emented.disk_api.communication.SystemItemImportRequest;
import com.emented.disk_api.entity.SystemItem;

import java.time.Instant;

public interface SystemItemService {

    void importItem(SystemItemImportRequest systemItemImportRequest);

    void deleteItemById(String id, Instant date);

    SystemItem getItemById(String id);

    SystemItemHistoryResponse getItemsUpdatedInLast24Hours(Instant date);
}
