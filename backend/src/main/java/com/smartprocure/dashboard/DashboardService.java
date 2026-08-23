package com.smartprocure.dashboard;

import com.smartprocure.domain.repository.InventoryRepository;
import com.smartprocure.domain.repository.InvoiceRepository;
import com.smartprocure.domain.repository.PurchaseOrderRepository;
import com.smartprocure.domain.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final VendorRepository vendorRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final InvoiceRepository invoiceRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public DashboardDto getDashboardMetrics() {
        long totalVendors = vendorRepository.count();
        long activeVendors = vendorRepository.countByStatus("APPROVED");
        long pendingVendorApprovals = vendorRepository.countByStatus("UNDER_REVIEW");
        long pendingPurchaseOrders = purchaseOrderRepository.countByStatus("PENDING_APPROVAL");
        long pendingInvoices = invoiceRepository.countByStatus("SUBMITTED");
        long lowStockAlerts = inventoryRepository.findLowStockInventories().size();
        BigDecimal totalValue = purchaseOrderRepository.calculateTotalProcurementValue();

        return DashboardDto.builder()
                .totalVendors(totalVendors)
                .activeVendors(activeVendors)
                .pendingVendorApprovals(pendingVendorApprovals)
                .pendingPurchaseOrders(pendingPurchaseOrders)
                .pendingInvoices(pendingInvoices)
                .lowStockAlerts(lowStockAlerts)
                .monthlyProcurementValue(totalValue != null ? totalValue : BigDecimal.ZERO)
                .build();
    }
}
