package com.smartprocure.service;

import com.smartprocure.dto.request.ProductCreateDTO;
import com.smartprocure.dto.response.ProductResponseDTO;

import java.util.List;

public interface ProductService {
    ProductResponseDTO createProduct(ProductCreateDTO createDTO);
    List<ProductResponseDTO> getAllProducts();
    ProductResponseDTO getProductById(Long id);
    List<ProductResponseDTO> getLowStockProducts();
    ProductResponseDTO toggleProductStatus(Long id, boolean active);
}
