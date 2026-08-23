package com.smartprocure.modules.procurement.dto;

import com.smartprocure.modules.procurement.enums.PurchaseOrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO Payload for Purchase Order contract operations.
 *
 * @author Principal Java Architect
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderDTO {

    private Long id;
    private String poNumber;

    private Long purchaseRequestId;
    private String purchaseRequestNumber;

    @NotNull(message = "Vendor ID is required")
    private Long vendorId;
    private String vendorCode;
    private String vendorCompanyName;

    private Long createdByUserId;
    private String createdByUserName;

    private LocalDateTime orderDate;
    private LocalDate expectedDeliveryDate;

    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private BigDecimal grandTotal;

    private PurchaseOrderStatus status;

    @NotEmpty(message = "At least one order line item is required")
    @Valid
    private List<PurchaseOrderItemDTO> items;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
