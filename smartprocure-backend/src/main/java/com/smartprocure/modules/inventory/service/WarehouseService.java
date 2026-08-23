package com.smartprocure.modules.inventory.service;

import com.smartprocure.modules.inventory.dto.WarehouseDTO;

import java.util.List;

/**
 * Enterprise Service Contract for Warehouse Facility Management.
 *
 * @author Principal Java Architect
 */
public interface WarehouseService {

    WarehouseDTO createWarehouse(WarehouseDTO warehouseDTO);

    WarehouseDTO updateWarehouse(Long id, WarehouseDTO warehouseDTO);

    WarehouseDTO getWarehouseById(Long id);

    List<WarehouseDTO> getAllWarehouses();

    void deleteWarehouse(Long id);
}
