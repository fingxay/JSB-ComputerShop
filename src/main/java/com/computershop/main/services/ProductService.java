package com.computershop.main.services;

import com.computershop.main.entities.Product;
import com.computershop.main.entities.Image;
import com.computershop.main.entities.Category;
import com.computershop.main.repositories.ProductRepository;
// import com.computershop.main.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ImageService imageService;
    
    /**
     * Get all products
     */
    public List<Product> getAllProducts() {
        return productRepository.findAllWithCategoryAndImage();
    }
    
    /**
     * Get product by ID
     */
    public Optional<Product> getProductById(Integer productId) {
        return productRepository.findById(productId);
    }
    
    /**
     * Search products by name
     */
    public List<Product> searchProductsByName(String keyword) {
        return productRepository.findByProductNameContainingIgnoreCase(keyword);
    }
    
    /**
     * Search products by description
     */
    public List<Product> searchProductsByDescription(String keyword) {
        return productRepository.findByDescriptionContainingIgnoreCase(keyword);
    }
    
    /**
     * Search products (name and description)
     */
    public List<Product> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword);
    }
    
    /**
     * Get products by price range
     */
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }
    
    /**
     * Get products with minimum stock
     */
    public List<Product> getProductsWithMinStock(Integer minStock) {
        return productRepository.findByStockQuantityGreaterThan(minStock);
    }
    
    /**
     * Get in-stock products
     */
    public List<Product> getInStockProducts() {
        return productRepository.findInStockProducts();
    }
    
    /**
     * Get out-of-stock products
     */
    public List<Product> getOutOfStockProducts() {
        return productRepository.findOutOfStockProducts();
    }
    
    /**
     * Get products ordered by price (ascending)
     */
    public List<Product> getProductsByPriceAsc() {
        return productRepository.findAllByOrderByPriceAsc();
    }
    
    /**
     * Get products ordered by price (descending)
     */
    public List<Product> getProductsByPriceDesc() {
        return productRepository.findAllByOrderByPriceDesc();
    }
    
    /**
     * Get top selling products
     */
    public List<Product> getTopSellingProducts() {
        return productRepository.findTopSellingProducts();
    }
    
    /**
     * Create new product
     */
    public Product createProduct(Product product) {
        // Set default image if not provided
        if (product.getImage() == null) {
            product.setImage(imageService.getPlaceholderImage());
        }
        
        return productRepository.save(product);
    }
    
    /**
     * Create product with image URL
     */
    public Product createProduct(String productName, String description, 
                               BigDecimal price, Integer stockQuantity, String imageUrl) {
        Product product = new Product();
        product.setProductName(productName);
        product.setDescription(description);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        
        // Get or create image
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            Image image = imageService.getOrCreateImageByUrl(imageUrl);
            product.setImage(image);
        } else {
            product.setImage(imageService.getPlaceholderImage());
        }
        
        return productRepository.save(product);
    }
    
    /**
     * Update product
     */
    public Product updateProduct(Integer productId, Product productDetails) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        
        product.setProductName(productDetails.getProductName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStockQuantity(productDetails.getStockQuantity());
        
        if (productDetails.getImage() != null) {
            product.setImage(productDetails.getImage());
        }
        
        return productRepository.save(product);
    }
    
    /**
     * Update product stock
     */
    public Product updateProductStock(Integer productId, Integer newStock) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        
        product.setStockQuantity(newStock);
        return productRepository.save(product);
    }
    
    /**
     * Decrease product stock (for orders)
     */
    public Product decreaseStock(Integer productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        
        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + product.getStockQuantity() + ", Requested: " + quantity);
        }
        
        product.setStockQuantity(product.getStockQuantity() - quantity);
        return productRepository.save(product);
    }
    
    /**
     * Increase product stock (for returns/restocking)
     */
    public Product increaseStock(Integer productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        
        product.setStockQuantity(product.getStockQuantity() + quantity);
        return productRepository.save(product);
    }
    
    /**
     * Delete product
     */
    public void deleteProduct(Integer productId) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found with id: " + productId);
        }
        productRepository.deleteById(productId);
    }
    
    /**
     * Check if product is in stock
     */
    public boolean isInStock(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        return product.getStockQuantity() > 0;
    }
    
    /**
     * Check if product has sufficient stock
     */
    public boolean hasSufficientStock(Integer productId, Integer requestedQuantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        return product.getStockQuantity() >= requestedQuantity;
    }
    
    /**
     * Count in-stock products
     */
    public long countInStockProducts() {
        return productRepository.countInStockProducts();
    }
    
    /**
     * Get featured products for homepage
     */
    public List<Product> getFeaturedProducts(int limit) {
        List<Product> allFeatured = productRepository.findFeaturedProductsWithDetails();
        return allFeatured.stream().limit(limit).toList();
    }
    
    /**
     * Get total number of products
     */
    public long getTotalProducts() {
        return productRepository.count();
    }
    
    /**
     * Get all unique categories
     */
    public List<Category> getAllCategories() {
        return productRepository.findDistinctCategories();
    }
    
    /**
     * Get all category names
     */
    public List<String> getAllCategoryNames() {
        return getAllCategories().stream()
                .map(Category::getCategoryName)
                .toList();
    }
    
    /**
     * Get products by category
     */
    public List<Product> getProductsByCategory(Category category) {
        return productRepository.findByCategory(category);
    }
    
    /**
     * Get products by category name
     */
    public List<Product> getProductsByCategoryName(String categoryName) {
        return productRepository.findDistinctCategories().stream()
                .filter(cat -> cat.getCategoryName().equals(categoryName))
                .findFirst()
                .map(this::getProductsByCategory)
                .orElse(List.of());
    }
    
    /**
     * Get products by category ID
     */
    public List<Product> getProductsByCategoryId(Integer categoryId) {
        return productRepository.findByCategoryCategoryId(categoryId);
    }
    
    /**
     * Filter products by price range
     */
    public List<Product> filterProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }
    
    /**
     * Get products sorted by price ascending
     */
    public List<Product> getProductsSortedByPriceAsc() {
        return productRepository.findAllByOrderByPriceAsc();
    }
    
    /**
     * Get products sorted by price descending
     */
    public List<Product> getProductsSortedByPriceDesc() {
        return productRepository.findAllByOrderByPriceDesc();
    }
    
    /**
     * Get low stock products
     */
    public List<Product> getLowStockProducts(int limit) {
        return productRepository.findLowStockProducts(limit);
    }
    
    /**
     * Update product stock quantity
     */
    public void updateStock(Integer productId, int newStock) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setStockQuantity(newStock);
            productRepository.save(product);
        }
    }
    
}