package com.emented.disk_api.communication;

import java.util.List;

public class SystemItemHistoryResponse {
    private final List<SystemItemHistoryUnit> items;

    public SystemItemHistoryResponse(List<SystemItemHistoryUnit> items) {
        this.items = items;
    }

    public List<SystemItemHistoryUnit> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "SystemItemHistoryResponse{" +
                "items=" + items.toString() +
                '}';
    }
}
