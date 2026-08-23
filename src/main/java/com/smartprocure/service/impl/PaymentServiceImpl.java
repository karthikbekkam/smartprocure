package com.smartprocure.service.impl;

import com.smartprocure.domain.entity.Invoice;
import com.smartprocure.domain.entity.Payment;
import com.smartprocure.domain.entity.User;
import com.smartprocure.domain.enums.InvoiceStatus;
import com.smartprocure.domain.enums.PaymentStatus;
import com.smartprocure.domain.repository.InvoiceRepository;
import com.smartprocure.domain.repository.PaymentRepository;
import com.smartprocure.domain.repository.UserRepository;
import com.smartprocure.dto.request.PaymentCreateDTO;
import com.smartprocure.dto.response.PaymentResponseDTO;
import com.smartprocure.exception.BadRequestException;
import com.smartprocure.exception.ResourceNotFoundException;
import com.smartprocure.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PaymentResponseDTO processPayment(PaymentCreateDTO paymentCreateDTO, String username) {
        Invoice invoice = invoiceRepository.findById(paymentCreateDTO.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", paymentCreateDTO.getInvoiceId()));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BadRequestException("This invoice has already been paid.");
        }

        if (invoice.getStatus() == InvoiceStatus.VOID || invoice.getStatus() == InvoiceStatus.REJECTED) {
            throw new BadRequestException("Cannot process payment for invoice in " + invoice.getStatus() + " status.");
        }

        BigDecimal currentPaid = invoice.getPaidAmount() != null ? invoice.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal totalAmount = invoice.getTotalAmount() != null ? invoice.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal remainingPayable = totalAmount.subtract(currentPaid);

        if (paymentCreateDTO.getAmount() == null || paymentCreateDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Payment amount must be greater than zero.");
        }

        if (paymentCreateDTO.getAmount().compareTo(remainingPayable) > 0) {
            throw new BadRequestException("Payment amount ($" + paymentCreateDTO.getAmount() + 
                    ") exceeds remaining payable balance ($" + remainingPayable + ").");
        }

        User processedBy = null;
        if (username != null && !username.isEmpty()) {
            processedBy = userRepository.findByEmail(username).orElse(null);
        }
        if (processedBy == null) {
            processedBy = userRepository.findAll().stream().findFirst().orElse(null);
        }

        String paymentNumber = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String refNum = paymentCreateDTO.getReferenceNumber();
        if (refNum == null || refNum.isBlank()) {
            refNum = "REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }

        Payment payment = Payment.builder()
                .paymentNumber(paymentNumber)
                .invoice(invoice)
                .amount(paymentCreateDTO.getAmount())
                .paymentDate(LocalDateTime.now())
                .paymentMethod(paymentCreateDTO.getPaymentMethod())
                .referenceNumber(refNum)
                .status(PaymentStatus.COMPLETED)
                .processedBy(processedBy)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Update Invoice status and paid amount
        BigDecimal newPaidAmount = currentPaid.add(paymentCreateDTO.getAmount());
        invoice.setPaidAmount(newPaidAmount);
        if (newPaidAmount.compareTo(totalAmount) >= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        }
        invoiceRepository.save(invoice);

        return mapToDTO(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        return mapToDTO(payment);
    }

    private PaymentResponseDTO mapToDTO(Payment payment) {
        Invoice invoice = payment.getInvoice();
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .paymentNumber(payment.getPaymentNumber())
                .invoiceId(invoice != null ? invoice.getId() : null)
                .invoiceNumber(invoice != null ? invoice.getInvoiceNumber() : "N/A")
                .vendorName(invoice != null && invoice.getVendor() != null ? invoice.getVendor().getCompanyName() : "N/A")
                .poNumber(invoice != null && invoice.getPurchaseOrder() != null ? invoice.getPurchaseOrder().getPoNumber() : "N/A")
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .paymentMethod(payment.getPaymentMethod())
                .referenceNumber(payment.getReferenceNumber())
                .status(payment.getStatus())
                .build();
    }
}
