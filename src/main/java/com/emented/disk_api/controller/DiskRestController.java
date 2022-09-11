package com.emented.disk_api.controller;

import com.emented.disk_api.communication.SystemItemImportRequest;
import com.emented.disk_api.entity.SystemItem;
import com.emented.disk_api.exception.SystemItemValidationException;
import com.emented.disk_api.service.SystemItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RestController
public class DiskRestController {

    private final SystemItemService systemItemService;

    @Autowired
    public DiskRestController(SystemItemService systemItemService) {
        this.systemItemService = systemItemService;
    }

    @PostMapping("/imports")
    public String importSystemItems(@RequestBody @Validated SystemItemImportRequest systemItemImportRequest) {
        systemItemService.importItem(systemItemImportRequest);
        return "Insertion or update were completed successfully";
    }

    @Validated
    @DeleteMapping("/delete/{id}")
    public String deleteSystemItem(@PathVariable @NotNull @NotBlank String id) {
        systemItemService.deleteItemById(id);
        return "Удаление прошло успешно.";
    }

    @Validated
    @GetMapping("/nodes/{id}")
    public SystemItem getSystemItem(@PathVariable @NotNull @NotBlank String id) {
        return systemItemService.getItemById(id);
    }
}
