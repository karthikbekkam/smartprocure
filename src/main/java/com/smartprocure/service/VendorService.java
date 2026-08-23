package com.smartprocure.service;

import com.smartprocure.domain.enums.VendorStatus;
import com.smartprocure.dto.request.VendorCreateDTO;
import com.smartprocure.dto.response.VendorResponseDTO;

import java.util.List;

public interface VendorService {
    VendorResponseDTO registerVendor(VendorCreateDTO createDTO);
    VendorResponseDTO updateVendorStatus(Long id, VendorStatus status);
    List<VendorResponseDTO> getAllVendors();
    List<VendorResponseDTO> getVendorsByStatus(VendorStatus status);
    VendorResponseDTO getVendorById(Long id);
}
