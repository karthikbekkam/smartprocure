package com.smartprocure.service.impl;

import com.smartprocure.exception.BadRequestException;
import com.smartprocure.domain.entity.Warehouse;
import com.smartprocure.domain.repository.WarehouseRepository;
import com.smartprocure.exception.DuplicateResourceException;
import com.smartprocure.exception.ResourceNotFoundException;
import com.smartprocure.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Override
    @Transactional
    public Warehouse createWarehouse(Warehouse warehouse) {
        if (warehouseRepository.existsByCode(warehouse.getCode())) {
            throw new DuplicateResourceException("Warehouse code already exists: " + warehouse.getCode());
        }
        return warehouseRepository.save(warehouse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Warehouse getWarehouseById(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));
    }

    @Override
    @Transactional
    public Warehouse toggleWarehouseStatus(Long id, boolean active) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));
        
        if (warehouse.getActive() != null && warehouse.getActive() == active) {
            throw new BadRequestException("Warehouse " + warehouse.getCode() + " is already " + (active ? "ACTIVE" : "INACTIVE") + ".");
        }

        warehouse.setActive(active);
        return warehouseRepository.save(warehouse);
    }
}
