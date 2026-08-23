package com.smartprocure.modules.finance.dto;

import com.smartprocure.modules.finance.enums.PaymentMethod;
import com.smartprocure.modules.finance.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for Payment transaction history display.
 *
 * @author Principal Java Architect
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {

    private Long id;
    private String paymentNumber;

    private Long invoiceId;
    private String invoiceNumber;

    private LocalDateTime paymentDate;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String transactionReference;
    private PaymentStatus status;
    private String remarks;

    private LocalDateTime createdAt;
}
