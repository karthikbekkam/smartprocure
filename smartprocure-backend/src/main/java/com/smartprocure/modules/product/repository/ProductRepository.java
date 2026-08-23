package com.smartprocure.modules.product.repository;

import com.smartprocure.modules.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for Product Entity supporting dynamic specification search.
 *
 * @author Principal Java Architect
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySku(String sku);

    Boolean existsBySku(String sku);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
}
