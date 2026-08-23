package com.smartprocure.vendor;

import com.smartprocure.common.exception.BusinessRuleException;
import com.smartprocure.common.exception.DuplicateResourceException;
import com.smartprocure.common.exception.ResourceNotFoundException;
import com.smartprocure.domain.AuditLog;
import com.smartprocure.domain.Notification;
import com.smartprocure.domain.Vendor;
import com.smartprocure.domain.repository.AuditLogRepository;
import com.smartprocure.domain.repository.NotificationRepository;
import com.smartprocure.domain.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VendorService {

    private final VendorRepository vendorRepository;
    private final AuditLogRepository auditLogRepository;
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public Page<VendorDto> getAllVendors(Pageable pageable) {
        return vendorRepository.findAll(pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public Page<VendorDto> getVendorsByStatus(String status, Pageable pageable) {
        return vendorRepository.findByStatus(status, pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public VendorDto getVendorById(Long id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", id));
        return mapToDto(vendor);
    }

    @Transactional
    public VendorDto createVendor(VendorDto dto) {
        if (vendorRepository.findByTaxId(dto.getTaxId()).isPresent()) {
            throw new DuplicateResourceException("Vendor with Tax ID already exists: " + dto.getTaxId());
        }

        Vendor vendor = Vendor.builder()
                .companyName(dto.getCompanyName())
                .taxId(dto.getTaxId())
                .contactEmail(dto.getContactEmail())
                .contactPhone(dto.getContactPhone())
                .address(dto.getAddress())
                .status("REGISTERED")
                .paymentTerms(dto.getPaymentTerms() != null ? dto.getPaymentTerms() : "NET_30")
                .businessLicenseNumber(dto.getBusinessLicenseNumber())
                .bankAccountDetails(dto.getBankAccountDetails())
                .build();

        Vendor saved = vendorRepository.save(vendor);
        logAudit("VENDOR_REGISTERED", saved.getId(), "Vendor registered: " + saved.getCompanyName());
        return mapToDto(saved);
    }

    @Transactional
    public VendorDto submitForReview(Long id) {
        Vendor vendor = getVendorEntity(id);
        if (!"REGISTERED".equals(vendor.getStatus())) {
            throw new BusinessRuleException("Only REGISTERED vendors can be submitted for review");
        }
        vendor.setStatus("UNDER_REVIEW");
        Vendor saved = vendorRepository.save(vendor);
        logAudit("VENDOR_SUBMITTED_FOR_REVIEW", saved.getId(), "Vendor submitted for compliance review");
        return mapToDto(saved);
    }

    @Transactional
    public VendorDto approveVendor(Long id) {
        Vendor vendor = getVendorEntity(id);
        if (!"UNDER_REVIEW".equals(vendor.getStatus()) && !"REGISTERED".equals(vendor.getStatus())) {
            throw new BusinessRuleException("Vendor must be REGISTERED or UNDER_REVIEW for approval");
        }
        vendor.setStatus("APPROVED");
        Vendor saved = vendorRepository.save(vendor);
        logAudit("VENDOR_APPROVED", saved.getId(), "Vendor approved for procurement activities");

        if (saved.getUser() != null) {
            notificationRepository.save(Notification.builder()
                    .recipient(saved.getUser())
                    .title("Vendor Account Approved")
                    .message("Congratulations! Your vendor profile " + saved.getCompanyName() + " has been approved.")
                    .type("APPROVAL")
                    .build());
        }

        return mapToDto(saved);
    }

    @Transactional
    public VendorDto rejectVendor(Long id, String reason) {
        Vendor vendor = getVendorEntity(id);
        vendor.setStatus("REJECTED");
        vendor.setRejectionReason(reason);
        Vendor saved = vendorRepository.save(vendor);
        logAudit("VENDOR_REJECTED", saved.getId(), "Vendor rejected. Reason: " + reason);

        if (saved.getUser() != null) {
            notificationRepository.save(Notification.builder()
                    .recipient(saved.getUser())
                    .title("Vendor Account Rejected")
                    .message("Your vendor application was rejected. Reason: " + reason)
                    .type("APPROVAL")
                    .build());
        }

        return mapToDto(saved);
    }

    @Transactional
    public VendorDto activateVendor(Long id) {
        Vendor vendor = getVendorEntity(id);
        if (!"APPROVED".equals(vendor.getStatus()) && !"INACTIVE".equals(vendor.getStatus())) {
            throw new BusinessRuleException("Only APPROVED or INACTIVE vendors can be activated");
        }
        vendor.setStatus("ACTIVE");
        Vendor saved = vendorRepository.save(vendor);
        logAudit("VENDOR_ACTIVATED", saved.getId(), "Vendor account activated");
        return mapToDto(saved);
    }

    @Transactional
    public VendorDto deactivateVendor(Long id) {
        Vendor vendor = getVendorEntity(id);
        vendor.setStatus("INACTIVE");
        Vendor saved = vendorRepository.save(vendor);
        logAudit("VENDOR_DEACTIVATED", saved.getId(), "Vendor account deactivated");
        return mapToDto(saved);
    }

    private Vendor getVendorEntity(Long id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", id));
    }

    private void logAudit(String action, Long entityId, String details) {
        auditLogRepository.save(AuditLog.builder()
                .action(action)
                .module("VENDOR_MANAGEMENT")
                .entityName("Vendor")
                .entityId(entityId)
                .details(details)
                .result("SUCCESS")
                .build());
    }

    private VendorDto mapToDto(Vendor vendor) {
        return VendorDto.builder()
                .id(vendor.getId())
                .companyName(vendor.getCompanyName())
                .taxId(vendor.getTaxId())
                .contactEmail(vendor.getContactEmail())
                .contactPhone(vendor.getContactPhone())
                .address(vendor.getAddress())
                .status(vendor.getStatus())
                .rating(vendor.getRating())
                .paymentTerms(vendor.getPaymentTerms())
                .rejectionReason(vendor.getRejectionReason())
                .businessLicenseNumber(vendor.getBusinessLicenseNumber())
                .bankAccountDetails(vendor.getBankAccountDetails())
                .build();
    }
}
