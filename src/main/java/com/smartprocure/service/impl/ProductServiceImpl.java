package com.smartprocure.service.impl;

import com.smartprocure.domain.entity.Category;
import com.smartprocure.domain.entity.Product;
import com.smartprocure.domain.repository.CategoryRepository;
import com.smartprocure.domain.repository.ProductRepository;
import com.smartprocure.dto.request.ProductCreateDTO;
import com.smartprocure.dto.response.ProductResponseDTO;
import com.smartprocure.exception.DuplicateResourceException;
import com.smartprocure.exception.ResourceNotFoundException;
import com.smartprocure.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public ProductResponseDTO createProduct(ProductCreateDTO createDTO) {
        if (productRepository.existsBySku(createDTO.getSku())) {
            throw new DuplicateResourceException("Product SKU already exists: " + createDTO.getSku());
        }

        Category category = null;
        if (createDTO.getCategoryId() != null) {
            category = categoryRepository.findById(createDTO.getCategoryId()).orElse(null);
        }
        if (category == null) {
            category = categoryRepository.findAll().stream().findFirst().orElse(null);
        }

        Product product = Product.builder()
                .sku(createDTO.getSku())
                .name(createDTO.getName())
                .description(createDTO.getDescription())
                .category(category)
                .unitPrice(createDTO.getUnitPrice())
                .reorderLevel(createDTO.getReorderLevel())
                .active(true)
                .build();

        Product saved = productRepository.save(product);
        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return mapToDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getLowStockProducts() {
        return productRepository.findLowStockProducts().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductResponseDTO toggleProductStatus(Long id, boolean active) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        product.setActive(active);
        return mapToDTO(productRepository.save(product));
    }

    private ProductResponseDTO mapToDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .categoryName(product.getCategory().getName())
                .unitPrice(product.getUnitPrice())
                .reorderLevel(product.getReorderLevel())
                .active(product.getActive())
                .build();
    }
}
