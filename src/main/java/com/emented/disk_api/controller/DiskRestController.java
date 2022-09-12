package com.emented.disk_api.controller;

import com.emented.disk_api.communication.Response;
import com.emented.disk_api.communication.SystemItemHistoryResponse;
import com.emented.disk_api.communication.SystemItemImportRequest;
import com.emented.disk_api.entity.SystemItem;
import com.emented.disk_api.service.SystemItemService;
import com.emented.disk_api.service.SystemItemConditionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.time.Instant;

@RestController
@Validated
public class DiskRestController {

    private final SystemItemService systemItemService;

    private final SystemItemConditionService systemItemConditionService;

    @Autowired
    public DiskRestController(SystemItemService systemItemService,
                              SystemItemConditionService systemItemConditionService) {
        this.systemItemService = systemItemService;
        this.systemItemConditionService = systemItemConditionService;
    }

    @PostMapping("/imports")
    public Response importSystemItems(@RequestBody @Validated SystemItemImportRequest systemItemImportRequest) {
        systemItemService.importItem(systemItemImportRequest);
        return new Response(HttpStatus.OK.value(),
                "Insertion or update were completed successfully.");
    }

    @DeleteMapping("/delete/{id}")
    public Response deleteSystemItem(@PathVariable @NotBlank String id,
                                   @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Instant date) {
        systemItemService.deleteItemById(id, date);
        return new Response(HttpStatus.OK.value(), "Deletion was completed successfully.");
    }

    @GetMapping("/nodes/{id}")
    public SystemItem getSystemItem(@PathVariable @NotBlank String id) {
        return systemItemService.getItemById(id);
    }

    @GetMapping("/updates")
    public SystemItemHistoryResponse getUpdates(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Instant date) {
        return systemItemService.getItemsUpdatedInLast24Hours(date);
    }

    @GetMapping("/node/{id}/history")
    public SystemItemHistoryResponse getHistoryForSystemItem(@PathVariable @NotBlank String id,
                                                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
                                                                 Instant dateStart,
                                                             @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
                                                                 Instant dateEnd) {
        return systemItemConditionService.getHistoryForSystemItem(id, dateStart, dateEnd);
    }
}
