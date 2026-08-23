package com.smartprocure.modules.procurement.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Purchase Request line items.
 *
 * @author Principal Java Architect
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequestItemDTO {

    private Long id;

    @NotNull(message = "Product ID is required")
    private Long productId;
    private String productSku;
    private String productName;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Estimated unit price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private BigDecimal estimatedUnitPrice;

    private BigDecimal totalPrice;
}
