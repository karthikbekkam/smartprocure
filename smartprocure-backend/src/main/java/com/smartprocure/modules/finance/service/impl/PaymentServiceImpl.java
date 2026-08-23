package com.smartprocure.modules.finance.service.impl;

import com.smartprocure.core.exception.BusinessRuleViolationException;
import com.smartprocure.core.exception.ResourceNotFoundException;
import com.smartprocure.modules.finance.dto.PaymentDTO;
import com.smartprocure.modules.finance.dto.PaymentRequest;
import com.smartprocure.modules.finance.entity.Invoice;
import com.smartprocure.modules.finance.entity.Payment;
import com.smartprocure.modules.finance.enums.InvoiceStatus;
import com.smartprocure.modules.finance.enums.PaymentStatus;
import com.smartprocure.modules.finance.repository.InvoiceRepository;
import com.smartprocure.modules.finance.repository.PaymentRepository;
import com.smartprocure.modules.finance.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service Implementation for processing payment receipts and updating invoice payment status.
 *
 * @author Principal Java Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;

    @Override
    @Transactional
    public PaymentDTO processPayment(PaymentRequest paymentRequest) {
        log.info("Processing payment for Invoice ID: {} of amount: {}", paymentRequest.getInvoiceId(), paymentRequest.getAmount());

        Invoice invoice = invoiceRepository.findById(paymentRequest.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", paymentRequest.getInvoiceId()));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BusinessRuleViolationException("Invoice " + invoice.getInvoiceNumber() + " is already PAID in full.");
        }

        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new BusinessRuleViolationException("Cannot process payment for a CANCELLED invoice.");
        }

        BigDecimal remainingBalance = invoice.getTotalAmount().subtract(invoice.getPaidAmount());
        if (paymentRequest.getAmount().compareTo(remainingBalance) > 0) {
            throw new BusinessRuleViolationException("Payment amount (" + paymentRequest.getAmount() + ") exceeds remaining invoice balance (" + remainingBalance + ").");
        }

        BigDecimal newPaidAmount = invoice.getPaidAmount().add(paymentRequest.getAmount());
        invoice.setPaidAmount(newPaidAmount);

        if (newPaidAmount.compareTo(invoice.getTotalAmount()) >= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        }

        invoiceRepository.save(invoice);

        String paymentNumber = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Payment payment = Payment.builder()
                .paymentNumber(paymentNumber)
                .invoice(invoice)
                .paymentDate(LocalDateTime.now())
                .amount(paymentRequest.getAmount())
                .paymentMethod(paymentRequest.getPaymentMethod())
                .transactionReference(paymentRequest.getTransactionReference())
                .status(PaymentStatus.COMPLETED)
                .remarks(paymentRequest.getRemarks())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Successfully recorded Payment ID: {} under reference: {}", savedPayment.getId(), savedPayment.getPaymentNumber());

        return mapToDTO(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByInvoice(Long invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        return mapToDTO(payment);
    }

    private PaymentDTO mapToDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .paymentNumber(payment.getPaymentNumber())
                .invoiceId(payment.getInvoice().getId())
                .invoiceNumber(payment.getInvoice().getInvoiceNumber())
                .paymentDate(payment.getPaymentDate())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .transactionReference(payment.getTransactionReference())
                .status(payment.getStatus())
                .remarks(payment.getRemarks())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
