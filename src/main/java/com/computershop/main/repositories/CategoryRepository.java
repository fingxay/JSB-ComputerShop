package com.computershop.main.repositories;

import com.computershop.main.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    
    /**
     * Find category by name
     */
    Optional<Category> findByCategoryName(String categoryName);
    
    /**
     * Find category by name (case insensitive)
     */
    Optional<Category> findByCategoryNameIgnoreCase(String categoryName);
    
    /**
     * Check if category exists by name
     */
    boolean existsByCategoryName(String categoryName);
    
    /**
     * Find all categories ordered by name
     */
    List<Category> findAllByOrderByCategoryNameAsc();
    
    /**
     * Get all category names
     */
    @Query("SELECT c.categoryName FROM Category c ORDER BY c.categoryName")
    List<String> findAllCategoryNames();
    
    /**
     * Count products in each category
     */
    @Query("SELECT c.categoryName, COUNT(p) FROM Category c LEFT JOIN c.products p GROUP BY c.categoryId, c.categoryName")
    List<Object[]> countProductsInCategories();
}