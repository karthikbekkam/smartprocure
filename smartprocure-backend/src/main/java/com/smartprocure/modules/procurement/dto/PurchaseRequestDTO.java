package com.smartprocure.modules.procurement.dto;

import com.smartprocure.modules.procurement.enums.PurchaseRequestStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO Payload for Purchase Request (Requisition) lifecycle operations.
 *
 * @author Principal Java Architect
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequestDTO {

    private Long id;
    private String requestNumber;

    private Long requestedByUserId;
    private String requestedByUserName;
    private String requestedByUserEmail;

    @NotBlank(message = "Department is required")
    private String department;

    private LocalDate neededByDate;
    private BigDecimal totalEstimatedAmount;
    private PurchaseRequestStatus status;
    private String rejectionReason;

    @NotEmpty(message = "At least one requisition line item is required")
    @Valid
    private List<PurchaseRequestItemDTO> items;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
