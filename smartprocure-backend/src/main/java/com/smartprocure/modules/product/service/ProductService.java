package com.smartprocure.modules.product.service;

import com.smartprocure.core.payload.PagedResponse;
import com.smartprocure.modules.product.dto.ProductDTO;

/**
 * Enterprise Service Contract for Product Master Data Operations.
 *
 * @author Principal Java Architect
 */
public interface ProductService {

    ProductDTO createProduct(ProductDTO productDTO);

    ProductDTO updateProduct(Long id, ProductDTO productDTO);

    ProductDTO getProductById(Long id);

    ProductDTO getProductBySku(String sku);

    PagedResponse<ProductDTO> getAllProducts(int page, int size, String sortBy, String sortDir, String search, Long categoryId);

    void deleteProduct(Long id);
}
