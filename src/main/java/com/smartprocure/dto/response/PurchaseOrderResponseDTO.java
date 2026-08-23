package com.smartprocure.dto.response;

import com.smartprocure.domain.enums.PurchaseOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderResponseDTO {
    private Long id;
    private String poNumber;
    private String vendorName;
    private PurchaseOrderStatus status;
    private BigDecimal totalAmount;
    private LocalDate deliveryDate;
    private LocalDateTime createdAt;
}
