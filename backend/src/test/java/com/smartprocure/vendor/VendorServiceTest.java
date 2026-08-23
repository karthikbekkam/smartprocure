package com.smartprocure.vendor;

import com.smartprocure.common.exception.DuplicateResourceException;
import com.smartprocure.domain.Vendor;
import com.smartprocure.domain.repository.AuditLogRepository;
import com.smartprocure.domain.repository.NotificationRepository;
import com.smartprocure.domain.repository.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
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

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private VendorService vendorService;

    private VendorDto vendorDto;

    @BeforeEach
    void setUp() {
        vendorDto = VendorDto.builder()
                .companyName("Acme Global")
                .taxId("TAX-998877")
                .contactEmail("contact@acme.com")
                .contactPhone("+15550100")
                .address("100 Enterprise Way")
                .build();
    }

    @Test
    void createVendor_Success() {
        when(vendorRepository.findByTaxId("TAX-998877")).thenReturn(Optional.empty());
        when(vendorRepository.save(any(Vendor.class))).thenAnswer(invocation -> {
            Vendor v = invocation.getArgument(0);
            v.setId(1L);
            return v;
        });

        VendorDto result = vendorService.createVendor(vendorDto);

        assertNotNull(result);
        assertEquals("Acme Global", result.getCompanyName());
        assertEquals("REGISTERED", result.getStatus());
        verify(vendorRepository, times(1)).save(any(Vendor.class));
    }

    @Test
    void createVendor_DuplicateTaxId_ThrowsException() {
        when(vendorRepository.findByTaxId("TAX-998877")).thenReturn(Optional.of(new Vendor()));

        assertThrows(DuplicateResourceException.class, () -> vendorService.createVendor(vendorDto));
        verify(vendorRepository, never()).save(any(Vendor.class));
    }
}
