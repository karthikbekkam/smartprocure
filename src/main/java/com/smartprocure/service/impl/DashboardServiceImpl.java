package com.smartprocure.service.impl;

import com.smartprocure.domain.enums.PurchaseOrderStatus;
import com.smartprocure.domain.enums.VendorStatus;
import com.smartprocure.domain.repository.InvoiceRepository;
import com.smartprocure.domain.repository.ProductRepository;
import com.smartprocure.domain.repository.PurchaseOrderRepository;
import com.smartprocure.domain.repository.VendorRepository;
import com.smartprocure.dto.response.DashboardMetricsDTO;
import com.smartprocure.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final VendorRepository vendorRepository;
    private final ProductRepository productRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final InvoiceRepository invoiceRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardMetricsDTO getMetrics() {
        long totalVendors = vendorRepository.count();
        long pendingVendors = vendorRepository.countByStatus(VendorStatus.PENDING_APPROVAL);
        long totalProducts = productRepository.count();
        long lowStockProducts = productRepository.findLowStockProducts().size();
        long openPOs = purchaseOrderRepository.countByStatus(PurchaseOrderStatus.ISSUED)
                + purchaseOrderRepository.countByStatus(PurchaseOrderStatus.CONFIRMED);

        BigDecimal pendingInvoicesAmt = invoiceRepository.calculatePendingInvoicesAmount();
        if (pendingInvoicesAmt == null) pendingInvoicesAmt = BigDecimal.ZERO;

        BigDecimal totalSpend = purchaseOrderRepository.calculateTotalSpend();
        if (totalSpend == null) totalSpend = BigDecimal.ZERO;

        return DashboardMetricsDTO.builder()
                .totalVendors(totalVendors)
                .pendingVendors(pendingVendors)
                .totalProducts(totalProducts)
                .lowStockProducts(lowStockProducts)
                .openPurchaseOrders(openPOs)
                .pendingInvoicesAmount(pendingInvoicesAmt)
                .totalSpend(totalSpend)
                .build();
    }
}
