package com.smartprocure.dto.response;

import com.smartprocure.domain.enums.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceResponseDTO {
    private Long id;
    private String invoiceNumber;
    private String poNumber;
    private String vendorName;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private InvoiceStatus status;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
}
