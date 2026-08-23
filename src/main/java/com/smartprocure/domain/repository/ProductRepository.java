package com.smartprocure.domain.repository;

import com.smartprocure.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);
    Boolean existsBySku(String sku);
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByActiveTrue();
    
    @Query("SELECT DISTINCT i.product FROM Inventory i WHERE i.quantityAvailable <= i.product.reorderLevel")
    List<Product> findLowStockProducts();
}
