package com.emented.disk_api.service;

import com.emented.disk_api.communication.SystemItemHistoryResponse;
import com.emented.disk_api.communication.SystemItemHistoryUnit;
import com.emented.disk_api.dao.SystemItemConditionRepository;
import com.emented.disk_api.dao.SystemItemRepository;
import com.emented.disk_api.entity.SystemItem;
import com.emented.disk_api.exception.InvalidTimeIntervalException;
import com.emented.disk_api.exception.SystemItemNotFoundException;
import com.emented.disk_api.util.SystemItemConverter;
import com.emented.disk_api.validation.ValidationErrorsEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class SystemItemConditionServiceImpl implements SystemItemConditionService {

    private final SystemItemConverter systemItemConverter;

    private final SystemItemConditionRepository systemItemConditionRepository;

    private final SystemItemRepository systemItemRepository;

    @Autowired
    public SystemItemConditionServiceImpl(SystemItemConverter systemItemConverter,
                                          SystemItemConditionRepository systemItemConditionRepository,
                                          SystemItemRepository systemItemRepository) {
        this.systemItemConverter = systemItemConverter;
        this.systemItemConditionRepository = systemItemConditionRepository;
        this.systemItemRepository = systemItemRepository;
    }

    /**
     * The method responsible for getting history of item changes during some period of time
     *
     * @param id        Item's id
     * @param dateStart Start point
     * @param dateEnd   End point
     * @return SystemItemHistoryResponse
     */
    @Override
    public SystemItemHistoryResponse getHistoryForSystemItem(String id,
                                                             Instant dateStart,
                                                             Instant dateEnd) {
        if (dateStart.isAfter(dateEnd)) {
            throw new InvalidTimeIntervalException(ValidationErrorsEnum.INVALID_TIME_STAMP.getMessage());
        }
        Optional<SystemItem> systemItemOptional = systemItemRepository.findById(id);
        if (systemItemOptional.isEmpty()) {
            throw new SystemItemNotFoundException(ValidationErrorsEnum.ITEM_NOT_FOUND.getMessage());
        }
        List<SystemItemHistoryUnit> systemItemConditions = systemItemConditionRepository
                .findAllBySystemItemIdAndUpdateDateGreaterThanEqualAndUpdateDateLessThan(id, dateStart, dateEnd)
                .stream().map(systemItemConverter::convertSystemItemConditionToHistoryUnit).toList();

        return new SystemItemHistoryResponse(systemItemConditions);
    }

    /**
     * Method responsible for saving the condition of item
     *
     * @param systemItem Item
     */
    @Override
    public void saveCondition(SystemItem systemItem) {
        systemItemConditionRepository.save(systemItemConverter.convertSystemItemToCondition(systemItem));
    }
}
