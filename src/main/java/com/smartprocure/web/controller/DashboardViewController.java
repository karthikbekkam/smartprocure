package com.smartprocure.web.controller;

import com.smartprocure.service.DashboardService;
import com.smartprocure.service.PurchaseOrderService;
import com.smartprocure.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardViewController {

    private final DashboardService dashboardService;
    private final PurchaseOrderService purchaseOrderService;
    private final VendorService vendorService;

    @GetMapping
    public String dashboard(Model model, Authentication authentication) {
        model.addAttribute("metrics", dashboardService.getMetrics());

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String roleName = authority.getAuthority().toUpperCase();
            if (roleName.startsWith("ROLE_")) {
                roleName = roleName.substring(5);
            }

            switch (roleName) {
                case "ADMIN":
                case "ADMINISTRATOR":
                    return "dashboard/admin-dashboard";
                case "PROCUREMENT_MANAGER":
                case "PROCUREMENT":
                    return "dashboard/procurement-dashboard";
                case "FINANCE_MANAGER":
                case "FINANCE":
                    return "dashboard/finance-dashboard";
                case "WAREHOUSE_MANAGER":
                case "WAREHOUSE":
                    return "dashboard/warehouse-dashboard";
                case "VENDOR":
                    return "dashboard/vendor-dashboard";
                default:
                    break;
            }
        }

        return "dashboard/admin-dashboard"; // Default fallback for authenticated users
    }

    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        model.addAttribute("metrics", dashboardService.getMetrics());
        return "dashboard/admin-dashboard";
    }

    @GetMapping("/procurement")
    public String procurementDashboard(Model model) {
        model.addAttribute("metrics", dashboardService.getMetrics());
        model.addAttribute("purchaseRequests", purchaseOrderService.getAllPurchaseRequests());
        model.addAttribute("vendors", vendorService.getAllVendors());
        return "dashboard/procurement-dashboard";
    }

    @GetMapping("/finance")
    public String financeDashboard(Model model) {
        model.addAttribute("metrics", dashboardService.getMetrics());
        return "dashboard/finance-dashboard";
    }

    @GetMapping("/warehouse")
    public String warehouseDashboard(Model model) {
        model.addAttribute("metrics", dashboardService.getMetrics());
        return "dashboard/warehouse-dashboard";
    }

    @GetMapping("/vendor")
    public String vendorDashboard(Model model) {
        model.addAttribute("metrics", dashboardService.getMetrics());
        return "dashboard/vendor-dashboard";
    }
}