package com.smartprocure.modules.finance.service.impl;

import com.smartprocure.core.exception.BusinessRuleViolationException;
import com.smartprocure.core.exception.ResourceNotFoundException;
import com.smartprocure.core.payload.PagedResponse;
import com.smartprocure.modules.finance.dto.InvoiceDTO;
import com.smartprocure.modules.finance.entity.Invoice;
import com.smartprocure.modules.finance.enums.InvoiceStatus;
import com.smartprocure.modules.finance.repository.InvoiceRepository;
import com.smartprocure.modules.finance.service.InvoiceService;
import com.smartprocure.modules.procurement.entity.PurchaseOrder;
import com.smartprocure.modules.procurement.repository.PurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service Implementation for Invoice generation and billing lifecycle management.
 *
 * @author Principal Java Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PurchaseOrderRepository orderRepository;

    @Override
    @Transactional
    public InvoiceDTO generateInvoiceFromPurchaseOrder(Long purchaseOrderId, LocalDate dueDate) {
        log.info("Generating invoice for Purchase Order ID: {}", purchaseOrderId);

        PurchaseOrder order = orderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", purchaseOrderId));

        String invoiceNumber = "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        LocalDate calculatedDueDate = dueDate != null ? dueDate : LocalDate.now().plusDays(30);

        Invoice invoice = Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .purchaseOrder(order)
                .vendor(order.getVendor())
                .invoiceDate(LocalDate.now())
                .dueDate(calculatedDueDate)
                .subtotal(order.getTotalAmount())
                .taxAmount(order.getTaxAmount())
                .totalAmount(order.getGrandTotal())
                .paidAmount(BigDecimal.ZERO)
                .status(InvoiceStatus.ISSUED)
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Successfully generated Invoice ID: {} with number: {}", savedInvoice.getId(), savedInvoice.getInvoiceNumber());

        return mapToDTO(savedInvoice);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceDTO getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));
        return mapToDTO(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceDTO getInvoiceByNumber(String invoiceNumber) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "invoiceNumber", invoiceNumber));
        return mapToDTO(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<InvoiceDTO> getAllInvoices(int page, int size, String sortBy, String sortDir, String statusStr) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Invoice> pageResult;
        if (statusStr != null && !statusStr.trim().isEmpty()) {
            InvoiceStatus status = InvoiceStatus.valueOf(statusStr.toUpperCase());
            pageResult = invoiceRepository.findByStatus(status, pageable);
        } else {
            pageResult = invoiceRepository.findAll(pageable);
        }

        List<InvoiceDTO> content = pageResult.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return PagedResponse.<InvoiceDTO>builder()
                .content(content)
                .page(pageResult.getNumber())
                .size(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .last(pageResult.isLast())
                .build();
    }

    @Override
    @Transactional
    public void cancelInvoice(Long id) {
        log.info("Cancelling Invoice ID: {}", id);
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BusinessRuleViolationException("Cannot cancel an invoice that has already been PAID in full.");
        }

        invoice.setStatus(InvoiceStatus.CANCELLED);
        invoiceRepository.save(invoice);
    }

    private InvoiceDTO mapToDTO(Invoice invoice) {
        BigDecimal remainingBalance = invoice.getTotalAmount().subtract(invoice.getPaidAmount());

        return InvoiceDTO.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .purchaseOrderId(invoice.getPurchaseOrder().getId())
                .purchaseOrderNumber(invoice.getPurchaseOrder().getPoNumber())
                .vendorId(invoice.getVendor().getId())
                .vendorCode(invoice.getVendor().getVendorCode())
                .vendorCompanyName(invoice.getVendor().getCompanyName())
                .invoiceDate(invoice.getInvoiceDate())
                .dueDate(invoice.getDueDate())
                .subtotal(invoice.getSubtotal())
                .taxAmount(invoice.getTaxAmount())
                .totalAmount(invoice.getTotalAmount())
                .paidAmount(invoice.getPaidAmount())
                .remainingBalance(remainingBalance)
                .status(invoice.getStatus())
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .build();
    }
}
