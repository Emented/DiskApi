package com.emented.disk_api.dao;

import com.emented.disk_api.entity.SystemItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface SystemItemRepository extends JpaRepository<SystemItem, String> {
    List<SystemItem> findAllByDateIsBetween(Instant leftBorder, Instant rightBorder);
}
