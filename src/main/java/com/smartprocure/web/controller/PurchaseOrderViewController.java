package com.smartprocure.web.controller;

import com.smartprocure.service.PurchaseOrderService;
import com.smartprocure.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderViewController {

    private final PurchaseOrderService purchaseOrderService;
    private final VendorService vendorService;

    @GetMapping
    public String listPurchaseOrders(Model model) {
        model.addAttribute("purchaseOrders", purchaseOrderService.getAllPurchaseOrders());
        model.addAttribute("vendors", vendorService.getAllVendors());
        return "purchase-orders/list";
    }
}
