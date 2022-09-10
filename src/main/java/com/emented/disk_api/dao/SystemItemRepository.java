package com.emented.disk_api.dao;

import com.emented.disk_api.entity.SystemItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemItemRepository extends JpaRepository<SystemItem, String> {
}
