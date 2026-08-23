package com.smartprocure.invoice;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceDto {

    private Long id;
    private String invoiceNumber;

    @NotNull(message = "Purchase Order ID is required")
    private Long purchaseOrderId;
    private String poNumber;

    @NotNull(message = "Vendor ID is required")
    private Long vendorId;
    private String vendorName;

    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private String status;
}
