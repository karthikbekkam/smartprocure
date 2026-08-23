package com.smartprocure.service.impl;

import com.smartprocure.domain.entity.Vendor;
import com.smartprocure.domain.enums.VendorStatus;
import com.smartprocure.domain.repository.VendorRepository;
import com.smartprocure.dto.request.VendorCreateDTO;
import com.smartprocure.dto.response.VendorResponseDTO;
import com.smartprocure.exception.DuplicateResourceException;
import com.smartprocure.exception.ResourceNotFoundException;
import com.smartprocure.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;

    @Override
    @Transactional
    public VendorResponseDTO registerVendor(VendorCreateDTO createDTO) {
        if (vendorRepository.findByTaxId(createDTO.getTaxId()).isPresent()) {
            throw new DuplicateResourceException("Vendor already exists with Tax ID: " + createDTO.getTaxId());
        }

        Vendor vendor = Vendor.builder()
                .companyName(createDTO.getCompanyName())
                .taxId(createDTO.getTaxId())
                .contactEmail(createDTO.getContactEmail())
                .contactPhone(createDTO.getContactPhone())
                .address(createDTO.getAddress())
                .paymentTerms(createDTO.getPaymentTerms() != null ? createDTO.getPaymentTerms() : "NET_30")
                .status(VendorStatus.PENDING_APPROVAL)
                .rating(5.0)
                .build();

        Vendor savedVendor = vendorRepository.save(vendor);
        return mapToDTO(savedVendor);
    }

    @Override
    @Transactional
    public VendorResponseDTO updateVendorStatus(Long id, VendorStatus status) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", id));

        vendor.setStatus(status);
        Vendor updatedVendor = vendorRepository.save(vendor);
        return mapToDTO(updatedVendor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VendorResponseDTO> getAllVendors() {
        return vendorRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VendorResponseDTO> getVendorsByStatus(VendorStatus status) {
        return vendorRepository.findByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VendorResponseDTO getVendorById(Long id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", id));
        return mapToDTO(vendor);
    }

    private VendorResponseDTO mapToDTO(Vendor vendor) {
        return VendorResponseDTO.builder()
                .id(vendor.getId())
                .companyName(vendor.getCompanyName())
                .taxId(vendor.getTaxId())
                .contactEmail(vendor.getContactEmail())
                .contactPhone(vendor.getContactPhone())
                .address(vendor.getAddress())
                .status(vendor.getStatus())
                .rating(vendor.getRating())
                .paymentTerms(vendor.getPaymentTerms())
                .createdAt(vendor.getCreatedAt())
                .build();
    }
}
