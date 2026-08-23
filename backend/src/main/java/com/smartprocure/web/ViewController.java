package com.smartprocure.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String register() {
        return "auth/register";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard/admin-dashboard";
    }

    @GetMapping("/vendors")
    public String vendors() {
        return "vendor/list";
    }

    @GetMapping("/products")
    public String products() {
        return "product/list";
    }

    @GetMapping("/purchase-requests")
    public String purchaseRequests() {
        return "purchase/pr-list";
    }

    @GetMapping("/purchase-orders")
    public String purchaseOrders() {
        return "purchase/po-list";
    }

    @GetMapping("/invoices")
    public String invoices() {
        return "invoice/list";
    }

    @GetMapping("/inventory")
    public String inventory() {
        return "inventory/list";
    }
}
