package com.smartprocure.web.rest;

import com.smartprocure.dto.request.ProductCreateDTO;
import com.smartprocure.dto.response.ApiResponse;
import com.smartprocure.dto.response.ProductResponseDTO;
import com.smartprocure.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Catalog", description = "Endpoints for managing enterprise products, SKUs, and reorder levels")
public class ProductRestController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get All Products", description = "Retrieve list of active products in catalog")
    public ResponseEntity<ApiResponse> getAllProducts() {
        List<ProductResponseDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.ok("Products fetched successfully", products));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER', 'PROCUREMENT_MANAGER')")
    @Operation(summary = "Get Low Stock Products", description = "Retrieve products whose quantity is below reorder level")
    public ResponseEntity<ApiResponse> getLowStockProducts() {
        List<ProductResponseDTO> products = productService.getLowStockProducts();
        return ResponseEntity.ok(ApiResponse.ok("Low stock products fetched", products));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER', 'WAREHOUSE_MANAGER')")
    @Operation(summary = "Create Product", description = "Add a new product SKU into catalog")
    public ResponseEntity<ApiResponse> createProduct(@Valid @RequestBody ProductCreateDTO createDTO) {
        ProductResponseDTO product = productService.createProduct(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Product created successfully", product));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROCUREMENT_MANAGER')")
    @Operation(summary = "Toggle Product Active Status")
    public ResponseEntity<ApiResponse> toggleProductStatus(@PathVariable Long id, @RequestParam boolean active) {
        ProductResponseDTO product = productService.toggleProductStatus(id, active);
        return ResponseEntity.ok(ApiResponse.ok("Product active status updated to " + active, product));
    }
}
