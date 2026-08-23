package com.smartprocure.modules.finance.dto;

import com.smartprocure.modules.finance.enums.InvoiceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO Payload for Invoice Master Data operations.
 *
 * @author Principal Java Architect
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDTO {

    private Long id;
    private String invoiceNumber;

    @NotNull(message = "Purchase Order ID is required")
    private Long purchaseOrderId;
    private String purchaseOrderNumber;

    private Long vendorId;
    private String vendorCode;
    private String vendorCompanyName;

    private LocalDate invoiceDate;
    private LocalDate dueDate;

    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private BigDecimal remainingBalance;

    private InvoiceStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
