package com.smartprocure.modules.product.repository;

import com.smartprocure.modules.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for Category Entity.
 *
 * @author Principal Java Architect
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByCode(String code);

    Boolean existsByCode(String code);

    List<Category> findByParentCategoryIsNull();
}
