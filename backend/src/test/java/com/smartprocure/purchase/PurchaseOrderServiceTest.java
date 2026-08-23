package com.smartprocure.purchase;

import com.smartprocure.domain.Product;
import com.smartprocure.domain.PurchaseOrder;
import com.smartprocure.domain.User;
import com.smartprocure.domain.Vendor;
import com.smartprocure.domain.repository.ProductRepository;
import com.smartprocure.domain.repository.PurchaseOrderRepository;
import com.smartprocure.domain.repository.UserRepository;
import com.smartprocure.domain.repository.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private VendorRepository vendorRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PurchaseOrderService purchaseOrderService;

    private PurchaseOrderDto purchaseOrderDto;

    @BeforeEach
    void setUp() {
        PurchaseOrderDto.ItemDto item = PurchaseOrderDto.ItemDto.builder()
                .productId(1L)
                .quantityOrdered(10)
                .unitPrice(new BigDecimal("100.00"))
                .build();

        purchaseOrderDto = PurchaseOrderDto.builder()
                .vendorId(1L)
                .items(List.of(item))
                .build();
    }

    @Test
    void createPurchaseOrder_Success() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@smartprocure.local");
        SecurityContextHolder.setContext(securityContext);

        when(vendorRepository.findById(1L)).thenReturn(Optional.of(Vendor.builder().companyName("Acme").build()));
        when(userRepository.findByEmail("admin@smartprocure.local")).thenReturn(Optional.of(User.builder().firstName("System").lastName("Admin").build()));
        when(productRepository.findById(1L)).thenReturn(Optional.of(Product.builder().sku("SKU-1").name("Monitor").build()));

        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenAnswer(invocation -> {
            PurchaseOrder po = invocation.getArgument(0);
            po.setId(1L);
            return po;
        });

        PurchaseOrderDto result = purchaseOrderService.createPurchaseOrder(purchaseOrderDto);

        assertNotNull(result);
        assertEquals("Acme", result.getVendorName());
        assertEquals(new BigDecimal("1000.00"), result.getTotalAmount());
        verify(purchaseOrderRepository, times(1)).save(any(PurchaseOrder.class));
    }
}
