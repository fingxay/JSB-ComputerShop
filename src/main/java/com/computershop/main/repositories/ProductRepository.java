package com.computershop.main.repositories;

import com.computershop.main.entities.Product;
// import com.computershop.main.entities.Product;
import com.computershop.main.entities.Category;
import com.computershop.main.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
    /**
     * Find products by name containing keyword (case insensitive)
     */
    List<Product> findByProductNameContainingIgnoreCase(String keyword);
    
    /**
     * Find products by description containing keyword
     */
    List<Product> findByDescriptionContainingIgnoreCase(String keyword);
    
    /**
     * Find products by price range
     */
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    /**
     * Find products with stock quantity greater than specified amount
     */
    List<Product> findByStockQuantityGreaterThan(Integer minStock);
    
    /**
     * Find products by image
     */
    List<Product> findByImage(Image image);
    
    /**
     * Find products in stock (stock > 0)
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0")
    List<Product> findInStockProducts();
    
    /**
     * Find out of stock products
     */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity = 0")
    List<Product> findOutOfStockProducts();
    
    /**
     * Search products by name or description
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProducts(@Param("keyword") String keyword);
    
    /**
     * Find top selling products (products with most orders)
     */
    @Query("SELECT p FROM Product p JOIN OrderDetail od ON p.productId = od.product.productId " +
           "GROUP BY p.productId ORDER BY SUM(od.quantity) DESC")
    List<Product> findTopSellingProducts();
    
    /**
     * Find products ordered by price ascending
     */
    List<Product> findAllByOrderByPriceAsc();
    
    /**
     * Find products ordered by price descending
     */
    List<Product> findAllByOrderByPriceDesc();
    
    /**
     * Count products in stock
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity > 0")
    long countInStockProducts();
    
    /**
     * Find featured products for homepage (newest products with good stock)
     */
    @Query(value = "SELECT TOP :limit * FROM products WHERE stock_quantity > 0 ORDER BY created_at DESC", nativeQuery = true)
    List<Product> findFeaturedProducts(@Param("limit") int limit);
    
    /**
     * Find all distinct categories
     */
    @Query("SELECT DISTINCT c FROM Category c JOIN c.products p")
    List<Category> findDistinctCategories();
    
    /**
     * Find products by category
     */
    List<Product> findByCategory(Category category);
    
    /**
     * Find products by category ID
     */
    List<Product> findByCategoryCategoryId(Integer categoryId);
    
    /**
     * Find low stock products
     */
    @Query(value = "SELECT TOP :limit * FROM products WHERE stock_quantity < 20 ORDER BY stock_quantity ASC", nativeQuery = true)
    List<Product> findLowStockProducts(@Param("limit") int limit);
}