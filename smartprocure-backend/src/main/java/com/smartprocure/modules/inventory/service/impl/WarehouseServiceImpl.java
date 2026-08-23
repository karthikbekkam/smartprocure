package com.smartprocure.modules.inventory.service.impl;

import com.smartprocure.core.exception.BusinessRuleViolationException;
import com.smartprocure.core.exception.ResourceNotFoundException;
import com.smartprocure.modules.auth.entity.User;
import com.smartprocure.modules.auth.repository.UserRepository;
import com.smartprocure.modules.inventory.dto.WarehouseDTO;
import com.smartprocure.modules.inventory.entity.Warehouse;
import com.smartprocure.modules.inventory.repository.WarehouseRepository;
import com.smartprocure.modules.inventory.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for Warehouse facility management.
 *
 * @author Principal Java Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public WarehouseDTO createWarehouse(WarehouseDTO warehouseDTO) {
        log.info("Creating warehouse facility: {}", warehouseDTO.getName());

        if (warehouseRepository.existsByCode(warehouseDTO.getCode())) {
            throw new BusinessRuleViolationException("Warehouse with code " + warehouseDTO.getCode() + " already exists.");
        }

        User manager = null;
        if (warehouseDTO.getManagerUserId() != null) {
            manager = userRepository.findById(warehouseDTO.getManagerUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", warehouseDTO.getManagerUserId()));
        }

        Warehouse warehouse = Warehouse.builder()
                .code(warehouseDTO.getCode())
                .name(warehouseDTO.getName())
                .location(warehouseDTO.getLocation())
                .capacity(warehouseDTO.getCapacity())
                .manager(manager)
                .build();

        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        log.info("Successfully registered warehouse ID: {} with code: {}", savedWarehouse.getId(), savedWarehouse.getCode());

        return mapToDTO(savedWarehouse);
    }

    @Override
    @Transactional
    public WarehouseDTO updateWarehouse(Long id, WarehouseDTO warehouseDTO) {
        log.info("Updating warehouse facility ID: {}", id);
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));

        User manager = null;
        if (warehouseDTO.getManagerUserId() != null) {
            manager = userRepository.findById(warehouseDTO.getManagerUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", warehouseDTO.getManagerUserId()));
        }

        warehouse.setName(warehouseDTO.getName());
        warehouse.setLocation(warehouseDTO.getLocation());
        warehouse.setCapacity(warehouseDTO.getCapacity());
        warehouse.setManager(manager);

        Warehouse updated = warehouseRepository.save(warehouse);
        return mapToDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseDTO getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));
        return mapToDTO(warehouse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseDTO> getAllWarehouses() {
        return warehouseRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteWarehouse(Long id) {
        log.info("Deleting warehouse facility ID: {}", id);
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));
        warehouseRepository.delete(warehouse);
    }

    private WarehouseDTO mapToDTO(Warehouse warehouse) {
        return WarehouseDTO.builder()
                .id(warehouse.getId())
                .code(warehouse.getCode())
                .name(warehouse.getName())
                .location(warehouse.getLocation())
                .capacity(warehouse.getCapacity())
                .managerUserId(warehouse.getManager() != null ? warehouse.getManager().getId() : null)
                .managerName(warehouse.getManager() != null ? warehouse.getManager().getFirstName() + " " + warehouse.getManager().getLastName() : null)
                .build();
    }
}
