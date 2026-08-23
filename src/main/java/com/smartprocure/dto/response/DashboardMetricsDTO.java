package com.smartprocure.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardMetricsDTO {
    private long totalVendors;
    private long pendingVendors;
    private long totalProducts;
    private long lowStockProducts;
    private long openPurchaseOrders;
    private BigDecimal pendingInvoicesAmount;
    private BigDecimal totalSpend;
}
