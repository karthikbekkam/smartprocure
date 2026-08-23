package com.smartprocure.dto.response;

import com.smartprocure.domain.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDTO {
    private Long id;
    private String paymentNumber;
    private Long invoiceId;
    private String invoiceNumber;
    private String vendorName;
    private String poNumber;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private String referenceNumber;
    private PaymentStatus status;
}
