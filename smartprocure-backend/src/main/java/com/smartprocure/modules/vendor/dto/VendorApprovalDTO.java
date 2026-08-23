package com.smartprocure.modules.vendor.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Procurement Manager / Admin vendor onboarding approval decision.
 *
 * @author Principal Java Architect
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorApprovalDTO {

    @NotNull(message = "Approval decision (true/false) is required")
    private Boolean approved;

    private String remarks;
}
