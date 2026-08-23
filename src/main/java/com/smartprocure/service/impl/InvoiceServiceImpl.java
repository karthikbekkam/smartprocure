package com.smartprocure.service.impl;

import com.smartprocure.domain.entity.Invoice;
import com.smartprocure.domain.entity.Payment;
import com.smartprocure.domain.entity.PurchaseOrder;
import com.smartprocure.domain.enums.InvoiceStatus;
import com.smartprocure.domain.repository.InvoiceRepository;
import com.smartprocure.domain.repository.PaymentRepository;
import com.smartprocure.domain.repository.PurchaseOrderRepository;
import com.smartprocure.dto.request.InvoiceCreateDTO;
import com.smartprocure.dto.response.InvoiceResponseDTO;
import com.smartprocure.exception.DuplicateResourceException;
import com.smartprocure.exception.ResourceNotFoundException;
import com.smartprocure.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public InvoiceResponseDTO createInvoice(InvoiceCreateDTO createDTO) {
        if (invoiceRepository.findByInvoiceNumber(createDTO.getInvoiceNumber()).isPresent()) {
            throw new DuplicateResourceException("Invoice number already exists: " + createDTO.getInvoiceNumber());
        }

        PurchaseOrder po = purchaseOrderRepository.findById(createDTO.getPurchaseOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("PurchaseOrder", "id", createDTO.getPurchaseOrderId()));

        Invoice invoice = Invoice.builder()
                .invoiceNumber(createDTO.getInvoiceNumber())
                .purchaseOrder(po)
                .vendor(po.getVendor())
                .invoiceDate(createDTO.getInvoiceDate())
                .dueDate(createDTO.getDueDate())
                .totalAmount(createDTO.getTotalAmount())
                .paidAmount(BigDecimal.ZERO)
                .status(InvoiceStatus.SUBMITTED)
                .build();

        Invoice saved = invoiceRepository.save(invoice);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public InvoiceResponseDTO updateInvoiceStatus(Long id, InvoiceStatus status) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));
        invoice.setStatus(status);
        return mapToDTO(invoiceRepository.save(invoice));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponseDTO> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponseDTO getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));
        return mapToDTO(invoice);
    }

    private InvoiceResponseDTO mapToDTO(Invoice invoice) {
        return InvoiceResponseDTO.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .poNumber(invoice.getPurchaseOrder() != null ? invoice.getPurchaseOrder().getPoNumber() : "N/A")
                .vendorName(invoice.getVendor() != null ? invoice.getVendor().getCompanyName() : "Unassigned Vendor")
                .totalAmount(invoice.getTotalAmount())
                .paidAmount(invoice.getPaidAmount())
                .status(invoice.getStatus())
                .invoiceDate(invoice.getInvoiceDate())
                .dueDate(invoice.getDueDate())
                .build();
    }
}
