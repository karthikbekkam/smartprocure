package com.smartprocure.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PurchaseRequestCreateDTO {

    @NotBlank(message = "Department is required")
    private String department;

    @NotNull(message = "Total amount is required")
    private BigDecimal totalAmount;

    private String notes;
}
