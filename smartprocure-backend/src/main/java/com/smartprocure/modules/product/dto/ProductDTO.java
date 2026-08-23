package com.smartprocure.modules.product.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for Product Master Data payload transfer.
 *
 * @author Principal Java Architect
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;

    @NotBlank(message = "Product SKU is required")
    private String sku;

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 150, message = "Product name must be between 2 and 150 characters")
    private String name;

    private String description;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private BigDecimal unitPrice;

    @NotBlank(message = "Unit of Measure (UOM) is required")
    private String unitOfMeasure;

    @NotNull(message = "Reorder level is required")
    @Min(value = 0, message = "Reorder level cannot be negative")
    private Integer reorderLevel;

    @NotNull(message = "Category ID is required")
    private Long categoryId;
    private String categoryName;

    private Long preferredVendorId;
    private String preferredVendorCompanyName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
