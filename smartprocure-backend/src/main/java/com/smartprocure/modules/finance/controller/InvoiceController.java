package com.smartprocure.modules.finance.controller;

import com.smartprocure.core.payload.ApiResponse;
import com.smartprocure.core.payload.PagedResponse;
import com.smartprocure.modules.finance.dto.InvoiceDTO;
import com.smartprocure.modules.finance.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Enterprise REST Controller for Accounts Payable Invoice Management.
 *
 * @author Principal Java Architect
 */
@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoice Management", description = "Endpoints for Accounts Payable Invoices, Billing, and Outstanding Balance Audits")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/generate/{purchaseOrderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE_MANAGER', 'PROCUREMENT_MANAGER')")
    @Operation(summary = "Generate Invoice from Purchase Order", description = "Generates a billing invoice from an active purchase order.")
    public ResponseEntity<ApiResponse<InvoiceDTO>> generateInvoice(
            @PathVariable Long purchaseOrderId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {
        InvoiceDTO created = invoiceService.generateInvoiceFromPurchaseOrder(purchaseOrderId, dueDate);
        return new ResponseEntity<>(ApiResponse.success(created, "Invoice generated successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Invoice by ID", description = "Retrieves details of a specific financial invoice.")
    public ResponseEntity<ApiResponse<InvoiceDTO>> getInvoiceById(@PathVariable Long id) {
        InvoiceDTO invoice = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(ApiResponse.success(invoice, "Invoice details retrieved successfully"));
    }

    @GetMapping("/number/{invoiceNumber}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Invoice by Invoice Number", description = "Finds invoice record by unique billing number.")
    public ResponseEntity<ApiResponse<InvoiceDTO>> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        InvoiceDTO invoice = invoiceService.getInvoiceByNumber(invoiceNumber);
        return ResponseEntity.ok(ApiResponse.success(invoice, "Invoice details retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Paginated Invoices List", description = "Fetches invoices list filtered optionally by status.")
    public ResponseEntity<ApiResponse<PagedResponse<InvoiceDTO>>> getAllInvoices(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
            @RequestParam(value = "status", required = false) String status) {

        PagedResponse<InvoiceDTO> response = invoiceService.getAllInvoices(page, size, sortBy, sortDir, status);
        return ResponseEntity.ok(ApiResponse.success(response, "Invoices retrieved successfully"));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'FINANCE_MANAGER')")
    @Operation(summary = "Cancel Invoice", description = "Cancels an unpaid or outstanding invoice.")
    public ResponseEntity<ApiResponse<Void>> cancelInvoice(@PathVariable Long id) {
        invoiceService.cancelInvoice(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Invoice cancelled successfully"));
    }
}
