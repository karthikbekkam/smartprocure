package com.smartprocure.purchase;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseRequestDto {

    private Long id;
    private String prNumber;

    @NotBlank(message = "Department is required")
    private String department;

    private String status;
    private BigDecimal totalAmount;
    private Long requestedById;
    private String requestedByName;

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
        private String productSku;
        private Integer quantity;
        private BigDecimal estimatedUnitPrice;
        private BigDecimal totalPrice;
    }
}
