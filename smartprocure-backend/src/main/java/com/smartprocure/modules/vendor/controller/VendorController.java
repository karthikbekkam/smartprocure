package com.smartprocure.modules.vendor.controller;

import com.smartprocure.core.payload.ApiResponse;
import com.smartprocure.core.payload.PagedResponse;
import com.smartprocure.modules.vendor.dto.VendorApprovalDTO;
import com.smartprocure.modules.vendor.dto.VendorDTO;
import com.smartprocure.modules.vendor.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Enterprise REST Controller for Vendor Master Data Management & Onboarding Workflows.
 *
 * @author Principal Java Architect
 */
@RestController
@RequestMapping("/api/v1/vendors")
@RequiredArgsConstructor
@Tag(name = "Vendor Management", description = "Endpoints for Supplier Onboarding, Compliance Approval, and Master Data Operations")
public class VendorController {

    private final VendorService vendorService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'VENDOR')")
    @Operation(summary = "Onboard New Vendor", description = "Submits a new vendor registration for compliance verification.")
    public ResponseEntity<ApiResponse<VendorDTO>> createVendor(@Valid @RequestBody VendorDTO vendorDTO) {
        VendorDTO created = vendorService.createVendor(vendorDTO);
        return new ResponseEntity<>(ApiResponse.success(created, "Vendor onboarding request submitted successfully"), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_MANAGER', 'WAREHOUSE_MANAGER')")
    @Operation(summary = "Get Paginated Vendor Master Directory", description = "Fetches vendor directory with search filter and pagination.")
    public ResponseEntity<ApiResponse<PagedResponse<VendorDTO>>> getAllVendors(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
            @RequestParam(value = "search", required = false) String search) {

        PagedResponse<VendorDTO> response = vendorService.getAllVendors(page, size, sortBy, sortDir, search);
        return ResponseEntity.ok(ApiResponse.success(response, "Vendors directory retrieved successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_MANAGER', 'VENDOR')")
    @Operation(summary = "Get Vendor Details by ID", description = "Retrieves complete master data record for a specific vendor.")
    public ResponseEntity<ApiResponse<VendorDTO>> getVendorById(@PathVariable Long id) {
        VendorDTO vendor = vendorService.getVendorById(id);
        return ResponseEntity.ok(ApiResponse.success(vendor, "Vendor record retrieved successfully"));
    }

    @GetMapping("/code/{vendorCode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'FINANCE_MANAGER')")
    @Operation(summary = "Get Vendor by Unique Vendor Code", description = "Finds vendor by unique ERP identifier code.")
    public ResponseEntity<ApiResponse<VendorDTO>> getVendorByCode(@PathVariable String vendorCode) {
        VendorDTO vendor = vendorService.getVendorByCode(vendorCode);
        return ResponseEntity.ok(ApiResponse.success(vendor, "Vendor record retrieved successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    @Operation(summary = "Update Vendor Master Data", description = "Updates vendor contact, address, or banking details.")
    public ResponseEntity<ApiResponse<VendorDTO>> updateVendor(
            @PathVariable Long id,
            @Valid @RequestBody VendorDTO vendorDTO) {
        VendorDTO updated = vendorService.updateVendor(id, vendorDTO);
        return ResponseEntity.ok(ApiResponse.success(updated, "Vendor details updated successfully"));
    }

    @PatchMapping("/{id}/approval")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    @Operation(summary = "Process Vendor Onboarding Approval", description = "Approves or rejects a vendor compliance application.")
    public ResponseEntity<ApiResponse<VendorDTO>> processVendorApproval(
            @PathVariable Long id,
            @Valid @RequestBody VendorApprovalDTO approvalDTO) {
        VendorDTO updated = vendorService.processVendorApproval(id, approvalDTO);
        return ResponseEntity.ok(ApiResponse.success(updated, "Vendor approval status updated successfully"));
    }

    @PatchMapping("/{id}/rating")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    @Operation(summary = "Update Vendor Performance Score", description = "Updates score rating for vendor performance.")
    public ResponseEntity<ApiResponse<VendorDTO>> updateVendorRating(
            @PathVariable Long id,
            @RequestParam Double rating) {
        VendorDTO updated = vendorService.updateVendorRating(id, rating);
        return ResponseEntity.ok(ApiResponse.success(updated, "Vendor performance rating updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete Vendor Record", description = "Removes a vendor master record from the system.")
    public ResponseEntity<ApiResponse<Void>> deleteVendor(@PathVariable Long id) {
        vendorService.deleteVendor(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Vendor record deleted successfully"));
    }
}
