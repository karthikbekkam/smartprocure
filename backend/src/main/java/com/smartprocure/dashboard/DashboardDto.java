package com.smartprocure.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDto {
    private long totalVendors;
    private long activeVendors;
    private long pendingVendorApprovals;
    private long pendingPurchaseOrders;
    private long pendingInvoices;
    private long lowStockAlerts;
    private BigDecimal monthlyProcurementValue;
}
