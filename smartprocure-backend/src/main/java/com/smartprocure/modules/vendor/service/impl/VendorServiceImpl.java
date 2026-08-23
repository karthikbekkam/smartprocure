package com.smartprocure.modules.vendor.service.impl;

import com.smartprocure.core.exception.BusinessRuleViolationException;
import com.smartprocure.core.exception.ResourceNotFoundException;
import com.smartprocure.core.payload.PagedResponse;
import com.smartprocure.modules.vendor.dto.VendorApprovalDTO;
import com.smartprocure.modules.vendor.dto.VendorDTO;
import com.smartprocure.modules.vendor.entity.Vendor;
import com.smartprocure.modules.vendor.enums.VendorStatus;
import com.smartprocure.modules.vendor.repository.VendorRepository;
import com.smartprocure.modules.vendor.service.VendorService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Enterprise Service Implementation for Vendor Master Data, Onboarding Approval & Performance Scoring.
 *
 * @author Principal Java Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;

    @Override
    @Transactional
    public VendorDTO createVendor(VendorDTO vendorDTO) {
        log.info("Creating vendor record for company: {}", vendorDTO.getCompanyName());

        if (vendorRepository.existsByTaxId(vendorDTO.getTaxId())) {
            throw new BusinessRuleViolationException("A vendor with Tax ID / VAT " + vendorDTO.getTaxId() + " already exists.");
        }

        if (vendorRepository.existsByEmail(vendorDTO.getEmail())) {
            throw new BusinessRuleViolationException("A vendor with email " + vendorDTO.getEmail() + " already exists.");
        }

        String generatedCode = "VND-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Vendor vendor = Vendor.builder()
                .vendorCode(generatedCode)
                .companyName(vendorDTO.getCompanyName())
                .contactPersonName(vendorDTO.getContactPersonName())
                .email(vendorDTO.getEmail())
                .phone(vendorDTO.getPhone())
                .taxId(vendorDTO.getTaxId())
                .address(vendorDTO.getAddress())
                .city(vendorDTO.getCity())
                .country(vendorDTO.getCountry())
                .postalCode(vendorDTO.getPostalCode())
                .bankName(vendorDTO.getBankName())
                .bankAccountNumber(vendorDTO.getBankAccountNumber())
                .swiftBic(vendorDTO.getSwiftBic())
                .rating(5.0)
                .status(VendorStatus.PENDING_APPROVAL)
                .build();

        Vendor savedVendor = vendorRepository.save(vendor);
        log.info("Successfully registered vendor ID: {} with code: {}", savedVendor.getId(), savedVendor.getVendorCode());

        return mapToDTO(savedVendor);
    }

    @Override
    @Transactional
    public VendorDTO updateVendor(Long id, VendorDTO vendorDTO) {
        log.info("Updating vendor details for ID: {}", id);
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", id));

        vendor.setCompanyName(vendorDTO.getCompanyName());
        vendor.setContactPersonName(vendorDTO.getContactPersonName());
        vendor.setPhone(vendorDTO.getPhone());
        vendor.setAddress(vendorDTO.getAddress());
        vendor.setCity(vendorDTO.getCity());
        vendor.setCountry(vendorDTO.getCountry());
        vendor.setPostalCode(vendorDTO.getPostalCode());
        vendor.setBankName(vendorDTO.getBankName());
        vendor.setBankAccountNumber(vendorDTO.getBankAccountNumber());
        vendor.setSwiftBic(vendorDTO.getSwiftBic());

        Vendor updatedVendor = vendorRepository.save(vendor);
        return mapToDTO(updatedVendor);
    }

    @Override
    @Transactional(readOnly = true)
    public VendorDTO getVendorById(Long id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", id));
        return mapToDTO(vendor);
    }

    @Override
    @Transactional(readOnly = true)
    public VendorDTO getVendorByCode(String vendorCode) {
        Vendor vendor = vendorRepository.findByVendorCode(vendorCode)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "vendorCode", vendorCode));
        return mapToDTO(vendor);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<VendorDTO> getAllVendors(int page, int size, String sortBy, String sortDir, String search) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Vendor> spec = (root, query, cb) -> {
            if (search == null || search.trim().isEmpty()) {
                return cb.conjunction();
            }
            String searchPattern = "%" + search.trim().toLowerCase() + "%";
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.like(cb.lower(root.get("companyName")), searchPattern));
            predicates.add(cb.like(cb.lower(root.get("vendorCode")), searchPattern));
            predicates.add(cb.like(cb.lower(root.get("taxId")), searchPattern));
            predicates.add(cb.like(cb.lower(root.get("email")), searchPattern));
            return cb.or(predicates.toArray(new Predicate[0]));
        };

        Page<Vendor> vendorsPage = vendorRepository.findAll(spec, pageable);

        List<VendorDTO> content = vendorsPage.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return PagedResponse.<VendorDTO>builder()
                .content(content)
                .page(vendorsPage.getNumber())
                .size(vendorsPage.getSize())
                .totalElements(vendorsPage.getTotalElements())
                .totalPages(vendorsPage.getTotalPages())
                .last(vendorsPage.isLast())
                .build();
    }

    @Override
    @Transactional
    public VendorDTO processVendorApproval(Long id, VendorApprovalDTO approvalDTO) {
        log.info("Processing approval decision for Vendor ID: {}", id);
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", id));

        if (approvalDTO.getApproved()) {
            vendor.setStatus(VendorStatus.APPROVED);
        } else {
            vendor.setStatus(VendorStatus.REJECTED);
        }

        Vendor updatedVendor = vendorRepository.save(vendor);
        log.info("Vendor ID: {} status updated to: {}", id, vendor.getStatus());
        return mapToDTO(updatedVendor);
    }

    @Override
    @Transactional
    public VendorDTO updateVendorRating(Long id, Double newRating) {
        if (newRating < 0.0 || newRating > 5.0) {
            throw new BusinessRuleViolationException("Rating must be between 0.0 and 5.0");
        }

        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", id));

        vendor.setRating(newRating);
        Vendor savedVendor = vendorRepository.save(vendor);
        return mapToDTO(savedVendor);
    }

    @Override
    @Transactional
    public void deleteVendor(Long id) {
        log.info("Deleting vendor ID: {}", id);
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", id));

        vendorRepository.delete(vendor);
    }

    private VendorDTO mapToDTO(Vendor vendor) {
        return VendorDTO.builder()
                .id(vendor.getId())
                .vendorCode(vendor.getVendorCode())
                .companyName(vendor.getCompanyName())
                .contactPersonName(vendor.getContactPersonName())
                .email(vendor.getEmail())
                .phone(vendor.getPhone())
                .taxId(vendor.getTaxId())
                .address(vendor.getAddress())
                .city(vendor.getCity())
                .country(vendor.getCountry())
                .postalCode(vendor.getPostalCode())
                .bankName(vendor.getBankName())
                .bankAccountNumber(vendor.getBankAccountNumber())
                .swiftBic(vendor.getSwiftBic())
                .rating(vendor.getRating())
                .status(vendor.getStatus())
                .createdAt(vendor.getCreatedAt())
                .updatedAt(vendor.getUpdatedAt())
                .build();
    }
}
