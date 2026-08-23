package com.smartprocure.vendor;

import com.smartprocure.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/vendors")
@RequiredArgsConstructor
public class VendorController {

    private final VendorService vendorService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<VendorDto>>> getAllVendors(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok("Vendors fetched successfully", vendorService.getAllVendors(pageable)));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<Page<VendorDto>>> getVendorsByStatus(@PathVariable String status, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok("Vendors fetched by status", vendorService.getVendorsByStatus(status, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VendorDto>> getVendorById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Vendor fetched successfully", vendorService.getVendorById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VendorDto>> createVendor(@Valid @RequestBody VendorDto dto) {
        return ResponseEntity.ok(ApiResponse.ok("Vendor created successfully", vendorService.createVendor(dto)));
    }

    @PostMapping("/{id}/submit-review")
    public ResponseEntity<ApiResponse<VendorDto>> submitForReview(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Vendor submitted for compliance review", vendorService.submitForReview(id)));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('PROCUREMENT_MANAGER')")
    public ResponseEntity<ApiResponse<VendorDto>> approveVendor(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Vendor approved successfully", vendorService.approveVendor(id)));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('PROCUREMENT_MANAGER')")
    public ResponseEntity<ApiResponse<VendorDto>> rejectVendor(@PathVariable Long id, @RequestParam(required = false, defaultValue = "Compliance criteria not met") String reason) {
        return ResponseEntity.ok(ApiResponse.ok("Vendor rejected", vendorService.rejectVendor(id, reason)));
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('PROCUREMENT_MANAGER')")
    public ResponseEntity<ApiResponse<VendorDto>> activateVendor(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Vendor activated", vendorService.activateVendor(id)));
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('ADMINISTRATOR') or hasAuthority('PROCUREMENT_MANAGER')")
    public ResponseEntity<ApiResponse<VendorDto>> deactivateVendor(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Vendor deactivated", vendorService.deactivateVendor(id)));
    }
}
