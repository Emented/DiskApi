package com.emented.disk_api.communication;

import com.emented.disk_api.entity.SystemItemType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class SystemItemImport {

    @NotNull(message = "ID must not be null")
    private String id;

    private String url;

    private String parentId;

    @NotNull(message = "Type must not be null")
    @Enumerated(EnumType.STRING)
    private SystemItemType type;

    private Integer size;

    public SystemItemImport(String id, String url, String parentId, SystemItemType type, Integer size) {
        this.id = id;
        this.url = url;
        this.parentId = parentId;
        this.type = type;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public SystemItemType getType() {
        return type;
    }

    public void setType(SystemItemType type) {
        this.type = type;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "SystemItemImport{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", parentId='" + parentId + '\'' +
                ", type=" + type +
                ", size=" + size +
                '}';
    }
}