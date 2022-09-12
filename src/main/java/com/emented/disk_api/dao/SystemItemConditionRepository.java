package com.emented.disk_api.dao;

import com.emented.disk_api.entity.SystemItemCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemItemConditionRepository extends JpaRepository<SystemItemCondition, Long> {

}
