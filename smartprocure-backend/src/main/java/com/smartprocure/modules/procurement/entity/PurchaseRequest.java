package com.smartprocure.modules.procurement.entity;

import com.smartprocure.core.entity.BaseEntity;
import com.smartprocure.modules.auth.entity.User;
import com.smartprocure.modules.procurement.enums.PurchaseRequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Enterprise Purchase Request (Requisition) Entity representing internal department material demands.
 *
 * @author Principal Java Architect
 */
@Entity
@Table(name = "purchase_requests", indexes = {
        @Index(name = "idx_pr_number", columnList = "request_number", unique = true),
        @Index(name = "idx_pr_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseRequest extends BaseEntity {

    @Column(name = "request_number", nullable = false, unique = true, length = 50)
    private String requestNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by_user_id", nullable = false)
    private User requestedBy;

    @Column(name = "department", nullable = false, length = 100)
    private String department;

    @Column(name = "needed_by_date")
    private LocalDate neededByDate;

    @Column(name = "total_estimated_amount", precision = 15, scale = 2)
    private BigDecimal totalEstimatedAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private PurchaseRequestStatus status = PurchaseRequestStatus.DRAFT;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @OneToMany(mappedBy = "purchaseRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PurchaseRequestItem> items = new ArrayList<>();

    public void addItem(PurchaseRequestItem item) {
        items.add(item);
        item.setPurchaseRequest(this);
    }
}
