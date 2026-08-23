package com.smartprocure.web.rest;

import com.smartprocure.domain.enums.VendorStatus;
import com.smartprocure.dto.request.VendorCreateDTO;
import com.smartprocure.dto.response.ApiResponse;
import com.smartprocure.dto.response.VendorResponseDTO;
import com.smartprocure.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vendors")
@RequiredArgsConstructor
@Tag(name = "Vendor Management", description = "Endpoints for managing vendors, onboard requests, and approvals")
public class VendorRestController {

    private final VendorService vendorService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_MANAGER')")
    @Operation(summary = "Get All Vendors", description = "Retrieve a list of all registered enterprise vendors")
    public ResponseEntity<ApiResponse> getAllVendors(@RequestParam(required = false) VendorStatus status) {
        List<VendorResponseDTO> vendors;
        if (status != null) {
            vendors = vendorService.getVendorsByStatus(status);
        } else {
            vendors = vendorService.getAllVendors();
        }
        return ResponseEntity.ok(ApiResponse.ok("Vendors fetched successfully", vendors));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_MANAGER', 'VENDOR')")
    @Operation(summary = "Get Vendor by ID", description = "Retrieve detailed information of a vendor")
    public ResponseEntity<ApiResponse> getVendorById(@PathVariable Long id) {
        VendorResponseDTO vendor = vendorService.getVendorById(id);
        return ResponseEntity.ok(ApiResponse.ok("Vendor details retrieved", vendor));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'VENDOR')")
    @Operation(summary = "Register Vendor", description = "Onboard a new supplier/vendor into SmartProcure")
    public ResponseEntity<ApiResponse> registerVendor(@Valid @RequestBody VendorCreateDTO createDTO) {
        VendorResponseDTO vendor = vendorService.registerVendor(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Vendor registered successfully", vendor));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    @Operation(summary = "Update Vendor Status", description = "Approve, reject, or suspend vendor status")
    public ResponseEntity<ApiResponse> updateStatus(@PathVariable Long id, @RequestParam VendorStatus status) {
        VendorResponseDTO vendor = vendorService.updateVendorStatus(id, status);
        return ResponseEntity.ok(ApiResponse.ok("Vendor status updated to " + status, vendor));
    }
}
