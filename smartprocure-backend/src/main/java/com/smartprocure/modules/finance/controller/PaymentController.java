package com.smartprocure.modules.finance.controller;

import com.smartprocure.core.payload.ApiResponse;
import com.smartprocure.modules.finance.dto.PaymentDTO;
import com.smartprocure.modules.finance.dto.PaymentRequest;
import com.smartprocure.modules.finance.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Enterprise REST Controller for Payment Disbursements & Transaction Receipts.
 *
 * @author Principal Java Architect
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Management", description = "Endpoints for Processing Disbursements, Wire Transfers, and Payment Receipts")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE_MANAGER')")
    @Operation(summary = "Process Payment Transaction", description = "Records a payment transaction against an active invoice.")
    public ResponseEntity<ApiResponse<PaymentDTO>> processPayment(@Valid @RequestBody PaymentRequest paymentRequest) {
        PaymentDTO payment = paymentService.processPayment(paymentRequest);
        return new ResponseEntity<>(ApiResponse.success(payment, "Payment transaction processed successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/invoice/{invoiceId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Payment Receipts by Invoice", description = "Retrieves complete transaction history for an invoice.")
    public ResponseEntity<ApiResponse<List<PaymentDTO>>> getPaymentsByInvoice(@PathVariable Long invoiceId) {
        List<PaymentDTO> payments = paymentService.getPaymentsByInvoice(invoiceId);
        return ResponseEntity.ok(ApiResponse.success(payments, "Invoice payment receipts retrieved successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Payment Receipt by ID", description = "Retrieves transaction details of a specific payment receipt.")
    public ResponseEntity<ApiResponse<PaymentDTO>> getPaymentById(@PathVariable Long id) {
        PaymentDTO payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(ApiResponse.success(payment, "Payment transaction details retrieved successfully"));
    }
}
