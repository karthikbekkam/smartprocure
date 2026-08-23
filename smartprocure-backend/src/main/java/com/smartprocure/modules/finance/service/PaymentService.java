package com.smartprocure.modules.finance.service;

import com.smartprocure.modules.finance.dto.PaymentDTO;
import com.smartprocure.modules.finance.dto.PaymentRequest;

import java.util.List;

/**
 * Enterprise Service Contract for Payment Processing & Reconciliations.
 *
 * @author Principal Java Architect
 */
public interface PaymentService {

    PaymentDTO processPayment(PaymentRequest paymentRequest);

    List<PaymentDTO> getPaymentsByInvoice(Long invoiceId);

    PaymentDTO getPaymentById(Long id);
}
