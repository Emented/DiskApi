package com.emented.disk_api.communication;


import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.List;


public class SystemItemImportRequest {

    @NotNull(message = "List of items must not be null")
    @Size(min = 1, message = "There should be at least 1 item in list")
    @Valid
    private List<SystemItemImport> items;

    @NotNull(message = "Date must not be null")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mmX")
    private Instant updateDate;

    public SystemItemImportRequest(List<SystemItemImport> items, Instant updateDate) {
        this.items = items;
        this.updateDate = updateDate;
    }

    public List<SystemItemImport> getItems() {
        return items;
    }

    public void setItems(List<SystemItemImport> items) {
        this.items = items;
    }

    public Instant getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Instant updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        return "SystemItemImportRequest{" +
                "items=" + items +
                ", updateDate=" + updateDate +
                '}';
    }
}
