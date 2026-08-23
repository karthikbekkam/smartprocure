package com.smartprocure.modules.product.service.impl;

import com.smartprocure.core.exception.BusinessRuleViolationException;
import com.smartprocure.core.exception.ResourceNotFoundException;
import com.smartprocure.modules.product.dto.CategoryDTO;
import com.smartprocure.modules.product.entity.Category;
import com.smartprocure.modules.product.repository.CategoryRepository;
import com.smartprocure.modules.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service Implementation for Managing Hierarchical Product Categories.
 *
 * @author Principal Java Architect
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        log.info("Creating product category: {}", categoryDTO.getName());

        if (categoryRepository.existsByCode(categoryDTO.getCode())) {
            throw new BusinessRuleViolationException("Category code " + categoryDTO.getCode() + " already exists.");
        }

        Category parent = null;
        if (categoryDTO.getParentCategoryId() != null) {
            parent = categoryRepository.findById(categoryDTO.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryDTO.getParentCategoryId()));
        }

        Category category = Category.builder()
                .name(categoryDTO.getName())
                .code(categoryDTO.getCode())
                .description(categoryDTO.getDescription())
                .parentCategory(parent)
                .build();

        Category savedCategory = categoryRepository.save(category);
        log.info("Successfully created Category ID: {}", savedCategory.getId());

        return mapToDTO(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        log.info("Updating product category ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());

        if (categoryDTO.getParentCategoryId() != null) {
            Category parent = categoryRepository.findById(categoryDTO.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryDTO.getParentCategoryId()));
            category.setParentCategory(parent);
        } else {
            category.setParentCategory(null);
        }

        Category updated = categoryRepository.save(category);
        return mapToDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return mapToDTO(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getRootCategories() {
        return categoryRepository.findByParentCategoryIsNull().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        log.info("Deleting product category ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        categoryRepository.delete(category);
    }

    private CategoryDTO mapToDTO(Category category) {
        List<CategoryDTO> subCategories = null;
        if (category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
            subCategories = category.getSubCategories().stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        }

        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .code(category.getCode())
                .description(category.getDescription())
                .parentCategoryId(category.getParentCategory() != null ? category.getParentCategory().getId() : null)
                .parentCategoryName(category.getParentCategory() != null ? category.getParentCategory().getName() : null)
                .subCategories(subCategories)
                .build();
    }
}
