package com.smartprocure.web.controller;

import com.smartprocure.domain.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/warehouses")
@RequiredArgsConstructor
public class WarehouseViewController {

    private final WarehouseRepository warehouseRepository;

    @GetMapping
    public String listWarehouses(Model model) {
        model.addAttribute("warehouses", warehouseRepository.findAll());
        return "warehouses/list";
    }
}
