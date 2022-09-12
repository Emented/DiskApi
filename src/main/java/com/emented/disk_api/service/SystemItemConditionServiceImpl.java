package com.emented.disk_api.service;

import com.emented.disk_api.communication.SystemItemHistoryResponse;
import com.emented.disk_api.dao.SystemItemConditionRepository;
import com.emented.disk_api.entity.SystemItem;
import com.emented.disk_api.util.SystemItemConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class SystemItemConditionServiceImpl implements SystemItemConditionService {

    private final SystemItemConverter systemItemConverter;

    private final SystemItemConditionRepository systemItemConditionRepository;

    @Autowired
    public SystemItemConditionServiceImpl(SystemItemConverter systemItemConverter,
                                          SystemItemConditionRepository systemItemConditionRepository) {
        this.systemItemConverter = systemItemConverter;
        this.systemItemConditionRepository = systemItemConditionRepository;
    }

    @Override
    public SystemItemHistoryResponse getHistoryForSystemItem(String id, Instant dateStart, Instant dateEnd) {
        return null;
    }

    @Override
    public void saveCondition(SystemItem systemItem) {
        systemItemConditionRepository.save(systemItemConverter.convertSystemItemToCondition(systemItem));
    }


}
