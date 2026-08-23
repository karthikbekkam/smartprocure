package com.smartprocure.product;

import com.smartprocure.common.exception.DuplicateResourceException;
import com.smartprocure.common.exception.ResourceNotFoundException;
import com.smartprocure.domain.Category;
import com.smartprocure.domain.Product;
import com.smartprocure.domain.repository.CategoryRepository;
import com.smartprocure.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return mapToDto(product);
    }

    @Transactional
    public ProductDto createProduct(ProductDto dto) {
        if (productRepository.existsBySku(dto.getSku())) {
            throw new DuplicateResourceException("Product with SKU already exists: " + dto.getSku());
        }

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", dto.getCategoryId()));

        Product product = Product.builder()
                .sku(dto.getSku())
                .name(dto.getName())
                .description(dto.getDescription())
                .category(category)
                .unitPrice(dto.getUnitPrice())
                .reorderLevel(dto.getReorderLevel() != null ? dto.getReorderLevel() : 10)
                .active(true)
                .build();

        return mapToDto(productRepository.save(product));
    }

    private ProductDto mapToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .unitPrice(product.getUnitPrice())
                .reorderLevel(product.getReorderLevel())
                .active(product.getActive())
                .build();
    }
}
