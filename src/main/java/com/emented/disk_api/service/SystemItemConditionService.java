package com.emented.disk_api.service;

import com.emented.disk_api.communication.SystemItemHistoryResponse;
import com.emented.disk_api.entity.SystemItem;

import java.time.Instant;

public interface SystemItemConditionService {

    SystemItemHistoryResponse getHistoryForSystemItem(String id, Instant dateStart, Instant dateEnd);

    void saveCondition(SystemItem systemItem);
}
