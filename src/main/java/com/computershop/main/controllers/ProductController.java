package com.computershop.main.controllers;

import com.computershop.main.entities.Product;
import com.computershop.main.entities.Category;
import com.computershop.main.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public String productsPage(@RequestParam(value = "category", required = false) String category,
                              @RequestParam(value = "search", required = false) String search,
                              @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
                              @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
                              @RequestParam(value = "sort", required = false, defaultValue = "name") String sort,
                              @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                              @RequestParam(value = "size", required = false, defaultValue = "12") int size,
                              Model model) {
        
        try {
            List<Product> products;
            
            // Apply filters and search
            if (search != null && !search.trim().isEmpty()) {
                products = productService.searchProductsByName(search.trim());
            } else if (category != null && !category.trim().isEmpty()) {
                products = productService.getProductsByCategoryName(category);
            } else {
                products = productService.getAllProducts();
            }
            
            // Apply price filters
            if (minPrice != null || maxPrice != null) {
                products = productService.filterProductsByPriceRange(
                    minPrice != null ? minPrice : BigDecimal.ZERO,
                    maxPrice != null ? maxPrice : BigDecimal.valueOf(Double.MAX_VALUE)
                );
            }
            
            // Apply sorting
            switch (sort) {
                case "price-asc":
                    products = productService.getProductsSortedByPriceAsc();
                    break;
                case "price-desc":
                    products = productService.getProductsSortedByPriceDesc();
                    break;
                case "popular":
                    products = productService.getTopSellingProducts();
                    break;
                default:
                    // Default: by name
                    break;
            }
            
            model.addAttribute("products", products);
            model.addAttribute("categories", productService.getAllCategoryNames());
            model.addAttribute("totalProducts", products.size());
            
            // Filter parameters
            model.addAttribute("selectedCategory", category);
            model.addAttribute("searchQuery", search);
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);
            model.addAttribute("currentSort", sort);
            
        } catch (Exception e) {
            model.addAttribute("products", List.of());
            model.addAttribute("categories", List.of());
            model.addAttribute("totalProducts", 0);
            model.addAttribute("error", "Đã xảy ra lỗi khi tải sản phẩm: " + e.getMessage());
        }
        
        return "products";
    }

    @GetMapping("/{id}")
    public String productDetail(@PathVariable("id") Integer productId, Model model) {
        try {
            Optional<Product> productOpt = productService.getProductById(productId);
            
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                model.addAttribute("product", product);
                
                // Get related products (same category)
                List<Product> relatedProducts = List.of();
                if (product.getCategory() != null) {
                    relatedProducts = productService.getProductsByCategory(product.getCategory());
                    relatedProducts.removeIf(p -> p.getProductId().equals(productId)); // Remove current product
                    if (relatedProducts.size() > 4) {
                        relatedProducts = relatedProducts.subList(0, 4); // Limit to 4
                    }
                }
                model.addAttribute("relatedProducts", relatedProducts);
                
                // Check stock availability
                model.addAttribute("inStock", product.getStockQuantity() > 0);
                model.addAttribute("lowStock", product.getStockQuantity() > 0 && product.getStockQuantity() < 10);
                
                return "product-detail"; // Will need to create this template
            } else {
                model.addAttribute("error", "Không tìm thấy sản phẩm");
                return "redirect:/products";
            }
            
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            return "redirect:/products";
        }
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam("q") String query, Model model) {
        try {
            if (query == null || query.trim().isEmpty()) {
                return "redirect:/products";
            }
            
            List<Product> products = productService.searchProductsByName(query.trim());
            List<Product> descriptionResults = productService.searchProductsByDescription(query.trim());
            
            // Combine results (avoid duplicates)
            for (Product product : descriptionResults) {
                if (!products.contains(product)) {
                    products.add(product);
                }
            }
            
            model.addAttribute("products", products);
            model.addAttribute("searchQuery", query);
            model.addAttribute("totalProducts", products.size());
            model.addAttribute("categories", productService.getAllCategoryNames());
            
            return "products";
            
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi tìm kiếm: " + e.getMessage());
            return "redirect:/products";
        }
    }

    @GetMapping("/category/{category}")
    public String productsByCategory(@PathVariable("category") String category, Model model) {
        try {
            List<Product> products = productService.getProductsByCategoryName(category);
            
            model.addAttribute("products", products);
            model.addAttribute("selectedCategory", category);
            model.addAttribute("totalProducts", products.size());
            model.addAttribute("categories", productService.getAllCategoryNames());
            
            return "products";
            
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            return "redirect:/products";
        }
    }

    @PostMapping("/{id}/add-to-cart")
    @ResponseBody
    public String addToCart(@PathVariable("id") Integer productId,
                           @RequestParam("quantity") Integer quantity,
                           HttpSession session) {
        try {
            // Check if user is logged in
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                return "error:Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng";
            }
            
            // Check product availability
            Optional<Product> productOpt = productService.getProductById(productId);
            if (productOpt.isEmpty()) {
                return "error:Không tìm thấy sản phẩm";
            }
            
            Product product = productOpt.get();
            if (product.getStockQuantity() < quantity) {
                return "error:Không đủ hàng trong kho";
            }
            
            // Add to cart logic would go here
            // For now, just return success
            return "success:Đã thêm sản phẩm vào giỏ hàng";
            
        } catch (Exception e) {
            return "error:Đã xảy ra lỗi: " + e.getMessage();
        }
    }
}