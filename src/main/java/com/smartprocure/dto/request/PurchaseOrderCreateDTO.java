package com.smartprocure.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PurchaseOrderCreateDTO {

    private Long purchaseRequestId;

    @NotNull(message = "Vendor ID is required")
    private Long vendorId;

    private LocalDate deliveryDate;
}
