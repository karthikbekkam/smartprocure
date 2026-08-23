package com.smartprocure.modules.product.service;

import com.smartprocure.modules.product.dto.CategoryDTO;

import java.util.List;

/**
 * Enterprise Service Contract for Product Category Tree Management.
 *
 * @author Principal Java Architect
 */
public interface CategoryService {

    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);

    CategoryDTO getCategoryById(Long id);

    List<CategoryDTO> getAllCategories();

    List<CategoryDTO> getRootCategories();

    void deleteCategory(Long id);
}
