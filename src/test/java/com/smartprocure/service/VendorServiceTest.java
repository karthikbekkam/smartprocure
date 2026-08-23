package com.smartprocure.service;

import com.smartprocure.domain.entity.Vendor;
import com.smartprocure.domain.enums.VendorStatus;
import com.smartprocure.domain.repository.VendorRepository;
import com.smartprocure.dto.request.VendorCreateDTO;
import com.smartprocure.dto.response.VendorResponseDTO;
import com.smartprocure.service.impl.VendorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendorServiceTest {

    @Mock
    private VendorRepository vendorRepository;

    @InjectMocks
    private VendorServiceImpl vendorService;

    private Vendor sampleVendor;

    @BeforeEach
    void setUp() {
        sampleVendor = Vendor.builder()
                .companyName("Test Logistics")
                .taxId("US-TAX-000")
                .contactEmail("test@domain.com")
                .contactPhone("+1-555-0100")
                .address("100 Main St")
                .status(VendorStatus.PENDING_APPROVAL)
                .rating(5.0)
                .build();
    }

    @Test
    @DisplayName("Should successfully register a new vendor")
    void shouldRegisterNewVendor() {
        VendorCreateDTO dto = new VendorCreateDTO();
        dto.setCompanyName("Test Logistics");
        dto.setTaxId("US-TAX-000");
        dto.setContactEmail("test@domain.com");
        dto.setContactPhone("+1-555-0100");
        dto.setAddress("100 Main St");

        when(vendorRepository.findByTaxId("US-TAX-000")).thenReturn(Optional.empty());
        when(vendorRepository.save(any(Vendor.class))).thenReturn(sampleVendor);

        VendorResponseDTO result = vendorService.registerVendor(dto);

        assertNotNull(result);
        assertEquals("Test Logistics", result.getCompanyName());
        assertEquals(VendorStatus.PENDING_APPROVAL, result.getStatus());
        verify(vendorRepository, times(1)).save(any(Vendor.class));
    }
}
