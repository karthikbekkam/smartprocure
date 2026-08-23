package com.smartprocure.warehouse;

import com.smartprocure.common.exception.DuplicateResourceException;
import com.smartprocure.common.exception.ResourceNotFoundException;
import com.smartprocure.domain.User;
import com.smartprocure.domain.Warehouse;
import com.smartprocure.domain.repository.UserRepository;
import com.smartprocure.domain.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<WarehouseDto> getAllWarehouses(Pageable pageable) {
        return warehouseRepository.findAll(pageable).map(this::mapToDto);
    }

    @Transactional
    public WarehouseDto createWarehouse(WarehouseDto dto) {
        if (warehouseRepository.existsByCode(dto.getCode())) {
            throw new DuplicateResourceException("Warehouse code already exists: " + dto.getCode());
        }

        User manager = null;
        if (dto.getManagerId() != null) {
            manager = userRepository.findById(dto.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", dto.getManagerId()));
        }

        Warehouse warehouse = Warehouse.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .location(dto.getLocation())
                .capacity(dto.getCapacity())
                .manager(manager)
                .build();

        return mapToDto(warehouseRepository.save(warehouse));
    }

    private WarehouseDto mapToDto(Warehouse w) {
        return WarehouseDto.builder()
                .id(w.getId())
                .code(w.getCode())
                .name(w.getName())
                .location(w.getLocation())
                .capacity(w.getCapacity())
                .managerId(w.getManager() != null ? w.getManager().getId() : null)
                .managerName(w.getManager() != null ? w.getManager().getFirstName() + " " + w.getManager().getLastName() : "Unassigned")
                .build();
    }
}
