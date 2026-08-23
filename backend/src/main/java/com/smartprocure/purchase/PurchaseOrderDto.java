package com.smartprocure.purchase;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderDto {

    private Long id;
    private String poNumber;
    private Long purchaseRequestId;

    @NotNull(message = "Vendor ID is required")
    private Long vendorId;
    private String vendorName;

    private String status;
    private BigDecimal totalAmount;
    private LocalDate deliveryDate;
    private String createdByUserName;

    @Builder.Default
    private List<ItemDto> items = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemDto {
        private Long id;
        private Long productId;
        private String productName;
        private Integer quantityOrdered;
        private Integer quantityReceived;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
    }
}
