package com.smartprocure.web.controller;

import com.smartprocure.domain.repository.WarehouseRepository;
import com.smartprocure.service.InventoryService;
import com.smartprocure.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryViewController {

    private final InventoryService inventoryService;
    private final ProductService productService;
    private final WarehouseRepository warehouseRepository;

    @GetMapping
    public String listInventory(Model model) {
        model.addAttribute("inventories", inventoryService.getAllInventoryDTOs());
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("warehouses", warehouseRepository.findAll());
        return "inventory/list";
    }
}
