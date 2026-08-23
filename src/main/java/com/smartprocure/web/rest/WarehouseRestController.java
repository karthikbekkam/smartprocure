package com.smartprocure.web.rest;

import com.smartprocure.domain.entity.Warehouse;
import com.smartprocure.dto.response.ApiResponse;
import com.smartprocure.service.WarehouseService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
public class WarehouseRestController {

    private final WarehouseService warehouseService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllWarehouses() {
        List<Warehouse> warehouses = warehouseService.getAllWarehouses();
        return ResponseEntity.ok(ApiResponse.ok("Warehouses retrieved successfully", warehouses));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createWarehouse(@RequestBody WarehouseRequest request) {
        Warehouse warehouse = Warehouse.builder()
                .code(request.getCode())
                .name(request.getName())
                .location(request.getCity() != null ? request.getCity() + (request.getAddress() != null ? ", " + request.getAddress() : "") : (request.getLocation() != null ? request.getLocation() : "Default Location"))
                .capacity(request.getCapacity() != null ? request.getCapacity() : 10000)
                .build();

        Warehouse saved = warehouseService.createWarehouse(warehouse);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Warehouse facility registered successfully", saved));
    }

    @PatchMapping("/{id}/status")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'PROCUREMENT_MANAGER')")
    public ResponseEntity<ApiResponse> toggleWarehouseStatus(@PathVariable Long id, @RequestParam boolean active) {
        Warehouse warehouse = warehouseService.toggleWarehouseStatus(id, active);
        return ResponseEntity.ok(ApiResponse.ok("Warehouse active status updated to " + active, warehouse));
    }

    @Data
    public static class WarehouseRequest {
        private String name;
        private String code;
        private String city;
        private String address;
        private String location;
        private Integer capacity;
    }
}
