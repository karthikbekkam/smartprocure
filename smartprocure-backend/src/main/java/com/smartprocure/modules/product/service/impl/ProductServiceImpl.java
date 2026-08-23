package com.smartprocure.modules.product.service.impl;

import com.smartprocure.core.exception.BusinessRuleViolationException;
import com.smartprocure.core.exception.ResourceNotFoundException;
import com.smartprocure.core.payload.PagedResponse;
import com.smartprocure.modules.product.dto.ProductDTO;
import com.smartprocure.modules.product.entity.Category;
import com.smartprocure.modules.product.entity.Product;
import com.smartprocure.modules.product.repository.CategoryRepository;
import com.smartprocure.modules.product.repository.ProductRepository;
import com.smartprocure.modules.product.service.ProductService;
import com.smartprocure.modules.vendor.entity.Vendor;
import com.smartprocure.modules.vendor.repository.VendorRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for Managing Product Master Data catalog.
 *
 * @author Principal Java Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final VendorRepository vendorRepository;

    @Override
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        log.info("Creating product record with SKU: {}", productDTO.getSku());

        if (productRepository.existsBySku(productDTO.getSku())) {
            throw new BusinessRuleViolationException("Product with SKU " + productDTO.getSku() + " already exists.");
        }

        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productDTO.getCategoryId()));

        Vendor preferredVendor = null;
        if (productDTO.getPreferredVendorId() != null) {
            preferredVendor = vendorRepository.findById(productDTO.getPreferredVendorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", productDTO.getPreferredVendorId()));
        }

        Product product = Product.builder()
                .sku(productDTO.getSku())
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .unitPrice(productDTO.getUnitPrice())
                .unitOfMeasure(productDTO.getUnitOfMeasure())
                .reorderLevel(productDTO.getReorderLevel() != null ? productDTO.getReorderLevel() : 10)
                .category(category)
                .preferredVendor(preferredVendor)
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("Successfully created Product ID: {} with SKU: {}", savedProduct.getId(), savedProduct.getSku());

        return mapToDTO(savedProduct);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        log.info("Updating product details for ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", productDTO.getCategoryId()));

        Vendor preferredVendor = null;
        if (productDTO.getPreferredVendorId() != null) {
            preferredVendor = vendorRepository.findById(productDTO.getPreferredVendorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", productDTO.getPreferredVendorId()));
        }

        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setUnitPrice(productDTO.getUnitPrice());
        product.setUnitOfMeasure(productDTO.getUnitOfMeasure());
        product.setReorderLevel(productDTO.getReorderLevel());
        product.setCategory(category);
        product.setPreferredVendor(preferredVendor);

        Product updatedProduct = productRepository.save(product);
        return mapToDTO(updatedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return mapToDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductBySku(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "sku", sku));
        return mapToDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProductDTO> getAllProducts(int page, int size, String sortBy, String sortDir, String search, Long categoryId) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }

            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim().toLowerCase() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("name")), searchPattern);
                Predicate skuLike = cb.like(cb.lower(root.get("sku")), searchPattern);
                Predicate descLike = cb.like(cb.lower(root.get("description")), searchPattern);
                predicates.add(cb.or(nameLike, skuLike, descLike));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Product> productsPage = productRepository.findAll(spec, pageable);

        List<ProductDTO> content = productsPage.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return PagedResponse.<ProductDTO>builder()
                .content(content)
                .page(productsPage.getNumber())
                .size(productsPage.getSize())
                .totalElements(productsPage.getTotalElements())
                .totalPages(productsPage.getTotalPages())
                .last(productsPage.isLast())
                .build();
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        log.info("Deleting product ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        productRepository.delete(product);
    }

    private ProductDTO mapToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .unitPrice(product.getUnitPrice())
                .unitOfMeasure(product.getUnitOfMeasure())
                .reorderLevel(product.getReorderLevel())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .preferredVendorId(product.getPreferredVendor() != null ? product.getPreferredVendor().getId() : null)
                .preferredVendorCompanyName(product.getPreferredVendor() != null ? product.getPreferredVendor().getCompanyName() : null)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
