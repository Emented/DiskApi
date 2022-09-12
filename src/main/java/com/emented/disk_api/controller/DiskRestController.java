package com.emented.disk_api.controller;

import com.emented.disk_api.communication.SystemItemImportRequest;
import com.emented.disk_api.entity.SystemItem;
import com.emented.disk_api.exception.SystemItemValidationException;
import com.emented.disk_api.service.SystemItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@RestController
@Validated
public class DiskRestController {

    private final SystemItemService systemItemService;

    @Autowired
    public DiskRestController(SystemItemService systemItemService) {
        this.systemItemService = systemItemService;
    }

    @PostMapping("/imports")
    public String importSystemItems(@RequestBody @Validated SystemItemImportRequest systemItemImportRequest) {
        systemItemService.importItem(systemItemImportRequest);
        return "Insertion or update were completed successfully.";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteSystemItem(@PathVariable @NotBlank String id,
                                   @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Instant date) {
        systemItemService.deleteItemById(id, date);
        return "Deletion was completed successfully.";
    }

    @GetMapping("/nodes/{id}")
    public SystemItem getSystemItem(@PathVariable @NotBlank String id) {
        return systemItemService.getItemById(id);
    }
}
