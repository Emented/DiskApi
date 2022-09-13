package com.emented.disk_api.entity;


import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "system_item_conditions")
public class SystemItemCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "system_item_id")
    private String systemItemId;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "system_item_id", insertable = false, updatable = false)
    private SystemItem systemItem;

    @Column(name = "parent_id")
    private String parentId;

    @Column(name = "update_date")
    private Instant updateDate;

    @Column(name = "size")
    private Long size;

    @Column(name = "url")
    private String url;

    public SystemItemCondition() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSystemItemId() {
        return systemItemId;
    }

    public void setSystemItemId(String systemItemId) {
        this.systemItemId = systemItemId;
    }

    public SystemItem getSystemItem() {
        return systemItem;
    }

    public void setSystemItem(SystemItem systemItem) {
        this.systemItem = systemItem;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Instant getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Instant updateDate) {
        this.updateDate = updateDate;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "SystemItemCondition{" +
                "updateDate=" + updateDate +
                '}';
    }
}
