package com.smartprocure.web.controller;

import com.smartprocure.service.PurchaseOrderService;
import com.smartprocure.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/purchase-requests")
@RequiredArgsConstructor
public class PurchaseRequestViewController {

    private final PurchaseOrderService purchaseOrderService;
    private final VendorService vendorService;

    @GetMapping
    public String listPurchaseRequests(Model model) {
        model.addAttribute("purchaseRequests", purchaseOrderService.getAllPurchaseRequests());
        model.addAttribute("vendors", vendorService.getAllVendors());
        return "purchase-requests/list";
    }
}
