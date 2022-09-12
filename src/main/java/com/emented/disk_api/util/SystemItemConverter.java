package com.emented.disk_api.util;

import com.emented.disk_api.communication.SystemItemHistoryUnit;
import com.emented.disk_api.communication.SystemItemImport;
import com.emented.disk_api.entity.SystemItem;
import com.emented.disk_api.entity.SystemItemCondition;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class SystemItemConverter {

    public SystemItem convertSystemItemImportToSystemItem(SystemItemImport systemItemImport, Instant updateDate) {
        SystemItem systemItem = new SystemItem();
        systemItem.setId(systemItemImport.getId());
        systemItem.setUrl(systemItemImport.getUrl());
        systemItem.setParentId(systemItemImport.getParentId());
        systemItem.setType(systemItemImport.getType());
        systemItem.setSize(systemItemImport.getSize());
        systemItem.setDate(updateDate);

        return systemItem;
    }

    public SystemItemHistoryUnit convertSystemItemToHistoryUnit(SystemItem systemItem) {
        SystemItemHistoryUnit systemItemHistoryUnit = new SystemItemHistoryUnit();
        systemItemHistoryUnit.setId(systemItem.getId());
        systemItemHistoryUnit.setUrl(systemItem.getUrl());
        systemItemHistoryUnit.setParentId(systemItem.getParentId());
        systemItemHistoryUnit.setParentId(systemItem.getParentId());
        systemItemHistoryUnit.setType(systemItem.getType());
        systemItemHistoryUnit.setSize(systemItem.getSize());
        systemItemHistoryUnit.setDate(systemItem.getDate());

        return systemItemHistoryUnit;
    }

    public SystemItemHistoryUnit convertSystemUnitConditionToHistoryUnit(SystemItemCondition systemItemCondition) {
        SystemItemHistoryUnit systemItemHistoryUnit = new SystemItemHistoryUnit();
        systemItemHistoryUnit.setId(systemItemCondition.getSystemItem().getId());
        systemItemHistoryUnit.setType(systemItemCondition.getSystemItem().getType());
        systemItemHistoryUnit.setSize(systemItemCondition.getSize());
        systemItemHistoryUnit.setUrl(systemItemCondition.getUrl());
        systemItemHistoryUnit.setDate(systemItemCondition.getUpdateDate());
        systemItemHistoryUnit.setParentId(systemItemCondition.getParentId());

        return systemItemHistoryUnit;
    }

    public SystemItemCondition convertSystemItemToCondition(SystemItem systemItem) {
        SystemItemCondition systemItemCondition = new SystemItemCondition();
        systemItemCondition.setSystemItem(systemItem);
        systemItemCondition.setUpdateDate(systemItem.getDate());
        systemItemCondition.setUrl(systemItem.getUrl());
        systemItemCondition.setSize(systemItem.getSize());
        systemItemCondition.setParentId(systemItem.getParentId());

        return systemItemCondition;
    }

}
