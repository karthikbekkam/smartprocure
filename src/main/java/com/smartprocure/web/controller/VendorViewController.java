package com.smartprocure.web.controller;

import com.smartprocure.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vendors")
@RequiredArgsConstructor
public class VendorViewController {

    private final VendorService vendorService;

    @GetMapping
    public String listVendors(Model model) {
        model.addAttribute("vendors", vendorService.getAllVendors());
        return "vendors/list";
    }

    @GetMapping("/new")
    public String vendorForm() {
        return "vendors/form";
    }

    @GetMapping("/{id}")
    public String vendorDetail(@PathVariable Long id, Model model) {
        model.addAttribute("vendor", vendorService.getVendorById(id));
        return "vendors/detail";
    }
}
