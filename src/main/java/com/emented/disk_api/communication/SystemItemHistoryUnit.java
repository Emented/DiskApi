package com.emented.disk_api.communication;

import com.emented.disk_api.entity.SystemItemType;

import java.time.Instant;

public class SystemItemHistoryUnit extends SystemItemImport {

    private Instant date;

    public SystemItemHistoryUnit() {
        super();
    }

    public SystemItemHistoryUnit(String id, String url, String parentId, SystemItemType type, Long size, Instant date) {
        super(id, url, parentId, type, size);
        this.date = date;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "SystemItemHistoryUnit{" +
                "date=" + date +
                '}';
    }
}
