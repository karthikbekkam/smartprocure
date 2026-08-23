package com.smartprocure.modules.vendor.service;

import com.smartprocure.core.payload.PagedResponse;
import com.smartprocure.modules.vendor.dto.VendorApprovalDTO;
import com.smartprocure.modules.vendor.dto.VendorDTO;

/**
 * Enterprise Service Contract for Vendor Lifecycle Management & Performance Evaluation.
 *
 * @author Principal Java Architect
 */
public interface VendorService {

    VendorDTO createVendor(VendorDTO vendorDTO);

    VendorDTO updateVendor(Long id, VendorDTO vendorDTO);

    VendorDTO getVendorById(Long id);

    VendorDTO getVendorByCode(String vendorCode);

    PagedResponse<VendorDTO> getAllVendors(int page, int size, String sortBy, String sortDir, String search);

    VendorDTO processVendorApproval(Long id, VendorApprovalDTO approvalDTO);

    VendorDTO updateVendorRating(Long id, Double newRating);

    void deleteVendor(Long id);
}
