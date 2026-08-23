package com.smartprocure.web.controller;

import com.smartprocure.service.InvoiceService;
import com.smartprocure.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/invoices")
@RequiredArgsConstructor
public class InvoiceViewController {

    private final InvoiceService invoiceService;
    private final PurchaseOrderService purchaseOrderService;

    @GetMapping
    public String listInvoices(Model model) {
        model.addAttribute("invoices", invoiceService.getAllInvoices());
        model.addAttribute("purchaseOrders", purchaseOrderService.getAllPurchaseOrders());
        return "invoices/list";
    }
}
