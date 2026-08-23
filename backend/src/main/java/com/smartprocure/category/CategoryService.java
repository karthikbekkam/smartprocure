package com.smartprocure.category;

import com.smartprocure.common.exception.DuplicateResourceException;
import com.smartprocure.common.exception.ResourceNotFoundException;
import com.smartprocure.domain.Category;
import com.smartprocure.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<CategoryDto> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return mapToDto(category);
    }

    @Transactional
    public CategoryDto createCategory(CategoryDto dto) {
        if (categoryRepository.existsByCode(dto.getCode())) {
            throw new DuplicateResourceException("Category code already exists: " + dto.getCode());
        }

        Category category = Category.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .active(true)
                .build();

        return mapToDto(categoryRepository.save(category));
    }

    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        if (dto.getActive() != null) {
            category.setActive(dto.getActive());
        }

        return mapToDto(categoryRepository.save(category));
    }

    private CategoryDto mapToDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .code(category.getCode())
                .name(category.getName())
                .description(category.getDescription())
                .active(category.getActive())
                .build();
    }
}
