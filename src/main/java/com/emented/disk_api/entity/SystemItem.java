package com.emented.disk_api.entity;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.Set;




@Entity
@Table(name = "system_items")
public class SystemItem {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "url")
    private String url;

    @Column(name = "date", nullable = false)
    private Instant date;

    @Column(name = "parent_id")
    private String parentId;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private SystemItemType type;

    @Column(name = "size")
    private Long size;

    @OneToMany(mappedBy = "parentId", cascade = CascadeType.ALL)
    private Set<SystemItem> children = null;

    @OneToMany(mappedBy = "systemItem", cascade = CascadeType.ALL)
    private List<SystemItemCondition> conditions;

    public SystemItem() {

    }

    public SystemItem(String id, String url, Instant date, String parentId, SystemItemType type, Long size) {
        this.id = id;
        this.url = url;
        this.date = date;
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

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
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

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Set<SystemItem> getChildren() {
        if (children.isEmpty()) return null;
        return children;
    }

    public void setChildren(Set<SystemItem> children) {
        this.children = children;
    }

    public List<SystemItemCondition> getConditions() {
        return conditions;
    }

    public void setConditions(List<SystemItemCondition> conditions) {
        this.conditions = conditions;
    }

    @Override
    public String toString() {
        return "SystemItem{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", date=" + date +
                ", parentId='" + parentId + '\'' +
                ", type=" + type +
                ", size=" + size +
                '}';
    }
}
