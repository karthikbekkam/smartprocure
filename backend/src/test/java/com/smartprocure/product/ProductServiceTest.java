package com.smartprocure.product;

import com.smartprocure.common.exception.DuplicateResourceException;
import com.smartprocure.domain.Category;
import com.smartprocure.domain.Product;
import com.smartprocure.domain.repository.CategoryRepository;
import com.smartprocure.domain.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        productDto = ProductDto.builder()
                .sku("SKU-TEST-001")
                .name("Test Laptop Pro")
                .categoryId(1L)
                .unitPrice(new BigDecimal("1299.99"))
                .reorderLevel(10)
                .build();
    }

    @Test
    void createProduct_Success() {
        when(productRepository.existsBySku("SKU-TEST-001")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(Category.builder().code("CAT-1").name("Hardware").build()));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        ProductDto result = productService.createProduct(productDto);

        assertNotNull(result);
        assertEquals("SKU-TEST-001", result.getSku());
        assertEquals("Test Laptop Pro", result.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void createProduct_DuplicateSku_ThrowsException() {
        when(productRepository.existsBySku("SKU-TEST-001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> productService.createProduct(productDto));
        verify(productRepository, never()).save(any(Product.class));
    }
}
