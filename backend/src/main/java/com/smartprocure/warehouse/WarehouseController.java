package com.smartprocure.warehouse;

import com.smartprocure.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<WarehouseDto>>> getAllWarehouses(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok("Warehouses fetched successfully", warehouseService.getAllWarehouses(pageable)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WarehouseDto>> createWarehouse(@Valid @RequestBody WarehouseDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Warehouse created successfully", warehouseService.createWarehouse(dto)));
    }
}
