package com.smartprocure.service;

import com.smartprocure.domain.entity.Warehouse;

import java.util.List;

public interface WarehouseService {
    Warehouse createWarehouse(Warehouse warehouse);
    List<Warehouse> getAllWarehouses();
    Warehouse getWarehouseById(Long id);
    Warehouse toggleWarehouseStatus(Long id, boolean active);
}
